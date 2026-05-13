package com.example.tsuki;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom circular progress view matching the mockup:
 * - Thick pink arc drawn from top, clockwise
 * - Small white-filled circle dot at the start of the arc
 * - Percentage text drawn in the center
 */
public class CircularProgressView extends View {

    private Paint arcPaint;
    private Paint trackPaint;
    private Paint dotPaint;
    private Paint textPaint;

    private float progress = 0f; // 0–100
    private RectF oval = new RectF();

    private static final float STROKE_WIDTH = 28f;
    private static final float DOT_RADIUS = 16f;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Background track (light pink ring)
        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(STROKE_WIDTH);
        trackPaint.setColor(Color.parseColor("#FADADD"));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);

        // Foreground arc (primary pink)
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(STROKE_WIDTH);
        arcPaint.setColor(Color.parseColor("#FF657D"));
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        // Dot at the start of the arc
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(Color.WHITE);

        // Percentage text
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#1C4E88"));
        textPaint.setTextSize(72f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }

    /**
     * Set progress value (0–100) and redraw.
     */
    public void setProgress(float progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(cx, cy) - STROKE_WIDTH;

        oval.set(cx - radius, cy - radius, cx + radius, cy + radius);

        // Draw background track
        canvas.drawArc(oval, -90, 360, false, trackPaint);

        // Draw progress arc (starts at top = -90°)
        float sweepAngle = 360f * (progress / 100f);
        canvas.drawArc(oval, -90, sweepAngle, false, arcPaint);

        // Draw dot at the start position (top of circle, -90°)
        double startRad = Math.toRadians(-90);
        float dotX = cx + radius * (float) Math.cos(startRad);
        float dotY = cy + radius * (float) Math.sin(startRad);
        canvas.drawCircle(dotX, dotY, DOT_RADIUS, dotPaint);

        // Draw percentage text in center
        String text = (int) progress + "%";
        float textY = cy - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(text, cx, textY, textPaint);
    }
}
