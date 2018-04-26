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

import akka.util.ByteString
import play.api.Logger
import play.api.http.{ContentTypes, Status}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.FakeRequest
import play.mvc.Http
import play.mvc.Http.MimeTypes
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.{Await, Future}
import scala.xml.NodeSeq
import scala.concurrent.duration._

class XmlControllerSpec extends UnitSpec with WithFakeApplication {

  implicit val mat = fakeApplication.materializer

  val requestBody: NodeSeq = <Pong>hello</Pong>

  "POST /xml with good xml" should {
    "return 200" in {
      val fakeRequest = FakeRequest("POST", "/xml")
        .withHeaders(Http.HeaderNames.CONTENT_TYPE -> ContentTypes.XML, Http.HeaderNames.ACCEPT -> XmlController.VndHmrcXml5)
        .withBody(requestBody)

      val result: Future[Result] = XmlController.handleXmlPost().apply(fakeRequest)
      status(await(result)(5 seconds)) shouldBe Status.OK
    }
  }
}
