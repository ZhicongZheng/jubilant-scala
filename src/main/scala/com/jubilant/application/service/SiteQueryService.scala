package com.jubilant.application.service

import com.jubilant.infra.inject.Module.articleQueryRepository
import com.jubilant.interfaces.dto.SiteInfoDto

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SiteQueryService {

  def getSiteInfo: Future[SiteInfoDto] =
    for {
      articleCount  <- articleQueryRepository.count()
      tagCount      <- articleQueryRepository.tagCount()
      categoryCount <- articleQueryRepository.categoryCount()
    } yield SiteInfoDto(articleCount, tagCount, categoryCount, 10000, SiteInfoDto.siteConfig)
}
