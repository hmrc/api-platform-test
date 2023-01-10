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

import uk.gov.hmrc.auth.core.AuthConnector

import org.mockito.MockitoSugar
import org.mockito.ArgumentMatchersSugar
import scala.concurrent.Future.{successful, failed}

trait AuthTestSupport extends MockitoSugar with ArgumentMatchersSugar {

  lazy val mockAuthConnector: AuthConnector = mock[AuthConnector]

  //TODO add predicate for privileged user authProviders

  def withAuthorizedUser(): Unit =
    when(mockAuthConnector.authorise[Unit](*, *)(*,*)).thenReturn(successful(()))

  def withUnauthorizedUser(error: Throwable): Unit =
    when(mockAuthConnector.authorise(*, *)(*, *)).thenReturn(failed(error))

}