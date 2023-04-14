package com.jubilant.infra.inject

import com.jubilant.domain.action.ActionRepository
import com.jubilant.domain.article.ArticleRepository
import com.jubilant.domain.auth.RoleRepository
import com.jubilant.domain.comment.CommentRepository
import com.jubilant.domain.user.UserRepository
import com.jubilant.infra.config.Config
import com.jubilant.infra.db.repository.impl._
import com.jubilant.infra.db.repository._
import com.jubilant.infra.db.slick_pg.PostgresProfile
import com.jubilant.infra.oss.{AliyunOssRepository, OssRepository}
import com.softwaremill.macwire._

import scala.concurrent.ExecutionContext

object Module {

  lazy val db: PostgresProfile.backend.Database = Config.db
  implicit val ec: ExecutionContext             = scala.concurrent.ExecutionContext.Implicits.global

  lazy val actionRepository: ActionRepository = wire[ActionRepositoryImpl]

  lazy val actionQueryRepository: ActionQueryRepository = wire[ActionQueryRepositoryImpl]

  lazy val articleRepository: ArticleRepository = wire[ArticleRepositoryImpl]

  lazy val articleQueryRepository: ArticleQueryRepository = wire[ArticleQueryRepositoryImpl]

  lazy val commentRepository: CommentRepository = wire[CommentRepositoryImpl]

  lazy val commentQueryRepository: CommentQueryRepository = wire[CommentQueryRepositoryImpl]

  lazy val roleRepository: RoleRepository = wire[RoleRepositoryImpl]

  lazy val roleQueryRepository: RoleQueryRepository = wire[RoleQueryRepositoryImpl]

  lazy val userRepository: UserRepository = wire[UserRepositoryImpl]

  lazy val userQueryRepository: UserQueryRepository = wire[UserQueryRepositoryImpl]

  lazy val ossRepository: OssRepository = wire[AliyunOssRepository]

}
