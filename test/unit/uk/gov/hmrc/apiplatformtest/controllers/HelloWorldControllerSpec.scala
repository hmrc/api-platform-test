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
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class HelloWorldControllerSpec extends UnitSpec with WithFakeApplication {

  val fakeRequest = FakeRequest("GET", "/")
  val RequestId = "X-REQUEST-ID"
  val ClientId = "x_Client_id"

  "GET /" should {
    "return 200" in {
      val result = HelloController.handle()(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "GET /hello/world" should {
    val idsInRequest = FakeRequest("GET", "/hello/world").withHeaders(RequestId -> "1234", ClientId -> "ABCD" )
    "return expected headers" in {
      val result = HelloController.handle()(idsInRequest)
      status(result) shouldBe Status.OK
      result.header.headers.keySet should contain (RequestId)
      result.header.headers.keySet should contain (ClientId)
    }
  }
}
