package core

import java.util.UUID

object RandomIdGenerator {

  def stringId: String = UUID.randomUUID().toString.replace("-", "")
}