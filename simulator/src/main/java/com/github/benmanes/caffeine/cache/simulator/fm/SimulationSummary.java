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

import java.util.List;
import java.util.Map;

import io.rollout.context.Context;
import io.rollout.context.RoxContext;

import com.github.benmanes.caffeine.cache.simulator.policy.PolicyStats;

/**
 * Prints a short feature-flagged summary of the simulation results, in addition to the
 * configured {@link com.github.benmanes.caffeine.cache.simulator.report.Reporter}. Both the
 * output format and the sample size are evaluated from the same {@link Context}, since they're
 * both relevant to the same reporting decision.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class SimulationSummary {

  private SimulationSummary() {}

  /** Prints a short summary of the results, in the format selected by the reporting flags. */
  public static void print(List<PolicyStats> results) {
    Context context = new RoxContext.Builder().from(Map.of("resultCount", results.size()));
    OutputFormat format = FeatureFlags.REPORTING_FLAGS.outputFormat.getValue(context);
    int sampleSize = FeatureFlags.REPORTING_FLAGS.maxTraceSampleSize.getValue(context);

    var sample = results.stream().limit(Math.min(sampleSize, results.size())).toList();
    switch (format) {
      case CSV -> printCsv(sample);
      case JSON -> printJson(sample);
      case TABLE -> printTable(sample);
    }
  }

  private static void printCsv(List<PolicyStats> sample) {
    for (var stats : sample) {
      System.out.println(stats.name() + "," + stats.hitRate());
    }
  }

  private static void printJson(List<PolicyStats> sample) {
    for (var stats : sample) {
      System.out.printf("{\"policy\":\"%s\",\"hitRate\":%s}%n", stats.name(), stats.hitRate());
    }
  }

  private static void printTable(List<PolicyStats> sample) {
    for (var stats : sample) {
      System.out.printf("%-30s %8.4f%n", stats.name(), stats.hitRate());
    }
  }
}
