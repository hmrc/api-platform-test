/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.Future._

@Singleton
class JsonController @Inject()(cc: ControllerComponents, parsers: PlayBodyParsers) extends BackendController(cc) {

  val VndHmrcJson50: String = "application/vnd.hmrc.5.0+json"
  val AcceptsJson50 = Accepting(VndHmrcJson50)

  final def handleJsonPost(): Action[JsValue] = {
    Action.async(parsers.json) { implicit request =>
      render.async {
        case AcceptsJson50() => successful(Ok(Json.toJson(request.body)))
        case _               => successful(UnsupportedMediaType)
      }
    }
  }
}

