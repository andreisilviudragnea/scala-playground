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

package org.example.kafkav2

import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.{
  KafkaProducer,
  Producer,
  ProducerConfig
}

import java.util.Properties

class DefaultKafkaProducerFactory[K, V](implicit
    logger: Logger,
    timeProvider: Time
) extends KafkaProducerFactory[K, V] {

  override def createProducer(
      properties: Properties,
      topics: Seq[String]
  ): Producer[K, V] = {
    val bootstrapServersConfig =
      properties.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)

    logger.info(
      s"Creating KafkaProducer for bootstrapServersConfig=$bootstrapServersConfig"
    )

    val startTime = timeProvider.currentTimeMillis()

    val producer = new KafkaProducer[K, V](properties)

    topics.foreach { producer.partitionsForTopic(_, bootstrapServersConfig) }

    logger.info(
      s"Created and initialized KafkaProducer for bootstrapServersConfig=$bootstrapServersConfig " +
        s"in initTimeMs=${timeProvider.currentTimeMillis() - startTime}"
    )

    producer
  }

  implicit class ProducerOps(producer: Producer[K, V]) {
    @SuppressWarnings(Array("CatchThrowable", "DisableSyntax.throw"))
    def partitionsForTopic(
        topic: String,
        bootstrapServersConfig: String
    ): Unit = {
      val partitionsForTopic =
        try {
          producer.partitionsFor(topic)
        } catch {
          case throwable: Throwable =>
            logger.error(
              s"partitionsFor($topic) error=${throwable.getMessage} bootstrapServersConfig=$bootstrapServersConfig"
            )
            logger.info(
              s"Closing producer for bootstrapServersConfig=$bootstrapServersConfig"
            )
            val closeMillis = timed(producer.close())
            logger.info(
              s"Closed producer for bootstrapServersConfig=$bootstrapServersConfig in closeMillis=$closeMillis"
            )
            throw throwable
        }

      logger.info(
        s"Topic=$topic has numPartitions=${partitionsForTopic.size()} for bootstrapServersConfig=$bootstrapServersConfig"
      )
    }
  }
}
