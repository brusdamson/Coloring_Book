package com.example.coloringbook.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.DrawFilter;
import android.graphics.Paint;
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
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.coloringbook.PaintActivity;
import com.example.coloringbook.R;
import com.example.coloringbook.common.Common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    private List<Bitmap> bitmapList = new ArrayList<>();
    private Bitmap defaultBitmap = null;
    float oldX;
    float oldY;

    public void undoLastAction() {
        if (bitmapList.size() > 0){
            bitmapList.remove(bitmapList.size() - 1);
            if (bitmapList.size() > 0){
                bitmap = bitmapList.get(bitmapList.size()-1);
            }else{
                bitmap = Bitmap.createBitmap(defaultBitmap);
            }

            invalidate();
        }
    }
    private void addLastAction(Bitmap b){
        bitmapList.add(b);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(mMinZoom, Math.min(mScaleFactor, mMaxZoom));
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

    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        int alpha = 0xFF << 24; // ?bitmap?24?
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (bitmap == null){
            Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), Common.PICTURE_SELECTED);
            bitmap = scaleCenterCrop(srcBitmap, h-600, w);
            for (int i = 0; i < bitmap.getWidth(); i++) {
                for (int j = 0; j < bitmap.getHeight(); j++) {
                    if (bitmap.getPixel(i,j) <= Color.BLACK){
                        bitmap.setPixel(i,j,Color.BLACK);
                    }
                    else{
                        bitmap.setPixel(i,j,Color.WHITE);
                    }
                }
            }
            if (defaultBitmap == null){
                defaultBitmap = Bitmap.createBitmap(bitmap);
            }
        }
    }

     public Bitmap scaleCenterCrop(Bitmap source, int newHeight,
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
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dest);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));

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
        //Проверка чтобы не выходило за границы картинки
        if(x > bitmap.getWidth() || x < 0)
            return;
        if(y > bitmap.getHeight() || y < 0)
            return;
        //Проверка на зум, чтобы не закрашивать при зуме
        if (oldx != refX || oldy != refY){
            return;
        }
        int targetColor = bitmap.getPixel(x,y);
        //Проверка чтобы не закрашивало черный цвет
        if (targetColor != Color.BLACK){
            FloodFill f = new FloodFill();
            f.floodFill(bitmap, new Point(x,y), targetColor, Common.COLOR_SELECTED);
            addLastAction(Bitmap.createBitmap(getBitmap()));
            invalidate();
        }
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
