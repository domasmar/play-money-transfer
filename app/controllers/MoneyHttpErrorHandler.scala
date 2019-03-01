package controllers

import javax.inject.Singleton
import money.exception.{NotEnoughBalance, RaceConditionTransaction}
import play.api.http.{HttpErrorHandler, Status}
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.{Logger, mvc}

import scala.concurrent.Future

@Singleton
class MoneyHttpErrorHandler extends HttpErrorHandler {
  val logger: Logger = Logger(classOf[MoneyHttpErrorHandler])

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    logger.error(s"Client error occured. Status code: $statusCode, message: $message")

    Future.successful(
      new mvc.Results.Status(statusCode)
    )
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error("Server error occurred", exception)

    exception match {
      case _: NotEnoughBalance => Future.successful(new Results.Status(Status.UNPROCESSABLE_ENTITY))
      case _: RaceConditionTransaction => Future.successful(new Results.Status(Status.CONFLICT))
      case _ => Future.successful(new Results.Status(Status.INTERNAL_SERVER_ERROR))
    }
  }
}
