/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.apiplatformtest.controllers

import java.time.Instant
import java.util.UUID
import java.util.UUID.randomUUID
import java.util.concurrent.{CountDownLatch, TimeUnit}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.scalatest.concurrent.Eventually
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.http.Status.{ACCEPTED, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.JsValue
import play.api.test.Helpers._
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import uk.gov.hmrc.apiplatformtest.services.NotificationsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.util.AsyncHmrcSpec

class NotificationsControllerSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite with StubControllerComponentsFactory with Eventually {

  implicit val actorSystemTest: ActorSystem = app.actorSystem
  implicit val mat: Materializer            = app.materializer

  val boxId: UUID            = randomUUID
  val clientId: String       = randomUUID.toString
  val notificationId: String = randomUUID.toString

  class FakeNotificationsService(cdl: CountDownLatch)(override implicit val ec: ExecutionContext) extends NotificationsService(null)(ec) {
    override def getBox(clientId: String)(implicit hc: HeaderCarrier): Future[UUID] = successful(boxId)

    override def saveNotification(boxId: UUID, payload: JsValue)(implicit hc: HeaderCarrier): Future[String] = {
      cdl.countDown()
      successful(notificationId)
    }
  }

  trait Setup {
    val mockNotificationsService: NotificationsService = mock[NotificationsService]
    val cdl                                            = new CountDownLatch(1)
    val underTest                                      = new NotificationsController(stubControllerComponents(), stubPlayBodyParsers, actorSystemTest, new FakeNotificationsService(cdl))
  }

  "saveNotification" should {
    val request         = FakeRequest().withHeaders("X-Client-ID" -> clientId)
    val requestWithBody = request.withBody("""{"hello":"world"}""")

    "not save notification immediately" in new Setup {

      await(underTest.triggerNotification()(requestWithBody))

      cdl.await(5, TimeUnit.MILLISECONDS) shouldBe false // HAS NOT SENT YET
    }

    "eventually save notification using the notifications service" in new Setup {
      await(underTest.triggerNotification()(requestWithBody))

      cdl.await(3, TimeUnit.SECONDS) shouldBe true // HAS NOW SENT IT
    }

    "return 200 with the box ID and correlation ID when there is a payload" in new Setup {
      val result = underTest.triggerNotification()(requestWithBody)

      status(result) shouldBe OK
      (contentAsJson(result) \ "boxId").as[UUID] shouldBe boxId
      (contentAsJson(result) \ "correlationId").asOpt[UUID] shouldBe defined
    }

    "return 200 with the box ID and correlation ID" in new Setup {
      val result = underTest.triggerNotification()(request)

      status(result) shouldBe OK
      (contentAsJson(result) \ "boxId").as[UUID] shouldBe boxId
      (contentAsJson(result) \ "correlationId").asOpt[UUID] shouldBe defined
    }

    "return 500 when client ID is missing in the request header" in new Setup {
      val result = underTest.triggerNotification()(FakeRequest())

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "handleNotificationPush" should {
    val request = FakeRequest().withBody("""{"hello":"world"}""")

    "return 200 by default" in new Setup {
      val result = underTest.handleNotificationPush(None, None)(request)

      status(result) shouldBe OK
    }

    "return specified status when a status is passed in" in new Setup {
      val result = underTest.handleNotificationPush(Some(ACCEPTED), None)(request)

      status(result) shouldBe ACCEPTED
    }

    "not delay the response by default" in new Setup {
      val before = Instant.now
      val result = await(underTest.handleNotificationPush(None, None)(request))
      val after  = Instant.now

      result.header.status shouldBe OK
      (after.toEpochMilli - before.toEpochMilli).toInt should be < 100
    }

    "delay the response when a delay is passed in" in new Setup {
      val before = Instant.now
      val result = await(underTest.handleNotificationPush(None, Some(2))(request))
      val after  = Instant.now

      result.header.status shouldBe OK
      (after.toEpochMilli - before.toEpochMilli).toInt should be > 2000
    }
  }

  "callbackValidation" should {
    val request   = FakeRequest()
    val challenge = UUID.randomUUID().toString

    "return 200 and the challenge" in new Setup {
      val result = underTest.callbackValidation(challenge)(request)

      status(result) shouldBe OK
      (contentAsJson(result) \ "challenge").as[String] shouldBe challenge
    }
  }
}
