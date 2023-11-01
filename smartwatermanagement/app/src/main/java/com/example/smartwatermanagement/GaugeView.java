package com.example.smartwatermanagement;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {
    private final int minValue = 0;
    private final int maxValue = 100;
    private int value = 10;
    private Paint gaugePaint;
    private Paint valuePaint;
    private Paint textPaint;

    public GaugeView(Context context) {
        super(context);
        init();
    }

    public GaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gaugePaint = new Paint();
        gaugePaint.setColor(Color.GRAY);
        gaugePaint.setStrokeWidth(10);
        gaugePaint.setStyle(Paint.Style.STROKE);

        valuePaint = new Paint();
        valuePaint.setColor(Color.BLUE);
        valuePaint.setStrokeWidth(10);
        valuePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE); // Set text color to black
        textPaint.setTextSize(16 * getResources().getDisplayMetrics().scaledDensity); // Set text size to 16sp
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2; // Center vertically
        int radius = Math.min(centerX, centerY);

        // Draw the gauge arc at the center
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, 180, 180, false, gaugePaint);

        // Draw the percentage indicator
        float angle = (value - minValue) / (float) (maxValue - minValue) * 180;
        canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, 180, angle, false, valuePaint);

        // Draw the percentage text in the center
        String percentageText = String.valueOf((int) ((angle / 180) * 100)) + "%";
        Rect bounds = new Rect();
        textPaint.getTextBounds(percentageText, 0, percentageText.length(), bounds);
        canvas.drawText(percentageText, centerX, centerY + bounds.height() / 2, textPaint);
    }

    public void setValue(int value) {
        if (value < minValue) {
            this.value = minValue;
        } else if (value > maxValue) {
            this.value = maxValue;
        } else {
            this.value = value;
        }
        invalidate();
    }
}
