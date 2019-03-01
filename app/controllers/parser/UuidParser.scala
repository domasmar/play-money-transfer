package controllers.parser

import java.util.UUID

import play.api.mvc.{PathBindable, QueryStringBindable}

trait UuidParser {

  implicit def uuidQueryStringBindable: QueryStringBindable[UUID] = new QueryStringBindable.Parsing[UUID](
    value => UUID.fromString(value),
    uuid => uuid.toString,
    (key: String, e: Exception) => s"Cannot parse $key as ${classOf[UUID]}: ${e.getMessage}"
  )

  implicit def uuidPathStringBindable: PathBindable[UUID] = new PathBindable.Parsing[UUID](
    value => UUID.fromString(value),
    uuid => uuid.toString,
    (key: String, e: Exception) => s"Cannot parse $key as ${classOf[UUID]}: ${e.getMessage}"
  )

}

object UuidParser extends UuidParser {

}
