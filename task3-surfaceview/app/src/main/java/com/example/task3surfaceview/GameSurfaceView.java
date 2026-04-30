package com.example.task3surfaceview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path userPath = new Path();
    private float ballX = 160f;
    private float ballY = 220f;
    private float velocityX = 8f;
    private float velocityY = 10f;
    private final float ballRadius = 42f;

    public GameSurfaceView(Context context) {
        super(context);
        init();
    }

    public GameSurfaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        setFocusable(true);

        fillPaint.setColor(Color.rgb(0, 188, 212));
        fillPaint.setStyle(Paint.Style.FILL);

        strokePaint.setColor(Color.rgb(255, 193, 7));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(8f);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(42f);

        pathPaint.setColor(Color.rgb(129, 199, 132));
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(10f);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        drawThread = new DrawThread(holder, this);
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Размер читается из Canvas при каждом кадре; отдельное состояние не требуется.
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (drawThread == null) {
            return;
        }
        drawThread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                retry = false;
            }
        }
        drawThread = null;
    }

    void drawFrame(Canvas canvas) {
        canvas.drawColor(Color.rgb(16, 24, 32));
        canvas.drawText("SurfaceView draw thread", 40f, 70f, textPaint);
        canvas.drawRect(40f, 115f, 300f, 235f, fillPaint);
        canvas.drawRect(340f, 115f, 620f, 235f, strokePaint);
        canvas.drawLine(40f, 285f, canvas.getWidth() - 40f, 285f, strokePaint);
        canvas.drawCircle(ballX, ballY, ballRadius, fillPaint);
        synchronized (userPath) {
            canvas.drawPath(userPath, pathPaint);
        }
        updateBall(canvas.getWidth(), canvas.getHeight());
    }

    private void updateBall(int width, int height) {
        ballX += velocityX;
        ballY += velocityY;
        if (ballX - ballRadius < 0 || ballX + ballRadius > width) {
            velocityX = -velocityX;
            ballX = Math.max(ballRadius, Math.min(ballX, width - ballRadius));
        }
        if (ballY - ballRadius < 0 || ballY + ballRadius > height) {
            velocityY = -velocityY;
            ballY = Math.max(ballRadius, Math.min(ballY, height - ballRadius));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (userPath) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                userPath.moveTo(event.getX(), event.getY());
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP) {
                userPath.lineTo(event.getX(), event.getY());
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private static class DrawThread extends Thread {
        private final SurfaceHolder surfaceHolder;
        private final GameSurfaceView surfaceView;
        private volatile boolean running;

        DrawThread(SurfaceHolder surfaceHolder, GameSurfaceView surfaceView) {
            super("SurfaceDrawThread");
            this.surfaceHolder = surfaceHolder;
            this.surfaceView = surfaceView;
        }

        void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        synchronized (surfaceHolder) {
                            surfaceView.drawFrame(canvas);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(16);
                } catch (InterruptedException exception) {
                    running = false;
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
