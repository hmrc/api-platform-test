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

import akka.stream.Materializer
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import play.api.http.ContentTypes.JSON
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.Status.OK
import play.api.libs.json.JsValue
import play.api.libs.json.Json.parse
import play.api.mvc.Result
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import uk.gov.hmrc.apiplatformtest.services.NotificationsService
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NotificationsControllerSpec extends UnitSpec with WithFakeApplication with StubControllerComponentsFactory with MockitoSugar {

  trait Setup{
    implicit val mat: Materializer = fakeApplication.materializer
    val boxId: UUID = UUID.randomUUID
    val payload: JsValue = parse("""{"message": "new notification"}""")
    val request: FakeRequest[JsValue] = FakeRequest().withHeaders(CONTENT_TYPE -> JSON).withBody(payload)
    val notificationId: String = UUID.randomUUID.toString

    val mockNotificationsService: NotificationsService = mock[NotificationsService]
    val underTest = new NotificationsController(stubControllerComponents(), mockNotificationsService)
  }

  "saveNotification" should {
    "save notification using the notifications service" in new Setup {
      when(mockNotificationsService.saveNotification(any(), any())(any())).thenReturn(Future.successful(notificationId))

      val result: Result = await(underTest.saveNotification(boxId)(request))

      verify(mockNotificationsService).saveNotification(meq(boxId), meq(payload))(any())
    }

    "return 200 with the notification ID" in new Setup {
      when(mockNotificationsService.saveNotification(any(), any())(any())).thenReturn(Future.successful(notificationId))

      val result: Result = await(underTest.saveNotification(boxId)(request))

      status(result) shouldBe OK
      (jsonBodyOf(result) \ "notificationId").as[String] shouldBe notificationId
    }
  }
}
