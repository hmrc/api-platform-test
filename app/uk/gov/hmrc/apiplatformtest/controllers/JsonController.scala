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

import javax.inject.{Inject, Singleton}

import akka.stream.Materializer
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import uk.gov.hmrc.apiplatformtest.services.HashingAlgorithm
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future._

@Singleton
class JsonController @Inject()(implicit mat: Materializer) extends BaseController {
  import JsonController._

  final def handleJsonPost(): Action[JsValue] = {
    Action.async(BodyParsers.parse.json) { implicit request =>
      render.async {
        case AcceptsJson50() => successful(Ok(Json.toJson(request.body)))
        case _               => successful(UnsupportedMediaType)
      }
    }
  }
  
  final def handleNRSJsonPost(): Action[String] = {
    Action(BodyParsers.parse.tolerantText) { implicit request =>
      val hash = HashingAlgorithm.sha256Hash(request.body)
      Ok(Json.obj("hash" -> hash))
    }
  }
}

object JsonController {
  val VndHmrcJson50: String = "application/vnd.hmrc.5.0+json"
  val AcceptsJson50 = Accepting(VndHmrcJson50)
}
