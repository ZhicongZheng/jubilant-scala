package com.jubilant.interfaces.dto

import com.jubilant.interfaces.dto.SiteInfoDto.SiteConfig

case class SiteInfoDto(
  articleCount: Long,
  categoryCount: Long,
  tagCount: Long,
  viewCount: Long,
  siteConfig: SiteConfig
)

object SiteInfoDto {

  case class SiteConfig(
    // 网站名称
    siteName: String,
    // 网站地址
    siteAddress: String,
    // 网站简介
    siteIntro: String,
    // 网站公告
    siteNotice: String,
    // 建站日期
    createSiteTime: String,
    // 备案号
    recordNumber: String,
    // 网站作者
    siteAuthor: String,
    // 网站作者头像
    authorAvatar: String,
    // 关于我的描述
    aboutMe: String,
    github: String
  )

  val siteConfig: SiteConfig =
    SiteConfig(
      siteName = "快乐乐园",
      siteAddress = "https://www.anyfun.top",
      siteIntro = "ZhengZhiCong 的后花园",
      siteNotice = "网站发布上线啦",
      createSiteTime = "2023.02.22",
      recordNumber = "京ICP备2023002242号-1",
      siteAuthor = "ZhengZhiCong",
      authorAvatar = "https://oss-lingxi.oss-cn-beijing.aliyuncs.com/png/3526720326-image.png",
      aboutMe = "Scala Java 服务端工程师",
      github = "https://github.com/zhengzzv"
    )

}
