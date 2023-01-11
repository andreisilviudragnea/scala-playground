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

import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import org.apache.kafka.common.TopicPartition

import java.util.concurrent.ConcurrentLinkedQueue
import scala.concurrent.Future

class SafeKafkaProducerMock[K, V] extends SafeKafkaProducer[K, V] {
  private val records = new ConcurrentLinkedQueue[ProducerRecord[K, V]]()

  override def send(record: ProducerRecord[K, V]): Future[RecordMetadata] = {
    records.offer(record)
    Future.successful(
      new RecordMetadata(new TopicPartition("", 0), 0, 0, 0, 0, 0)
    )
  }

  override def close(): Unit = {
    records.clear()
  }

  def getRecords: Array[ProducerRecord[K, V]] =
    records.toArray(new Array[ProducerRecord[K, V]](0))
}
