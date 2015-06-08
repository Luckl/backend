package com.leagueprojecto.api

import akka.actor._
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.stream.ActorFlowMaterializer
import akka.util.Timeout
import com.leagueprojecto.api.domain.{MatchHistory, Summoner}
import com.leagueprojecto.api.services.CacheService
import CacheService.CachedResponse
import com.leagueprojecto.api.services.riot.MatchHistoryService.GetMatchHistory
import com.leagueprojecto.api.services.riot.{MatchHistoryService, RiotService, SummonerService}
import com.leagueprojecto.api.services.riot.SummonerService.GetSummonerByName
import com.typesafe.config.ConfigFactory

import akka.pattern.ask

import scala.concurrent.duration._

object Startup extends App with JsonProtocols {
  implicit val system = ActorSystem("api")
  implicit val timeout: Timeout = 5.seconds

  implicit val executor = system.dispatcher
  implicit val materializer = ActorFlowMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  val regionMatcher = config.getString("riot.regions").r

  // Services
  val summonerService: ActorRef = system.actorOf(SummonerService.props)
//  val matchHistoryService: ActorRef = system.actorOf(MatchHistoryService.props)

  // Service caches
  val summonerCacheTime = config.getInt("riot.services.summonerbyname.cacheTime")
  val matchhistoryCacheTime = config.getInt("riot.services.matchhistory.cacheTime")
  val cachedSummonerService: ActorRef     = system.actorOf(CacheService.props[Summoner](summonerService, summonerCacheTime))
//  val cachedMatchHistoryService: ActorRef = system.actorOf(CacheService.props[List[MatchHistory]](matchHistoryService, matchhistoryCacheTime))

  val optionsSupport = {
    options {
      complete("")
    }
  }

  implicit def myExceptionHandler = ExceptionHandler {
    case e: SummonerService.SummonerNotFound  => complete(HttpResponse(NotFound))
    case e: RiotService.ServiceNotAvailable   => complete(HttpResponse(ServiceUnavailable))
    case e: RiotService.TooManyRequests       => complete(HttpResponse(TooManyRequests))
    case _                                    => complete(HttpResponse(InternalServerError))
  }

  val corsHeaders = List(RawHeader("Access-Control-Allow-Origin", "*"),
    RawHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE"),
    RawHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization"))

  def summonerRoute(implicit region: String) = {
    pathPrefix("summoner" / Segment) { name =>
      pathEndOrSingleSlash {
        get {
          complete {
            (cachedSummonerService ? GetSummonerByName(region, name)).mapTo[CachedResponse[Summoner]]
        }
        } ~ optionsSupport
      }
    }
  }

//  def matchhistoryRoute(implicit region: String) = {
//    pathPrefix("matchhistory" / LongNumber) { summonerId =>
//      pathEndOrSingleSlash {
//        get {
//          complete {
//            (cachedMatchHistoryService ? GetMatchHistory(region, summonerId)).mapTo[CachedResponse[List[MatchHistory]]]
//          }
//        } ~ optionsSupport
//      }
//    }
//  }


  val routes = {
  //  logRequestResult("API-service") {
      respondWithHeaders(corsHeaders) {
        pathPrefix("api" / regionMatcher) { regionSegment =>
          implicit val region = regionSegment.toLowerCase

          summonerRoute
//          ~
//          matchhistoryRoute
        }
      }
  }

  // Bind the HTTP endpoint. Specify http.interface and http.port in the configuration
  // to change the address and port to bind to.
  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
