package com.example.achieveme;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.widget.ImageView;

public class DrawProgressBar {
    public static void circularImageBar(ImageView iv2, int cur, int all) {

        int i = (int) (cur * 100. / all);
        Bitmap b = Bitmap.createBitmap(300, 300,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();

        paint.setColor(Color.parseColor("#c4c4c4"));
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(150, 150, 140, paint);
        paint.setColor(Color.parseColor("#8b00ff"));
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.FILL);
        final RectF oval = new RectF();
        paint.setStyle(Paint.Style.STROKE);
        oval.set(10,10,290,290);
        canvas.drawArc(oval, 270, ((i*360)/100), false, paint);
        paint.setStrokeWidth(0);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.parseColor("#8E8E93"));
        paint.setTextSize(140);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setStyle(Paint.Style.FILL);
        String progress = "" + cur + "/" + all;
        paint.setTextSize(paint.getTextSize() * 3 / progress.length());
        canvas.drawText(progress, 150, 150+(paint.getTextSize()/3), paint);
        iv2.setImageBitmap(b);
    }
}
