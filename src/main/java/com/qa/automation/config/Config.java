package com.qa.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

  private static final String DEFAULT_FILE = "config.properties";
  private static final String LOCAL_FILE = "config-local.properties";

  private static final Properties PROPS = new Properties();

  static {
    load(DEFAULT_FILE, true);
    load(LOCAL_FILE, false);
  }

  private Config() {}

  private static void load(String fileName, boolean required) {
    try (InputStream in = Config.class.getClassLoader().getResourceAsStream(fileName)) {
      if (in == null) {
        if (required) {
          throw new IllegalStateException("Cannot find " + fileName + " on classpath");
        }
        return;
      }
      PROPS.load(in);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot load " + fileName, e);
    }
  }

  public static String get(String key) {
    String fromSystem = System.getProperty(key);
    if (fromSystem != null) {
      return fromSystem;
    }

    String fromEnv = System.getenv(key.toUpperCase().replace('.', '_'));
    if (fromEnv != null) {
      return fromEnv;
    }

    return PROPS.getProperty(key);
  }

  public static String getRequired(String key) {
    String value = get(key);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(
          "Missing config value: "
              + key
              + " (set -D"
              + key
              + ", env "
              + key.toUpperCase().replace('.', '_')
              + ", or add it to "
              + LOCAL_FILE
              + ")");
    }
    return value;
  }

  public static int getInt(String key, int defaultValue) {
    String value = get(key);
    if (value == null || value.isBlank()) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Config value " + key + " is not a number: " + value, e);
    }
  }
}
