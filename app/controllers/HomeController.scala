package controllers

import models.Post

import javax.inject._
import play.api._
import play.api.mvc._

import java.io.File
import java.nio.file.Paths

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
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
        Redirect(routes.HomeController.index).flashing("error" -> "Missing file")
      }
  }

  def getAllPost(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    logger.info(Post.findAll().toString())
    Ok
  }

}
