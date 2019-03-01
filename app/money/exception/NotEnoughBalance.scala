package money.exception

import money.domain.Model.AccountId

class NotEnoughBalance(from: AccountId, to: AccountId, amount: Int)
  extends IllegalStateException(s"Not enough balance for '$from' to send '$amount' to '$to'")


