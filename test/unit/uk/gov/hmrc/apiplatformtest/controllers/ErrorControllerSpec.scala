/*
 * Copyright 2020 HM Revenue & Customs
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

import akka.stream.Materializer
import play.api.http.Status.TOO_MANY_REQUESTS
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class ErrorControllerSpec extends UnitSpec with WithFakeApplication {

  trait Setup{
    implicit val materializer: Materializer = fakeApplication.materializer
    val request = FakeRequest()
    val underTest = new ErrorController
  }

  "returnError" should {
    "return the error that was passed in" in new Setup {
      val result: Result = await(underTest.returnError(TOO_MANY_REQUESTS)(request))

      status(result) shouldBe TOO_MANY_REQUESTS
      (jsonBodyOf(result) \ "code").as[String] shouldBe "ERROR_429"
      (jsonBodyOf(result) \ "message").as[String] shouldBe "Generate a 429 error"
    }
  }
}
