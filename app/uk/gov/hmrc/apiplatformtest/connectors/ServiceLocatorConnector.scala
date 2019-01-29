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

package uk.gov.hmrc.apiplatformtest.connectors

import play.api.Logger
import play.api.http.ContentTypes.JSON
import play.api.libs.json.Json
import play.mvc.Http.HeaderNames.CONTENT_TYPE
import uk.gov.hmrc.apiplatformtest.config.{AppContext, WSHttp}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.{ HeaderCarrier, HttpPost }

case class Registration(serviceName: String, serviceUrl: String, metadata:Option[Map[String, String]] = None)

object Registration {
  implicit val format = Json.format[Registration]
}

trait ServiceLocatorConnector {
  val appName: String
  val appUrl: String
  val serviceUrl: String
  val handlerOK: () => Unit
  val handlerError: Throwable => Unit
  val metadata: Option[Map[String, String]]
  val http: HttpPost

  def register(implicit hc: HeaderCarrier): Future[Boolean] = {
    val registration = Registration(appName, appUrl, metadata)
    http.POST(s"$serviceUrl/registration", registration, Seq(CONTENT_TYPE -> JSON)) map { _ =>
      handlerOK()
      true
    } recover {
      case e: Throwable =>
        handlerError(e)
        false
    }
  }

}

object ServiceLocatorConnector extends ServiceLocatorConnector {
  override lazy val appName = AppContext.appName
  override lazy val appUrl = AppContext.appUrl
  override lazy val serviceUrl = AppContext.serviceLocatorUrl
  override val http: HttpPost = WSHttp
  override val handlerOK: () => Unit = () => Logger.info("Service is registered on the service locator")
  override val handlerError: Throwable => Unit = e => Logger.error(s"Service could not register on the service locator", e)
  override val metadata: Option[Map[String, String]] = Some(Map("third-party-api" -> "true"))
}
