package com.example.coloringbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.coloringbook.Interface.ImageOnClick;
import com.example.coloringbook.common.Common;

public class MainActivity extends AppCompatActivity {

    GridView androidGridView;
    Integer[] imageIDs = {
            R.drawable.bear, R.drawable.bla, R.drawable.horse,
            R.drawable.lion, R.drawable.olenb, R.drawable.olenb2,
            R.drawable.one, R.drawable.tiger,
    };

    private ImageOnClick imageOnClick;

    public void setImageOnClick(ImageOnClick imageOnClick) {
        this.imageOnClick = imageOnClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidGridView = (GridView) findViewById(R.id.gridview);
        androidGridView.setAdapter(new ImageAdapterGridView(this));

        int a = imageIDs[1];
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
//                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
//                intent.putExtra("id",imageIDs[position]);
//                startActivity(intent);
                Common.PICTURE_SELECTED = imageIDs[position];
                Intent intent = new Intent(MainActivity.this, PaintActivity.class);
                startActivity(intent);
            }
        });


    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;

        public ImageAdapterGridView(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imageIDs.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mImageView;

            if (convertView == null) {
                mImageView = new ImageView(mContext);
                mImageView.setLayoutParams(new GridView.LayoutParams(400, 600));
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageView.setBackground(AppCompatResources.getDrawable(mContext,R.drawable.border));
                mImageView.setPadding(16, 16, 16, 16);

            } else {
                mImageView = (ImageView) convertView;
            }
            mImageView.setImageResource(imageIDs[position]);
            return mImageView;
        }
    }
}
