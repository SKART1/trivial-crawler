package com.github.skart1
package components

import cats.Applicative
import cats.effect.{Async, ExitCode, IO, Resource, Sync}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import com.github.skart1.controllers.CrawlController
import io.circe.syntax._
import sttp.tapir.openapi.circe._
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import org.http4s.server.{Router, Server}
import sttp.tapir.docs.openapi.{OpenAPIDocsInterpreter, OpenAPIDocsOptions}
import sttp.tapir.openapi.{OpenAPI, Server => OpenApiServer}
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.{PublicEndpoint, endpoint, infallibleEndpoint, query, stringBody}

import scala.concurrent.{ExecutionContext, Future}

case class HttpModulesComponent[F[_]](server: Resource[F, Server])

object HttpModulesComponent {

  private def formOpenApiEndpoint[F[_]: Applicative](config: ConfigComponent)
                                                    (allEndpoints: List[ServerEndpoint[Any, F]]): ServerEndpoint[Any, F] = {
    val openApiDocs: OpenAPI =
      OpenAPIDocsInterpreter()
        .toOpenAPI(allEndpoints.map(_.endpoint), "Crawler", "Crawler openAPI")
        .addServer(OpenApiServer(s"http://${config.serverConfig.host}:${config.serverConfig.port}"))

    val openApiDocsJson = openApiDocs.asJson.spaces2

    endpoint
      .get
      .in("")
      .out(stringBody)
      .serverLogic(_ => openApiDocsJson.asRight[Unit].pure)
  }

  def apply[F[_]: Async](config: ConfigComponent, services: ServiceComponent[F]): HttpModulesComponent[F] = {
    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    //Crawl controller
    val crawlControllerController = new CrawlController[F](services.crawlerService)
    val crawlRoutes = crawlControllerController.endpoints.map(route => ("/", Http4sServerInterpreter[F]().toRoutes(route)))

    //Others controllers

    //OpenApiStuff
    //TODO: cors problem solve
    val allEndpoints = crawlControllerController.endpoints
    val openApiEndpoint = formOpenApiEndpoint(config)(allEndpoints)
    val openApiService = Http4sServerInterpreter[F]().toRoutes(openApiEndpoint)
    val openApiServiceWithCors = CORS.policy.withAllowOriginAll(openApiService)
    val openApiRoute =
      ("internal/swagger" -> openApiServiceWithCors)

    //All routes combined
    val allRoutes = crawlRoutes :+ openApiRoute

    //TODO: ssl
    val server = BlazeServerBuilder[F]
      .withExecutionContext(ec)
      .bindHttp(config.serverConfig.port, config.serverConfig.host)
      .withHttpApp(Router(allRoutes: _*).orNotFound)
      .resource

    new HttpModulesComponent[F](server)
  }

}
