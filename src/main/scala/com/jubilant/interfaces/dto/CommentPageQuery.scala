package com.jubilant.interfaces.dto

import com.jubilant.common.PageQuery

case class CommentPageQuery(page: Int, size: Int, resourceId: Option[Long] = None, typ: Option[Int] = None, parent: Option[Long] = None)
    extends PageQuery

object CommentPageQuery {}
