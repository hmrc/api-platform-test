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
import java.util.UUID.randomUUID

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.apache.pekko.stream.Materializer
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.Application
import play.api.http.HeaderNames.{CONTENT_TYPE, USER_AGENT}
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json.parse
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.util.AsyncHmrcSpec

class PushPullNotificationsApiConnectorSpec
    extends AsyncHmrcSpec
    with GuiceOneAppPerSuite
    with BeforeAndAfterAll with BeforeAndAfterEach {

  private val stubPort = 11111
  private val stubHost = "localhost"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(("metrics.jvm", false))
      .configure(("microservice.services.push-pull-notifications-api.port", stubPort))
      .build()

  implicit lazy val mat: Materializer = app.materializer
  private val wireMockServer          = new WireMockServer(wireMockConfig().port(stubPort))

  override def beforeAll(): Unit = {
    wireMockServer.start()
    WireMock.configureFor(stubHost, stubPort)
  }

  override def beforeEach(): Unit = {
    wireMockServer.resetAll()
  }

  override def afterAll(): Unit = {
    wireMockServer.stop()
  }

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val boxId: UUID            = randomUUID
    val encodedBoxName: String = "test/api-platform-test##1.0##callbackUrl"
    val notificationId: String = randomUUID.toString
    val clientId: String       = randomUUID.toString
    val payload: JsValue       = parse("""{"message": "new notification"}""")
    val notificationsPath      = s"/box/$boxId/notifications"
    val boxPath                = s"/box"

    val underTest: PushPullNotificationsApiConnector = app.injector.instanceOf[PushPullNotificationsApiConnector]
  }

  "saveNotification" should {
    "send proper request to save notification" in new Setup {
      wireMockServer.stubFor(
        post(notificationsPath).withRequestBody(equalTo(Json.stringify(payload)))
          .willReturn(aResponse()
            .withHeader(CONTENT_TYPE, "application/json")
            .withBody(Json.stringify(Json.toJson(CreateNotificationResponse(notificationId))))
            .withStatus(OK))
      )

      await(underTest.saveNotification(boxId, payload))

      wireMockServer.verify(
        postRequestedFor(urlPathEqualTo(notificationsPath))
          .withHeader(CONTENT_TYPE, equalTo("application/json"))
          .withHeader(USER_AGENT, equalTo("api-platform-test"))
      )
    }

    "return the notification ID" in new Setup {
      wireMockServer.stubFor(
        post(notificationsPath).withRequestBody(equalTo(Json.stringify(payload)))
          .willReturn(aResponse()
            .withHeader(CONTENT_TYPE, "application/json")
            .withBody(Json.stringify(Json.toJson(CreateNotificationResponse(notificationId))))
            .withStatus(OK))
      )

      val result: String = await(underTest.saveNotification(boxId, payload))

      result shouldBe notificationId
    }

    "throw upstream error response exception with statusCode of NOT_FOUND when the box does not exist" in new Setup {
      wireMockServer.stubFor(
        post(notificationsPath).withRequestBody(equalTo(Json.stringify(payload)))
          .willReturn(aResponse()
            .withStatus(NOT_FOUND))
      )

      intercept[UpstreamErrorResponse] {
        await(underTest.saveNotification(boxId, payload))
      }.statusCode shouldBe NOT_FOUND
    }
  }

  "getBoxId" should {
    "return the box ID" in new Setup {
      wireMockServer.stubFor(
        get(urlPathEqualTo(boxPath))
          .withQueryParam("boxName", equalTo(encodedBoxName))
          .withQueryParam("clientId", equalTo(clientId))
          .willReturn(aResponse()
            .withHeader(CONTENT_TYPE, "application/json")
            .withBody(Json.stringify(Json.obj("boxId" -> boxId)))
            .withStatus(OK))
      )

      val result: UUID = await(underTest.getBoxId(clientId))

      result shouldBe boxId
    }

    "throw upstream error response exception with statusCode of NOT_FOUND when the box does not exist" in new Setup {
      wireMockServer.stubFor(
        get(urlPathEqualTo(boxPath))
          .withQueryParam("boxName", equalTo(encodedBoxName))
          .withQueryParam("clientId", equalTo(clientId))
          .willReturn(aResponse()
            .withStatus(NOT_FOUND)
            .withBody("{}"))
      )

      intercept[UpstreamErrorResponse] {
        await(underTest.getBoxId(clientId))
      }.statusCode shouldBe NOT_FOUND
    }
  }
}
