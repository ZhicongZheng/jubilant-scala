package com.jubilant.infra.db.po

import java.time.LocalDateTime

trait Po {

  def id: Long

  def createBy: Long

  def updateBy(): Long

  def createAt: LocalDateTime

  def updateAt(): LocalDateTime

}
