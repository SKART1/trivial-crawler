package com.github.skart1
package controllers

import org.http4s.HttpRoutes
import sttp.tapir.server.ServerEndpoint

trait Controller[F[_]] {

  def endpoints: List[ServerEndpoint[Any, F]]

}

