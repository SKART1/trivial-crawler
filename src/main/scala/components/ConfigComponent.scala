package com.github.skart1
package components

import components.ConfigComponent.ServerConfig

import com.typesafe.config.{Config, ConfigFactory}
import pureconfig.generic.semiauto.deriveReader
import pureconfig.{ConfigReader, ConfigSource}


case class ConfigComponent(serverConfig: ServerConfig)

object ConfigComponent {
  implicit val serverConfigReader: ConfigReader[ServerConfig] = deriveReader[ServerConfig]

  case class ServerConfig(host: String, port: Int)

  private def registerStaticUnsafe[A: ConfigReader](config: Config)(path: String): A = {
    val result = ConfigSource.fromConfig(config.getConfig(path)).load[A]

    result.fold(
      x => throw new RuntimeException(x.prettyPrint()),
      cfg => cfg
    )
  }

  def apply(): ConfigComponent = {
    val config = ConfigFactory.load().resolve()

    val serverConfig = registerStaticUnsafe[ServerConfig](config)("server")

    ConfigComponent(serverConfig)
  }
}
