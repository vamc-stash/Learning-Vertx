package com.learn.college.common.config;

/** A class responsible for test application.json file */
public class ConfigUtil {

  // system property keys
  private static final String CONFIG_PATH = "config.path";
  private static final String SWAGGER_LOCATION = "swagger.location";
  private static final String LOG4J_CONFIG_FILE_PATH = "log4j.configurationFile";

  // system property values
  private static final String CONFIG_PATH_VALUE = "src/test/resources/";
  private static final String SWAGGER_LOCATION_VALUE = "src/test/resources/swagger.yaml";
  private static final String LOG4J_CONFIG_FILE_PATH_VALUE = "src/test/resources/log4j2.xml";

  private ConfigUtil() {}

  /** sets System Properties */
  public static void loadSystemProperties() {
    System.setProperty(CONFIG_PATH, CONFIG_PATH_VALUE);
    System.setProperty(SWAGGER_LOCATION, SWAGGER_LOCATION_VALUE);
    System.setProperty(LOG4J_CONFIG_FILE_PATH, LOG4J_CONFIG_FILE_PATH_VALUE);
  }
}
