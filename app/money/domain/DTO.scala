package money.domain

import money.domain.DTO._
import money.domain.Model.{AccountId, TransferId}
import play.api.libs.json._

object DTO {

  case class AccountCreateRequest(name: String)

  case class AccountView(id: AccountId, name: String)

  case class TransferCreateRequest(from: Option[AccountId], to: AccountId, amount: Int)

  case class TransfersView(accountId: AccountId, balance: Int, transfers: List[TransferView])

  case class TransferView(id: TransferId, from: Option[AccountId], to: AccountId, amount: Int)

}

object DTOReads {

  implicit val accountViewReads: Reads[AccountView] = Json.reads[AccountView]

  implicit val transferViewReads: Reads[TransferView] = Json.reads[TransferView]

  implicit val transfersViewReads: Reads[TransfersView] = Json.reads[TransfersView]

  implicit val accountCreateRequestRead: Reads[AccountCreateRequest] = Json.reads[AccountCreateRequest]

  implicit val transferCreateRequestRead: Reads[TransferCreateRequest] = Json.reads[TransferCreateRequest]

}

object DTOWrites {

  implicit val accountViewWrites: Writes[AccountView] = Json.writes[AccountView]

  implicit val transferViewWrites: Writes[TransferView] = Json.writes[TransferView]

  implicit val transfersViewWrites: Writes[TransfersView] = Json.writes[TransfersView]

  implicit val accountCreateRequestWrite: Writes[AccountCreateRequest] = Json.writes[AccountCreateRequest]

  implicit val transferCreateRequestWrite: Writes[TransferCreateRequest] = Json.writes[TransferCreateRequest]


}
