import sbt.*
import sbt.Keys.libraryDependencies

object Dependencies {

  object Versions {
    val scala         = "2.13.8"
    val config        = "1.4.2"
    val slick         = "3.4.1"
    val postgresql    = "42.5.4"
    val bcrypt        = "0.4"
    val tapir         = "1.2.12"
    val aliyunOss     = "3.16.1"
    val kaptcha       = "2.3.2"
    val slick_pg      = "0.21.1"
    val chimney       = "0.6.2"
    val ip2region     = "2.7.0"
    val sensitiveWord = "0.2.1"
    val circe         = "0.14.5"
    val logback       = "1.4.6"
    val http4s        = "0.23.18"
    val macwire       = "2.5.8"
  }

  object Compiles {

    val http4s: Seq[ModuleID] = Seq(
      "org.http4s" %% "http4s-ember-server" % Versions.http4s,
      "org.http4s" %% "http4s-circe"        % Versions.http4s,
      "org.http4s" %% "http4s-dsl"          % Versions.http4s
    )

    val circe = "io.circe" %% "circe-generic" % Versions.circe

    val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

    val config = "com.typesafe" % "config" % Versions.config

    val bcrypt = "org.mindrot" % "jbcrypt" % Versions.bcrypt

    val tapir: Seq[ModuleID] = Seq(
      "com.softwaremill.sttp.tapir"   %% "tapir-core"          % Versions.tapir,
      "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server" % Versions.tapir,
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"    % Versions.tapir,
      "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"  % Versions.tapir,
      "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui"    % Versions.tapir,
      "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"  % "0.3.2"
    )

    val slick_pg: Seq[ModuleID] = Seq(
      "com.github.tminglei" %% "slick-pg"            % Versions.slick_pg,
      "com.typesafe.slick"  %% "slick-hikaricp"      % Versions.slick,
      "com.github.tminglei" %% "slick-pg_circe-json" % Versions.slick_pg
    )

    val macwire: Seq[ModuleID] = Seq(
      "com.softwaremill.macwire" %% "macros" % Versions.macwire % Provided
    )

    val postgresql = "org.postgresql" % "postgresql" % Versions.postgresql

    val aliyunOss = "com.aliyun.oss" % "aliyun-sdk-oss" % Versions.aliyunOss

    // 敏感词过滤
    val sensitiveWord: ModuleID = "com.github.houbb" % "sensitive-word" % Versions.sensitiveWord

    // case class 转换
    val chimney = "io.scalaland" %% "chimney" % Versions.chimney

    val kaptcha = "com.github.penggle" % "kaptcha" % Versions.kaptcha

    val ip2region = "org.lionsoul" % "ip2region" % Versions.ip2region

  }

  import Compiles.*

  lazy val dependencies: Setting[Seq[ModuleID]] =
    libraryDependencies ++= Seq(
      config,
      logback,
      postgresql,
      bcrypt,
      kaptcha,
      aliyunOss,
      chimney,
      ip2region,
      sensitiveWord,
      circe
    ) ++ http4s ++ slick_pg ++ tapir ++ macwire

}
