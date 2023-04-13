package com.jubilant.infra.db.assembler

import com.jubilant.domain.comment.Comment
import com.jubilant.infra.db.po.CommentsPo
import io.scalaland.chimney.dsl._

object CommentAssembler {

  implicit def toPo(c: Comment): CommentsPo = c.into[CommentsPo].transform

  implicit def toDo(po: CommentsPo): Comment = po.into[Comment].withFieldComputed(_.reply, _ => Seq.empty[Comment]).transform

}
