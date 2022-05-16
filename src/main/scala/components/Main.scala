package com.github.skart1
package components

import cats.effect.{ExitCode, IO, IOApp, Resource}
import org.http4s.server.Server

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val configComponent = ConfigComponent()
    val serviceComponent = ServiceComponent[IO]()

    val resource: Resource[IO, Server] = HttpModulesComponent(configComponent, serviceComponent).server
    resource.use(_ => IO.never)
  }
}
