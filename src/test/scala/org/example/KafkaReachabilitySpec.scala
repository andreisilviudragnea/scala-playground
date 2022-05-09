/*
 * Copyright 2022 Andrei Silviu Dragnea
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

package org.example

import cats.effect.IO._
import org.apache.kafka.clients.admin.{Admin, AdminClientConfig}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import java.util.Properties

class KafkaReachabilitySpec extends AnyFunSuite with should.Matchers {
  ignore("kafka") {
    val clientSettings = new Properties()
    clientSettings.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "")
    val client = Admin.create(clientSettings)

    val topicNames = client.listTopics().names().get()

    println(topicNames)

    client.close()
  }
}
