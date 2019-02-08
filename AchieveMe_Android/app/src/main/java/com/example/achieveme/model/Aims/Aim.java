package com.example.achieveme.model.Aims;

public class Aim {

    private String name;
    private String date;
    private String time;
    private String description;

    private int id;

    public Aim(String name, String date, String time, String description) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public int getId() {
        return id;
    }
}
