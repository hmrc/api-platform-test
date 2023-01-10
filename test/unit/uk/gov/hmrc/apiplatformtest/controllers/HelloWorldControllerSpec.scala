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

import java.util.UUID.randomUUID
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.JsValue
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import play.api.test.Helpers._
import uk.gov.hmrc.apiplatformtest.models.Header
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters._
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.controllers.RestFormats.localDateFormats
import uk.gov.hmrc.util.AsyncHmrcSpec

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.{failed, successful}

class HelloWorldControllerSpec extends AsyncHmrcSpec with StubControllerComponentsFactory{

  trait Setup {
    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val underTest = new HelloController(mockAuthConnector, stubControllerComponents())
  }

  "GET /" should {
    "return 200" in new Setup {
      private val result = underTest.handle()(FakeRequest("GET", "/"))
      status(result) shouldBe OK
    }
  }

  "Hello Dave" should {
    "return 200 with the user details and request headers" in new Setup {
      private val internalId = randomUUID.toString
      private val externalId = randomUUID.toString
      private val applicationId = randomUUID().toString
      private val credentials = Credentials(randomUUID.toString, randomUUID.toString)
      private val name = Name(Some(randomUUID.toString), Some(randomUUID.toString))
      private val dateOfBirth = LocalDate.now
      private val postCode = randomUUID.toString
      private val email = randomUUID.toString
      private val affinityGroup = Individual
      private val agentCode = randomUUID.toString
      private val agentInformation = AgentInformation(Some(randomUUID.toString), Some(randomUUID.toString), Some(randomUUID.toString))
      private val credentialRole = User
      private val description = randomUUID.toString
      private val groupIdentifier = randomUUID.toString
      private val headers: Seq[(String, String)] = Seq("Host" -> "localhost", "User-Agent" -> "api-platform-test", "Client-ID" -> randomUUID.toString)
      when(
        mockAuthConnector
          .authorise(
            *,
            eqTo(allUserDetails and Retrievals.internalId and Retrievals.externalId and Retrievals.applicationId)
          )(*, *)
      ).thenReturn(
        successful(new ~(new ~(new ~(new  ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(Some(credentials), Some(name)),
          Some(dateOfBirth)), Some(postCode)), Some(email)), Some(affinityGroup)), Some(agentCode)), agentInformation), Some(credentialRole)),
          Some(description)), Some(groupIdentifier)), Some(internalId)), Some(externalId)), Some(applicationId))
        )
      )

      val result = underTest.handleDave()(FakeRequest().withHeaders(headers: _*))

      status(result) shouldBe OK
      val jsonResult: JsValue = contentAsJson(result)
      (jsonResult \ "internalId").as[String] shouldBe internalId
      (jsonResult \ "externalId").as[String] shouldBe externalId
      (jsonResult \ "applicationId").as[String] shouldBe applicationId
      (jsonResult \ "credentials").as[Credentials] shouldBe credentials
      (jsonResult \ "name").as[Name] shouldBe name
      (jsonResult \ "dateOfBirth").as[LocalDate] shouldBe dateOfBirth
      (jsonResult \ "postCode").as[String] shouldBe postCode
      (jsonResult \ "email").as[String] shouldBe email
      (jsonResult \ "affinityGroup").as[AffinityGroup] shouldBe affinityGroup
      (jsonResult \ "agentCode").as[String] shouldBe agentCode
      (jsonResult \ "agentInformation").as[AgentInformation] shouldBe agentInformation
      (jsonResult \ "credentialRole").as[CredentialRole] shouldBe credentialRole
      (jsonResult \ "description").as[String] shouldBe description
      (jsonResult \ "groupIdentifier").as[String] shouldBe groupIdentifier
      (jsonResult \ "headers").as[Seq[Header]] shouldBe headers.map(h => Header(h._1, h._2))
    }

    "return 401 if the authorisation fails" in new Setup {
      when(
        mockAuthConnector
          .authorise(
            *,
            eqTo(allUserDetails and Retrievals.internalId and Retrievals.externalId and Retrievals.applicationId)
          )(*, *)
      ).thenReturn(failed(InvalidBearerToken()))

      val result = underTest.handleDave()(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      val jsonResult: JsValue = contentAsJson(result)
      (jsonResult \ "errorMessage").as[String] shouldBe "Invalid bearer token"
    }
  }

  "Hello Bruce" should {
    "return 200 with application details when authorisation succeeds" in new Setup {
      private val retrievedAuthProviderId = GGCredId("123")
      private val retrievedCredentials = Credentials("123", "GG")
      private val retrievedClientId = "retrieved-client-id"
      private val retrievedApplicationName = "retrieved-application-name"
      private val retrievedApplicationId = "retrieved-application-id"

      when(mockAuthConnector.authorise(*, eqTo(authProviderId and credentials and clientId and applicationName and applicationId))(*, *))
        .thenReturn(successful(
          new ~(new ~(new ~(new ~(retrievedAuthProviderId, Some(retrievedCredentials)), Some(retrievedClientId)),
            Some(retrievedApplicationName)), Some(retrievedApplicationId))))

      val result = underTest.handleBruce()(FakeRequest())

      status(result) shouldBe OK

      val jsonResult: JsValue = contentAsJson(result)
      (jsonResult \ "authProviderId").as[GGCredId] shouldBe retrievedAuthProviderId
      (jsonResult \ "credentials").as[Credentials] shouldBe retrievedCredentials
      (jsonResult \ "clientId").as[String] shouldBe retrievedClientId
      (jsonResult \ "applicationName").as[String] shouldBe retrievedApplicationName
      (jsonResult \ "applicationId").as[String] shouldBe retrievedApplicationId
    }

    "return 401 if the authorisation fails" in new Setup {
      when(mockAuthConnector.authorise(*, eqTo(authProviderId and credentials and clientId and applicationName and applicationId))
      (*, *)).thenReturn(failed(InvalidBearerToken()))

      val result = underTest.handleBruce()(FakeRequest())

      status(result) shouldBe UNAUTHORIZED
      val jsonResult: JsValue = contentAsJson(result)
      (jsonResult \ "errorMessage").as[String] shouldBe "Invalid bearer token"
    }
  }
}
