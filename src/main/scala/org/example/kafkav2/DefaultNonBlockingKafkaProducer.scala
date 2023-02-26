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

import cats.effect.IO
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.{Callback, Producer, ProducerRecord}
import org.example.kafkav2.DefaultNonBlockingKafkaProducer.State

import java.util.concurrent.{ExecutorService, Executors}
import scala.util.{Failure, Success, Try}

object DefaultNonBlockingKafkaProducer {

  def make[K, V](
      producerFactory: () => Producer[K, V]
  )(implicit logger: Logger): NonBlockingKafkaProducer[K, V] = {
    new DefaultNonBlockingKafkaProducer(
      Executors.newSingleThreadExecutor(
        NonBlockingKafkaProducerSenderThreadFactory
      ),
      State.UninitializedProducer[K, V](producerFactory)
    )
  }

  sealed trait State[K, V]

  object State {
    final case class UninitializedProducer[K, V](
        producerFactory: () => Producer[K, V]
    ) extends State[K, V] // TODO: Add maxRetryCount and resend records accumulated during producer creation failures

    final case class ProducerCreationError[K, V](throwable: Throwable)
        extends State[K, V]

    final case class InitializedProducer[K, V](producer: Producer[K, V])
        extends State[K, V]
  }
}

class DefaultNonBlockingKafkaProducer[K, V](
    singleThreadExecutorService: ExecutorService,
    initialState: State[K, V]
)(implicit
    logger: Logger
) extends NonBlockingKafkaProducer[K, V] {
  @SuppressWarnings(Array("DisableSyntax.var"))
  private var state = initialState

  override def init(): IO[Unit] = {
    IO.async_ { k =>
      singleThreadExecutorService.execute { () =>
        state match {
          case State.UninitializedProducer(producerFactory) =>
            Try(producerFactory()) match {
              case Failure(throwable) =>
                k(Left(throwable))
                state = State.ProducerCreationError(throwable)
              case Success(producer) =>
                state = State.InitializedProducer(producer)
                k(Right(()))
            }
          case State.ProducerCreationError(throwable) => k(Left(throwable))
          case State.InitializedProducer(_)           => k(Right(()))
        }
      }
    }
  }

  @SuppressWarnings(
    Array(
      "DisableSyntax.null",
      "DisableSyntax.asInstanceOf",
      "NullParameter",
      "AsInstanceOf"
    )
  )
  override def send(
      producerRecord: ProducerRecord[K, V],
      callback: Callback
  ): Unit = {
    singleThreadExecutorService.execute { () =>
      state match {
        case State.UninitializedProducer(producerFactory) =>
          Try(producerFactory()) match {
            case Failure(throwable) =>
              logger.warn(s"DroppedRecord=$producerRecord")
              callback.onCompletion(null, throwable.asInstanceOf[Exception])
              state = State.ProducerCreationError(throwable)
            case Success(producer) =>
              producer.send(producerRecord, callback)
              state = State.InitializedProducer(producer)
          }
        case State.ProducerCreationError(throwable) =>
          logger.warn(s"DroppedRecord=$producerRecord")
          callback.onCompletion(null, throwable.asInstanceOf[Exception])
        case State.InitializedProducer(producer) =>
          producer.send(producerRecord, callback)
          ()
      }
    }
  }

  override def close(): Unit = {
    singleThreadExecutorService.execute { () =>
      state match {
        case State.UninitializedProducer(_)      => ()
        case State.ProducerCreationError(_)      => ()
        case State.InitializedProducer(producer) =>
          // TODO: Add logging here
          producer.close()
      }
    }
    singleThreadExecutorService.shutdown()
  }
}
