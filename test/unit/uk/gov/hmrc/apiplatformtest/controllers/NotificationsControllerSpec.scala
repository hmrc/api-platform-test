/*
 * Copyright 2020 HM Revenue & Customs
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

import akka.stream.Materializer
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{verify, when}
import org.scalatest.concurrent.Eventually
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.mvc.Result
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import uk.gov.hmrc.apiplatformtest.services.NotificationsService
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful
import scala.concurrent.duration._

class NotificationsControllerSpec extends UnitSpec with WithFakeApplication with StubControllerComponentsFactory with MockitoSugar with Eventually {

  trait Setup{
    implicit val mat: Materializer = fakeApplication.materializer
    val boxId: UUID = randomUUID
    val clientId: String = randomUUID.toString
    val request = FakeRequest().withHeaders("X-Client-ID" -> clientId)
    val notificationId: String = randomUUID.toString

    val mockNotificationsService: NotificationsService = mock[NotificationsService]
    val underTest = new NotificationsController(stubControllerComponents(), mockNotificationsService)
  }

  "saveNotification" should {
    "eventually save notification using the notifications service" in new Setup {
      when(mockNotificationsService.getBox(meq(clientId))(any())).thenReturn(successful(boxId))
      when(mockNotificationsService.saveNotification(any(), any())(any())).thenReturn(successful(notificationId))

      val result: Result = await(underTest.triggerNotification()(request))

      eventually(timeout(3.seconds), interval(100.milliseconds)) {
        verify(mockNotificationsService).saveNotification(meq(boxId), any())(any())
      }
    }

    "return 200 with the box ID and correlation ID" in new Setup {
      when(mockNotificationsService.getBox(meq(clientId))(any())).thenReturn(successful(boxId))

      val result: Result = await(underTest.triggerNotification()(request))

      status(result) shouldBe OK
      (jsonBodyOf(result) \ "boxId").as[UUID] shouldBe boxId
      (jsonBodyOf(result) \ "correlationId").asOpt[UUID] shouldBe defined
    }

    "return 500 when client ID is missing in the request header" in new Setup {
      val result: Result = await(underTest.triggerNotification()(FakeRequest()))

      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }
}
