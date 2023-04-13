package com.jubilant.infra.db.po

import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import java.time.LocalDateTime

final case class ActionPo(
  id: Long,
  typ: Int,
  resourceId: Long,
  resourceInfo: String,
  remoteIp: String,
  remoteAddress: String,
  createAt: LocalDateTime = LocalDateTime.now()
)

object ActionPo {

  /** Table description of table actions. Objects of this class serve as prototypes for rows in queries. */
  class ActionTable(_tableTag: Tag) extends Table[ActionPo](_tableTag, "actions") {
    def * = (id, typ, resourceId, resourceInfo, remoteIp, remoteAddress, createAt).<>((ActionPo.apply _).tupled, ActionPo.unapply)

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

    val typ: Rep[Int] = column[Int]("typ")

    /** Database column source_id SqlType(int8) */
    val resourceId: Rep[Long] = column[Long]("resource_id")

    val resourceInfo = column[String]("resource_info")

    /** Database column remote_address SqlType(inet), Length(2147483647,false) */
    val remoteAddress: Rep[String] = column[String]("remote_address")

    val remoteIp: Rep[String] = column[String]("remote_ip")

    /** Database column create_at SqlType(timestamp) */
    val createAt: Rep[LocalDateTime] = column[LocalDateTime]("create_at")

  }

}
