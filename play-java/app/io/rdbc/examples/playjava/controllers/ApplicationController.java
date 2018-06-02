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

package io.rdbc.examples.playjava.controllers;


import akka.NotUsed;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import io.rdbc.examples.playjava.Record;
import io.rdbc.examples.playjava.RecordFormData;
import io.rdbc.examples.playjava.views.html.list;
import io.rdbc.japi.ConnectionFactory;
import io.rdbc.japi.RowPublisher;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/*
class ApplicationController @Inject()(db: ConnectionFactory)(implicit ec: ExecutionContext)
  extends InjectedController with I18nSupport {

  private implicit val timeout = 30.seconds.timeout

  def stream = Action.async { _ =>
    db.connection().map { conn =>
      val pub = conn
        .statement(sql"SELECT i, t, s FROM rdbc_demo ORDER BY i, t, s")
        .stream()

      val source = Source.fromPublisher(pub).map { row =>
        Json.toJson(
          Record(row.intOpt("i"), row.instantOpt("t"), row.strOpt("s"))
        )
      }.alsoTo(Sink.onComplete(_ => conn.release()))
      Ok.chunked(source)
    }
  }

}
*/
public class ApplicationController extends Controller {

    private final ConnectionFactory db;
    private final list view;
    private final HttpExecutionContext httpEc;
    private final Form<RecordFormData> form;

    @Inject
    public ApplicationController(ConnectionFactory db,
                                 list view,
                                 FormFactory formFactory,
                                 HttpExecutionContext httpEc) {
        this.db = db;
        this.view = view;
        this.httpEc = httpEc;
        this.form = formFactory.form(RecordFormData.class);
    }

    public CompletionStage<Result> list() {
        final Executor currentHttpEc = httpEc.current();
        return db.withConnection(conn ->
                conn.statement("SELECT i, t, v FROM rdbc_demo ORDER BY i, t, v")
                        .noArgs().executeForSet().thenApply(rs ->
                        rs.getRows().stream().map(row ->
                                new Record(
                                        row.getIntOpt("i"),
                                        row.getInstantOpt("t"),
                                        row.getStrOpt("v")
                                )
                        ).collect(Collectors.toList())
                ).thenApplyAsync(
                        records -> Results.ok(view.render(records)),
                        currentHttpEc
                )
        );
    }

    public CompletionStage<Result> stream() {
        return db.getConnection().thenApply(conn -> {
            RowPublisher pub = conn.statement("SELECT i, t, v FROM rdbc_demo ORDER BY i, t, v")
                    .noArgs().stream();

            Source<ByteString, NotUsed> recordSource = Source.fromPublisher(pub)
                    .map(row -> Json.toJson(
                            new Record(
                                    row.getIntOpt("i"),
                                    row.getInstantOpt("t"),
                                    row.getStrOpt("v")
                            )).toString()
                    )
                    .map(ByteString::fromString)
                    .alsoTo(Sink.onComplete(ignore -> conn.release()));

            return Results.ok().chunked(recordSource).as(Http.MimeTypes.JSON);
        });
    }

    public CompletionStage<Result> insert() {
        RecordFormData record = form.bindFromRequest(request()).get();

        return db.withConnection(conn ->
                conn.statement("INSERT INTO rdbc_demo(i, t, v) VALUES (:i, :t, :v)")
                        .arg("i", record.getInteger())
                        .arg("t", record.getTimestamp())
                        .arg("v", record.getVarchar())
                        .bind().execute()
        ).thenApply(ignore -> Results.redirect(routes.ApplicationController.list()));
    }
}