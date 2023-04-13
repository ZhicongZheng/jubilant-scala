package com.jubilant.application.service

import com.jubilant.infra.db.repository.ArticleQueryRepository
import com.jubilant.interfaces.dto.SiteInfoDto

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SiteQueryService(articleQueryRepository: ArticleQueryRepository) {

  def getSiteInfo(): Future[SiteInfoDto] =
    for {
      articleCount  <- articleQueryRepository.count()
      tagCount      <- articleQueryRepository.tagCount()
      categoryCount <- articleQueryRepository.categoryCount()
    } yield SiteInfoDto(articleCount, tagCount, categoryCount, 10000, SiteInfoDto.siteConfig)
}
