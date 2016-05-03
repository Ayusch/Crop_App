package com.example.naveenjain.crop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Crop extends AppCompatActivity {
    MyImage imageView;
    private String LOG_TAG = "ayusch";
    Bitmap bitmap;
    RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (MyImage)findViewById(R.id.custom_image_view);
        mainLayout = (RelativeLayout)findViewById(R.id.crop_parent_layout);

        byte[] bytes = getIntent().getByteArrayExtra("bitmap");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        Log.i(LOG_TAG,"this is the bitmap in CROP class :"+bitmap.toString());
        imageView.setImageBitmap(bitmap);

    }

    public void onCropClicked(View view){

        ImageView leftImageView = new ImageView(this);
        ImageView rightImageView = new ImageView(this);

        Bitmap sourceBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();


        Bitmap leftBitmap = Bitmap.createBitmap(sourceBitmap,0,0, (int) imageView.touchX,imageView.getHeight());
        //Bitmap rightBitmap = Bitmap.createBitmap(sourceBitmap,(int)imageView.touchX,0,,imageView.getHeight());
     // Bitmap rightBitmap = Bitmap.createBitmap(left,0,0, (int) imageView.touchX,imageView.getHeight());

        Log.i(LOG_TAG," We are in onCropClicked method and bitmap = "+bitmap);


        leftImageView.setImageBitmap(leftBitmap);
        //rightImageView.setImageBitmap(rightBitmap);

        RelativeLayout.LayoutParams layoutParamsLeft = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsLeft.addRule(RelativeLayout.ALIGN_LEFT);

        RelativeLayout.LayoutParams layoutParamsRight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsRight.addRule(RelativeLayout.ALIGN_RIGHT);


        mainLayout.removeView(imageView);
        mainLayout.addView(leftImageView,layoutParamsLeft);
    }




}
