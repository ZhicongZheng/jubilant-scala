package com.jubilant.application.command

case class ChangePasswordCommand(oldPassword: String, newPassword: String)

object ChangePasswordCommand {}
