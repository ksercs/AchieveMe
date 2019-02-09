package com.example.achieveme.model.Aims;

import com.google.gson.annotations.SerializedName;

public class Aim {

    private String name;
    private String date;
    private String time;
    private String description;
    private int parent_id;

    @SerializedName("pk")
    private int id;

    public Aim(String name, String date, String time, String description) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getId() {
        return id;
    }
}
