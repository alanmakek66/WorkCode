package com.example.a0321;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

public class ThickStrikethroughSpan implements LineBackgroundSpan {

    private final float strokeWidth;
    private final int color;

    public ThickStrikethroughSpan(float strokeWidth, int color) {
        this.strokeWidth = strokeWidth;
        this.color = color;
    }

    @Override
    public void drawBackground(
            Canvas canvas, Paint paint, int left, int right, int top, int baseline,
            int bottom, CharSequence text, int start, int end, int lineNumber
    ) {
        Paint originalPaint = new Paint(paint); // 保存原始画笔设置
        float y = top + ((bottom - top) / 2f); // 确保是 float 类型

        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);

        canvas.drawLine(left, y, right, y, paint);

        paint.set(originalPaint); // 恢复原始画笔设置
    }
}
