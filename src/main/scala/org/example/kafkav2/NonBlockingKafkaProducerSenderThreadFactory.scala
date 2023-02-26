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

import cats.implicits.catsSyntaxEq

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

object NonBlockingKafkaProducerSenderThreadFactory extends ThreadFactory {
  private val threadNumber = new AtomicInteger(1)

  /** Customized from java.util.concurrent.Executors.DefaultThreadFactory
    */
  @SuppressWarnings(Array("DisableSyntax.null", "NullParameter"))
  override def newThread(r: Runnable): Thread = {
    val t = new Thread(
      null,
      r,
      s"kafka-producer-sender-${threadNumber.getAndIncrement()}",
      0
    )

    // $COVERAGE-OFF$
    if (t.isDaemon) {
      t.setDaemon(false)
    }
    // $COVERAGE-ON$

    if (t.getPriority =!= Thread.NORM_PRIORITY) {
      // $COVERAGE-OFF$
      t.setPriority(Thread.NORM_PRIORITY)
      // $COVERAGE-ON$
    }

    t
  }
}
