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

import play.api.http.ContentTypes.JSON
import play.api.http.HeaderNames.{ACCEPT, CONTENT_TYPE}
import play.api.http.Status.{OK, UNSUPPORTED_MEDIA_TYPE}
import play.api.libs.json.Json.parse
import play.api.test.{FakeRequest, StubBodyParserFactory, StubControllerComponentsFactory}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class JsonControllerSpec extends UnitSpec with WithFakeApplication with StubControllerComponentsFactory with StubBodyParserFactory {

  trait Setup{
    implicit val mat = fakeApplication.materializer
    val underTest = new JsonController(stubControllerComponents(), stubPlayBodyParsers)
  }


  private val payload = """{"firstName":"Alvise","surname":"Fransescoli","dateOfBirth":"04-01-1731"}"""
  private val request = FakeRequest("POST", "/json").withBody(parse(payload))

  "POST /json with good json" should {

    "return 200 with expected response body" in new Setup {
      val req = request.withHeaders(
        CONTENT_TYPE -> JSON,
        ACCEPT -> underTest.VndHmrcJson50)

      val result = await(underTest.handleJsonPost()(req))

      status(result) shouldBe OK
      bodyOf(result) shouldBe payload
    }

    "return 415 when using a wrong Accept header" in new Setup {
      val req = request.withHeaders(
        CONTENT_TYPE -> JSON,
        ACCEPT -> JSON)

      val result = await(underTest.handleJsonPost()(req))

      status(result) shouldBe UNSUPPORTED_MEDIA_TYPE
      bodyOf(result) shouldBe ""
    }
  }

}
