package com.jubilant.interfaces.routes

import cats.effect.IO
import cats.implicits.toTraverseOps
import com.jubilant.infra.inject.Module.{ec, ossRepository}
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.multipart.Multipart

import java.io.{File, FileOutputStream}
import scala.concurrent.Future

object FileRoutes {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  def authRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] { case req @ POST -> Root / "files" / "upload" =>
    req.decode[Multipart[IO]] { multipart =>
      multipart.parts.map { part =>
        part.body.compile.toVector.flatMap { bytes =>
          IO.fromFuture(IO {
            Future {
              val filename = part.filename.getOrElse("")
              val file     = new File(s"/tmp/$filename")
              val fos      = new FileOutputStream(file)
              fos.write(bytes.toArray)
              fos.close()
              file
            }.flatMap(file => ossRepository.upload(file, file.getName))
          })
        }
      }.sequence.flatMap(paths => Ok(paths.asJson))
    }
  }
}
