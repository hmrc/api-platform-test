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
import uk.gov.hmrc.apiplatformtest.models.DummyAnswer
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatDummyAnswer
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CityControllerSpec extends UnitSpec with WithFakeApplication {

  private implicit val materializer = fakeApplication.materializer

  "CityController" should {

    "accept the resource `GET /city-details/Leeds/address` " in {

      val request = FakeRequest(method = "GET", path = "/city-details/Leeds/address")

      val result = CityController.showCityAndAddress("Leeds")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/city-details/Leeds/address",
        method = "GET",
        resourceDetails = "City: Leeds, Address: Oxford Street"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `PUT /city-details/Leeds/address` " in {

      val request = FakeRequest(method = "PUT", path = "/city-details/Leeds/address")

      val result = CityController.showCityAndAddress("Leeds")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/city-details/Leeds/address",
        method = "PUT",
        resourceDetails = "City: Leeds, Address: Oxford Street"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `GET /city-details/London/SW208HR` " in {

      val request = FakeRequest(method = "GET", path = "/city-details/London/SW208HR")

      val result = CityController.showCityAndPostcode("London", "SW208HR")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/city-details/London/SW208HR",
        method = "GET",
        resourceDetails = "City: London, Postcode: SW208HR"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `POST /city-details/London/SW208HR` " in {

      val request = FakeRequest(method = "POST", path = "/city-details/London/SW208HR")

      val result = CityController.showCityAndPostcode("London", "SW208HR")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/city-details/London/SW208HR",
        method = "POST",
        resourceDetails = "City: London, Postcode: SW208HR"
      )

      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

  }

}
