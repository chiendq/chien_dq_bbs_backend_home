package controllers

import io.jsonwebtoken.{Jwts, SignatureAlgorithm}
import models.{LoginPayLoad, Post}
import play.api._
import play.api.http.HttpEntity
import play.api.mvc._
import utils.SecurityConstants._

import java.io.File
import java.nio.file.Paths
import java.util.Date
import javax.inject._

@Singleton
class HomeController @Inject()(
                                val controllerComponents: ControllerComponents) extends BaseController {
  lazy val logger = Logger(getClass)


  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok
  }

  def sendImage(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    request.body.asMultipartFormData.get
      .file("picture")
      .map { picture =>
        // only get the last part of the filename
        // otherwise someone can send a path like ../../home/foo/bar.txt to write to other files on the system
        val filename = Paths.get(picture.filename).getFileName
        val fileSize = picture.fileSize
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


  def login(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val jsBody = request.body.asJson.get
    val loginPayLoad = jsBody.as[LoginPayLoad]
    val token = Jwts.builder()
      .setSubject(loginPayLoad.username)
      .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
      .signWith(SignatureAlgorithm.HS512, SECRET)
      .compact()

    Result
      .apply(
        header = ResponseHeader(OK, Map[String, String](HEADER_STRING -> (TOKEN_PREFIX + token))),
        body = HttpEntity.NoEntity)
  }


  def testAuthentication(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val token = request.headers(HEADER_STRING)
    Jwts.parser()
      .setSigningKey(SECRET)
      .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
      .getBody.getSubject match {
      case null => Unauthorized
      case _ => Ok
    }
  }

}
