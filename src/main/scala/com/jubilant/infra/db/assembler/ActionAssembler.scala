package com.jubilant.infra.db.assembler

import com.jubilant.domain.action.Action
import com.jubilant.infra.db.po.ActionPo

object ActionAssembler {

  implicit def fromDo(a: Action): ActionPo =
    ActionPo(a.id, a.typ, a.resourceId, a.resourceInfo, a.remoteIp, a.remoteAddress, a.createAt)

  implicit def toDo(po: ActionPo): Action =
    Action(po.id, po.typ, po.resourceId, po.resourceInfo, po.remoteIp, po.remoteAddress, po.createAt)

}
