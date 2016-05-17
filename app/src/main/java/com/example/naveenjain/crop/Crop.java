package com.example.naveenjain.crop;

import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.provider.MediaStore.Images.Media.getBitmap;

public class Crop extends AppCompatActivity implements View.OnClickListener {
    MyImage imageView;
    private String LOG_TAG = "ayusch";
    Bitmap bitmap;
    LinearLayout mainLayout;
    LinearLayout cropButtonLayout;
    Button cropButton,setButton,sendButton;
    ImageView leftImageView;
    ImageView rightImageView;
    Drawable borderDrawable;
    private View.OnClickListener filterClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cropButton = (Button)findViewById(R.id.crop_button);
        cropButtonLayout = (LinearLayout)findViewById(R.id.crop_button_layout) ;
        imageView = (MyImage)findViewById(R.id.custom_image_view);
        mainLayout = (LinearLayout)findViewById(R.id.crop_parent_layout);

        sendButton = new Button(this);
        setButton = new Button(this);

        Uri imgUri = getIntent().getParcelableExtra("imageUri");

        Bitmap bitmap = null;
        try {
            bitmap = getBitmap(getContentResolver(),imgUri);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(LOG_TAG,""+e);
        }
        try {
            bitmap=rotateBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);

    filterImageViews();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    public void onCropClicked(View view){
        TransitionManager.beginDelayedTransition(mainLayout,null);

        leftImageView = new ImageView(this);
        rightImageView = new ImageView(this);

        borderDrawable = ContextCompat.getDrawable(this, R.drawable.border_style);
        /*Setting layout properties*/

        Bitmap sourceBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        float[] coordinates = getBitmapCoordinates(sourceBitmap); // coordinates of imageview mapped according to coordinates of actual bitmap(image)

        Bitmap leftBitmap,rightBitmap;
        if (coordinates[0]<0){
             leftBitmap = null;
             rightBitmap = Bitmap.createBitmap(sourceBitmap,0,0,sourceBitmap.getWidth(),sourceBitmap.getHeight());

        }else if (coordinates[0]>sourceBitmap.getWidth()){
            leftBitmap = Bitmap.createBitmap(sourceBitmap,0,0,sourceBitmap.getWidth(),sourceBitmap.getHeight());
            rightBitmap = null;

        }else {
            leftBitmap = Bitmap.createBitmap(sourceBitmap,0,0,(int)coordinates[0],sourceBitmap.getHeight());
            rightBitmap = Bitmap.createBitmap(sourceBitmap,(int)coordinates[0],0,(int)(sourceBitmap.getWidth()-coordinates[0]),sourceBitmap.getHeight());

        }


        /* Child LinearLayout to hold two imageViews */
        LinearLayout imageViewsLayout  = (LinearLayout)findViewById(R.id.imageviews_layout);

        //LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxHeight,2.0f);
        LinearLayout.LayoutParams setButtonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        LinearLayout.LayoutParams sendButtonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);

        LinearLayout.LayoutParams layoutParamsLeft = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        LinearLayout.LayoutParams layoutParamsRight = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);

        leftImageView.setImageBitmap(leftBitmap);
        rightImageView.setImageBitmap(rightBitmap);

        //imageViewsLayout.setLayoutParams(linearLayoutParams);

        /* Buttons to be set below two imageViews in android */

        setButton.setText("Set Wallpaper");
        setButton.setBackgroundColor(Color.RED);

        setButton.setLayoutParams(setButtonLayoutParams);

        sendButton.setText("Send");
        sendButton.setBackgroundColor(Color.RED);

        sendButton.setLayoutParams(sendButtonLayoutParams);

        /* Adding the cropped imageviews to new layout */
        if (leftBitmap==null){
            imageViewsLayout.addView(rightImageView,layoutParamsRight);
        }else if (rightBitmap==null){
            imageViewsLayout.addView(leftImageView,layoutParamsLeft);
        }else{
            imageViewsLayout.addView(leftImageView,layoutParamsLeft);
            imageViewsLayout.addView(rightImageView,layoutParamsRight);
        }

        /* Setting imageview properties*/
        layoutParamsLeft.setMargins(0,0,20,0);

        /* Setting the ids */
        leftImageView.setId(View.generateViewId());
        rightImageView.setId(View.generateViewId());
        sendButton.setId(View.generateViewId());
        setButton.setId(View.generateViewId());

        imageViewsLayout.removeView(imageView);
        cropButtonLayout.removeView(cropButton);
        cropButtonLayout.addView(setButton);
        cropButtonLayout.addView(sendButton);

        leftImageView.setOnClickListener(this);
        rightImageView.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        setButton.setOnClickListener(this);

        // to remove filters scroll view
        HorizontalScrollView hsv = (HorizontalScrollView)findViewById(R.id.filters_scrollview);
        hsv.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);

        if(v.getId()==leftImageView.getId()){
                leftImageView.setBackground(borderDrawable);
                leftImageView.setSelected(true);
                rightImageView.setBackground(null);
                rightImageView.setSelected(false);
        }else if(v.getId()==rightImageView.getId()){
            leftImageView.setBackground(null);
            leftImageView.setSelected(false);
            rightImageView.setBackground(borderDrawable);
            rightImageView.setSelected(true);
        }else if(v.getId()==setButton.getId()){
            if (leftImageView.isSelected()) {
                try {
                    wallpaperManager.setBitmap(((BitmapDrawable) leftImageView.getDrawable()).getBitmap());
                    Toast.makeText(this,"Wallpaper Set Successfully !!",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (rightImageView.isSelected()){
                try {
                    wallpaperManager.setBitmap(((BitmapDrawable) rightImageView.getDrawable()).getBitmap());
                    Toast.makeText(this,"Wallpaper Set Successfully !!",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!leftImageView.isSelected() && !rightImageView.isSelected())
                Toast.makeText(this,"Please Select an Image ",Toast.LENGTH_SHORT).show();


        }else if(v.getId()==sendButton.getId()){
            Intent shareIntent = new Intent();
            Uri uriToImage=null;
            if(leftImageView.isSelected())
                uriToImage=getImageUri(this,((BitmapDrawable) leftImageView.getDrawable()).getBitmap());
            else if (rightImageView.isSelected())
                uriToImage=getImageUri(this,((BitmapDrawable) rightImageView.getDrawable()).getBitmap());
            else
                Toast.makeText(this,"Please Select an Image ",Toast.LENGTH_SHORT).show();


            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "Send Via"));

        }
    }

    private void filterImageViews(){
        LinearLayout filterImageLayout = (LinearLayout)findViewById(R.id.filterList);
        LinearLayout imgLayout=null;
        LinearLayout.LayoutParams imgLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for(int i = 0;i<5;i++){
            ImageView imgView = new ImageView(this);
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.desert);
            bmp = scaleDownBitmap(bmp,80,this);
            imgView.setImageBitmap(bmp);
            imgView.setId(i);
            imgView.setAdjustViewBounds(true);
            imgView.setOnClickListener(imgClick);
            imgLayout = new LinearLayout(this);
            imgLayoutParams.setMargins(5,0,0,0);
            imgLayout.setLayoutParams(imgLayoutParams);
            imgLayout.addView(imgView);

            filterImageLayout.addView(imgLayout);
        }

    }

 View.OnClickListener imgClick = new View.OnClickListener() {
     @Override
     public void onClick(View v) {
        Toast.makeText(getApplicationContext()," "+v.getId(),Toast.LENGTH_SHORT).show();
     }
 };



    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "title", null);
        try {
            bytes.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.parse(path);
    }

    public float[] getBitmapCoordinates(Bitmap src){
        Matrix inverse = new Matrix();
        imageView.getImageMatrix().invert(inverse);
        float[] pts = {imageView.touchX,imageView.touchY};
        inverse.mapPoints(pts);
        return pts;
    }

    public Bitmap rotateBitmap(Bitmap imgFile) throws IOException {
        Uri imgUri = getImageUri(getApplicationContext(),imgFile);
        Bitmap myBitmap = getBitmap(getContentResolver(),imgUri);


        try {
            ExifInterface exif = new ExifInterface(getRealPathFromUri(getApplicationContext(),imgUri));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {

        }
        return myBitmap;
    }

    public String getRealPathFromUri(Context context,Uri contentUri){
        Cursor cursor = null;
        try{
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor=context.getContentResolver().query(contentUri,proj,null,null,null);
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
    }


    public static Bitmap scaleDownBitmap(Bitmap photo,int newHeight,Context context){
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int)(newHeight*densityMultiplier);
        int w = (int)(h*photo.getWidth()/((double)photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo,w,h,true);

        return photo;
    }

}
