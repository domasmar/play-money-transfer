package money.dao

import java.sql.ResultSet
import java.util.UUID

import javax.inject.{Inject, Singleton}
import money.dao.RowMapper.list
import money.dao.TransferDao._
import money.domain.Model.{AccountId, Transfer}

@Singleton
class TransferDao @Inject()(txManager: TxManager) {

  def selectAccount(accountId: AccountId): List[Transfer] = txManager.inTransaction { conn =>
    val ps = conn.prepareStatement(SelectAccountTransferSql)
    ps.setString(1, accountId.toString)
    ps.setString(2, accountId.toString)
    val rs = ps.executeQuery()
    list(rs)
  }

  def save(transfer: Transfer): Int = txManager.inTransaction { conn =>
    val ps = conn.prepareStatement(InsertTransferSql)
    ps.setString(1, transfer.id.toString)
    ps.setString(2, transfer.fromAccount.map(_.toString).orNull)
    ps.setString(3, transfer.toAccount.toString)
    ps.setInt(4, transfer.amount)
    ps.executeUpdate()
  }

}

object TransferDao {

  val SelectAccountTransferSql: String = "SELECT id, from_account, to_account, amount FROM transfer WHERE from_account = ? OR to_account = ? "

  val InsertTransferSql: String = "INSERT INTO Transfer(id, from_account, to_account, amount) VALUES (?, ?, ?, ?)"

  implicit def accountRowMapper: RowMapper[Transfer] = { rs: ResultSet =>
    Transfer(
      UUID.fromString(rs.getString("id")),
      Option(rs.getString("from_account")).map(UUID.fromString),
      UUID.fromString(rs.getString("to_account")),
      rs.getInt("amount")
    )
  }

}
