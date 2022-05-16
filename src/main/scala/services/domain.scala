package com.github.skart1
package services

object domain {
  case class Item(url: String)

  sealed trait CrawlResult

  case class SuccessCrawlResult(name: String) extends CrawlResult
  case class ErrorCrawlResult(error: Exception) extends CrawlResult

  sealed trait Errors

  case class UrlParsingException(msg: String) extends Exception(msg) with Errors
  case class MultipleTitlesException(msg: String) extends Exception(msg) with Errors
}
