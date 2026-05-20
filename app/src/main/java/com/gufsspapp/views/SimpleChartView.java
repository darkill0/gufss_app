package com.gufsspapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class SimpleChartView extends View {

    private float[] data;
    private Paint linePaint;
    private Paint fillPaint;
    private Paint dotPaint;
    private Paint gridPaint;

    public SimpleChartView(Context context) {
        super(context);
        init();
    }

    public SimpleChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#2EA043"));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dpToPx(2));
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(Color.parseColor("#2EA043"));
        dotPaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#21262D"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(dpToPx(1));

        // Default data
        data = new float[]{50f, 60f, 55f, 70f, 65f, 80f, 75f};
    }

    public void setData(float[] data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.length < 2) return;

        int w = getWidth();
        int h = getHeight();
        int paddingH = dpToPx(8);
        int paddingV = dpToPx(8);

        float maxVal = 0f;
        float minVal = Float.MAX_VALUE;
        for (float v : data) {
            maxVal = Math.max(maxVal, v);
            minVal = Math.min(minVal, v);
        }
        float range = maxVal - minVal;
        if (range == 0) range = 1;

        // Draw horizontal grid lines
        int gridLines = 4;
        for (int i = 0; i <= gridLines; i++) {
            float y = paddingV + (h - 2 * paddingV) * i / gridLines;
            canvas.drawLine(paddingH, y, w - paddingH, y, gridPaint);
        }

        float stepX = (w - 2f * paddingH) / (data.length - 1);

        // Build path
        Path linePath = new Path();
        Path fillPath = new Path();

        for (int i = 0; i < data.length; i++) {
            float x = paddingH + i * stepX;
            float y = paddingV + (h - 2 * paddingV) * (1f - (data[i] - minVal) / range);

            if (i == 0) {
                linePath.moveTo(x, y);
                fillPath.moveTo(x, h - paddingV);
                fillPath.lineTo(x, y);
            } else {
                // Smooth curve using cubic bezier
                float prevX = paddingH + (i - 1) * stepX;
                float prevY = paddingV + (h - 2 * paddingV) * (1f - (data[i - 1] - minVal) / range);
                float cx1 = prevX + stepX / 3f;
                float cx2 = x - stepX / 3f;
                linePath.cubicTo(cx1, prevY, cx2, y, x, y);
                fillPath.cubicTo(cx1, prevY, cx2, y, x, y);
            }
        }

        // Close fill path
        float lastX = paddingH + (data.length - 1) * stepX;
        fillPath.lineTo(lastX, h - paddingV);
        fillPath.close();

        // Gradient fill
        fillPaint.setShader(new LinearGradient(
                0, paddingV, 0, h - paddingV,
                Color.parseColor("#602EA043"),
                Color.parseColor("#002EA043"),
                Shader.TileMode.CLAMP));

        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(linePath, linePaint);

        // Draw dots at key points
        for (int i = 0; i < data.length; i += Math.max(1, data.length / 6)) {
            float x = paddingH + i * stepX;
            float y = paddingV + (h - 2 * paddingV) * (1f - (data[i] - minVal) / range);
            canvas.drawCircle(x, y, dpToPx(3), dotPaint);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
