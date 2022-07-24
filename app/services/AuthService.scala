package services

import io.jsonwebtoken.{Claims, Jwts}
import jwt.SecurityConstants._
import play.api.Configuration

import javax.inject.Inject
import scala.util.Try
class AuthService @Inject()(config: Configuration) {

  def validateJwt(token: String): Try[Claims] = {
    Try{
      Jwts.parser()
        .setSigningKey(SECRET)
        .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
        .getBody
    }
  }

}
