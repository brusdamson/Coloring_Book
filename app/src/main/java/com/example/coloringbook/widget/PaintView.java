package com.example.coloringbook.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.print.PrintAttributes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.coloringbook.common.Common;

import java.util.LinkedList;
import java.util.Queue;

public class PaintView extends View {

    Bitmap bitmap;
    Canvas currentCanvas;
    private float mPositionX, mPositionY;
    private float refX, refY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private final static float mMinZoom = 1.0f;
    private final static float mMaxZoom = 2.0f;
    float oldX;
    float oldY;
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mScaleFactor, Math.min(mScaleFactor, mMaxZoom));
            invalidate();

            return true;
        }
    }

    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), Common.PICTURE_SELECTED);
//        PaintView paintView = findViewById(R.id.paint_view);
//        ViewGroup.LayoutParams params = paintView.getLayoutParams();
//        params.width = (int)(paintView.getResources().getDisplayMetrics().density * 400);
//        params.height = (int)(paintView.getResources().getDisplayMetrics().density * 50);
//        paintView.setLayoutParams(params);


//        int width = (int)(this.getResources().getDisplayMetrics().density*400);
//        int height = (int)(this.getResources().getDisplayMetrics().density*400);
//        bitmap = Bitmap.createScaledBitmap(srcBitmap, w,h,false);
        bitmap = scaleCenterCrop(srcBitmap, h, w);
    }
    static public Bitmap scaleCenterCrop(Bitmap source, int newHeight,
                                         int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top
                + scaledHeight);//from ww w  .j a va 2s. co m

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
                source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmap(canvas);
    }

    private void drawBitmap(Canvas canvas) {
        canvas.save();
        canvas.translate(mPositionX, mPositionY);
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                paint((int)((refX - mPositionX) / mScaleFactor), (int)((refY - mPositionY) / mScaleFactor),oldX,oldY);
                break;
            case MotionEvent.ACTION_DOWN:
                refX = event.getX();
                refY = event.getY();
                oldX = refX;
                oldY = refY;
                break;

            case MotionEvent.ACTION_MOVE:
                float nX = event.getX();
                float nY = event.getY();

                mPositionX += nX - refX;
                mPositionY += nY - refY;

                refX = nX;
                refY = nY;

                invalidate();
        }
        return true;
    }

    private void paint(int x, int y, float oldx, float oldy) {
        if(x > bitmap.getWidth() || x < 0)
            return;
        if(y > bitmap.getHeight() || y < 0)
            return;
        if (oldx != refX || oldy != refY){
            return;
        }
        int targetColor = bitmap.getPixel(x,y);
        FloodFill f = new FloodFill();
        f.floodFill(bitmap, new Point(x,y), targetColor, Common.COLOR_SELECTED);
        invalidate();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public class FloodFill {
        public void floodFill(Bitmap  image, Point node, int targetColor,
                              int replacementColor) {
            int width = image.getWidth();
            int height = image.getHeight();
            int target = targetColor;
            int replacement = replacementColor;
            if (target != replacement) {
                Queue<Point> queue = new LinkedList<Point>();
                do {
                    int x = node.x;
                    int y = node.y;
                    while (x > 0 && image.getPixel(x - 1, y) == target) {
                        x--;
                    }
                    boolean spanUp = false;
                    boolean spanDown = false;
                    while (x < width && image.getPixel(x, y) == target) {
                        image.setPixel(x, y, replacement);
                        if (!spanUp && y > 0 && image.getPixel(x, y - 1) == target) {
                            queue.add(new Point(x, y - 1));
                            spanUp = true;
                        } else if (spanUp && y > 0
                                && image.getPixel(x, y - 1) != target) {
                            spanUp = false;
                        }
                        if (!spanDown && y < height - 1
                                && image.getPixel(x, y + 1) == target) {
                            queue.add(new Point(x, y + 1));
                            spanDown = true;
                        } else if (spanDown && y < height - 1
                                && image.getPixel(x, y + 1) != target) {
                            spanDown = false;
                        }
                        x++;
                    }
                } while ((node = queue.poll()) != null);
            }
        }
    }
}
