package com.jubilant.interfaces.dto

import com.jubilant.domain.comment.Comment
import com.jubilant.infra.db.po.{ArticlePo, CommentsPo}

case class CommentEmailInfo(article: ArticlePo, replyPo: Option[CommentsPo], current: Comment)
