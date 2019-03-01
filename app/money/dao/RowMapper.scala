package money.dao

import java.sql.ResultSet

import scala.collection.mutable.ArrayBuffer

trait RowMapper[T] {
  def map(rs: ResultSet): T
}

object RowMapper {

  def singleOpt[T](rs: ResultSet)(implicit mapper: RowMapper[T]): Option[T] = {
    if (rs.next()) {
      Some(mapper.map(rs))
    } else {
      None
    }
  }

  def single[T](rs: ResultSet)(implicit mapper: RowMapper[T]): T = {
    singleOpt(rs).getOrElse(throw new IllegalStateException("ResultSet does not contain any results"))
  }

  def list[T](rs: ResultSet)(implicit mapper: RowMapper[T]): List[T] = {
    val buffer: ArrayBuffer[T] = ArrayBuffer()
    while (rs.next()) {
      buffer += mapper.map(rs)
    }
    buffer.toList
  }

}
