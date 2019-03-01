package unit.money.dao

import java.sql.Connection

import money.dao.TxManager
import org.mockito.Mockito.{when, _}
import org.mockito.invocation.InvocationOnMock
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import play.api.db.Database

import scala.util.{Failure, Try}

class TxManagerTest extends FlatSpec with Matchers with MockitoSugar {

  trait TxManagerContext {
    val db = mock[Database]
    val manager = new TxManager(db)
    when(db.getConnection(false)).thenAnswer { _: InvocationOnMock =>
      val connection = mock[Connection]

      when(connection.isClosed).thenReturn(false)
      when(connection.close()).thenAnswer { _: InvocationOnMock =>
        when(connection.isClosed).thenReturn(true)
      }

      lastConnection = connection
      lastConnection
    }
    var lastConnection: Connection = _
  }

  behavior of "TxManager"

  it should "open connection when take is invoked" in new TxManagerContext {
    manager.inTransaction { c => c }

    verify(db).getConnection(false)
  }

  it should "create 2 connections if isTransaction is separate" in new TxManagerContext {
    val firstConn = manager.inTransaction { c => c }
    val secondConn = manager.inTransaction { c => c }

    firstConn should not be secondConn
  }

  it should "create single connection if inTransaction is nested" in new TxManagerContext {
    val (firstConn, secondConn) = manager.inTransaction { c1 =>
      manager.inTransaction { c2 =>
        (c1, c2)
      }
    }

    firstConn shouldBe secondConn
  }

  it should "rollback and close connection when exception occurs in inTransaction block" in new TxManagerContext {
    Try {
      manager.inTransaction { _ =>
        throw new Exception()
      }
    }

    verify(lastConnection, times(1)).rollback()
    verify(lastConnection, times(0)).commit()
    verify(lastConnection, times(1)).close()
  }

  it should "commit and close connection when no exception occurs" in new TxManagerContext {
    manager.inTransaction { c => c }

    verify(lastConnection, times(0)).rollback()
    verify(lastConnection, times(1)).commit()
    verify(lastConnection, times(1)).close()
  }

  it should "not commit and close connection if there was not exception inside nested inTransaction block" in new TxManagerContext {
    manager.inTransaction { _ =>
      manager.inTransaction { _ => }
      verify(lastConnection, times(0)).commit()
      verify(lastConnection, times(0)).close()
    }
  }

  it should "close connection if exception occurs inside nested transaction block" in new TxManagerContext {
    Try {
      manager.inTransaction { _ =>
        manager.inTransaction { _ =>
          throw new Exception()
        }
      }
    }

    verify(lastConnection, times(1)).rollback()
    verify(lastConnection, times(0)).commit()
    verify(lastConnection, times(1)).close()
  }

  it should "rethrow exception if any occurs" in new TxManagerContext {
    var ex = new Exception

    val result = Try {
      manager.inTransaction { _ =>
        throw ex
      }
    }

    result shouldBe Failure(ex)
  }

}
