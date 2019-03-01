package money.dao

import java.sql.Connection

import javax.inject.{Inject, Singleton}
import play.api.db.Database

/**
  * Holds and releases connections for each Thread. If Exception occurs transaction gets rollbacked
  */
@Singleton
class TxManager @Inject()(db: Database) {

  type StackedConnections = StackedHolder[Connection]

  val threadConnections: ThreadLocal[StackedConnections] = ThreadLocal.withInitial[StackedConnections] {
    () =>
      new StackedHolder[Connection](() => {
        val conn = db.getConnection(false)
        conn
      })
  }

  def inTransaction[T](block: Connection => T): T = {
    val holder = threadConnections.get()

    val conn = holder.take()

    try {
      val result = block(conn)
      holder.release()

      if (!holder.isTaken()) {
        conn.commit()
        conn.close()
      }

      result
    } catch {
      case t: Throwable =>
        if (!conn.isClosed) {
          conn.rollback()
          conn.close()
        }
        holder.releaseAll()
        throw t
    }
  }

}


