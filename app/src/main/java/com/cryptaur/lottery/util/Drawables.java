package com.cryptaur.lottery.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.TextPaint;

public class Drawables {

    public static Drawable createDrawableWithText(Resources resources, String text, int bgResId, int textColor) {
        Drawable drw = VectorDrawableCompat.create(resources, bgResId, null);
        int width = drw.getIntrinsicWidth();
        int height = drw.getIntrinsicHeight();
        drw.setBounds(0, 0, width, height);
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        drw.draw(c);
        TextPaint tp = new TextPaint();
        tp.setTextSize(11 * resources.getDisplayMetrics().density);
        tp.setColor(textColor);
        float textWidth = tp.measureText(text);
        Rect rc = new Rect();
        tp.getTextBounds(text, 0, text.length(), rc);
        c.drawText(text, (width - rc.width()) / 2 - rc.left, (height + rc.height()) / 2, tp);
        android.graphics.drawable.Drawable drw2 = new BitmapDrawable(resources, bm);
        drw2.setBounds(0, 0, drw.getIntrinsicWidth(), drw.getIntrinsicHeight());
        return drw2;
    }
}
