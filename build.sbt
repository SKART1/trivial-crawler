import Dependencies.versions

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val logback_core = "ch.qos.logback" % "logback-core" % versions.logbackVersion
val logback = "ch.qos.logback" % "logback-classic" % versions.logbackVersion

val typesafe_config = "com.typesafe" % "config" % versions.typesafeConfigVersion
val pureconfig_core = "com.github.pureconfig" %% "pureconfig-core" % versions.pureConfigVersion
val pureconfig_generic = "com.github.pureconfig" %% "pureconfig-generic" % versions.pureConfigVersion

val catsCore    = "org.typelevel" %% "cats-core"    % versions.cats
val catsEffect  = "org.typelevel" %% "cats-effect"  % versions.catsEffect

val tapir = "com.softwaremill.sttp.tapir" %% "tapir-core" % versions.tapirAllVersion
val tapir_https_server = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % versions.tapirAllVersion
val tapir_open_api_docs = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % versions.tapirAllVersion
val tapir_open_api_docs_format ="com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % versions.tapirAllVersion
val tapir_tethys = "com.softwaremill.sttp.tapir" %% "tapir-json-tethys" % versions.tapirAllVersion

val tethys_core = "com.tethys-json" %% "tethys-core" % versions.tethysAllVersion
val tethys_derivation = "com.tethys-json" %% "tethys-derivation" % versions.tethysAllVersion

val http4s_core = "org.http4s" %% "http4s-core" % versions.http4s
val http4s_blaze = "org.http4s" %% "http4s-blaze-core" % versions.http4s
val http4s_blaze_server = "org.http4s" %% "http4s-blaze-server" % versions.http4s
val http4s_dsl = "org.http4s" %% "http4s-dsl" % versions.http4s

val http4s_blaze_client = "org.http4s" %% "http4s-blaze-client" % versions.http4s
val http4s_scala_xml = "org.http4s" %% "http4s-scala-xml" % versions.http4s

val jsoup = "org.jsoup" % "jsoup" % versions.jsoup

lazy val root = (project in file("."))
  .settings(
    name := "crawler",
    idePackagePrefix := Some("com.github.skart1"),
    projectDependencies ++= Seq(
      logback_core,
      logback,
      typesafe_config,
      pureconfig_core,
      pureconfig_generic,
      catsCore,
      catsEffect,
      tapir,
      tapir_https_server,
      tapir_open_api_docs,
      tapir_open_api_docs_format,
      tapir_tethys,
      tethys_core,
      tethys_derivation,
      http4s_core,
      http4s_blaze_server,
      http4s_dsl,
      http4s_blaze_client,
      jsoup,
    )
  )
