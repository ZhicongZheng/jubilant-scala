package com.jubilant.common

import cats.effect.IO
import org.http4s.Request
import org.lionsoul.ip2region.xdb.Searcher
import org.slf4j.LoggerFactory

object Ip2Region {

  private val logger = LoggerFactory.getLogger(getClass)

  private[this] val searcher: Searcher = Searcher.newWithBuffer(
    getClass.getResourceAsStream("/ipdb/ip2region.xdb").readAllBytes()
  )

  def search(ip: String): String = searcher.search(ip)

  def parseIp(request: Request[IO]): String = {
    val remoteIp = request.remoteAddr.map(addr => addr.toString)

    if (remoteIp.contains("0:0:0:0:0:0:0:1")) {
      return "127.0.0.1"
    }
    val realIp: Option[String]       = request.headers.headers.find(_.name.toString == "X-Real-IP").map(_.value).filter(checkIP)
    val forwardedFor: Option[String] = request.headers.headers.find(_.name.toString == "X-Forwarded-For").map(_.value).filter(checkIP)
    logger.debug(s"realIp : $realIp")
    logger.debug(s"forwardedFor: $forwardedFor")
    logger.debug(s"remoteAddress: $remoteIp")
    realIp.orElse(forwardedFor).getOrElse(remoteIp.get)
  }

  def parseAddress(request: Request[IO]): String = {
    val ip = parseIp(request)
    search(ip)
  }

  private def checkIP(ip: String): Boolean =
    ip.nonEmpty && "unkown".equalsIgnoreCase(ip) && ip.split(".").length == 4

}
