/*
 * Copyright 2017 rdbc contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rdbc.examples.play.controllers

import java.time._
import javax.inject.Inject

import akka.stream.scaladsl.Source
import io.rdbc.examples.play.{Record, views}
import io.rdbc.sapi._
import io.rdbc.util.Futures._
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.InjectedController

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class ApplicationController @Inject()(db: ConnectionFactory)(implicit ec: ExecutionContext)
  extends InjectedController with I18nSupport {

  private implicit val timeout = 30.seconds.timeout

  def list = Action.async { implicit request =>
    db.withConnection { conn =>
      conn
        .statement(sql"SELECT i, t, s FROM rdbc_demo ORDER BY i, t, s")
        .executeForSet().map { rs =>
        val records = rs.rows.map { row =>
          Record(row.intOpt("i"), row.instantOpt("t"), row.strOpt("s"))
        }
        Ok(views.html.Application.list(records))
      }
    }
  }

  def stream = Action.async { _ =>
    db.connection().map { conn =>
      val pub = conn
        .statement(sql"SELECT i, t, s FROM rdbc_demo ORDER BY i, t, s")
        .stream()

      val source = Source.fromPublisher(pub).map { row =>
        Json.toJson(
          Record(row.intOpt("i"), row.instantOpt("t"), row.strOpt("s"))
        )
      }.watchTermination() { case (_, done) =>
        done.andThenF { case _ =>
          conn.release()
        }
      }
      //TODO this construct of connection releasing after stream completes
      // will be so common that stream needs to facilitate handling this
      Ok.chunked(source)
    }
  }

  def insert = Action.async { implicit request =>
    form.bindFromRequest().fold(
      errForm => {
        Logger.error(s"error in mapping: ${errForm.errors}")
        Future.successful(())
      },
      r => {
        db.withConnection { conn =>
          conn
            .statement(sql"INSERT INTO rdbc_demo(i, t, s) VALUES (${r.i}, ${r.t}, ${r.s})")
            .execute()
        }
      }
    ).map { _ =>
      Redirect(routes.ApplicationController.list)
    }
  }

  private val form = Form(
    mapping(
      "i" -> optional(number),
      "t" -> optional(localDateTime("yyyy-MM-dd'T'HH:mm").transform[Instant](
        ldt => ldt.toInstant(ZoneOffset.UTC),
        inst => LocalDateTime.ofInstant(inst, ZoneOffset.UTC)
      )),
      "s" -> optional(text)
    )(Record.apply)(Record.unapply)
  )

}
