package utils;

public class QueryUtils {
    public static String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "username VARCHAR(255) PRIMARY KEY NOT NULL, " +
            "password VARCHAR(255) NOT NULL" +
            ")";
    public static String CREATE_ROLE_TABLE = "CREATE TABLE IF NOT EXISTS roles (" +
            "role VARCHAR(255) PRIMARY KEY NOT NULL" +
            ")";
    public static String CREATE_USER_ROLE_TABLE = "CREATE TABLE IF NOT EXISTS users_roles (" +
            "username VARCHAR(255) NOT NULL, " +
            "role VARCHAR(255) NOT NULL, " +
            "PRIMARY KEY (username, role), " +
            "FOREIGN KEY (username) REFERENCES users(username), " +
            "FOREIGN KEY (role) REFERENCES roles(role)"+
            ")";
    //public static String INSERT_PERMITTED_ROLES = "INSERT INTO roles (role) VALUES (ROLE_ADMIN), (ROLE_USER)"; //insert through console
    public static String ADD_USER = "INSERT INTO users (username, password) VALUES (?, ?)";
    public static String GET_USER = "SELECT * FROM users WHERE username = ?";
    public static String SELECT_ONE_USER = "SELECT * FROM users WHERE username = ? AND password = ?";
    public static String ADD_USER_ROLE = "INSERT INTO users_roles (username, role) VALUES (?, ?)";
    public static String GET_USER_ROLE = "SELECT * FROM roles WHERE role = ?";
}
