package money.service

import javax.inject.{Inject, Singleton}
import money.dao.{TransferDao, TxManager}
import money.domain.DTO.{TransferCreateRequest, TransferView, TransfersView}
import money.domain.Model.{AccountId, Transfer}
import money.exception.{NotEnoughBalance, RaceConditionTransaction}

@Singleton
class TransferService @Inject()(transferDao: TransferDao, accountService: AccountService, dbManager: TxManager) {

  def buildBulkView(accountId: AccountId, transfers: List[Transfer]): TransfersView = {
    val balance = Transfer.totalBalance(accountId, transfers)
    TransfersView(accountId, balance, transfers.map(buildView))
  }

  def buildView(transfer: Transfer): TransferView = {
    TransferView(transfer.id, transfer.fromAccount, transfer.toAccount, transfer.amount)
  }

  def createTransfer(request: TransferCreateRequest): Transfer = dbManager.inTransaction { _ =>
    request.from match {
      case Some(from) => createTransferFromTo(from, request.to, request.amount)
      case None => createInitialTransfer(request.to, request.amount)
    }
  }

  private def createTransferFromTo(from: AccountId, to: AccountId, amount: Int): Transfer = dbManager.inTransaction { _ =>
    val fromAccount = accountService.getAccount(from)

    val currentBalance = getBalance(fromAccount.id)
    if (currentBalance < amount) {
      throw new NotEnoughBalance(from, to, amount)
    }

    val transfer = Transfer(Some(from), to, amount)
    transferDao.save(transfer)

    val isUpdatedFrom = accountService.updateLastTransaction(transfer.id, fromAccount) == 1

    if (!isUpdatedFrom) {
      throw new RaceConditionTransaction()
    }

    transfer
  }

  def getBalance(accountId: AccountId): Int = dbManager.inTransaction { implicit conn =>
    Transfer.totalBalance(accountId, transferDao.selectAccount(accountId))
  }

  private def createInitialTransfer(to: AccountId, amount: Int): Transfer = dbManager.inTransaction { _ =>
    val transfer = Transfer(None, to, amount)
    transferDao.save(transfer)
    transfer
  }

  def getTransfers(accountId: AccountId): List[Transfer] = {
    transferDao.selectAccount(accountId)
  }

}
