package com.jubilant.infra.config

import com.jubilant.infra.db.slick_pg.PostgresProfile
import com.jubilant.infra.db.slick_pg.PostgresProfile.api._
import com.typesafe.config.ConfigFactory

object Config {
  case class OssConfig(endpoint: String, accessKeyId: String, accessKeySecret: String, bucketName: String)

  private val config = ConfigFactory.load()

  val db: PostgresProfile.backend.Database = Database.forConfig("database")

  val ossConfig: OssConfig = {
    val ossConfig = config.getConfig("oss.aliyun")
    OssConfig(
      ossConfig.getString("endpoint"),
      ossConfig.getString("accessKeyId"),
      ossConfig.getString("accessKeySecret"),
      ossConfig.getString("bucketName")
    )
  }

}
