/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.test.FakeRequest
import uk.gov.hmrc.apiplatformtest.models.CiaoAnswer
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatCiaoAnswer
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CiaoControllerSpec extends UnitSpec with WithFakeApplication {

  private implicit val materializer = fakeApplication.materializer

  "CiaoController" should {

    // Successful routes.

    "accept the resource `GET /` " in {

      val request = FakeRequest(method = "GET", path = "/")

      val result = CiaoController.handleEmpty()(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = CiaoAnswer(uri = "/", resourceDetails = "Ciao Empty!")
      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `GET /ciao?surname=Kennedy&firstName=John&middleName=Fitzgerald` " in {

      val request = FakeRequest(
        method = "GET",
        path = "/ciao?surname=Kennedy&firstName=John&middleName=Fitzgerald"
      )

      val result = CiaoController.handleCiao(
        surname = Some("Kennedy"),
        firstName = Some("John"),
        middleName = Some("Fitzgerald")
      )(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = CiaoAnswer(
        uri = "/ciao?surname=Kennedy&firstName=John&middleName=Fitzgerald",
        resourceDetails = "Ciao: Some(Kennedy), Some(John), Some(Fitzgerald)"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `GET /ciao/rossi` " in {

      val request = FakeRequest(method = "GET", path = "/ciao/rossi")

      val result = CiaoController.handleCiaoSurname(surname = "rossi")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = CiaoAnswer(uri = "/ciao/rossi", resourceDetails = "Ciao Surname: rossi")
      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `GET /ciao/rossi/valentino?middleName=alvise` " in {

      val request = FakeRequest(method = "GET", path = "/ciao/rossi/valentino?middleName=alvise")

      val result = CiaoController.handleCiaoFullName(
        surname = "rossi",
        firstName = "valentino",
        middleName = Some("alvise")
      )(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = CiaoAnswer(
        uri = "/ciao/rossi/valentino?middleName=alvise",
        resourceDetails = "Ciao Full Name: rossi, valentino, Some(alvise)"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    // Expected failing routes.

    "fail on the resource `GET /ciao/kennedy/john/fitzgerald` " in {

      val request = FakeRequest(method = "GET", path = "/ciao/kennedy/john/fitzgerald")

      val result = CiaoController.handleNotImplemented()(request)

      status(result) shouldBe Status.NOT_IMPLEMENTED

      val expectedAnswer = CiaoAnswer(
        uri = "/ciao/kennedy/john/fitzgerald",
        resourceDetails = "Ciao Not Implemented!"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }
  }

}
