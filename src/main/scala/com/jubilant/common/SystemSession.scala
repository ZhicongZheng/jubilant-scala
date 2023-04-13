package com.jubilant.common

import cats.effect.IO
import com.jubilant.common.Constant.COOKIE_NAME
import org.http4s.{Request, ResponseCookie}

import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.{ConcurrentHashMap, ScheduledThreadPoolExecutor, TimeUnit}
import scala.jdk.CollectionConverters.CollectionHasAsScala

object SystemSession {

  private val sessions = new ConcurrentHashMap[String, Session]

  private val scheduler = new ScheduledThreadPoolExecutor(1)

  // 10 分钟检查一次过期的session ,并且删除
  scheduler.schedule(
    new Runnable {
      override def run(): Unit = {
        val expiredCookie = sessions.keySet.asScala.filter { cookie =>
          sessions.get(cookie).expireTime.isAfter(LocalDateTime.now())
        }
        expiredCookie.foreach(cookie => sessions.remove(cookie))
      }
    },
    10L,
    TimeUnit.MINUTES
  )

  private case class Session(map: java.util.Map[String, String] = new ConcurrentHashMap[String, String], expireTime: LocalDateTime) {
    def put(k: String, v: String): String = map.put(k, v)

    def get(k: String): Option[String] = Option(map.get(k))
  }

  def putVal(cookie: String, k: String, v: String): Unit = {
    val session = sessions.computeIfAbsent(cookie, _ => Session(expireTime = LocalDateTime.now().plusDays(1L)))
    session.put(k, v)
  }

  def getVal(cookie: String, k: String): Option[String] = Option(sessions.get(cookie)) match {
    case Some(session) => session.get(k)
    case None          => None
  }

  def rmVal(cookie: String, k: String): Unit = Option(sessions.get(cookie)).map(_.map.remove(k))

  def rmCookie(cookie: String): Unit = sessions.remove(cookie)

  def parseCookie(request: Request[IO]): Option[String] = request.cookies.find(_.name == COOKIE_NAME).map(_.content)

  def generatorCookie: ResponseCookie =
    ResponseCookie(COOKIE_NAME, UUID.randomUUID().toString, domain = Some("localhost"), path = Some("/"), httpOnly = true)

}
