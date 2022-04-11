package com.example.coloringbook.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.coloringbook.common.Common;

import java.util.LinkedList;
import java.util.Queue;

public class PaintView extends View {

    Bitmap bitmap;
    Canvas currentCanvas;

    public PaintView(Context context) {
        super(context);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), Common.PICTURE_SELECTED);
        bitmap = Bitmap.createScaledBitmap(srcBitmap, w,h,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getX() > bitmap.getWidth() || event.getX() < 0)
            return true;
        if(event.getY() > bitmap.getHeight() || event.getY() < 0)
            return true;
        paint((int)event.getX(),(int)event.getY());
        return true;
    }

    private void paint(int x, int y) {
        int targetColor = bitmap.getPixel(x,y);
        FloodFill f = new FloodFill();
        f.floodFill(bitmap, new Point(x,y), targetColor, Common.COLOR_SELECTED);
        invalidate();
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
