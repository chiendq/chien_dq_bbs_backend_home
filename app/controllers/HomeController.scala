package controllers

import jwt.{JWTConfig, JWTSecretKey}
import models.{Post, User, UserEmail, UserId}

import javax.inject._
import play.api._
import play.api.mvc._
import services.JWTService

import java.io.File
import java.nio.file.Paths

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
}
