package com.qa.automation.config;

import java.time.Duration;

public final class Timeouts {

  public static final Duration SHORT = Duration.ofSeconds(Config.getInt("timeout.short", 5));
  public static final Duration MEDIUM = Duration.ofSeconds(Config.getInt("timeout.medium", 10));
  public static final Duration LONG = Duration.ofSeconds(Config.getInt("timeout.long", 30));

  private Timeouts() {}
}
