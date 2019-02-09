package com.example.achieveme.model.Lists;

import com.google.gson.annotations.SerializedName;

public class ListRes {

    @SerializedName("pk")
    private int list_id;
    private ListFields fields;

    public ListFields getFields() {
        return fields;
    }

    public int getId() {
        return list_id;
    }

    public ListRes(int list_id, ListFields fields) {
        this.list_id = list_id;
        this.fields = fields;
    }
}
