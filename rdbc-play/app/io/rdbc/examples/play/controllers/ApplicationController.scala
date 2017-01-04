package io.rdbc.examples.play.controllers

import javax.inject.Inject

import io.rdbc.examples.play.views
import io.rdbc.sapi.Interpolators.SqlInterpolator
import io.rdbc.sapi.{Connection, ConnectionFactory}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.concurrent.duration._

class ApplicationController @Inject()(db: ConnectionFactory) extends Controller {

  private implicit val timeout: FiniteDuration = 30.seconds

  def index = Action.async { _ =>

    val xsFut = withConnection { conn =>
      val rsFut = for {
        select <- conn.select(sql"select x from test order by x")
        rs <- select.executeForSet()
      } yield rs

      rsFut.map { rs =>
        rs.rows.map(_.int("x"))
      }
    }

    xsFut.map { xs =>
      Ok(views.html.Application.index(xs))
    }
  }

  private def withConnection[A](body: Connection => Future[A]): Future[A] = {
    db.connection().flatMap { conn =>
      body(conn).andThen { case _ =>
        conn.release()
      }
    }
  }

}
