package money.service

import javax.inject.{Inject, Singleton}
import money.dao.{AccountDao, TxManager}
import money.domain.DTO.{AccountCreateRequest, AccountView}
import money.domain.Model.{Account, AccountId, TransferId}

@Singleton
class AccountService @Inject()(accountDao: AccountDao, dbManager: TxManager) {

  def buildView(account: Account): AccountView = {
    AccountView(account.id, account.name)
  }

  def createAccount(request: AccountCreateRequest): Account = {
    val account = Account(request.name)
    accountDao.save(account)
    account
  }

  def getAccount(accountId: AccountId): Account = {
    accountDao.select(accountId)
  }

  def updateLastTransaction(newTransfer: TransferId, account: Account): Int = {
    accountDao.updateLastTransfer(newTransfer, account)
  }

}
