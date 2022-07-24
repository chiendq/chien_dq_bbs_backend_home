package models

import play.api.libs.json.{Json, OFormat}

case class LoginPayLoad(username: String, password: String)

object LoginPayLoad{
  implicit lazy val format: OFormat[LoginPayLoad] = Json.format[LoginPayLoad]
}
