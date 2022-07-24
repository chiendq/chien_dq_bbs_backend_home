package services

import javax.inject.Inject
import jwt.JWTConfig
import core.AuthorizationToken
import models.User
import pdi.jwt.Jwt.clock
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

class JWTService(config: JWTConfig) {

  def createToken(user: User): AuthorizationToken = {
    val json = s"""{ "id": "${user.id.string}", "email": "${user.email.string}" }"""
    val expiresInSeconds = 30L * 24 * 60 * 60 // 30 days
    val claim = JwtClaim(json).issuedNow(clock)
      .expiresIn(expiresInSeconds)(clock)

    val token = Jwt.encode(claim, config.secretKey.string, JwtAlgorithm.HS384)
    AuthorizationToken(token)
  }

  def  decodeToken(token: AuthorizationToken): JwtClaim = {
    Jwt
      .decode(token.string, config.secretKey.string, Seq(JwtAlgorithm.HS384))
      .map { decodedClaim =>
//        val json = Json.fromJson(decodedClaim)
//        val id = (json \ "id").as[String]
//        val email = (json \ "email").as[String]
//
//        println(s"id = $id")
//        println(s"email = $email")
        decodedClaim
      }
      .get
  }
}