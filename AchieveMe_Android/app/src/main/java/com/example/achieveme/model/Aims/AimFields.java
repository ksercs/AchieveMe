package com.example.achieveme.model.Aims;


import com.example.achieveme.model.Description.Description;

public class AimFields {

    private String name;
    private String deadline;
    private boolean is_important = false;
    private boolean is_completed;
    private int list_id;
    private int parent_id;
    private String image;
    private Description description;

    public String getName() {
        return name;
    }

    public String getDeadline() {
        return deadline;
    }

    public boolean isIs_important() {
        return is_important;
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

    public int getParent_id() {
        return parent_id;
    }

    public String getImage() {
        return image;
    }

    public Description getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIs_important(boolean is_important) {
        this.is_important = is_important;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public AimFields(String name, String deadline) {
        this.name = name;
        this.deadline = deadline;
    }
}
