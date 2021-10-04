package org.example

import cats.effect.IO._
import org.apache.kafka.clients.admin.{Admin, AdminClientConfig}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should

import java.util.Properties

class KafkaReachabilitySpec extends AnyFunSuite with should.Matchers {
  test("kafka") {
    val clientSettings = new Properties()
    clientSettings.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "")
    val client = Admin.create(clientSettings)

    val topicNames = client.listTopics().names().get()

    println(topicNames)

    client.close()
  }
}
