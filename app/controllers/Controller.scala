package controllers

import javax.inject._
import money.domain.DTO.{AccountCreateRequest, TransferCreateRequest}
import money.domain.DTOReads._
import money.domain.DTOWrites._
import money.domain.Model.AccountId
import money.service.{AccountService, TransferService}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class Controller @Inject()(cc: ControllerComponents, accountService: AccountService, transferService: TransferService)
  extends AbstractController(cc) {

  def createAccount() = Action.async(parse.json[AccountCreateRequest]) { request =>
    val createRequest = request.body
    val newAccount = accountService.createAccount(createRequest)
    val newAccountView = accountService.buildView(newAccount)
    Future.successful(Created(Json.toJson(newAccountView)))
  }

  def getTransfers(accountId: AccountId) = Action.async {
    val transfers = transferService.getTransfers(accountId)
    val transfersView = transferService.buildBulkView(accountId, transfers)
    Future.successful(Ok(Json.toJson(transfersView)))
  }

  def createTransfer() = Action.async(parse.json[TransferCreateRequest]) { request =>
    val transferRequest = request.body
    val transfer = transferService.createTransfer(transferRequest)
    val transferView = transferService.buildView(transfer)
    Future.successful(Created(Json.toJson(transferView)))
  }

}
