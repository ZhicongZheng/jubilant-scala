package com.jubilant.interfaces.api.endpoints

import sttp.model.{Part, StatusCode}
import sttp.tapir._

object FileEndpoints {
  private val baseEndpoint = securedWithBearerEndpoint.in("files").tag("Files")

  def endpoints = Seq(uploadEndpoint)
  final case class MultipartInput(file: Part[TapirFile]) extends Serializable

  val uploadEndpoint = baseEndpoint.post
    .name("fileUpload")
    .summary("上传文件")
    .description("上传文件接口")
    .in(multipartBody[MultipartInput])
    .in("upload")
    .out(statusCode(StatusCode.Ok))
    .out(stringBody)
}
