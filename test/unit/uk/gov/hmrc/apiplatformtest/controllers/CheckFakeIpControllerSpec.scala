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

import play.api.http.{HeaderNames, Status}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CheckFakeIpControllerSpec extends UnitSpec with WithFakeApplication {

  val fakeRequest = FakeRequest("GET", "/")

  "GET /" should {
    "return 200" in {
      val result = CheckFakeIpController.handle()(fakeRequest)
      status(result) shouldBe Status.OK
      result.header.headers.keys.exists(_ == HeaderNames.LOCATION)
    }
  }
}
