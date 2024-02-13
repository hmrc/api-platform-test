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

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import uk.gov.hmrc.auth.core.UnsupportedAuthProvider
import uk.gov.hmrc.util.AsyncHmrcSpec

class PrivilegedApiControllerSpec extends AsyncHmrcSpec with AuthTestSupport with StubControllerComponentsFactory {

  private val requestIdHeader = "X-REQUEST-ID"
  private val clientIdHeader  = "x_Client_id"
  private val dummyHeader     = "DUMMY"

  trait Setup {
    val underTest = new PrivilegedApiController(mockAuthConnector, stubControllerComponents())
  }

  // implicit val timeout = org.apache.pekko.util.Timeout(defaultTimeout)
  // GET        /privileged
  "PrivilegedApiController" should {
    val request = FakeRequest("GET", "/privileged")
      .withHeaders(
        dummyHeader        -> dummyHeader,
        requestIdHeader    -> "1234",
        clientIdHeader     -> "ABCD",
        HeaderNames.ACCEPT -> MimeTypes.JSON
      )

    "handle privileged apps correctly" in new Setup {
      withAuthorizedUser()
      val result = underTest.handlePrivilegedAccess()(request)
      status(result) shouldBe Status.OK

      contentAsString(result) shouldBe "{\"message\":\"Request coming from a privileged application\"}"
    }

    "handle authorisation errors correctly" in new Setup {
      withUnauthorizedUser(UnsupportedAuthProvider())
      val result = underTest.handlePrivilegedAccess()(request)
      status(result) shouldBe Status.FORBIDDEN

      contentAsString(result) shouldBe "{\"message\":\"Only privileged applications can access this endpoint\"}"
    }
  }
}
