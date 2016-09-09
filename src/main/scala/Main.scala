import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import utils.{Config, MigrationConfig}

import scala.concurrent.ExecutionContext

object Main extends App with Config with MigrationConfig {
  private implicit val system = ActorSystem()
  protected implicit val executor: ExecutionContext = system.dispatcher
  protected val log: LoggingAdapter = Logging(system, getClass)
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  migrate()

  private val routes: Route = new Routes().routes

  Http().bindAndHandle(handler = logRequestResult("log")(routes), interface = httpInterface, port = httpPort)
}