/*
 * Copyright 2019 HM Revenue & Customs
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

import akka.stream.Materializer
import org.joda.time.LocalDate
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.apiplatformtest.models.Header
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters._
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.{AgentInformation, Credentials, Name, ~}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future.{failed, successful}

class HelloWorldControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  trait Setup {
    implicit val mat: Materializer = fakeApplication.materializer
    implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val helloController: HelloController = new HelloController {
      override val authConnector: AuthConnector = mockAuthConnector
    }
  }

  "GET /" should {
    "return 200" in new Setup {
      private val result = helloController.handle()(FakeRequest("GET", "/"))
      status(result) shouldBe OK
    }
  }

  "Hello Dave" should {
    "return 200 with the user details and request headers" in new Setup {
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
      private val unreadMessageCount = 1
      private val headers: Seq[(String, String)] = Seq("User-Agent" -> "api-platform-test", "Client-ID" -> randomUUID.toString)
      when(
        mockAuthConnector
          .authorise(
            any(),
            ArgumentMatchers.eq(allUserDetails)
          )(any(), any())
      ).thenReturn(
        successful(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(new ~(Some(credentials), Some(name)),
          Some(dateOfBirth)), Some(postCode)), Some(email)), Some(affinityGroup)), Some(agentCode)),
          agentInformation), Some(credentialRole)), Some(description)), Some(groupIdentifier)), Some(unreadMessageCount))
        )
      )

      val result: Result = await(helloController.handleDave()(FakeRequest().withHeaders(headers: _*)))

      status(result) shouldBe OK
      val jsonResult: JsValue = jsonBodyOf(result)
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
      (jsonResult \ "unreadMessageCount").as[Int] shouldBe unreadMessageCount
      (jsonResult \ "headers").as[Seq[Header]] shouldBe headers.map(h => Header(h._1, h._2))
    }

    "return 401 if the authorisation fails" in new Setup {
      when(
        mockAuthConnector
          .authorise(
            any(),
            ArgumentMatchers.eq(allUserDetails)
          )(any(), any())
      ).thenReturn(failed(InvalidBearerToken()))

      val result: Result = await(helloController.handleDave()(FakeRequest()))

      status(result) shouldBe UNAUTHORIZED
      val jsonResult: JsValue = jsonBodyOf(result)
      (jsonResult \ "errorMessage").as[String] shouldBe "Invalid bearer token"
    }
  }
}
