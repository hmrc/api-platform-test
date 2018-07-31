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
import uk.gov.hmrc.fraudprevention.headervalidators.impl._
import uk.gov.hmrc.fraudprevention.model.{ErrorConversion, ErrorResponse}
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatNoFraudAnswer
import uk.gov.hmrc.apiplatformtest.models.NoFraudAnswer
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class FraudPreventionControllerSpec extends UnitSpec with WithFakeApplication with ErrorConversion {

  private implicit val materializer = fakeApplication.materializer

  private lazy val fakeRequest = FakeRequest()

  private val controllerRouteFunctions = Map(
    "handleFraud()()" -> FraudPreventionController.handleFraud(),
    "handleFraudWithFilter()()" -> FraudPreventionController.handleFraudWithFilter())

  controllerRouteFunctions.foreach { f =>

    s"FraudPreventionController.${f._1}" should {

      "block requests missing required headers" in {
        val result = f._2(fakeRequest)
        status(result) shouldBe Status.PRECONDITION_FAILED
        val expectedAnswer = ErrorResponse(List("Header Gov-Client-Colour-Depth is missing", "Header Gov-Client-Public-Port is missing"))
        await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
      }

      "block requests with unexpected values for required headers" in {
        val request = fakeRequest.withHeaders(
          GovClientPublicPortHeaderValidator.headerName -> "-1",
          GovClientColourDepthHeaderValidator.headerName -> "12"
        )
        val result = f._2(request)
        status(result) shouldBe Status.PRECONDITION_FAILED
        val expectedAnswer = ErrorResponse(List("Invalid port number for header Gov-Client-Public-Port: -1"))
        await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
      }

      "block requests that has multiple values for required headers that must have one value only" in {
        val request = fakeRequest.withHeaders(
          GovClientPublicPortHeaderValidator.headerName -> "8080",
          GovClientPublicPortHeaderValidator.headerName -> "9000",
          GovClientColourDepthHeaderValidator.headerName -> "12"
        )
        val result = f._2(request)
        status(result) shouldBe Status.PRECONDITION_FAILED
        val expectedAnswer = ErrorResponse(List("Multiple values for header Gov-Client-Public-Port"))
        await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
      }

      "proxy requests that have valid values for all required headers" in {
        val request = fakeRequest.withHeaders(
          GovClientPublicPortHeaderValidator.headerName -> "21",
          GovClientColourDepthHeaderValidator.headerName -> "36"
        )
        val result = f._2(request)
        status(result) shouldBe Status.OK
        val expectedAnswer = NoFraudAnswer("All required headers have been sent correctly in the request.")
        await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
      }

    }

  }

}
