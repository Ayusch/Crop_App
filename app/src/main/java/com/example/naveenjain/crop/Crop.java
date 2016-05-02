package com.example.naveenjain.crop;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Crop extends AppCompatActivity {
    MyImage imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (MyImage)findViewById(R.id.custom_image_view);

        Bitmap bitmap = (Bitmap)this.getIntent().getParcelableExtra("bitmap");
        imageView.setImageBitmap(bitmap);

    }

}
