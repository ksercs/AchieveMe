package com.example.achieveme.model.Aims;

import android.util.EventLogTags;

import com.example.achieveme.model.Description.Description;
import com.google.gson.annotations.SerializedName;

public class AimFields {

    private String name;
    private String deadline;
    private boolean is_imortant;
    private boolean is_remind;
    private boolean is_completed;
    private int list_id;
    private String image;
    private Description description;

    public String getName() {
        return name;
    }

    public String getDeadline() {
        return deadline;
    }

    public boolean isIs_imortant() {
        return is_imortant;
    }

    public boolean isIs_remind() {
        return is_remind;
    }

    public boolean isIs_completed() {
        return is_completed;
    }

    public void setIs_completed(boolean is_completed) {
        this.is_completed = is_completed;
    }

    public int getList_id() {
        return list_id;
    }

    public String getImage() {
        return image;
    }

    public Description getDescription() {
        return description;
    }

    public AimFields(String name, String deadline) {
        this.name = name;
        this.deadline = deadline;
    }
}
