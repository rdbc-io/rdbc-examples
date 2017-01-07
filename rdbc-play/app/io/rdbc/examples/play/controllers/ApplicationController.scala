package io.rdbc.examples.play.controllers

import java.time._
import javax.inject.Inject

import io.rdbc.examples.play.{Record, views}
import io.rdbc.sapi.ConnectionFactory
import io.rdbc.sapi.Interpolators.SqlInterpolator
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.concurrent.duration._

class ApplicationController @Inject()(db: ConnectionFactory, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  private implicit val timeout: FiniteDuration = 30.seconds

  def list = Action.async { _ =>

    val recordsFut = db.withConnection { conn =>
      for {
        select <- conn.select(sql"SELECT i, t, s FROM rdbc_demo ORDER BY i, t, s")
        rs <- select.executeForSet()
      } yield {
        rs.rows.map { row =>
          Record(row.intOpt("i"), row.instantOpt("t"), row.strOpt("s"))
        }

      }
    }

    recordsFut.map { records =>
      Ok(views.html.Application.list(records, form))
    }
  }

  //TODO streaming

  def insert = Action.async { implicit request =>
    form.bindFromRequest().fold(
      errForm => {
        Logger.error(s"error in mapping: ${errForm.errors}")
        Future.successful(())
      },
      r => {
        db.withConnection { conn =>
          for {
            select <- conn.insert(sql"INSERT INTO rdbc_demo(i, t, s) VALUES (${r.i}, ${r.t}, ${r.s})")
            _ <- select.execute()
          } yield ()
        }
      }
    ).map { _ =>
      Redirect(routes.ApplicationController.list)
    }
  }

  private val form = Form(
    mapping(
      "x" -> optional(number),
      "t" -> optional(localDateTime("yyyy-MM-dd'T'HH:mm").transform[Instant](
        ldt => ldt.toInstant(ZoneOffset.UTC),
        inst => LocalDateTime.ofInstant(inst, ZoneOffset.UTC)
      )),
      "s" -> optional(text)
    )(Record.apply)(Record.unapply)
  )

}
