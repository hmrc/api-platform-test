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

import akka.stream.Materializer
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class HeadersControllerSpec extends UnitSpec with WithFakeApplication {

  private implicit lazy val materializer: Materializer = fakeApplication.materializer

  private val requestIdHeader = "X-REQUEST-ID"
  private val clientIdHeader = "x_Client_id"
  private val dummyHeader = "DUMMY"

  "GET /headers" should {

    val request = FakeRequest("GET", "/headers")
      .withHeaders(
        dummyHeader -> dummyHeader,
        requestIdHeader -> "1234",
        clientIdHeader -> "ABCD",
        HeaderNames.ACCEPT -> MimeTypes.JSON)

    "return expected headers" in {
      val result = await(HeadersController.handle()(request))
      status(result) shouldBe Status.OK

      val responseBody = jsonBodyOf(result).toString()
      responseBody shouldBe s"""{"request":{"name":"$requestIdHeader","value":"1234"},"client":{"name":"$clientIdHeader","value":"ABCD"}}"""
      responseBody should not contain dummyHeader

      val headersInTheResponse = result.header.headers.keySet
      headersInTheResponse should contain (requestIdHeader)
      headersInTheResponse should contain (clientIdHeader)
      headersInTheResponse should not contain dummyHeader
    }

  }

}
