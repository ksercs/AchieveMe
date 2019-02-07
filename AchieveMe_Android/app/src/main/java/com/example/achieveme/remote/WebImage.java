package com.example.achieveme.remote;

import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.net.URL;

public class WebImage {

    public static Drawable LoadImage(String url) {
        try {
            InputStream in = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(in, "pic");
        } catch (Throwable t) {
            return null;
        }

    }
}
