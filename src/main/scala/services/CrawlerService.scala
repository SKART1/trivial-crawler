package com.github.skart1
package services

import services.domain.{CrawlResult, Item}

trait CrawlerService[F[_]] {
  def crawl(addresses: List[domain.Item]): F[List[(Item, CrawlResult)]]
}
