package com.learn.college.common.config;

public class EnumConstants {

  /** Enum representing environment variables in the config file */
  public enum Environment {
    LOCAL("local"),
    TEST("test");

    private final String env;

    Environment(final String env) {
      this.env = env;
    }

    @Override
    public String toString() {
      return env;
    }
  }

  /** Enum representing the type of resource used in resource table */
  public enum RESOURCE_TYPE {
    API("API"),
    WEB("WEB");

    private final String value;

    RESOURCE_TYPE(final String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  /** Enum representing User Roles */
  public enum ROLES {
    ADMIN(1),
    TEACHER(2),
    STUDENT(3);

    private final int value;

    ROLES(final int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  /** Health Procedure */
  public enum HEALTH_PROCEDURE {
    DB_CONNECTION_STATUS,
    DISK_SPACE_AVAILABILITY_STATUS
  }
}
