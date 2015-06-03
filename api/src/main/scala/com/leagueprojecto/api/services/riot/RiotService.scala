package com.leagueprojecto.api.services.riot

import akka.actor.{ActorSystem, Actor}
import akka.http.Http
import akka.http.model.{HttpResponse, HttpRequest}
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.{ExecutionContextExecutor, Future}

object RiotService {
  class ServiceNotAvailable(message: String) extends Exception
  class TooManyRequests(message: String) extends Exception
}

trait RiotService {
  this: Actor =>

  private val config = context.system.settings.config
  val api_key = config.getString("riot.api-key")

  implicit def executor: ExecutionContextExecutor = context.system.dispatcher
  implicit val materializer: FlowMaterializer = ActorFlowMaterializer()

  lazy val riotConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http(context.system).outgoingConnection("www.google.com", 443)
//    Http(context.system).outgoingConnection(config.getString("riot.api-hostname"), config.getInt("riot.api-port"))


  def endpoint(region: String, service: String) =
    s"/api/lol/$region/$service?api_key=$api_key"

  def riotRequest(httpRequest: HttpRequest): Future[HttpResponse] =
    Source.single(httpRequest).via(riotConnectionFlow).runWith(Sink.head)

  // Services
  val summonerByName = config.getString("riot.services.summonerbyname.endpoint")
  val matchHistoryBySummonerId = config.getString("riot.services.matchhistory.endpoint")
}
