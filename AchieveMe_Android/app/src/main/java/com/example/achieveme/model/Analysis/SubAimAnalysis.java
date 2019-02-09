package com.example.achieveme.model.Analysis;

public class SubAimAnalysis extends AimAnalysis {
    int parent_id;

    public SubAimAnalysis(String text, int parent_id) {
        super(text);
        this.parent_id = parent_id;
    }
}
