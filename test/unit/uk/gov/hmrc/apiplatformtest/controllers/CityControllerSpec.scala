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

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import uk.gov.hmrc.apiplatformtest.models.DummyAnswer
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatDummyAnswer
import uk.gov.hmrc.util.AsyncHmrcSpec


class CityControllerSpec extends AsyncHmrcSpec with StubControllerComponentsFactory {

  trait Setup{
    val underTest = new CityController(stubControllerComponents())
  }

  "CityController" should {

    // tests for the `/city-details/:cityName/address` URL

    "accept the resource `GET /city-details/Leeds/address` " in new Setup {

      val request = FakeRequest(method = "GET", path = "/city-details/Leeds/address")

      val result = underTest.showCityAndAddress("Leeds")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/city-details/Leeds/address",
        method = "GET",
        resourceDetails = "City: Leeds, Address: Oxford Street"
      )

      contentAsJson(result) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `PUT /city-details/Leeds/address` " in new Setup {

      val request = FakeRequest(method = "PUT", path = "/city-details/Leeds/address")

      val result = underTest.showCityAndAddress("Leeds")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/city-details/Leeds/address",
        method = "PUT",
        resourceDetails = "City: Leeds, Address: Oxford Street"
      )

      contentAsJson(result) shouldBe Json.toJson(expectedAnswer)
    }

    // tests for the `/city-details/:cityName/:postcode` URL

    "accept the resource `GET /city-details/London/SW208HR` " in new Setup {

      val request = FakeRequest(method = "GET", path = "/city-details/London/SW208HR")

      val result = underTest.showCityAndPostcode("London", "SW208HR")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/city-details/London/SW208HR",
        method = "GET",
        resourceDetails = "City: London, Postcode: SW208HR"
      )

      contentAsJson(result) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `POST /city-details/London/SW208HR` " in new Setup {

      val request = FakeRequest(method = "POST", path = "/city-details/London/SW208HR")

      val result = underTest.showCityAndPostcode("London", "SW208HR")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/city-details/London/SW208HR",
        method = "POST",
        resourceDetails = "City: London, Postcode: SW208HR"
      )

      contentAsJson(result) shouldBe Json.toJson(expectedAnswer)
    }

    "accept the resource `DELETE /city-details/London/SW208HR` " in new Setup {

      val request = FakeRequest(method = "DELETE", path = "/city-details/London/SW208HR")

      val result = underTest.showCityAndPostcode("London", "SW208HR")(request)

      status(result) shouldBe Status.OK

      val expectedAnswer = DummyAnswer(
        uri = "/city-details/London/SW208HR",
        method = "DELETE",
        resourceDetails = "City: London, Postcode: SW208HR"
      )

      contentAsJson(result) shouldBe Json.toJson(expectedAnswer)
    }

  }

}
