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

import play.api.http.ContentTypes.JSON
import play.api.http.HeaderNames.{ACCEPT, CONTENT_TYPE}
import play.api.http.Status.{OK, UNSUPPORTED_MEDIA_TYPE}
import play.api.libs.json.Json
import play.api.libs.json.Json.parse
import play.api.test.FakeRequest
import uk.gov.hmrc.apiplatformtest.controllers.JsonController._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class JsonControllerSpec extends UnitSpec with WithFakeApplication {

  implicit private val mat = fakeApplication.materializer

  private val payload = """{"firstName":"Alvise","surname":"Fransescoli","dateOfBirth":"04-01-1731"}"""
  private val request = FakeRequest("POST", "/json").withBody(parse(payload))

  "POST /json with good json" should {

    "return 200 with expected response body" in {
      val req = request.withHeaders(
        CONTENT_TYPE -> JSON,
        ACCEPT -> VndHmrcJson50)

      val result = await(handleJsonPost()(req))

      status(result) shouldBe OK
      bodyOf(result) shouldBe payload
    }

    "return 415 when using a wrong Accept header" in {
      val req = request.withHeaders(
        CONTENT_TYPE -> JSON,
        ACCEPT -> JSON)

      val result = await(handleJsonPost()(req))

      status(result) shouldBe UNSUPPORTED_MEDIA_TYPE
      bodyOf(result) shouldBe ""
    }
  }

  "POST to NRS endpoint returns valid SHA-256 without effecting the payload" should {
    "returns 93A23971A914E5EACBF0A8D25154CDA309C3C1C72FBB9914D47C60F3CB681588 from hello world" in {
      val req = FakeRequest("POST", "/nrs-json")
        .withBody("""{"hello":"world"}""")
        .withHeaders(
          CONTENT_TYPE -> "text/plain",
          ACCEPT -> VndHmrcJson50)
      
      val result = await(handleNRSJsonPost()(req))
      
  
      status(result) shouldBe OK
      (Json.parse(bodyOf(result)) \ "hash").validate[String].get shouldBe "93A23971A914E5EACBF0A8D25154CDA309C3C1C72FBB9914D47C60F3CB681588"
    }
  
    "returns 3DC606AA0263D3EBC41F34C55C5545A6C346FD4B47DB2AC746DADF7F958201E5 from hello world with spacing" in {
      val req = FakeRequest("POST", "/nrs-json")
          .withBody(""" { "hello" : "world" } """)
          .withHeaders(
                        CONTENT_TYPE -> "text/plain",
                        ACCEPT -> VndHmrcJson50)
    
      val result = await(handleNRSJsonPost()(req))
    
    
      status(result) shouldBe OK
      val hash = (Json.parse(bodyOf(result)) \ "hash").validate[String].get
      hash should not be "93A23971A914E5EACBF0A8D25154CDA309C3C1C72FBB9914D47C60F3CB681588"
      hash shouldBe "3DC606AA0263D3EBC41F34C55C5545A6C346FD4B47DB2AC746DADF7F958201E5"
    }

  }
}
