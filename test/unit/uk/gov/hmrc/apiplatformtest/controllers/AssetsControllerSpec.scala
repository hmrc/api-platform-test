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

import akka.actor.ActorSystem
import controllers.Assets
import org.joda.time.DateTime
import play.api.http.Status.OK
import play.api.mvc.Results.Ok
import play.api.mvc.{Action, ActionBuilder, AnyContent}
import play.api.test.{FakeRequest, StubControllerComponentsFactory}
import uk.gov.hmrc.apiplatformtest.config.AppContext
import play.api.test.Helpers._

import uk.gov.hmrc.util.AsyncHmrcSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future.successful
import scala.concurrent.duration._

class AssetsControllerSpec extends AsyncHmrcSpec with StubControllerComponentsFactory {
  implicit val actorSystemTest: ActorSystem = ActorSystem("test-actor-system")
  val fileName = "file.xml"
  val assetsAction: Action[AnyContent] = new ActionBuilder.IgnoringBody().async(successful(Ok("some stuff")))

  trait Setup {
    val mockAppContext: AppContext = mock[AppContext]
    val mockAssets: Assets = mock[Assets]
    when(mockAssets.at(fileName)).thenReturn(assetsAction)
    val underTest = new AssetsController(stubControllerComponents(), actorSystemTest, mockAppContext, mockAssets)
  }

  "at" should {
    val request = FakeRequest()

    "delay the response by the duration set in the config" in new Setup {
      when(mockAppContext.assetsDelay).thenReturn(FiniteDuration(2, "sec"))

      val before = new DateTime()
      val result = await(underTest.at(fileName)(request))
      val after = new DateTime()

      result.header.status shouldBe OK
      (after.getMillis - before.getMillis).toInt should be > 2000
    }
  }
}
