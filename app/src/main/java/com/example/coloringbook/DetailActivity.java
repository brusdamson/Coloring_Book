package com.example.coloringbook;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

public class DetailActivity extends AppCompatActivity {

    ImageView thisImageView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getIntent().getExtras();

        int imageId = 0;
        if (arguments != null){
            //Получаем переданный id картинки из прошлой активности
            imageId = arguments.getInt("id");
        }

        //Отображаем новую страницу чтобы проинициализировать текущий imageView
        setContentView(R.layout.activity_detail);

        thisImageView = findViewById(R.id.imageView);
        //Получаем текущий imageView
        if (imageId > 0){
            //Устанавливаем картинку для imageView
            thisImageView.setImageResource(imageId);
        }

        //OnTouch
        thisImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int)motionEvent.getX();
                int y = (int)motionEvent.getY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_HOVER_MOVE:
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                }
                //323 882
                Toast.makeText(DetailActivity.this,("X: " + x + " Y: " + y), Toast.LENGTH_LONG).show();

//                thisImageView = findViewById(R.id.imageView);
//                Bitmap bitmapOriginal = ((BitmapDrawable)thisImageView.getDrawable()).getBitmap();
//                Bitmap bitmapCopy = bitmapOriginal.copy(Bitmap.Config.ARGB_8888, true);
//                FloodFill f = new FloodFill();
//                f.floodFill(bitmapCopy, new Point(x,y), bitmapCopy.getPixel(x,y), Color.parseColor("#fcba03"));
//                thisImageView.setImageBitmap(bitmapCopy);

                return false;
            }

        });
    }

}