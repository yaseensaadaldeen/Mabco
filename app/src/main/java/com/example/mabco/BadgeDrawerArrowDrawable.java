package com.example.mabco;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

public class BadgeDrawerArrowDrawable extends DrawerArrowDrawable {
    private final Paint backgroundPaint = new Paint();
    private final Paint textPaint = new Paint();
    private String text = null;
    private boolean enabled = true;

    // Fraction of the drawable's intrinsic size we want the badge to be.
    private static final float SIZE_FACTOR = .3f;
    private static final float HALF_SIZE_FACTOR = SIZE_FACTOR / 2;

    public BadgeDrawerArrowDrawable(Context context) {
        super(context);
        backgroundPaint.setColor(Color.RED);
        backgroundPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(SIZE_FACTOR * getIntrinsicHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (!enabled) {
            return;
        }
        Rect bounds = getBounds();
        float x = (1 - HALF_SIZE_FACTOR) * bounds.width();
        float y = HALF_SIZE_FACTOR * bounds.height();
        canvas.drawCircle(
                x,
                y,
                SIZE_FACTOR * bounds.width(),
                backgroundPaint
        );
        if (text == null || text.isEmpty()) {
            return;
        }
        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, x, y + textBounds.height() / 2, textPaint);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            invalidateSelf();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setText(String text) {
        if (this.text != text) {
            this.text = text;
            invalidateSelf();
        }
    }

    public String getText() {
        return text;
    }

    public int getBackgroundColor() {
        return backgroundPaint.getColor();
    }

    public void setBackgroundColor(int color) {
        if (backgroundPaint.getColor() != color) {
            backgroundPaint.setColor(color);
            invalidateSelf();
        }
    }

    public int getTextColor() {
        return textPaint.getColor();
    }

    public void setTextColor(int color) {
        if (textPaint.getColor() != color) {
            textPaint.setColor(color);
            invalidateSelf();
        }
    }
}