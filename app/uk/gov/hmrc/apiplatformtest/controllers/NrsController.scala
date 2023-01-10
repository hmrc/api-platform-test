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

import play.api.libs.json._
import play.api.mvc._
import uk.gov.hmrc.apiplatformtest.services.HashingAlgorithm
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class NrsController @Inject() (cc: ControllerComponents, parsers: PlayBodyParsers) extends BackendController(cc) {

  final def handleNrsPost(): Action[String] = {
    Action(parsers.tolerantText) { implicit request =>
      val hash = HashingAlgorithm.sha256Hash(request.body)
      Ok(Json.obj("hash" -> hash))
    }
  }

}
