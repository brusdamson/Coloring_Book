package com.example.coloringbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {

    ImageView thisImageView;

    ScaleGestureDetector scaleGestureDetector;
    float scaleFactor = 1.0f;

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

       scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event);
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= scaleGestureDetector.getScaleFactor();
            thisImageView.setScaleX(scaleFactor);
            thisImageView.setScaleY(scaleFactor);
            return true;
        }
    }
}