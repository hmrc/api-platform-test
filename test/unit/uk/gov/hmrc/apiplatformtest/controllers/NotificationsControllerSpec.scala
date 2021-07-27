/*
 * Copyright 2021 HM Revenue & Customs
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

import java.util.UUID
import java.util.UUID.randomUUID

import akka.actor.ActorSystem
import akka.stream.Materializer
import org.joda.time.DateTime
import org.scalatest.concurrent.Eventually
import play.api.http.Status.{ACCEPTED, INTERNAL_SERVER_ERROR, OK}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import uk.gov.hmrc.apiplatformtest.services.NotificationsService
import uk.gov.hmrc.util.AsyncHmrcSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful
import scala.concurrent.duration._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class NotificationsControllerSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite with StubControllerComponentsFactory with Eventually {

  implicit val actorSystemTest: ActorSystem = app.injector.instanceOf[ActorSystem]
  implicit val mat: Materializer = app.injector.instanceOf[Materializer]

  val boxId: UUID = randomUUID
  val clientId: String = randomUUID.toString
  val notificationId: String = randomUUID.toString

  trait Setup {
    val mockNotificationsService: NotificationsService = mock[NotificationsService]
    val underTest = new NotificationsController(stubControllerComponents(), stubPlayBodyParsers, actorSystemTest, mockNotificationsService)
  }

  "saveNotification" should {
    val request = FakeRequest().withHeaders("X-Client-ID" -> clientId)

    "eventually save notification using the notifications service" in new Setup {
      when(mockNotificationsService.getBox(eqTo(clientId))(*)).thenReturn(successful(boxId))
      when(mockNotificationsService.saveNotification(*, *)(*)).thenReturn(successful(notificationId))

      await(underTest.triggerNotification()(request))

      eventually(timeout(3.seconds), interval(100.milliseconds)) {
        verify(mockNotificationsService).saveNotification(eqTo(boxId), *)(*)
      }
    }

    "return 200 with the box ID and correlation ID" in new Setup {
      when(mockNotificationsService.getBox(eqTo(clientId))(*)).thenReturn(successful(boxId))

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
      val before = new DateTime()
      val result = await(underTest.handleNotificationPush(None, None)(request))
      val after = new DateTime()

      result.header.status shouldBe OK
      (after.getMillis - before.getMillis).toInt should be < 100
    }

    "delay the response when a delay is passed in" in new Setup {
      val before = new DateTime()
      val result = await(underTest.handleNotificationPush(None, Some(2))(request))
      val after = new DateTime()

      result.header.status shouldBe OK
      (after.getMillis - before.getMillis).toInt should be > 2000
    }
  }

  "callbackValidation" should {
    val request = FakeRequest()
    val challenge = UUID.randomUUID().toString

    "return 200 and the challenge" in new Setup {
      val result = underTest.callbackValidation(challenge)(request)

      status(result) shouldBe OK
      (contentAsJson(result) \ "challenge").as[String] shouldBe challenge
    }
  }
}
