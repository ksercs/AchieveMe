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


    public SubAimRes(int aim_id, AimFields fields) {
        this.aim_id = aim_id;
        this.fields = fields;
    }

    public SubAimRes() {
    }

}