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

package uk.gov.hmrc.apiplatformtest.controllers

import controllers.AssetsBuilder
import javax.inject.{Inject, Singleton}
import play.api.http.{ContentTypes, HeaderNames, HttpErrorHandler}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.apiplatformtest.config.{ApiAccess, AppContext}
import uk.gov.hmrc.apiplatformtest.views.txt
import uk.gov.hmrc.play.bootstrap.controller.BaseController

@Singleton
class DocumentationController @Inject()(httpErrorHandler: HttpErrorHandler, appContext: AppContext)
  extends AssetsBuilder(httpErrorHandler) with BaseController {

  def definition = Action {
    Ok(txt.definition(ApiAccess.build(appContext.access)))
      .withHeaders(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON)
  }

  def raml(version: String, file: String): Action[AnyContent] = {
    super.at(s"/public/api/conf/$version", file)
  }
}

