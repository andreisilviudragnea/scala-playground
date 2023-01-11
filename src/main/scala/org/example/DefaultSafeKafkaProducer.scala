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

import cats.implicits.catsSyntaxEq
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord, RecordMetadata}

import java.util.Properties
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{CompletableFuture, ExecutorService, Executors, ThreadFactory}
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}
import scala.jdk.DurationConverters.ScalaDurationOps

object DefaultSafeKafkaProducer {
  def make[K, V](
      properties: Properties,
      topic: String,
      producerCloseTimeout: FiniteDuration
  )(implicit logger: Logger): CompletableFuture[SafeKafkaProducer[K, V]] = {
    val executorService = Executors.newSingleThreadExecutor(new ThreadFactory {
      private val threadNumber = new AtomicInteger(1)

      /** Customized from
        * [[java.util.concurrent.Executors.DefaultThreadFactory]]
        */
      @SuppressWarnings(Array("DisableSyntax.null"))
      override def newThread(r: Runnable): Thread = {
        val t = new Thread(
          null,
          r,
          s"kafka-producer-sender-${threadNumber.getAndIncrement()}",
          0
        )
        if (t.isDaemon) {
          t.setDaemon(false)
        }
        if (t.getPriority =!= Thread.NORM_PRIORITY) {
          // $COVERAGE-OFF$
          t.setPriority(Thread.NORM_PRIORITY)
          // $COVERAGE-ON$
        }
        t
      }
    })
    CompletableFuture
      .supplyAsync(
        { () =>
          val producer = new KafkaProducer[K, V](properties)
          logger.info(
            s"Partitions for topic $topic: ${producer.partitionsFor(topic)}"
          )
          producer
        },
        executorService
      )
      .thenApply {
        DefaultSafeKafkaProducer(executorService, _, producerCloseTimeout)
      }
  }
}

final case class DefaultSafeKafkaProducer[K, V](
    executorService: ExecutorService,
    producer: Producer[K, V],
    producerCloseTimeout: FiniteDuration
) extends SafeKafkaProducer[K, V] {

  def send(record: ProducerRecord[K, V]): Future[RecordMetadata] = {
    val promise = Promise[RecordMetadata]()
    executorService.execute { () =>
      producer.send(
        record,
        (recordMetadata: RecordMetadata, exception: Exception) =>
          Option(exception) match {
            case None            => promise.success(recordMetadata)
            case Some(exception) =>
              // $COVERAGE-OFF$
              promise.failure(exception)
            // $COVERAGE-ON$
          }
      )
      ()
    }
    promise.future
  }

  override def close(): Unit = {
    producer.close(producerCloseTimeout.toJava)
    executorService.shutdown()
  }
}
