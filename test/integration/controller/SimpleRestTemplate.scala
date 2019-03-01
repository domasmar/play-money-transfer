package integration.controller

import play.api.libs.json.{Reads, Writes}
import play.api.test.WsTestClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait SimpleRestTemplate {

  def get[T](url: String, port: Int)(implicit reads: Reads[T]): Response[T] = {
    val responseAsync = WsTestClient.wsUrl(url)(port).get()
    val response = Await.result(responseAsync, Duration.Inf)

    Response[T](
      response.status,
      response.json.as[T]
    )
  }

  def post[B, T](url: String, port: Int, body: B)(implicit writes: Writes[B], reads: Reads[T]): Response[T] = {
    val responseAsync = WsTestClient.wsUrl(url)(port).post(writes.writes(body))
    val response = Await.result(responseAsync, Duration.Inf)

    Response[T](
      response.status,
      response.json.as[T]
    )
  }

  def postNoResponse[B](url: String, port: Int, body: B)(implicit writes: Writes[B]): Response[Unit] = {
    val responseAsync = WsTestClient.wsUrl(url)(port).post(writes.writes(body))
    val response = Await.result(responseAsync, Duration.Inf)

    Response[Unit](
      response.status,
      null
    )
  }


  case class Response[T](status: Int, body: T)

}
