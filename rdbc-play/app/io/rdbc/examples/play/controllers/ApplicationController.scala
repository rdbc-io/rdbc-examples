package io.rdbc.examples.play.controllers

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

  private val form = Form(
    mapping(
      "x" -> optional(number),
      "t" -> optional(localDateTime("yyyy-MM-dd'T'HH:mm")),
      "s" -> optional(text)
    )(Record.apply)(Record.unapply)
  )

  def list = Action.async { _ =>

    val recordsFut = db.withConnection { conn =>
      for {
        select <- conn.select(sql"SELECT x, t, s FROM test ORDER BY x, t, s")
        rs <- select.executeForSet()
      } yield {
        rs.rows.map { row =>
          Record(row.intOpt("x"), row.localDateTimeOpt("t"), row.strOpt("s"))
        }

      }
    }

    recordsFut.map { records =>
      Ok(views.html.Application.list(records, form))
    }
  }

  def add = Action.async { implicit request =>
    form.bindFromRequest().fold(
      errForm => {
        Logger.error(s"error in mapping: ${errForm.errors}")
        Future.successful()
      },
      r => {
        db.withConnection { conn =>
          for {
            select <- conn.insert(sql"INSERT INTO test(x, t, s) VALUES (${r.x}, ${r.t}, ${r.s})")
            _ <- select.execute()
          } yield ()
        }
      }
    ).map { _ =>
      Redirect(routes.ApplicationController.list)
    }
  }

}
