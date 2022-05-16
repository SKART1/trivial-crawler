package com.github.skart1
package controllers

import controllers.CrawlController.{CrawlRequest, CrawlResponseItem}
import services.CrawlerService
import services.domain.{CrawlResult, ErrorCrawlResult, Item, SuccessCrawlResult}

import cats.effect.Async
import cats.implicits.{catsSyntaxEitherId, catsSyntaxOptionId, toFunctorOps}
import sttp.tapir.EndpointIO.annotations.jsonbody
import sttp.tapir.Schema
import sttp.tapir.json.tethysjson
import sttp.tapir.server.http4s.Http4sServerInterpreter
import tethys.{JsonReader, JsonWriter}
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import sttp.tapir.{Schema, endpoint, plainBody}
import sttp.tapir._
import sttp.tapir.json.tethysjson._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ServerEndpoint.Full
import tethys.{JsonReader, JsonWriter}
import tethys.derivation.semiauto.{jsonReader, jsonWriter}

class CrawlController[F[_] : Async](crawlerService: CrawlerService[F]) extends Controller[F] {

  import sttp.tapir._

  private val crawlEndpoint: PublicEndpoint[CrawlRequest, Nothing, List[CrawlResponseItem], Any] =
    infallibleEndpoint
      .post
      .in("crawl")
      .in(EndpointInput.derived[CrawlRequest])
      .out(tethysjson.jsonBody[List[CrawlResponseItem]])

  override def endpoints: List[ServerEndpoint[Any, F]] = {
    def serverLogic(request: CrawlRequest): F[List[CrawlResponseItem]] = {
      for {
        crawlResult <- crawlerService.crawl(request.items.map(Item))
        response = crawlResult.map {
          case (_, SuccessCrawlResult(success)) => CrawlResponseItem(success.some, None)
          case (_, ErrorCrawlResult(error)) => CrawlResponseItem(None, error.getMessage.some)
        }
      } yield response
    }

    val crawlEndpointFull: Full[Unit, Unit, CrawlRequest, Nothing, List[CrawlResponseItem], Any, F] = crawlEndpoint.serverLogic(serverLogic(_).map(_.asRight[Nothing]))

    List(
      crawlEndpointFull,
    )
  }
}

object CrawlController {
  case class CrawlRequest(@jsonbody
                          items: List[String])

  //TODO: either in response
  case class CrawlResponseItem(result: Option[String],
                               error: Option[String])

  implicit final val crawlResponseItemWriter: JsonWriter[CrawlResponseItem] = jsonWriter
  implicit final val crawlResultReader: JsonReader[CrawlResponseItem] = jsonReader
  implicit final val crawlResponseItemSchema: Schema[CrawlResponseItem] = Schema.derived
}
