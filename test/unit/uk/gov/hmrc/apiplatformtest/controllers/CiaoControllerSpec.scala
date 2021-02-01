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

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import uk.gov.hmrc.apiplatformtest.models.DummyAnswer
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatDummyAnswer
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CiaoControllerSpec extends UnitSpec with WithFakeApplication with StubControllerComponentsFactory {

  trait Setup{
    val underTest = new CiaoController(stubControllerComponents())
  }

  private implicit val materializer = fakeApplication.materializer

  "CiaoController" should {

    // Successful routes.

    "accept the resource `GET /` " in new Setup {

      val request = FakeRequest(method = "GET", path = "/")

      val result = underTest.handleEmpty()(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(uri = "/", method = "GET", resourceDetails = "Ciao Empty!")
      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `GET /ciao?surname=Kennedy&firstName=John&middleName=Fitzgerald` " in new Setup {

      val request = FakeRequest(
        method = "GET",
        path = "/ciao?surname=Kennedy&firstName=John&middleName=Fitzgerald"
      )

      val result = underTest.handleCiao(
        surname = Some("Kennedy"),
        firstName = Some("John"),
        middleName = Some("Fitzgerald")
      )(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/ciao?surname=Kennedy&firstName=John&middleName=Fitzgerald",
        method = "GET",
        resourceDetails = "Ciao: Some(Kennedy), Some(John), Some(Fitzgerald)"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `GET /ciao/Rossi` " in new Setup {

      val request = FakeRequest(method = "GET", path = "/ciao/Rossi")

      val result = underTest.handleCiaoSurname(surname = "Rossi")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(uri = "/ciao/Rossi", method = "GET", resourceDetails = "Ciao Surname: Rossi")
      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `GET /ciao/Rossi/Valentino?middleName=Alvise` " in new Setup {

      val request = FakeRequest(method = "GET", path = "/ciao/Rossi/Valentino?middleName=Alvise")

      val result = underTest.handleCiaoFullName(
        surname = "Rossi",
        firstName = "Valentino",
        middleName = Some("Alvise")
      )(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/ciao/Rossi/Valentino?middleName=Alvise",
        method = "GET",
        resourceDetails = "Ciao Full Name: Rossi, Valentino, Some(Alvise)"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    // Expected failing routes.

    "fail on the resource `GET /ciao/Kennedy/John/Fitzgerald` " in new Setup {

      val request = FakeRequest(method = "GET", path = "/ciao/Kennedy/John/Fitzgerald")

      val result = underTest.handleNotImplemented()(request)

      status(result) shouldBe Status.NOT_IMPLEMENTED

      val expectedAnswer = DummyAnswer(
        uri = "/ciao/Kennedy/John/Fitzgerald",
        method = "GET",
        resourceDetails = "Ciao Not Implemented!"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }
  }

}
