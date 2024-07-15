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

import scala.xml.NodeSeq

import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.testkit.NoMaterializer

import play.api.http.ContentTypes.XML
import play.api.http.HeaderNames.{ACCEPT, CONTENT_TYPE}
import play.api.http.Status.{OK, UNSUPPORTED_MEDIA_TYPE}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, StubControllerComponentsFactory, StubPlayBodyParsersFactory}
import uk.gov.hmrc.util.AsyncHmrcSpec

class XmlControllerSpec extends AsyncHmrcSpec with StubControllerComponentsFactory with StubPlayBodyParsersFactory {

  implicit val mat: Materializer = NoMaterializer

  trait Setup {
    val underTest = new XmlController(stubControllerComponents(), stubPlayBodyParsers)
  }

  private val requestBody: NodeSeq = <Pong>hello</Pong>
  private val request              = FakeRequest("POST", "/xml").withBody(requestBody)

  "POST /xml with good xml" should {

    "return 200 with expected response body" in new Setup {
      val req = request.withHeaders(
        CONTENT_TYPE -> XML,
        ACCEPT       -> underTest.VndHmrcXml50
      )

      val result = underTest.handleXmlPost()(req)

      status(result) shouldBe OK
      contentAsString(result) shouldBe <Ping><Pong>hello</Pong></Ping>.toString()
    }

    "return 415 when using a wrong Accept header" in new Setup {
      val req = request.withHeaders(
        CONTENT_TYPE -> XML,
        ACCEPT       -> XML
      )

      val result = underTest.handleXmlPost()(req)

      status(result) shouldBe UNSUPPORTED_MEDIA_TYPE
      contentAsString(result) shouldBe ""
    }

  }

}
