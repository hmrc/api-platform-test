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

import play.api.mvc._

import scala.concurrent.Future.successful
import scala.xml.NodeSeq
import uk.gov.hmrc.http.HeaderCarrier

trait XmlController extends CommonController {

  import XmlController._

  final def handleXmlPost(): Action[NodeSeq] =
    Action.async(BodyParsers.parse.xml) {
      implicit request: Request[NodeSeq] =>
        render.async {
          case AcceptsXml50() => successful(Ok(<Ping>{request.body.head}</Ping>).as(XML))
          case _ => successful(UnsupportedMediaType)
        }
    }
}

object XmlController extends XmlController {
  override implicit val hc: HeaderCarrier = HeaderCarrier()

  val VndHmrcXml50: String = "application/vnd.hmrc.5.0+xml"
  val AcceptsXml50 = Accepting(VndHmrcXml50)
}
