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

package uk.gov.hmrc.apiplatformtest.models

import play.api.data.validation.ValidationError
import play.api.libs.json._
import uk.gov.hmrc.auth.core.retrieve.{AgentInformation, Credentials, Name}

object JsonFormatters {

  implicit val formatDummyAnswer = Json.format[DummyAnswer]
  implicit val formatNoFraudAnswer = Json.format[NoFraudAnswer]
  implicit val formatPrivilegedAccessAnswer = Json.format[PrivilegedAccessAnswer]
  implicit val formatName = Json.format[Name]
  implicit val formatCredentials = Json.format[Credentials]
  implicit val formatAgentInformation = Json.format[AgentInformation]

  implicit def tuple2Reads[A, B, C](implicit aReads: Reads[A], bReads: Reads[B]): Reads[(A, B)] = Reads[(A, B)] {
    case JsArray(arr) if arr.size == 2 => for {
      a <- aReads.reads(arr.head)
      b <- bReads.reads(arr(1))
    } yield (a, b)
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected array of two elements"))))
  }

  implicit def tuple2Writes[A, B](implicit aWrites: Writes[A], bWrites: Writes[B]): Writes[(A, B)] = new Writes[(A, B)] {
    def writes(tuple: (A, B)) = JsArray(Seq(aWrites.writes(tuple._1), bWrites.writes(tuple._2)))
  }
}
