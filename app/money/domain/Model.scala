package money.domain

import java.util.UUID

object Model {

  type AccountId = UUID
  type TransferId = UUID

  case class Account(id: AccountId, name: String, lastTransfer: Option[TransferId])

  case class Transfer(id: TransferId, fromAccount: Option[AccountId], toAccount: AccountId, amount: Int)

  object Transfer {
    def apply(from: Option[AccountId], to: AccountId, amount: Int): Transfer = {
      Transfer(UUID.randomUUID(), from, to, amount)
    }

    def totalBalance(user: AccountId, transfers: List[Transfer]): Int = {
      transfers.foldRight(0) { case (transfer, total) =>
        if (transfer.fromAccount.contains(user)) total - transfer.amount
        else if (transfer.toAccount == user) total + transfer.amount
        else total
      }
    }
  }

  object Account {
    def apply(name: String): Account = {
      Account(UUID.randomUUID(), name, None)
    }
  }

}
