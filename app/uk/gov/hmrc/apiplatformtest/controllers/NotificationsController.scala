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
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future.successful
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.after

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, PlayBodyParsers}
import uk.gov.hmrc.apiplatformtest.services.NotificationsService
import uk.gov.hmrc.apiplatformtest.utils.ApplicationLogger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class NotificationsController @Inject() (
    cc: ControllerComponents,
    parsers: PlayBodyParsers,
    actorSystem: ActorSystem,
    notificationsService: NotificationsService
  )(implicit val ec: ExecutionContext
  ) extends BackendController(cc) with ApplicationLogger {

  def triggerNotification(): Action[String] = {
    Action.async(parsers.tolerantText) { implicit request =>
      val message = if (request.hasBody) {
        logger.info(s"/notifications endpoint: Received notification with payload")
        request.body
      } else "test message"

      def runAsyncProcess(boxId: UUID, correlationId: UUID)(implicit hc: HeaderCarrier): Future[String] = {
        // Here we would run some asynchronous process, and then save the notification
        val delay   = FiniteDuration(1, TimeUnit.SECONDS)
        after(delay, actorSystem.scheduler)(successful(())) flatMap { _ =>
          notificationsService.saveNotification(boxId, Json.obj("correlationId" -> correlationId, "message" -> message))
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
  }

  def handleNotificationPush(status: Option[Int], delayInSeconds: Option[Int]): Action[String] = Action.async(parsers.tolerantText) { implicit request =>
    lazy val MaxBodyLength: Int = 10240
    val bodyToLog               = if (request.body.length > MaxBodyLength) request.body.substring(0, MaxBodyLength) + "...(truncated to 10kb)" else request.body
    logger.info(s"/destination/notifications endpoint: Received notification with payload '${bodyToLog}' and headers '${request.headers.toMap}'")
    val delay                   = FiniteDuration(delayInSeconds.getOrElse(0).toLong, TimeUnit.SECONDS)
    after(delay, actorSystem.scheduler)(successful(new Status(status.getOrElse(OK))))
  }

  def callbackValidation(challenge: String): Action[AnyContent] = Action.async { _ =>
    successful(Ok(Json.obj("challenge" -> challenge)))
  }
}
