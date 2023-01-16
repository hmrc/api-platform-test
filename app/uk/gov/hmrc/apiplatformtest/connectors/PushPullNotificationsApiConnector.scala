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

package uk.gov.hmrc.apiplatformtest.connectors

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import play.api.libs.json.{JsValue, Json, OFormat}
import uk.gov.hmrc.apiplatformtest.connectors.CreateNotificationResponse.formatCreateNotificationResponse
import uk.gov.hmrc.apiplatformtest.connectors.PushPullNotificationsApiConnector.Config
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, UpstreamErrorResponse}

@Singleton
class PushPullNotificationsApiConnector @Inject() (http: HttpClient, config: Config)(implicit ec: ExecutionContext) {

  lazy val serviceBaseUrl: String = config.baseUrl

  def saveNotification(boxId: UUID, payload: JsValue)(implicit hc: HeaderCarrier): Future[String] = {
    http
      .POST[JsValue, CreateNotificationResponse](s"$serviceBaseUrl/box/$boxId/notifications", payload)
      .map(_.notificationId)
  }

  def getBoxId(clientId: String)(implicit hc: HeaderCarrier): Future[UUID] = {
    http
      .GET[Either[UpstreamErrorResponse, JsValue]](s"$serviceBaseUrl/box", Seq("boxName" -> "test/api-platform-test##1.0##callbackUrl", "clientId" -> clientId))
      .map {
        case Right(r)  => (r \ "boxId").as[UUID]
        case Left(err) => throw err
      }
  }
}

object PushPullNotificationsApiConnector {
  case class Config(baseUrl: String)
}

case class CreateNotificationResponse(notificationId: String)

object CreateNotificationResponse {
  implicit val formatCreateNotificationResponse: OFormat[CreateNotificationResponse] = Json.format[CreateNotificationResponse]
}
