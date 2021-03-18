package com.learnVertx.api.api.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Book {
    private static final AtomicInteger COUNTER = new AtomicInteger();
    private final int id;
    private String name;
    private String author;
    private int price;

    public Book(String name, String author, int price) {
        this.id = COUNTER.getAndIncrement();
        this.name = name;
        this.author = author;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getPrice() {
        return price;
    }

    public Book setName(String name) {
        this.name = name;
        return this;
    }

    public Book setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Book setPrice(int price) {
        this.price = price;
        return this;
    }
}
