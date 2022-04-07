package com.example.coloringbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {

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

        //Получаем текущий imageView
        ImageView thisImageView = findViewById(R.id.imageView2);
        if (imageId > 0){
            //Устанавливаем картинку для imageView
            thisImageView.setImageResource(imageId);
        }

    }
}