package integration.controller

import money.domain.DTO._
import money.domain.DTOReads._
import money.domain.DTOWrites._
import money.domain.Model.AccountId
import org.scalatest.{FlatSpec, Matchers}
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test.TestServer

class ControllerSpec extends FlatSpec with Matchers with SimpleRestTemplate {

  trait ServerContext {
    var Port: Int = 3333
    var testServer = TestServer(Port, GuiceApplicationBuilder(
      configuration = Configuration(inMemoryDatabase().toList: _ *)
    ).build())

    def withApp[T](block: => T): T = {
      running[T](testServer)(block)
    }

  }

  behavior of "Saving new account"

  it should "save new account and return response 201 containing AccountView" in new ServerContext {
    withApp {
      val accountCreateRequest = AccountCreateRequest("Test_name")
      val Response(status, accountView) = post[AccountCreateRequest, AccountView]("/account", Port, accountCreateRequest)

      status shouldBe 201
      accountView.name shouldBe "Test_name"
      accountView.id should not be null
    }
  }

  behavior of "Making a transaction"

  it should "create new transaction even if `from` is not defined and return 201 container TransferView" in new ServerContext {
    withApp {
      val bob = createAccount("Bob", Port)
      val transferRequest = TransferCreateRequest(None, bob.id, 100)
      val Response(status, transferView) = post[TransferCreateRequest, TransferView]("/transfer", Port, transferRequest)

      status shouldBe 201
      transferView.from shouldBe empty
      transferView.to shouldBe bob.id
      transferView.amount shouldBe 100
    }
  }

  it should "create new transaction and return response 201 containing TransferView" in new ServerContext {
    withApp {
      val bob = createAccount("Bob", Port)
      val steve = createAccount("Steve", Port)
      createInitialBalance(bob.id, 300, Port)

      val transferRequest = TransferCreateRequest(Some(bob.id), steve.id, 100)
      val Response(status, transferView) = post[TransferCreateRequest, TransferView]("/transfer", Port, transferRequest)

      status shouldBe 201
      transferView.from shouldBe Some(bob.id)
      transferView.to shouldBe steve.id
      transferView.amount shouldBe 100
    }
  }

  it should "not create new transaction if balance is not enough to make a transaction and return response 422" in new ServerContext {
    withApp {
      val bob = createAccount("Bob", Port)
      val steve = createAccount("Steve", Port)
      createInitialBalance(bob.id, 300, Port)

      val transferRequest = TransferCreateRequest(Some(bob.id), steve.id, 500)
      val Response(status, _) = postNoResponse[TransferCreateRequest]("/transfer", Port, transferRequest)

      status shouldBe 422
    }
  }

  behavior of "Fetching account transactions"

  it should "return all made transactions with response code 200 containing TransfersView" in new ServerContext {
    withApp {
      val bob = createAccount("Bob", Port)
      val steve = createAccount("Steve", Port)
      createInitialBalance(bob.id, 300, Port)
      transfer(bob.id, steve.id, 100, Port)
      transfer(steve.id, bob.id, 100, Port)

      val Response(status, view) = get[TransfersView](s"/account/${bob.id}/transfer", Port)

      status shouldBe 200
      view.balance shouldBe 300
      view.transfers should have size 3
      view.transfers(0).amount shouldBe 300
      view.transfers(1).amount shouldBe 100
      view.transfers(2).amount shouldBe 100
    }
  }

  private def createAccount(name: String, port: Int): AccountView = {
    val accountCreateRequest = AccountCreateRequest(name)
    val Response(_, accountView) = post[AccountCreateRequest, AccountView]("/account", port, accountCreateRequest)

    accountView
  }

  private def createInitialBalance(accountId: AccountId, balance: Int, port: Int): TransferView = {
    val transferRequest = TransferCreateRequest(None, accountId, balance)
    val Response(_, transferView) = post[TransferCreateRequest, TransferView]("/transfer", port, transferRequest)

    transferView
  }

  private def transfer(from: AccountId, to: AccountId, balance: Int, port: Int): TransferView = {
    val transferRequest = TransferCreateRequest(Some(from), to, balance)
    val Response(_, transferView) = post[TransferCreateRequest, TransferView]("/transfer", port, transferRequest)

    transferView
  }
}
