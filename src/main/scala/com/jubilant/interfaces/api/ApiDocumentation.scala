package com.jubilant.interfaces.api

import com.jubilant.interfaces.api.endpoints.{
  ArticleEndpoints,
  CommentEndpoints,
  FileEndpoints,
  RoleEndpoints,
  SiteEndpoints,
  UserEndpoints
}
import sttp.apispec.openapi.Info
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

object ApiDocumentation {

  private val openApiInfo = Info("Tapir By ZhengZhiCong", "1.0.0")

  private val endpoints =
    UserEndpoints.endpoints ++ RoleEndpoints.endpoints ++ FileEndpoints.endpoints ++ ArticleEndpoints.endpoints ++ SiteEndpoints.endpoints ++ CommentEndpoints.endpoints

  private val openApiDocs = OpenAPIDocsInterpreter().toOpenAPI(endpoints, openApiInfo)

  val openApiYaml: String = openApiDocs.toYaml

}
