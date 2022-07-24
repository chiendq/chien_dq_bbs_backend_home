package controllers

import io.jsonwebtoken.{Jwts, SignatureAlgorithm}
import jwt.{JWTConfig, JWTSecretKey}
import models.{LoginPayLoad, Post, User, UserEmail, UserId}
import pdi.jwt._

import javax.inject._
import play.api._
import play.api.mvc._
import services.JWTService

import java.io.File
import java.nio.file.Paths
import java.util.Date

@Singleton
class HomeController @Inject()(
                                val controllerComponents: ControllerComponents) extends BaseController {
  lazy val logger = Logger(getClass)
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok
  }

  def sendImage(): Action[AnyContent] = Action{ implicit request: Request[AnyContent] =>
    request.body.asMultipartFormData.get
      .file("picture")
      .map { picture =>
        // only get the last part of the filename
        // otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
        val filename    = Paths.get(picture.filename).getFileName
        val fileSize    = picture.fileSize
        val contentType = picture.contentType
        val pathMoved = picture.ref.copyTo(new File(s"public/images/$filename"), replace = false)
        val fileDemo = new File("public/images/favicon.png")
        Ok("File uploaded")
      }
      .getOrElse {
        BadRequest("Can not upload file")
      }
  }

  def getAllPost(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    logger.info(Post.findAll().toString())
    Ok
  }

  def testCreateToken(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val jwtService = new JWTService(new JWTConfig {
      override def secretKey: JWTSecretKey = new JWTSecretKey("bimatkey")
    })
    val token = jwtService.createToken(User(UserId("123456789012"), UserEmail("chiendao@gmail.com")))

    logger.info(token.toString)
    Ok(jwtService.decodeToken(token).toString)
  }

  object SecurityConstants {
    val SECRET = "SecretToSignJWT"
    val EXPIRATION_TIME = 1800000 // 30 minutes
    val TOKEN_PREFIX = "Bearer "
    val HEADER_STRING = "Authorization"
  }

  import SecurityConstants._
  def login(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val token = Jwts.builder()
      .setSubject("this is user id")
      .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
      .signWith(SignatureAlgorithm.HS512, SECRET )
      .compact()
//    Ok.withSession((HEADER_STRING, TOKEN_PREFIX + token))
    Ok.withHeaders((HEADER_STRING, TOKEN_PREFIX + token))
  }


  def testAuthentication(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val token = request.headers(HEADER_STRING)
    logger.info(s"token $token")
    val claims = Jwts.parser()
      .setSigningKey(SECRET)
      .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
      .getBody

    val user = claims.getSubject
    if (user == null) {
      Unauthorized
    } else Ok
  }

}
