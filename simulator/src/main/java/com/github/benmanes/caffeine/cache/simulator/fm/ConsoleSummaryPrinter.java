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

import io.rollout.rox.server.Rox;

/**
 * A minimal console notice printed after a run completes. This was added independently of
 * {@link SimulationSummary} and so reads {@code reporting.outputFormat} through the dynamic API
 * rather than through the typed {@link FeatureFlags#REPORTING_FLAGS} container.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class ConsoleSummaryPrinter {

  private ConsoleSummaryPrinter() {}

  /** Prints a one-line notice naming the output format the summary was written in. */
  public static void printCompletionNotice() {
    String raw = Rox.dynamicAPI().getValue(
        FeatureFlags.FLAG_OUTPUT_FORMAT, "CSV", new String[] {"CSV", "JSON", "TABLE"});
    OutputFormat format = OutputFormat.valueOf(raw);
    System.out.println("Simulation summary written in " + format + " format.");
  }
}
