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

package uk.gov.hmrc.apiplatformtest.services

import java.util.UUID
import java.util.UUID.randomUUID

import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.{verify, when}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.parse
import uk.gov.hmrc.apiplatformtest.connectors.PushPullNotificationsApiConnector
import uk.gov.hmrc.http.HeaderCarrier

import uk.gov.hmrc.util.AsyncHmrcSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NotificationsServiceSpec extends AsyncHmrcSpec {

  trait Setup{
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val boxId: UUID = randomUUID
    val clientId: String = randomUUID.toString
    val payload: JsValue = parse("""{"message": "new notification"}""")
    val notificationId: String = randomUUID.toString

    val mockPushPullNotificationsApiConnector: PushPullNotificationsApiConnector = mock[PushPullNotificationsApiConnector]
    val underTest = new NotificationsService(mockPushPullNotificationsApiConnector)
  }

  "saveNotification" should {
    "save notification using the notifications connector" in new Setup {
      when(mockPushPullNotificationsApiConnector.saveNotification(*, *)(*)).thenReturn(Future.successful(notificationId))

      await(underTest.saveNotification(boxId, payload))

      verify(mockPushPullNotificationsApiConnector).saveNotification(meq(boxId), meq(payload))(*)
    }

    "return the notification ID" in new Setup {
      when(mockPushPullNotificationsApiConnector.saveNotification(*, *)(*)).thenReturn(Future.successful(notificationId))

      val result: String = await(underTest.saveNotification(boxId, payload))

      result shouldBe notificationId
    }
  }

  "getBox" should {
    "return the box ID" in new Setup {
      when(mockPushPullNotificationsApiConnector.getBoxId(*)(*)).thenReturn(Future.successful(boxId))

      val result: UUID = await(underTest.getBox(clientId))

      result shouldBe boxId
    }
  }
}
