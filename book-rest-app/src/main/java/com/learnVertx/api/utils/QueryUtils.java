package com.learnVertx.api.utils;

public class QueryUtils {
    public static final String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE IF NOT EXISTS Books (id int PRIMARY KEY AUTO_INCREMENT, name varchar(255), author varchar(255), price int(11))";
    public static final String SQL_SELECT_ALL_BOOKS = "SELECT * FROM BOOKS";
    public static final String SQL_SELECT_ONE_BOOK = "SELECT * FROM BOOKS WHERE ID = ?";
    public static final String SQL_INSERT_ONE_BOOK = "INSERT INTO BOOKS(name, author, price) VALUES (?, ?, ?)";
    public static final String SQL_UPDATE_ONE_BOOK = "UPDATE BOOKS SET PRICE = ? WHERE ID = ?";
    public static final String SQL_DELETE_ONE_BOOK = "DELETE FROM BOOKS WHERE ID = ?";
}
