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

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

import controllers.Assets
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.after

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import uk.gov.hmrc.apiplatformtest.config.AppContext

@Singleton
class AssetsController @Inject() (cc: ControllerComponents, actorSystem: ActorSystem, appContext: AppContext, assets: Assets)(implicit val ec: ExecutionContext)
    extends BackendController(cc) {

  def at(file: String): Action[AnyContent] = Action.async { implicit request =>
    after(appContext.assetsDelay, actorSystem.scheduler) {
      assets.at(file)(request)
    }
  }
}
