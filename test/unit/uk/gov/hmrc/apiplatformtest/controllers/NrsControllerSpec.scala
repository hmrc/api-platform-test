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

import play.api.http.ContentTypes.TEXT
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.Status.OK
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.test.{FakeRequest, StubControllerComponentsFactory, StubPlayBodyParsersFactory}
import uk.gov.hmrc.util.AsyncHmrcSpec
import akka.stream.testkit.NoMaterializer


class NrsControllerSpec extends AsyncHmrcSpec with StubControllerComponentsFactory with StubPlayBodyParsersFactory{

  implicit private val mat = NoMaterializer

  private val controller = new NrsController(stubControllerComponents(), stubPlayBodyParsers)

  "POST to NRS endpoint returns valid SHA-256 without effecting the payload" should {

    val request = FakeRequest("POST", "/nrs")
    val plainTextRequest = request.withHeaders(CONTENT_TYPE -> TEXT)

    "return the expected hash when sending a trimmed JSON payload" in {

      val req = plainTextRequest.withBody("""{"hello":"world"}""")

      val result = controller.handleNrsPost()(req)

      status(result) shouldBe OK

      val hash: String = extractHashFromJsonResponse(contentAsJson(result).toString())
      hash shouldBe "93A23971A914E5EACBF0A8D25154CDA309C3C1C72FBB9914D47C60F3CB681588"
    }

    "return the expected hash when sending a JSON payload with spaces" in {

      val req = plainTextRequest.withBody(""" { "hello" : "world" } """)

      val result = controller.handleNrsPost()(req)

      status(result) shouldBe OK

      val hash: String = extractHashFromJsonResponse(contentAsJson(result).toString())
      hash should not be "93A23971A914E5EACBF0A8D25154CDA309C3C1C72FBB9914D47C60F3CB681588"
      hash shouldBe "3DC606AA0263D3EBC41F34C55C5545A6C346FD4B47DB2AC746DADF7F958201E5"
    }

    "return the expected when sending a trimmed XML payload" in {

      val req = plainTextRequest.withBody("<Country>Guatemala</Country>")

      val result = controller.handleNrsPost()(req)

      status(result) shouldBe OK

      val hash: String = extractHashFromJsonResponse(contentAsString(result).toString())
      hash shouldBe "ADBBFE9C486272D641B216E8A18A79D8DA58E89CBB513ED317BA89A75D3C770A"
    }

    "return the expected when sending an XML payload with spaces" in {

      val req = plainTextRequest.withBody(" <Country> Guatemala </Country> ")

      val result = controller.handleNrsPost()(req)

      status(result) shouldBe OK

      val hash: String = extractHashFromJsonResponse(contentAsJson(result).toString)

      hash should not be "ADBBFE9C486272D641B216E8A18A79D8DA58E89CBB513ED317BA89A75D3C770A"
      hash shouldBe "872D370F3927C1736CD598B2A43D89CF1DC9B5F2C6C6287A4728BCBCF4DBCA85"
    }

  }

  private def extractHashFromJsonResponse(response: String): String = {
    (Json.parse(response) \ "hash").validate[String].get
  }

}
