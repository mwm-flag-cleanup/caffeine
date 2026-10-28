/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
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
package com.github.benmanes.caffeine.cache.simulator.fm;

import java.util.Map;

import io.rollout.context.Context;
import io.rollout.context.RoxContext;
import io.rollout.rox.server.Rox;

/**
 * Logs a diagnostic line about how large a trace sample the reporting flags would allow, for a
 * trace with the given event count. Reads {@code reporting.maxTraceSampleSize} through the
 * dynamic API, independently of {@link SimulationSummary}'s typed access to the same flag.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class TraceSampleDiagnostics {

  private TraceSampleDiagnostics() {}

  /** Logs the effective trace sample size that would be used for a trace of the given size. */
  public static void logEffectiveSampleSize(long traceEventCount) {
    Context context = new RoxContext.Builder().from(Map.of("traceEventCount", traceEventCount));
    int maxSampleSize = Rox.dynamicAPI().getInt(
        "reporting.maxTraceSampleSize", 1000, new int[] {}, context);
    long effective = Math.min(maxSampleSize, traceEventCount);
    System.out.printf("Trace sample size: %d of %d events.%n", effective, traceEventCount);
  }
}
