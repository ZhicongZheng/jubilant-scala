package com.jubilant.infra.oss

import com.aliyun.oss.{OSS, OSSClientBuilder}
import com.jubilant.infra.config.Config.ossConfig
import org.slf4j.{Logger, LoggerFactory}

import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait OssRepository {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def upload(file: File, fileName: String): Future[String]

  def extension(fileName: String): String = {
    val index = fileName.indexOf('.')
    if (index > 0) fileName.substring(index + 1) else fileName
  }

}

class AliyunOssRepository extends OssRepository {

  private lazy val ossClient: OSS = new OSSClientBuilder().build(ossConfig.endpoint, ossConfig.accessKeyId, ossConfig.accessKeySecret)

  override def upload(file: File, fileName: String): Future[String] = {
    val path = s"${extension(fileName)}/${System.currentTimeMillis()}-$fileName"
    val future = Future {
      Try(ossClient.putObject(ossConfig.bucketName, path, file)).map(_ => s"https://${ossConfig.bucketName}.${ossConfig.endpoint}/$path")
    } flatMap {
      case Success(url) => Future.successful(url)
      case Failure(ex) =>
        logger.error("upload error ", ex)
        Future.failed(ex)
    }
    future.onComplete(_ => if (file.exists()) file.delete())
    future
  }
}
