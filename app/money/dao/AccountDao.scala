package money.dao

import java.sql.ResultSet
import java.util.UUID

import javax.inject.{Inject, Singleton}
import money.dao.AccountDao._
import money.domain.Model.{Account, AccountId, TransferId}

@Singleton
class AccountDao @Inject()(txManager: TxManager) {

  def select(id: AccountId): Account = txManager.inTransaction { conn =>
    val ps = conn.prepareStatement(SelectSingleSql)
    ps.setString(1, id.toString)
    val rs = ps.executeQuery()
    RowMapper.single(rs)
  }

  def save(account: Account): Unit = txManager.inTransaction { conn =>
    val ps = conn.prepareStatement(InsertAccountSql)
    ps.setString(1, account.id.toString)
    ps.setString(2, account.name)
    ps.executeUpdate()
  }

  def updateLastTransfer(newLastTransfer: TransferId, account: Account): Int = txManager.inTransaction { conn =>
    account.lastTransfer match {
      case Some(lastTransfer) =>
        val ps = conn.prepareStatement(UpdateAccountLastTransferSql)
        ps.setString(1, newLastTransfer.toString)
        ps.setString(2, account.id.toString)
        ps.setString(3, lastTransfer.toString)
        ps.executeUpdate()
      case None =>
        val ps = conn.prepareStatement(UpdateAccountLastTransferOldIsNullSql)
        ps.setString(1, newLastTransfer.toString)
        ps.setString(2, account.id.toString)
        ps.executeUpdate()

    }

  }

}

object AccountDao {

  val SelectSingleSql: String = "SELECT id, name, last_transfer FROM account WHERE id = ?"

  val InsertAccountSql: String = "INSERT INTO Account (id, name) VALUES (?, ?)"

  val UpdateAccountLastTransferSql: String = "UPDATE Account SET last_transfer = ? WHERE id = ? AND last_transfer = ?"
  val UpdateAccountLastTransferOldIsNullSql: String = "UPDATE Account SET last_transfer = ? WHERE id = ? AND last_transfer IS NULL"

  implicit def accountRowMapper: RowMapper[Account] = (rs: ResultSet) => {
    Account(
      UUID.fromString(rs.getString("id")),
      rs.getString("name"),
      Option(rs.getString("last_transfer")).map(UUID.fromString),
    )
  }

}
