package com.jubilant.infra.config

import com.jubilant.infra.db.slick_pg.PostgresProfile
import com.jubilant.infra.db.slick_pg.PostgresProfile.api._

object Config {

  val db: PostgresProfile.backend.Database = Database.forConfig("database")



}
