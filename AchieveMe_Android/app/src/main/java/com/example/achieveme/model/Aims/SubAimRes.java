package com.example.achieveme.model.Aims;

import com.google.gson.annotations.SerializedName;

public class SubAimRes {
    @SerializedName("pk")
    private int aim_id;

    private AimFields fields;

    public AimFields getFields() {
        return fields;
    }

    public int getId() {
        return aim_id;
    }
}
