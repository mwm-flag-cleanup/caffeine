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

import java.util.logging.Logger;

import io.rollout.configuration.RoxContainer;
import io.rollout.flags.RoxEnum;
import io.rollout.flags.RoxFlag;
import io.rollout.flags.RoxInt;
import io.rollout.rox.server.Rox;
import io.rollout.rox.server.RoxOptions;

/**
 * CloudBees Feature Management (Rox) flags controlling optional simulator behavior. This is
 * wired up in the {@code simulator} module only, not the core {@code caffeine} cache library,
 * since the simulator is a standalone runnable benchmarking tool rather than a dependency that
 * ships to every consumer of the cache.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class FeatureFlags {
  private static final Logger logger = Logger.getLogger(FeatureFlags.class.getName());

  /** The dynamic-API key for {@link ReportingFlags#outputFormat}. */
  public static final String FLAG_OUTPUT_FORMAT = "reporting.outputFormat";

  /** The simulator-wide feature flags, registered under the default namespace. */
  public static final SimulatorFlags FLAGS = new SimulatorFlags();

  /** The reporting feature flags, registered under the {@code reporting} namespace. */
  public static final ReportingFlags REPORTING_FLAGS = new ReportingFlags();

  private FeatureFlags() {}

  /** Registers the flag containers and starts the SDK, if an API key is configured. */
  public static void init() {
    Rox.register(FLAGS);
    Rox.register("reporting", REPORTING_FLAGS);

    Rox.setCustomStringProperty("simulator.javaVendor", System.getProperty("java.vendor"));
    Rox.setCustomComputedIntegerProperty("simulator.availableProcessors",
        () -> Runtime.getRuntime().availableProcessors());

    String sdkKey = System.getenv("FM_SDK_KEY");
    if ((sdkKey == null) || sdkKey.isEmpty()) {
      logger.info("FM_SDK_KEY environment variable not set. Feature flags not initialised.");
      return;
    }
    var _ = Rox.setup(sdkKey, new RoxOptions.Builder().build());
  }

  /** Simulator flags registered under the default ("") namespace. */
  public static final class SimulatorFlags implements RoxContainer {
    /** Whether policies are evaluated concurrently rather than one at a time. */
    public final RoxFlag enableParallelPolicyEvaluation = new RoxFlag(false);
  }

  /** Flags registered under the {@code reporting} namespace. */
  public static final class ReportingFlags implements RoxContainer {
    /** The format used when printing the feature-flagged simulation summary. */
    public final RoxEnum<OutputFormat> outputFormat = new RoxEnum<>(OutputFormat.CSV);

    /** The maximum number of trace events sampled when building the simulation summary. */
    public final RoxInt maxTraceSampleSize = new RoxInt(1000);
  }
}
