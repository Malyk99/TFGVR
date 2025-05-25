package com.example.appfirebaselittledemons.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.appfirebaselittledemons.R;

import java.util.ArrayList;
import java.util.List;

public class Minigame4View extends View {
    private Paint ballPaint, holePaint, wallPaint;
    private float ballX, ballY, ballRadius = 40;
    private float velocityX = 0, velocityY = 0;
    private RectF hole;
    private boolean ballInHole = false;
    private List<RectF> internalWalls = new ArrayList<>();
    private Bitmap ballBitmap;
    private float ballRotation = 0f;
    private Bitmap backgroundBitmap;


    public Minigame4View(Context context) {
        super(context);
        init();
    }

    public Minigame4View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ballBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_head);
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.brickfloor);


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

        defineMazeWalls();

        ballX = ballRadius + 30;
        ballY = ballRadius + 30;

        float holeSize = 100f;
        hole = new RectF(w - holeSize - 50, h / 2f - holeSize / 2, w - 50, h / 2f + holeSize / 2);

        if (readyListener != null) {
            Log.d("Minigame4View", "Maze and hole initialized, ready to go.");
            readyListener.onReady();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (backgroundBitmap != null) {
            Rect dest = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(backgroundBitmap, null, dest, null);
        }


        for (RectF wall : internalWalls) {
            canvas.drawRect(wall, wallPaint);
        }

        canvas.drawOval(hole, holePaint);
        if (ballBitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postTranslate(-ballBitmap.getWidth() / 2f, -ballBitmap.getHeight() / 2f);
            matrix.postRotate(ballRotation);
            matrix.postTranslate(ballX, ballY);
            canvas.drawBitmap(ballBitmap, matrix, null);
        } else {
            canvas.drawCircle(ballX, ballY, ballRadius, ballPaint); // fallback
        }

    }

    private void defineMazeWalls() {
        internalWalls.clear();
        float wallThickness = 20f;
        int width = getWidth();
        int height = getHeight();

        int mazeIndex = (int) (Math.random() * 5);

                // BORDES EXTERNOS
        internalWalls.add(new RectF(0, 0, width, wallThickness)); // Top
        internalWalls.add(new RectF(0, 0, wallThickness, height)); // Left
        internalWalls.add(new RectF(width - wallThickness, 0, width, height)); // Right
        internalWalls.add(new RectF(0, height - wallThickness, width, height)); // Bottom

        switch (mazeIndex) {
            case 0:
                internalWalls.add(new RectF(width / 4f, 0, width / 4f + wallThickness, height * 0.6f));
                internalWalls.add(new RectF(width / 2f, height * 0.4f, width / 2f + wallThickness, height));
                internalWalls.add(new RectF(width * 0.75f, 0, width * 0.75f + wallThickness, height * 0.5f));
                break;

            case 1:
                internalWalls.add(new RectF(width * 0.3f, height * 0.2f, width * 0.3f + wallThickness, height));
                internalWalls.add(new RectF(width * 0.6f, 0, width * 0.6f + wallThickness, height * 0.7f));
                internalWalls.add(new RectF(width * 0.8f, height * 0.4f, width * 0.8f + wallThickness, height));
                break;

            case 2:
                internalWalls.add(new RectF(width * 0.25f, height * 0.25f, width * 0.25f + wallThickness, height * 0.75f));
                internalWalls.add(new RectF(width * 0.5f, height * 0.4f, width * 0.5f + wallThickness, height * 0.7f));
                internalWalls.add(new RectF(width * 0.7f, 0, width * 0.7f + wallThickness, height * 0.6f));
                break;

            case 3:
                internalWalls.add(new RectF(width * 0.15f, height * 0.2f, width * 0.15f + wallThickness, height * 0.8f));
                internalWalls.add(new RectF(width * 0.45f, 0, width * 0.45f + wallThickness, height * 0.5f));
                internalWalls.add(new RectF(width * 0.65f, height * 0.4f, width * 0.65f + wallThickness, height));
                break;

            case 4:
                internalWalls.add(new RectF(width * 0.2f, 0, width * 0.2f + wallThickness, height * 0.6f));
                internalWalls.add(new RectF(width * 0.4f, height * 0.3f, width * 0.4f + wallThickness, height));
                internalWalls.add(new RectF(width * 0.75f, height * 0.1f, width * 0.75f + wallThickness, height * 0.9f));
                break;
        }

    }


    public void updateBall(float deltaX, float deltaY) {
        if (ballInHole) return;

        // Landscape mode adjustment: X = deltaY, Y = deltaX
        float adjustedX = deltaY;
        float adjustedY = deltaX;

        velocityX += adjustedX;
        velocityY += adjustedY;


        velocityX *= 0.9f;
        velocityY *= 0.9f;
        ballRotation += (velocityX + velocityY) * 0.5f;


        float nextX = ballX + velocityX;
        float nextY = ballY + velocityY;

        for (RectF wall : internalWalls) {
            RectF ballRect = new RectF(nextX - ballRadius, nextY - ballRadius, nextX + ballRadius, nextY + ballRadius);
            if (RectF.intersects(wall, ballRect)) {

                if (ballX + ballRadius <= wall.left || ballX - ballRadius >= wall.right) {
                    velocityX = -velocityX * 0.5f;
                    nextX = ballX;
                }
                if (ballY + ballRadius <= wall.top || ballY - ballRadius >= wall.bottom) {
                    velocityY = -velocityY * 0.5f;
                    nextY = ballY;
                }
            }
        }

        ballX = Math.max(ballRadius, Math.min(nextX, getWidth() - ballRadius));
        ballY = Math.max(ballRadius, Math.min(nextY, getHeight() - ballRadius));

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

    public void resetBallAndMaze() {
        defineMazeWalls();
        resetBall();
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
