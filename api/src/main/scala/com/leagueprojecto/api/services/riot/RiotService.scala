package com.leagueprojecto.api.services.riot

import akka.actor.Actor
import com.ning.http.client.AsyncHttpClient

object RiotService {
  class ServiceNotAvailable(message: String) extends Exception
  class TooManyRequests(message: String) extends Exception
}

trait RiotService {
  this: Actor =>

  private val config = context.system.settings.config
  private val endpoint = config.getString("riot.api-endpoint")

  val httpClient: AsyncHttpClient = new AsyncHttpClient
  val api_key = config.getString("riot.api-key")

  // Services
  val summonerByName = config.getString("riot.service-summonerbyname")
  val matchHistoryBySummonerId = config.getString("riot.service-matchhistory")

  def riotApi(region: String, service: String) = s"$endpoint/$region/$service"
}