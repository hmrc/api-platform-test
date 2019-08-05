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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.Future
import scala.util.Random
import uk.gov.hmrc.http.HeaderCarrier

/**
  * Used for API-2989 testing where we return a location header containing IP address which should be stripped out
  * of the header by API gateway
  */
trait CheckFakeIpController extends BaseController {

  implicit val hc: HeaderCarrier

  // Random generator
  private val random = new scala.util.Random

  // Generate a random string of length n from the given alphabet
  private def randomString(alphabet: String)(n: Int): String =
    Stream.continually(random.nextInt(alphabet.length)).map(alphabet).take(n).mkString

  // Generate a random hex string of length n
  private def randomHexString(n: Int) =
    randomString("0123456789abcdef")(n)

  private def fakeObjectId() = {
    randomHexString(24)
  }

  def handle: Action[AnyContent] = Action.async {
    def bit() = (Random.nextInt(250)+1).toString
    val fakeId = fakeObjectId()
    val fakeNino = Random.nextString(9)
    val fakeIp = s"${bit()}.${bit()}.${bit()}.${bit()}"
    Future.successful(Ok(Json.toJson("""{ "message": "CheckFakeIp" }"""))
      .withHeaders(LOCATION -> s"/self-assessment/ni/$fakeNino/self-employments/$fakeId"))
  }
}

object CheckFakeIpController extends CheckFakeIpController {
  override implicit val hc: HeaderCarrier = HeaderCarrier()
}
