package com.example.achieveme.model.Aims;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import java8.util.Optional;


public class AimRes {

    @SerializedName("pk")
    private int aim_id;

    private AimFields fields;

    private Optional<List<AimRes>> subaims;

    public AimFields getFields() {
        return fields;
    }

    public int getId() {
        return aim_id;
    }

    public Optional<List<AimRes>> getSubaims() {
        return subaims;
    }
}
