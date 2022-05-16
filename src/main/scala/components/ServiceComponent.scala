package com.github.skart1
package components

import services.{CrawlerService, CrawlerServiceImpl}

import cats.Parallel
import cats.effect.Async

case class ServiceComponent[F[_]](crawlerService: CrawlerService[F])

object ServiceComponent {
  def apply[F[_]: Async: Parallel](): ServiceComponent[F] = {
    val crawlService = new CrawlerServiceImpl[F]()

    new ServiceComponent[F](crawlService)
  }
}
