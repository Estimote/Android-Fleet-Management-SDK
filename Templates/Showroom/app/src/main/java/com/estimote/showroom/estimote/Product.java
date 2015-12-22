package com.estimote.showroom.estimote;

public class Product {

    private String name;
    private String summary;

    public Product(String name, String summary) {
        this.name = name;
        this.summary = summary;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }
}
