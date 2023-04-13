package com.jubilant.infra.db.po

import slick.lifted.Rep

import java.time.LocalDateTime

trait IdTable {

  def id: Rep[Long]

}

trait BaseTable extends IdTable {

  def createBy: Rep[Long]

  def updateBy: Rep[Long]

  def createAt: Rep[LocalDateTime]

  def updateAt: Rep[LocalDateTime]
}
