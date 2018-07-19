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
import uk.gov.hmrc.fraudprevention.headervalidators.impl.GovClientPublicPortHeaderValidator
import uk.gov.hmrc.fraudprevention.model.{ErrorConversion, ErrorResponse}
import uk.gov.hmrc.apiplatformtest.models.JsonFormatters.formatNoFraudAnswer
import uk.gov.hmrc.apiplatformtest.models.NoFraudAnswer
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class FraudPreventionControllerSpec extends UnitSpec with WithFakeApplication with ErrorConversion {

  private implicit val materializer = fakeApplication.materializer

  "FraudPreventionController" should {

    lazy val request = FakeRequest()

    "block requests missing required headers" in {
      val result = FraudPreventionController.handleFraud()(request)
      status(result) shouldBe Status.PRECONDITION_FAILED
      val expectedAnswer = ErrorResponse("Missing or invalid headers: Gov-Client-Public-Port")
      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "block requests with unexpected values for required headers" in {
      val result = FraudPreventionController.handleFraud()(request.withHeaders(GovClientPublicPortHeaderValidator.headerName -> "-1"))
      status(result) shouldBe Status.PRECONDITION_FAILED
      val expectedAnswer = ErrorResponse("Missing or invalid headers: Gov-Client-Public-Port")
      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

    "proxy requests with valid values for required headers" in {
      val result = FraudPreventionController.handleFraud()(request.withHeaders(GovClientPublicPortHeaderValidator.headerName -> "12345"))
      status(result) shouldBe Status.OK
      val expectedAnswer = NoFraudAnswer("All required headers have been sent correctly in the request.")
      await(jsonBodyOf(result)) shouldBe Json.toJson(expectedAnswer)
    }

  }

}
