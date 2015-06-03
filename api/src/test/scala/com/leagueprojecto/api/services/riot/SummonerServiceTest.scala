package com.leagueprojecto.api.services.riot

import java.util.concurrent.{Executor, TimeUnit}

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.ning.http.client.{HttpResponseBodyPart, Response, ListenableFuture, AsyncHttpClient}
import org.scalatest.{FlatSpec, Matchers, GivenWhenThen}
import org.mockito.Mockito

import scala.collection.mutable

//class SummonerServiceTest extends FlatSpec with Matchers with GivenWhenThen {
//  implicit val actorSystem = ActorSystem.create("Test-API")
//  val endpoint = "https://euw.api.pvp.net/api/lol/EUW/v1.4/summoner/by-name/"
//
//  trait StartUp {
//    val httpClient      = Mockito.mock(classOf[AsyncHttpClient])
//    val summonerService = getMockedActor(httpClient)
//  }
//
//  "Summoner Service" should "map the respond in a Summoner class" in new StartUp {
//    Given("a valid username")
//    val name = "Wagglez"
//
//    Given("the mock returns a valid response")
//    Mockito.when(httpClient.prepareGet(endpoint + name).execute()).thenReturn(new SimpleFuture {
//      override def get(): Response = {
//        val response = new Response.ResponseBuilder()
//                          .accumulate(new HttpResponseBodyPart) //"""{}"""
//      }
//    })
//
//  }
//
//
//  def getMockedActor(client: AsyncHttpClient) = {
//    TestActorRef(new SummonerServiceTestActor(client))
//  }
//
//  class SummonerServiceTestActor(client: AsyncHttpClient) extends SummonerService {
//    override def httpClient = client
//  }
//}
//
//abstract class SimpleFuture extends ListenableFuture[Response] {
//  val listeners: mutable.MutableList[Runnable] = new mutable.MutableList[Runnable]
//
//  override def addListener(runnable: Runnable, executor: Executor): ListenableFuture[Response] = {
//    listeners += runnable
//    this
//  }
//
//  override def abort(throwable: Throwable): Unit = ???
//  override def isCancelled: Boolean = ???
//  override def get(timeout: Long, unit: TimeUnit): Response = ???
//  override def cancel(mayInterruptIfRunning: Boolean): Boolean = ???
//  override def isDone: Boolean = ???
//  override def done(): Unit = ???
//  override def touch(): Unit = ???
//}
