/*
 * Copyright 2020 HM Revenue & Customs
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

import java.util.UUID
import java.util.UUID.randomUUID

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.apiplatformtest.services.NotificationsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NotificationsController @Inject()(cc: ControllerComponents, notificationsService: NotificationsService)
                                       (implicit val ec: ExecutionContext) extends BackendController(cc) {

  def triggerNotification(): Action[AnyContent] = Action.async { implicit request =>
    request.headers.get("X-Client-ID") match {
      case Some(clientId) =>
        notificationsService.getBox(clientId) map { boxId =>
          val correlationId = randomUUID
          runAsyncProcess(boxId, correlationId)
          // Return result without waiting for the async process future to complete
          Ok(Json.obj("boxId" -> boxId, "correlationId" -> correlationId))
        }
      case _ =>
        Logger.error("X-Client-ID is missing")
        successful(Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError)))
    }
  }

  private def runAsyncProcess(boxId: UUID, correlationId: UUID)(implicit hc: HeaderCarrier): Future[String] = {
    // Here we would run some asynchronous process, and then save the notification
    Future {
      Thread.sleep(1000)
    } flatMap { _ =>
      notificationsService.saveNotification(boxId, Json.obj("correlationId" -> correlationId, "message" -> "test message"))
    }
  }
}
