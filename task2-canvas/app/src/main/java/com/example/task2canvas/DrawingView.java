package com.example.task2canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawingView extends View {
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fingerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path fingerPath = new Path();
    private float ballX = 120f;
    private float ballY = 260f;
    private float velocityX = 7f;
    private float velocityY = 9f;
    private final float ballRadius = 38f;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        fillPaint.setColor(Color.rgb(33, 150, 243));
        fillPaint.setStyle(Paint.Style.FILL);

        strokePaint.setColor(Color.rgb(230, 81, 0));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(8f);

        textPaint.setColor(Color.rgb(30, 30, 30));
        textPaint.setTextSize(42f);
        textPaint.setStyle(Paint.Style.FILL);

        fingerPaint.setColor(Color.rgb(76, 175, 80));
        fingerPaint.setStyle(Paint.Style.STROKE);
        fingerPaint.setStrokeWidth(10f);
        fingerPaint.setStrokeCap(Paint.Cap.ROUND);
        fingerPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(250, 250, 250));

        // Базовые примитивы Canvas: заливка, круг, прямоугольник, линия и текст.
        canvas.drawText("Canvas + Paint", 40f, 70f, textPaint);
        canvas.drawRect(40f, 110f, 300f, 230f, fillPaint);
        canvas.drawRect(340f, 110f, 620f, 230f, strokePaint);
        canvas.drawLine(40f, 280f, getWidth() - 40f, 280f, strokePaint);
        canvas.drawCircle(ballX, ballY, ballRadius, fillPaint);
        canvas.drawPath(fingerPath, fingerPaint);

        updateBallPosition();
        postInvalidateDelayed(16);
    }

    private void updateBallPosition() {
        int width = Math.max(getWidth(), 1);
        int height = Math.max(getHeight(), 1);
        ballX += velocityX;
        ballY += velocityY;

        if (ballX - ballRadius < 0 || ballX + ballRadius > width) {
            velocityX = -velocityX;
            ballX = clamp(ballX, ballRadius, width - ballRadius);
        }
        if (ballY - ballRadius < 0 || ballY + ballRadius > height) {
            velocityY = -velocityY;
            ballY = clamp(ballY, ballRadius, height - ballRadius);
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fingerPath.moveTo(x, y);
                return true;
            case MotionEvent.ACTION_MOVE:
                fingerPath.lineTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                fingerPath.lineTo(x, y);
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}
