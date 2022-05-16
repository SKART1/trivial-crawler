package com.github.skart1
package services

import services.domain.{CrawlResult, Item, SuccessCrawlResult, UrlParsingException}

import cats.Parallel
import cats.data.EitherT
import cats.effect.Async
import cats.effect.kernel.Resource
import cats.implicits._
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import org.jsoup.Jsoup

class CrawlerServiceImpl[F[_]: Async: Parallel]() extends CrawlerService[F] {
  private val httpClientResource: Resource[F, Client[F]] = client()

  private def client(): Resource[F, Client[F]] = {
    BlazeClientBuilder[F].resource //TODO: use other thread pool
  }

  private val titleDecoder: EntityDecoder[F, CrawlResult] = EntityDecoder.decodeBy(MediaType.text.html) { (m: Media[F]) =>
    EitherT {
      m.as[String].map { html =>
        SuccessCrawlResult(Jsoup.parse(html).getElementsByTag("title").text()).asRight
      } //TODO: streaming? laziness?
    }
  }

  override def crawl(addresses: List[Item]): F[List[(Item, CrawlResult)]] = {
    httpClientResource.use { client =>
      addresses.parTraverse(processAddress(client))
    }
  }

  private def processAddress(client: Client[F])(address: Item): F[(Item, CrawlResult)] = {

    def responseProcessor(f: Response[F]): F[(Item, CrawlResult)] = {
      implicit val decoder: EntityDecoder[F, CrawlResult] = titleDecoder

      f.as[CrawlResult].map((address, _))
    }

    for {
      uri <- Uri.fromString(address.url).left.map(error => UrlParsingException(error.sanitized)).liftTo[F]
      res <- client.get(uri)(responseProcessor) //TODO: redirects stuff
    } yield res
  }
}
