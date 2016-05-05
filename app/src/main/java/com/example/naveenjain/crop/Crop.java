package com.example.naveenjain.crop;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

public class Crop extends AppCompatActivity implements View.OnClickListener {
    MyImage imageView;
    private String LOG_TAG = "ayusch";
    Bitmap bitmap;
    RelativeLayout mainLayout;
    LinearLayout cropButtonLayout;
    Button cropButton,setButton,sendButton;
    ImageView leftImageView;
    ImageView rightImageView;
    Drawable borderDrawable;
    final int leftImageViewId,rightImageViewId,sendButtonId,setButtonId;

    public Crop(int leftImageViewId, int rightImageViewId, int sendButtonId, int setButtonId) {
        this.leftImageViewId = leftImageViewId;
        this.rightImageViewId = rightImageViewId;
        this.sendButtonId = sendButtonId;
        this.setButtonId = setButtonId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cropButton = (Button)findViewById(R.id.crop_button);
        cropButtonLayout = (LinearLayout)findViewById(R.id.crop_button_layout) ;
        imageView = (MyImage)findViewById(R.id.custom_image_view);
        mainLayout = (RelativeLayout)findViewById(R.id.crop_parent_layout);

        sendButton = new Button(this);
        setButton = new Button(this);

        Bitmap bitmap = (Bitmap)getIntent().getParcelableExtra("bitmap");

        Log.i(LOG_TAG,"this is the bitmap in CROP class :"+bitmap.toString());
        imageView.setImageBitmap(bitmap);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onCropClicked(View view){
        TransitionManager.beginDelayedTransition(mainLayout,null);


        leftImageView = new ImageView(this);
        rightImageView = new ImageView(this);


        borderDrawable = ContextCompat.getDrawable(this, R.drawable.border_style);
        /*Setting layout properties*/

        rightImageView.setBackground(borderDrawable);

        Bitmap sourceBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        float[] coordinates = getBitmapCoordinates(sourceBitmap); // coordinates of imageview mapped according to coordinates of actual bitmap(image)
        Bitmap leftBitmap = Bitmap.createBitmap(sourceBitmap,0,0, (int)coordinates[0],sourceBitmap.getHeight());
        Bitmap rightBitmap = Bitmap.createBitmap(sourceBitmap,(int)coordinates[0],0,(int)(sourceBitmap.getWidth()-coordinates[0]),sourceBitmap.getHeight());

        leftImageView.setImageBitmap(leftBitmap);
        rightImageView.setImageBitmap(rightBitmap);

        LinearLayout.LayoutParams layoutParamsLeft = new LinearLayout.LayoutParams(400,700,1.0f);
        LinearLayout.LayoutParams layoutParamsRight = new LinearLayout.LayoutParams(400,700,1.0f);

        /* Child LinearLayout to hold two imageViews */
        LinearLayout newLayout  = new LinearLayout(this);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,2.0f);
        newLayout.setLayoutParams(linearLayoutParams);


        /* Buttons to be set below two imageViews in android */

        setButton.setId(View.generateViewId());
        setButton.setText("Set Wallpaper");
        setButton.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams setButtonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        setButton.setLayoutParams(setButtonLayoutParams);

        sendButton.setId(View.generateViewId());
        sendButton.setText("Send");
        sendButton.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams sendButtonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        sendButton.setLayoutParams(sendButtonLayoutParams);

        /* Adding the cropped imageviews to new layout */
        newLayout.addView(leftImageView,layoutParamsLeft);
        newLayout.addView(rightImageView,layoutParamsRight);

        mainLayout.removeView(imageView);
        cropButtonLayout.removeView(cropButton);

        cropButtonLayout.addView(setButton);
        cropButtonLayout.addView(sendButton);
        mainLayout.addView(newLayout);


        /* Setting the ids */
        leftImageView.setId(View.generateViewId());
        rightImageView.setId(View.generateViewId());
        sendButton.setId(View.generateViewId());
        setButton.setId(View.generateViewId());

        leftImageView.setOnClickListener(this);
        rightImageView.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        setButton.setOnClickListener(this);
    }





    public float[] getBitmapCoordinates(Bitmap src){
        Matrix inverse = new Matrix();
        imageView.getImageMatrix().invert(inverse);
        float[] pts = {imageView.touchX,imageView.touchY};
        inverse.mapPoints(pts);
        return pts;
    }

}
