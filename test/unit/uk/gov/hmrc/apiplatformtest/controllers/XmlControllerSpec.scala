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

import play.api.http.ContentTypes.XML
import play.api.http.HeaderNames.{ACCEPT, CONTENT_TYPE}
import play.api.http.Status.{OK, UNSUPPORTED_MEDIA_TYPE}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.xml.NodeSeq

class XmlControllerSpec extends UnitSpec with WithFakeApplication {

  trait Setup{
    val underTest = new XmlController
  }

  implicit private val mat = fakeApplication.materializer

  private val requestBody: NodeSeq = <Pong>hello</Pong>
  private val request = FakeRequest("POST", "/xml").withBody(requestBody)

  "POST /xml with good xml" should {

    "return 200 with expected response body" in new Setup {
      val req = request.withHeaders(
        CONTENT_TYPE -> XML,
        ACCEPT -> underTest.VndHmrcXml50)

      val result = await(underTest.handleXmlPost().apply(req))

      status(result) shouldBe OK
      bodyOf(result) shouldBe <Ping><Pong>hello</Pong></Ping>.toString()
    }

    "return 415 when using a wrong Accept header" in new Setup {
      val req = request.withHeaders(
        CONTENT_TYPE -> XML,
        ACCEPT -> XML)

      val result = await(underTest.handleXmlPost()(req))

      status(result) shouldBe UNSUPPORTED_MEDIA_TYPE
      bodyOf(result) shouldBe ""
    }

  }

}
