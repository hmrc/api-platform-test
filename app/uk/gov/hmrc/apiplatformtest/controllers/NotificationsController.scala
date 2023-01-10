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

import java.util.UUID
import java.util.UUID.randomUUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.pattern.after
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, PlayBodyParsers}
import uk.gov.hmrc.apiplatformtest.services.NotificationsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.apiplatformtest.utils.ApplicationLogger

import scala.concurrent.Future.successful
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NotificationsController @Inject() (
    cc: ControllerComponents,
    parsers: PlayBodyParsers,
    actorSystem: ActorSystem,
    notificationsService: NotificationsService
  )(implicit val ec: ExecutionContext
  ) extends BackendController(cc) with ApplicationLogger {

  def triggerNotification(): Action[AnyContent] = Action.async { implicit request =>
    def runAsyncProcess(boxId: UUID, correlationId: UUID)(implicit hc: HeaderCarrier): Future[String] = {
      // Here we would run some asynchronous process, and then save the notification
      val delay = FiniteDuration(1, TimeUnit.SECONDS)
      after(delay, actorSystem.scheduler)(successful(())) flatMap { _ =>
        notificationsService.saveNotification(boxId, Json.obj("correlationId" -> correlationId, "message" -> "test message"))
      }
    }

    request.headers.get("X-Client-ID") match {
      case Some(clientId) =>
        notificationsService.getBox(clientId) map { boxId =>
          val correlationId = randomUUID
          runAsyncProcess(boxId, correlationId)
          // Return result without waiting for the async process future to complete
          Ok(Json.obj("boxId" -> boxId, "correlationId" -> correlationId))
        }
      case _              =>
        logger.error("X-Client-ID is missing")
        successful(Status(ErrorInternalServerError.httpStatusCode)(Json.toJson(ErrorInternalServerError)))
    }
  }

  def handleNotificationPush(status: Option[Int], delayInSeconds: Option[Int]): Action[String] = Action.async(parsers.tolerantText) { implicit request =>
    val bodyToLog = if (request.body.length > 10240) request.body.substring(0, 10240) + "...(truncated to 10kb)" else request.body
    logger.info(s"Received notification with payload '${bodyToLog}' and headers '${request.headers.toMap}'")
    val delay     = FiniteDuration(delayInSeconds.getOrElse(0), TimeUnit.SECONDS)
    after(delay, actorSystem.scheduler)(successful(new Status(status.getOrElse(OK))))
  }

  def callbackValidation(challenge: String): Action[AnyContent] = Action.async { _ =>
    successful(Ok(Json.obj("challenge" -> challenge)))
  }
}
