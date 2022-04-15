package com.example.coloringbook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.coloringbook.common.Common;
import com.example.coloringbook.widget.PaintView;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.util.LinkedList;
import java.util.Queue;

public class PaintActivity extends AppCompatActivity implements SpectrumPalette.OnColorSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        SpectrumPalette spectrumPalette = findViewById(R.id.palette);
        spectrumPalette.setOnColorSelectedListener(this);
    }

    @Override
    public void onColorSelected(int color) {
        Common.COLOR_SELECTED = color;
    }
}