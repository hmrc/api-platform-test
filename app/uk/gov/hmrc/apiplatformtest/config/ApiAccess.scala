/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.apiplatformtest.config

import play.api.Configuration
import play.api.libs.json.{Json, OFormat}

case class ApiAccess(`type`: String, whitelistedApplicationIds: Option[Seq[String]])

object ApiAccess {
  def build(config: Option[Configuration])(version: String): ApiAccess = ApiAccess(
    `type` = config.flatMap(_.getOptional[String](s"version-$version.type")).getOrElse("PRIVATE"),
    whitelistedApplicationIds = config.flatMap(_.getOptional[Seq[String]](s"version-$version.whitelistedApplicationIds")).orElse(Some(Seq.empty)))

  implicit val apiAccessFmt: OFormat[ApiAccess] = Json.format[ApiAccess]
}