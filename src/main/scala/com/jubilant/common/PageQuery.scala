package com.jubilant.common

trait PageQuery {

  val page: Int

  val size: Int

  def offset: Int = (page - 1) * size

  def limit: Int = size

}
