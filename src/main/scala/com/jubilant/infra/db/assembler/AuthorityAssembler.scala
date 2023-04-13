package com.jubilant.infra.db.assembler

import com.jubilant.domain.auth.{Permission, Role}
import com.jubilant.infra.db.po.{PermissionPo, RolePo}

object AuthorityAssembler {

  implicit def toDo(p: RolePo): Role = Role(p.id, p.code, p.name, Nil, p.createBy, p.updateBy, p.createAt, p.updateAt)

  implicit def fromDo(r: Role): RolePo = RolePo(r.id, r.code, r.name)

  implicit def toDo(p: PermissionPo): Permission =
    Permission(p.id, p.`type`, p.value, p.name, p.createBy, p.updateBy, p.createAt, p.updateAt)

  implicit def toRoleDoSeq(seq: Seq[RolePo]): Seq[Role] = seq.map(toDo)

  implicit def toRoleDoOpt(opt: Option[RolePo]): Option[Role] = opt.map(toDo)

  implicit def toPermissionDoSeq(seq: Seq[PermissionPo]): Seq[Permission] = seq.map(toDo)

  implicit def toPermissionDoOpt(opt: Option[PermissionPo]): Option[Permission] = opt.map(toDo)

}
