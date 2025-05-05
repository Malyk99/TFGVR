package com.example.appfirebaselittledemons.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Minigame4View extends View {
    private Paint ballPaint, holePaint, wallPaint;
    private float ballX, ballY, ballRadius = 40;
    private float velocityX = 0, velocityY = 0;
    private RectF hole;
    private boolean ballInHole = false;
    private List<RectF> internalWalls = new ArrayList<>();

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

        internalWalls.clear();
        float wallThickness = 20f;

        internalWalls.add(new RectF(
                w / 4f, h / 3f,
                w * 3f / 4f, h / 3f + wallThickness
        ));

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

        drawMaze(canvas);

        for (RectF wall : internalWalls) {
            canvas.drawRect(wall, wallPaint);
        }

        canvas.drawOval(hole, holePaint);

        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);
    }

    private void drawMaze(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        canvas.drawLine(0, 0, width, 0, wallPaint);
        canvas.drawLine(0, 0, 0, height, wallPaint);
        canvas.drawLine(width, 0, width, height, wallPaint);
        canvas.drawLine(0, height, width, height, wallPaint);

        canvas.drawLine(width / 4f, 0, width / 4f, height * 0.6f, wallPaint);
        canvas.drawLine(width / 2f, height * 0.4f, width / 2f, height, wallPaint);
        canvas.drawLine(width * 0.75f, 0, width * 0.75f, height * 0.5f, wallPaint);
    }

    public void updateBall(float deltaX, float deltaY) {
        if (ballInHole) return;

        float adjustedX = deltaY;
        float adjustedY = deltaX;

        velocityX += adjustedX;
        velocityY += adjustedY;

        float prevX = ballX;
        float prevY = ballY;

        ballX += velocityX;
        ballY += velocityY;

        for (RectF wall : internalWalls) {
            if (wall.intersects(ballX - ballRadius, ballY - ballRadius,
                    ballX + ballRadius, ballY + ballRadius)) {
                ballX = prevX;
                ballY = prevY;
                velocityX = -velocityX * 0.5f;
                velocityY = -velocityY * 0.5f;
                break;
            }
        }

        if (ballX < ballRadius || ballX > getWidth() - ballRadius) {
            velocityX = -velocityX * 0.5f;
        }
        if (ballY < ballRadius || ballY > getHeight() - ballRadius) {
            velocityY = -velocityY * 0.5f;
        }

        ballX = Math.max(ballRadius, Math.min(ballX, getWidth() - ballRadius));
        ballY = Math.max(ballRadius, Math.min(ballY, getHeight() - ballRadius));

        if (hole != null && hole.contains(ballX, ballY)) {
            ballInHole = true;
            if (listener != null) listener.onBallInHole();
        }

        Log.d("updateBall", "Ball updated: x=" + ballX + ", y=" + ballY);
        invalidate();
    }

    public void resetBall() {
        ballX = getWidth() / 4f;
        ballY = getHeight() / 2f;
        velocityX = velocityY = 0;
        ballInHole = false;
        invalidate();
    }

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
