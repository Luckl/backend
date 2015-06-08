package com.leagueprojecto.api.services.riot

import akka.actor.Status.Failure
import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.leagueprojecto.api.JsonProtocols
import com.leagueprojecto.api.domain.Summoner
import com.leagueprojecto.api.services.riot.RiotService.{TooManyRequests, ServiceNotAvailable}
import spray.json._

object SummonerService {
  case class GetSummonerByName(region: String, name: String)
  class SummonerNotFound(message: String) extends Exception

  def props: Props = Props[SummonerService]
}

class SummonerService extends Actor with ActorLogging with RiotService with JsonProtocols {
  import SummonerService._

  override def receive: Receive = {
    case GetSummonerByName(region, name) =>
      val origSender: ActorRef = sender()

      val endpoint1: Uri = endpoint(region, summonerByName + name)

      val future = riotRequest(RequestBuilding.Get(endpoint1))
      future onSuccess {
          case HttpResponse(OK, _, entity, _) =>
            Unmarshal(entity).to[String].onSuccess {
              case result: String =>
                val summoner = transform(result.parseJson.asJsObject)
                origSender ! summoner
            }
          case HttpResponse(NotFound, _, _, _) =>
            val message = s"No summoner found by name '$name' for region '$region'"
            log.warning(message)
            origSender ! Failure(new SummonerNotFound(message))
      }
      future onFailure {
        case x: Exception =>
          println("exception")
          x.printStackTrace()
      }


//          case 404 =>
//            val message = s"No summoner found by name '$name' for region '$region'"
//            log.warning(message)
//            origSender ! Failure(new SummonerNotFound(message))
//
//          case 429 =>
//            val message = s"Too many requests"
//            log.warning(message)
//            origSender ! Failure(new TooManyRequests(message))
//
//          case 503 =>
//            val message = s"SummonerService not available"
//            log.warning(message)
//            origSender ! Failure(new ServiceNotAvailable(message))
//
//          case code: Int =>
//            val message = s"Something went wrong. API call error code: $code"
//            log.warning(message)
//            origSender ! Failure(new IllegalStateException(message))

      }

//      val future: ListenableFuture[Response] = httpClient.prepareGet(riotApi(region, summonerByName + name))
//        .addQueryParam("api_key", api_key)
//        .execute()

//      future.addListener(new Runnable {
//        override def run(): Unit = {
//          val response = future.get()
//
//          response.getStatusCode match {
//            case 200 =>
//              val result = response
//                .getResponseBody
//                .parseJson
//                .asJsObject
//
//              val summoner = transform(result)
//              origSender ! summoner
//
//            case 404 =>
//              val message = s"No summoner found by name '$name' for region '$region'"
//              log.warning(message)
//              origSender ! Failure(new SummonerNotFound(message))
//
//            case 429 =>
//              val message = s"Too many requests"
//              log.warning(message)
//              origSender ! Failure(new TooManyRequests(message))
//
//            case 503 =>
//              val message = s"SummonerService not available"
//              log.warning(message)
//              origSender ! Failure(new ServiceNotAvailable(message))
//
//            case code: Int =>
//              val message = s"Something went wrong. API call error code: $code"
//              log.warning(message)
//              origSender ! Failure(new IllegalStateException(message))
//          }
//        }
//      }, context.dispatcher)
//  }

  private def transform(riotResult: JsObject): Summoner = {
    val firstKey = riotResult.fields.keys.head
    riotResult.fields.get(firstKey).get.convertTo[Summoner]
  }
}
