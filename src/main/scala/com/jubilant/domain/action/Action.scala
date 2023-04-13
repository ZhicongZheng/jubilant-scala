package com.jubilant.domain.action

import com.jubilant.domain.BaseEntity

import java.time.LocalDateTime

final case class Action(
  id: Long,
  typ: Int,
  resourceId: Long,
  resourceInfo: String,
  remoteIp: String,
  remoteAddress: String,
  createAt: LocalDateTime = LocalDateTime.now()
) extends BaseEntity

object Action {

  object Type {
    val VIEW_ARTICLE = 1
    val LICK_ARTICLE = 2
    val COMMENT      = 3
    val VISIT        = 4
  }

}
