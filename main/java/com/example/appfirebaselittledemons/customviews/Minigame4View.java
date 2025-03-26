package com.example.appfirebaselittledemons.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Minigame4View extends View {
    private Paint ballPaint, holePaint, wallPaint;
    private float ballX, ballY, ballRadius = 40;
    private float velocityX = 0, velocityY = 0;
    private RectF hole;
    private boolean ballInHole = false;

    public Minigame4View(Context context) {
        super(context);
        init();
    }

    public Minigame4View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ballPaint = new Paint();
        ballPaint.setColor(Color.BLUE);
        ballPaint.setAntiAlias(true);

        holePaint = new Paint();
        holePaint.setColor(Color.BLACK);
        holePaint.setAntiAlias(true);

        wallPaint = new Paint();
        wallPaint.setColor(Color.DKGRAY);
        wallPaint.setStrokeWidth(10);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ballX = w / 4f;
        ballY = h / 2f;
        float holeSize = 100f;
        hole = new RectF(w - holeSize - 50, h / 2f - holeSize / 2,
                w - 50, h / 2f + holeSize / 2);

        if (readyListener != null) {
            Log.d("Minigame4View", "Maze and hole initialized, ready to go.");
            readyListener.onReady();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw maze walls (simplified)
        canvas.drawLine(0, 0, getWidth(), 0, wallPaint);
        canvas.drawLine(0, 0, 0, getHeight(), wallPaint);
        canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), wallPaint);
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), wallPaint);

        // Draw hole
        canvas.drawOval(hole, holePaint);

        // Draw ball
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);
    }

    public void updateBall(float deltaX, float deltaY) {
        if (ballInHole) return;

        velocityX += deltaX;
        velocityY += deltaY;

        ballX += velocityX;
        ballY += velocityY;

        // Collision with walls
        if (ballX < ballRadius || ballX > getWidth() - ballRadius) {
            velocityX = -velocityX * 0.5f;
        }
        if (ballY < ballRadius || ballY > getHeight() - ballRadius) {
            velocityY = -velocityY * 0.5f;
        }

        ballX = Math.max(ballRadius, Math.min(ballX, getWidth() - ballRadius));
        ballY = Math.max(ballRadius, Math.min(ballY, getHeight() - ballRadius));

        // Check if in hole
        if (hole != null && hole.contains(ballX, ballY)) {
            ballInHole = true;
            if (listener != null) listener.onBallInHole();
        }

        invalidate();
    }

    public void resetBall() {
        ballX = getWidth() / 4f;
        ballY = getHeight() / 2f;
        velocityX = velocityY = 0;
        ballInHole = false;
        invalidate();
    }

    // Interface
    public interface BallInHoleListener {
        void onBallInHole();
    }

    private BallInHoleListener listener;

    public void setBallInHoleListener(BallInHoleListener listener) {
        this.listener = listener;
    }

    public interface OnReadyListener {
        void onReady();
    }

    private OnReadyListener readyListener;

    public void setOnReadyListener(OnReadyListener listener) {
        this.readyListener = listener;
    }
}
