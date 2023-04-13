package com.jubilant.interfaces.api.endpoints

import com.jubilant.application.command.{ChangePasswordCommand, CreateUserCommand, LoginCommand, UpdateUserCommand}
import com.jubilant.common.Page
import com.jubilant.interfaces.dto.UserDto
import sttp.model.{HeaderNames, StatusCode}
import sttp.tapir._
import sttp.tapir.generic.auto.schemaForCaseClass
import io.circe.generic.auto._
import sttp.tapir.json.circe.jsonBody

object UserEndpoints {

  private val baseUserEndpoint = endpoint.in("users").tag("Users")

  private val baseSecuredUserEndpoint = securedWithBearerEndpoint.in("users").tag("Users")

  def endpoints =
    Seq(
      loginEndpoint,
      logoutEndpoint,
      currentUserEndpoint,
      listByPageEndpoint,
      deleteUserEndpoint,
      createUserEndpoint,
      changePwdEndpoint,
      loginCodeEndpoint,
      changeRoleEndpoint,
      UpdateUserEndpoint
    )

  val loginEndpoint = baseUserEndpoint.post
    .name("login")
    .summary("用户登陆")
    .description("输入用户名和密码，登陆管理后台")
    .in("login")
    .in(jsonBody[LoginCommand])
    .out(statusCode(StatusCode.Ok))
    .out(header[String](HeaderNames.Authorization))

  val logoutEndpoint = baseSecuredUserEndpoint.post
    .name("logout")
    .summary("退出登陆")
    .description("用户退出登陆")
    .in("logout")
    .out(statusCode(StatusCode.Ok))

  val currentUserEndpoint = baseSecuredUserEndpoint.get
    .name("currentUser")
    .summary("当前用户信息")
    .description("获取当前用户信息，包括基本信息/权限/角色等，但是不包括密码")
    .in("current")
    .out(jsonBody[UserDto])

  val listByPageEndpoint = baseSecuredUserEndpoint.get
    .name("listUserByPage")
    .summary("分页获取用户")
    .description("分页的方式获取用户列表，支持排序")
    .in(query[Int]("page").default(1) / query[Int]("size").default(10))
    .out(jsonBody[Page[UserDto]])

  val deleteUserEndpoint = baseSecuredUserEndpoint.delete
    .name("deleteUser")
    .summary("删除用户")
    .description("根据id 删除用户")
    .in(path[Int]("id"))
    .out(statusCode(StatusCode.Ok))

  val createUserEndpoint = baseSecuredUserEndpoint.post
    .name("createUser")
    .summary("创建用户")
    .description("创建用户")
    .in(jsonBody[CreateUserCommand])
    .out(statusCode(StatusCode.Created))
    .out(jsonBody[Long])

  val changePwdEndpoint = baseSecuredUserEndpoint.put
    .name("changeUserPwd")
    .summary("修改密码")
    .description("当前登陆用户修改密码")
    .in("password")
    .in(jsonBody[ChangePasswordCommand])
    .out(statusCode(StatusCode.Ok))
    .errorOut(jsonBody[ErrorMessage])

  val loginCodeEndpoint = baseUserEndpoint.get
    .name("loginCode")
    .summary("登陆验证码")
    .description("登陆时获取验证码")
    .in("login-code")
    .out(stringBody)
    .errorOut(jsonBody[ErrorMessage])

  val changeRoleEndpoint = baseSecuredUserEndpoint.post
    .name("changeUserRole")
    .summary("修改用户的角色")
    .description("给用户分配角色")
    .in(path[Long]("userId") / "roles" / path[Long]("roleId"))
    .out(statusCode(StatusCode.Ok))

  val UpdateUserEndpoint = baseSecuredUserEndpoint.put
    .name("updateUser")
    .summary("更新用户信息")
    .description("更新用户基本信息")
    .in(jsonBody[UpdateUserCommand])
    .out(statusCode(StatusCode.Ok))

}
