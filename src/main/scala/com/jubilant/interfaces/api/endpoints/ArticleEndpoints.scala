package com.jubilant.interfaces.api.endpoints

import com.jubilant.application.command.{ArticleCategoryCommand, ArticleCommand, ArticleTagCommand}
import com.jubilant.common.Page
import com.jubilant.interfaces.dto.{ArticleCategoryDto, ArticleDto, ArticleTagDto}
import sttp.model.StatusCode
import sttp.tapir._
import io.circe.generic.auto._
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody

object ArticleEndpoints {

  val tagWithoutAuthEndpoint = endpoint.in("tags").tag("Article Tags").errorOut(jsonBody[ErrorMessage])

  val tagAuthEndpoint = securedWithBearerEndpoint.in("tags").tag("Article Tags")

  val categoryWithoutAuthEndpoint = endpoint.in("categories").tag("Article Categories").errorOut(jsonBody[ErrorMessage])

  val categoryAuthEndpoint = securedWithBearerEndpoint.in("categories").tag("Article Categories")

  val articleWithoutAuthEndpoint = endpoint.in("articles").tag("Article").errorOut(jsonBody[ErrorMessage])

  val articleAuthEndpoint = securedWithBearerEndpoint.in("articles").tag("Article")

  def endpoints =
    Seq(
      getArticleEndpoint,
      listTagsEndpoint,
      addTagsEndpoing,
      deleteTagsEndpoint,
      listCategoryEndpoint,
      addCategoryEndpoing,
      deleteCategoryEndpoint,
      createArticleEndpoint,
      deleteArticleEndpoint,
      updateArticleEndpoint,
      listArticleByPageEndpoint,
      releaseArticleEndpoint,
      offlineArticleEndpoint,
      likeArticleEndpoint,
      updateCategoryEndpoint
    )

  val getArticleEndpoint = articleWithoutAuthEndpoint.get
    .name("getArticle")
    .summary("获取文章详情")
    .description("根据id获取文章详情")
    .in(path[Long]("id"))
    .out(jsonBody[ArticleDto])

  val createArticleEndpoint = articleAuthEndpoint.post
    .name("createArticle")
    .summary("创建文章")
    .description("创建文章")
    .in(jsonBody[ArticleCommand])
    .out(statusCode(StatusCode.Created))
    .out(jsonBody[Long])

  val updateArticleEndpoint = articleAuthEndpoint.put
    .name("updateArticle")
    .summary("更新文章")
    .description("更新文章")
    .in(jsonBody[ArticleCommand])
    .out(statusCode(StatusCode.Ok))

  val deleteArticleEndpoint = articleAuthEndpoint.delete
    .name("deleteArticle")
    .summary("删除文章")
    .description("删除文章")
    .in(path[Long]("id"))
    .out(statusCode(StatusCode.Ok))

  val releaseArticleEndpoint = articleAuthEndpoint.post
    .name("releaseArticle")
    .summary("发布文章")
    .description("发布文章")
    .in("release")
    .in(path[Long]("id"))
    .out(statusCode(StatusCode.Ok))

  val offlineArticleEndpoint = articleAuthEndpoint.post
    .name("offlineArticle")
    .summary("下架文章")
    .description("下架文章")
    .in("offline")
    .in(path[Long]("id"))
    .out(statusCode(StatusCode.Ok))

  val likeArticleEndpoint = articleAuthEndpoint.post
    .name("likeArticle")
    .summary("点赞文章")
    .description("点赞文章")
    .in("like")
    .in(path[Long]("id") / query[Boolean]("like").default(true))
    .out(statusCode(StatusCode.Ok))

  val listArticleByPageEndpoint = articleWithoutAuthEndpoint.get
    .name("listArticleByPage")
    .summary("分页获取文章")
    .description("分页获取文章列表")
    .in(
      query[Int]("page").default(1) / query[Int]("size").default(10)
        / query[Option[Long]]("tag").default(None) / query[Option[Long]]("category").default(None)
        / query[Option[String]]("searchTitle").default(None)
    )
    .out(jsonBody[Page[ArticleDto]])

  val listTagsEndpoint = tagWithoutAuthEndpoint.get
    .name("listTags")
    .summary("获取文章标签列表")
    .description("获取文章标签列表")
    .out(jsonBody[Array[ArticleTagDto]])
    .out(statusCode(StatusCode.Ok))

  val addTagsEndpoing = tagAuthEndpoint.post
    .name("addTags")
    .summary("增加文章标签")
    .description("增加文章标签")
    .in(jsonBody[ArticleTagCommand])
    .out(statusCode(StatusCode.Created))

  val deleteTagsEndpoint = tagAuthEndpoint.delete
    .name("deleteTags")
    .summary("删除文章标签")
    .description("删除文章标签")
    .in(path[Long]("id"))
    .out(statusCode(StatusCode.Ok))

  val listCategoryEndpoint = categoryWithoutAuthEndpoint.get
    .name("listCategories")
    .summary("获取文章分类列表")
    .description("获取分类列表")
    .out(jsonBody[Array[ArticleCategoryDto]])
    .out(statusCode(StatusCode.Ok))

  val addCategoryEndpoing = categoryAuthEndpoint.post
    .name("addCategory")
    .summary("增加文章分类")
    .description("增加文章分类")
    .in(jsonBody[ArticleCategoryCommand])
    .out(statusCode(StatusCode.Created))

  val deleteCategoryEndpoint = categoryAuthEndpoint.delete
    .name("deleteCategory")
    .summary("删除文章分类")
    .description("删除文章分类")
    .in(path[Long]("id"))
    .out(statusCode(StatusCode.Ok))

  val updateCategoryEndpoint = categoryAuthEndpoint.put
    .name("updateCategory")
    .summary("更新分类")
    .description("更新文章分类")
    .in(jsonBody[ArticleCategoryCommand])
    .out(statusCode(StatusCode.Ok))

}
