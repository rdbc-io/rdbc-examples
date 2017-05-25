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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.concurrent.duration._

class ApplicationController @Inject()(db: ConnectionFactory, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  private implicit val timeout = 30.seconds.timeout

  def list = Action.async { _ =>
    db.withConnectionF { conn =>
      conn
        .statement(sql"SELECT i, t, s FROM rdbc_demo ORDER BY i, t, s")
        .executeForSet().map { rs =>
        val records = rs.rows.map { row =>
          Record(row.intOpt("i"), row.instantOpt("t"), row.strOpt("s"))
        }
        Ok(views.html.Application.list(records, form))
      }
    }
  }

  def stream = Action.async { _ =>
    db.connection().flatMap { conn =>
      conn
        .statement(sql"SELECT i, t, s FROM rdbc_demo ORDER BY i, t, s")
        .executeForStream().map { rs =>
        val source = Source.fromPublisher(rs.rows).map { row =>
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
  }

  def insert = Action.async { implicit request =>
    form.bindFromRequest().fold(
      errForm => {
        Logger.error(s"error in mapping: ${errForm.errors}")
        Future.successful(())
      },
      r => {
        db.withConnectionF { conn =>
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
