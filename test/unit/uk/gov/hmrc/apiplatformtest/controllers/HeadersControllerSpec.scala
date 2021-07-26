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

import play.api.test.Helpers._
import play.api.http.Status

import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.test.{FakeRequest, StubControllerComponentsFactory}

import uk.gov.hmrc.util.AsyncHmrcSpec


class HeadersControllerSpec extends AsyncHmrcSpec with StubControllerComponentsFactory {

  trait Setup{
    val underTest = new HeadersController(stubControllerComponents())
  }

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

    "return expected headers" in new Setup {
      val result = underTest.handle()(request)
      status(result) shouldBe Status.OK

      val responseBody = contentAsJson(result).toString()
      responseBody shouldBe s"""{"request":{"name":"$requestIdHeader","value":"1234"},"client":{"name":"$clientIdHeader","value":"ABCD"}}"""
      responseBody should not contain dummyHeader

      val actualHeaders = headers(result)
      actualHeaders should contain (requestIdHeader)
      actualHeaders should contain (clientIdHeader)
      actualHeaders should not contain dummyHeader
    }
  }
}
