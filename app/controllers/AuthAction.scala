package controllers


import play.api.http.HeaderNames
import play.api.mvc._
import services.AuthService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class UserRequest[A](request: Request[A]) extends WrappedRequest[A](request)

class AuthAction @Inject()(bodyParser: BodyParsers.Default, authService: AuthService)(implicit ec: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser

  override protected def executionContext: ExecutionContext = ec

  // A regex for parsing the Authorization header value
  private val headerTokenRegex = """Bearer (.+?)""".r

  // Called when a request is invoked. We should validate the bearer token here
  // and allow the request to proceed if it is valid.
  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {
    val bearerToken = extractBearerToken(request)
    bearerToken match {
      case Some(token) =>
        authService.validateJwt(token) match {
          case Success(claim) => block(UserRequest(request)) // token was valid - proceed!
          case Failure(t) => Future.successful(Results.Unauthorized(t.getMessage)) // token was invalid - return 401
        }
      case _ => Future.successful(Results.Unauthorized)
    }

    //    map { token =>
    //      authService.validateJwt(token) match {
    //        case Success(claim) => Future.successful(Results.Accepted)      // token was valid - proceed!
    //        case Failure(t) => Future.successful(Results.Unauthorized(t.getMessage))  // token was invalid - return 401
    //      }
    //    } getOrElse Future.successful(Results.Unauthorized)
  }

  // Helper for extracting the token value
  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }

}