package com.example.coloringbook;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.coloringbook.common.Common;
import com.example.coloringbook.widget.PaintView;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.Permission;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class PaintActivity extends AppCompatActivity implements SpectrumPalette.OnColorSelectedListener {

    private static final int PERMISSION_REQUEST = 10001;
    PaintView paintView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        initToolbar();

        SpectrumPalette spectrumPalette = findViewById(R.id.palette);
        spectrumPalette.setOnColorSelectedListener(this);
        paintView = findViewById(R.id.paint_view);

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button_24);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save){
            showDialogForSave();

        }else if (id == 16908332) {
            finish();
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showDialogForSave() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Сохранить изобрадение?");
            builder.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    save();
                    dialogInterface.dismiss();
                }
            });
            builder.show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            save();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void save() {
        Bitmap bitmap = paintView.getBitmap();
        String file_name = UUID.randomUUID()+".png";
        File folder = new File(Environment.getExternalStorageDirectory()+File.separator+Environment.DIRECTORY_PICTURES+File.separator+getString(R.string.app_name));
        if (!folder.exists()){
            folder.mkdirs();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(folder+File.separator+file_name);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(this,"Изображение сохранено", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    public void onColorSelected(int color) {
        Common.COLOR_SELECTED = color;
    }
}