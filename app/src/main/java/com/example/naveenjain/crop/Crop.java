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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import cn.Ragnarok.BitmapFilter;

import static android.provider.MediaStore.Images.Media.getBitmap;

@SuppressWarnings("ResourceType")
public class Crop extends AppCompatActivity implements View.OnClickListener {
    public  static MyImage imageView;
    public  static FilterImageThumbnails thumbnail;
    private static String LOG_TAG = "ayusch";
    public  static Bitmap bitmap;
    public  static LinearLayout  mainLayout,cropButtonLayout,imageViewsLayout;
    public  static Button cropButton,setButton,sendButton,rotateButton;
                   ImageView leftImageView,rightImageView;
    String[]      effectsNames;

    static int TARGET_WIDTH=0,TARGET_HEIGHT=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView           = (MyImage)findViewById(R.id.custom_image_view);
        TARGET_HEIGHT       =imageView.getHeight();
        TARGET_WIDTH        =imageView.getWidth();
        Uri imgUri          = getIntent().getParcelableExtra("imageUri");

        try {
            bitmap          = getBitmap(getContentResolver(),imgUri);
            bitmap          = scaleDownBitmap(bitmap,300,this);
          //scale();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(LOG_TAG,""+e);
        }
       // imageView.setImageBitmap(bitmap);
        Glide.with(this).load(getImageUri(getApplicationContext(),bitmap)).asBitmap().into(imageView);

        cropButton          = (Button)findViewById(R.id.crop_button);
        cropButtonLayout    = (LinearLayout)findViewById(R.id.crop_button_layout) ;
        mainLayout          = (LinearLayout)findViewById(R.id.crop_parent_layout);
        effectsNames        = getResources().getStringArray(R.array.effects);
        sendButton          = new Button(this);
        setButton           = new Button(this);
        rotateButton        = (Button)findViewById(R.id.rotate_button);
        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap temp =  ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                imageView.setImageBitmap(RotateBitmap(temp,90));
                temp.recycle();
            }
        });

    filterImageViews();
}


    public class BitmapWorker extends AsyncTask<byte[],Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(byte[]... params) {
            return decodeBitmapFromByteArray(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap ) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }


    public void scale(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray;
        byteArray = stream.toByteArray();

        try{
            stream.close();
        }catch (Exception e ){
            e.printStackTrace();
        }

       new BitmapWorker().execute(byteArray);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    public void onCropClicked(View view){
        TransitionManager.beginDelayedTransition(mainLayout,null);

        leftImageView = new ImageView(this);
        rightImageView = new ImageView(this);

        /*Setting layout properties*/


        Bitmap sourceBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        float[] coordinates = getBitmapCoordinates(sourceBitmap); // coordinates of imageview mapped according to coordinates of actual bitmap(image)

        Bitmap leftBitmap,rightBitmap;
        if (coordinates[0]<=0){
             leftBitmap  = null;
             rightBitmap = Bitmap.createBitmap(sourceBitmap,0,0,sourceBitmap.getWidth(),sourceBitmap.getHeight());

        }else if (coordinates[0]>=sourceBitmap.getWidth()){
            leftBitmap  = Bitmap.createBitmap(sourceBitmap,0,0,sourceBitmap.getWidth(),sourceBitmap.getHeight());
            rightBitmap = null;

        }else {
            leftBitmap  = Bitmap.createBitmap(sourceBitmap,0,0,(int)coordinates[0],sourceBitmap.getHeight());
            rightBitmap = Bitmap.createBitmap(sourceBitmap,(int)coordinates[0],0,(int)(sourceBitmap.getWidth()-coordinates[0]),sourceBitmap.getHeight());
        }



        /*  LinearLayout to hold two imageViews */

        imageViewsLayout = (LinearLayout)findViewById(R.id.imageviews_layout);

        // LAYOUT PARAMS FOR TWO CROPPED IMAGE VIEWS
        LinearLayout.LayoutParams layoutParamsLeft  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        LinearLayout.LayoutParams layoutParamsRight = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);

        // LAYOUT PARAMS FOR SET AND SEND BUTTONS
        LinearLayout.LayoutParams setButtonLayoutParams  = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        LinearLayout.LayoutParams sendButtonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);


        // LEFT IMAGE VIEW
        if(leftBitmap!=null)
        Glide.with(this).load(getImageUri(this,leftBitmap)).asBitmap().fitCenter().into(leftImageView);
        //leftImageView.setImageBitmap(leftBitmap);
        leftImageView.setId(View.generateViewId());
        leftImageView.setOnClickListener(this);

        // RIGHT IMAGE VIEW
        if(rightBitmap!=null)
        Glide.with(this).load(getImageUri(this,rightBitmap)).asBitmap().fitCenter().into(rightImageView);
        //rightImageView.setImageBitmap(rightBitmap);
        rightImageView.setId(View.generateViewId());
        rightImageView.setOnClickListener(this);


        //SET BUTTON
        setButton.setText("Set Wallpaper");
        setButton.setBackgroundColor(Color.RED);
        setButton.setLayoutParams(setButtonLayoutParams);
        setButton.setId(View.generateViewId());
        setButton.setOnClickListener(this);

        // SEND BUTTON
        sendButton.setText("Send");
        sendButton.setBackgroundColor(Color.RED);
        sendButton.setLayoutParams(sendButtonLayoutParams);
        sendButton.setId(View.generateViewId());
        sendButton.setOnClickListener(this);

        // Adding the cropped imageviews to new layout
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

        imageViewsLayout.removeView(imageView);

        // CROP BUTTON
        cropButtonLayout.removeView(cropButton);
        cropButtonLayout.removeView(rotateButton);
        cropButtonLayout.addView(setButton);
        cropButtonLayout.addView(sendButton);

        // to remove filters scroll view
        HorizontalScrollView hsv = (HorizontalScrollView)findViewById(R.id.filters_scrollview);
        hsv.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable borderDrawable = ContextCompat.getDrawable(this, R.drawable.border_style);

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


 View.OnClickListener imgClick = new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         if(v.getId()==BitmapFilter.HDR_STYLE){
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         }else if(v.getId()==BitmapFilter.OIL_STYLE)
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         else if(v.getId()==BitmapFilter.NEON_STYLE)
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         else if(v.getId()==BitmapFilter.OLD_STYLE)
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         else if(v.getId()==BitmapFilter.LOMO_STYLE)
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         else if(v.getId()==BitmapFilter.MOTION_BLUR_STYLE)
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         else if(v.getId()==BitmapFilter.GOTHAM_STYLE)
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         else if(v.getId()==BitmapFilter.TV_STYLE)
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         else if(v.getId()==BitmapFilter.BLOCK_STYLE)
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         else if(v.getId()==BitmapFilter.SKETCH_STYLE)
             Toast.makeText(getApplicationContext(),"Premium filter only ...",Toast.LENGTH_LONG).show();
         else{
            Toast.makeText(getApplicationContext(),"Applying Filter ...",Toast.LENGTH_SHORT).show();
            new MainImageTask().execute(v);
         }
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

    public static Bitmap RotateBitmap(Bitmap source, float angle){
        Log.i(LOG_TAG,"Rotating Bitmap");
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),matrix,true);
    }

    private  int calculateInSampleSize(BitmapFactory.Options bmOptions){
        final int photoWidth = bmOptions.outWidth;
        final int photoHeight = bmOptions.outHeight;
        int scaleFactor = 1;

        if(photoWidth>TARGET_WIDTH || photoHeight > TARGET_HEIGHT){
            final int halfPhotoWidth = photoWidth/2;
            final int halfPhotoHeight = photoHeight/2;
            while(halfPhotoWidth/scaleFactor > TARGET_WIDTH && halfPhotoHeight > TARGET_HEIGHT){
                scaleFactor*=2;
            }
        }
        return scaleFactor;
    }

    private Bitmap decodeBitmapFromByteArray(byte[] byteArray){

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeByteArray(byteArray,0,byteArray.length,bmOptions);

        bmOptions.inSampleSize=calculateInSampleSize(bmOptions);
        bmOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.length,bmOptions);
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


    private void filterImageViews(){

        LinearLayout filterImageLayout = (LinearLayout)findViewById(R.id.filterList);
        LinearLayout imgLayout=null;
        LinearLayout.LayoutParams imgLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup .LayoutParams.MATCH_PARENT);

        Bitmap bmp = bitmap;
      bmp = scaleDownBitmap(bmp,80,this);
        for(int i = 0;i<effectsNames.length;i++){
            thumbnail= new FilterImageThumbnails(getApplicationContext(),effectsNames[i],i);

          //  Glide.with(this).load(getImageUri(this,BitmapFilter.changeStyle(bmp,i))).into(thumbnail);
            thumbnail.setImageBitmap(BitmapFilter.changeStyle(bmp,i));
            thumbnail.setId(i);
            thumbnail.setOnClickListener(imgClick);
            imgLayout = new LinearLayout(this);

            imgLayoutParams.setMargins(5,0,0,0);
            imgLayout.setLayoutParams(imgLayoutParams);
            imgLayout.addView(thumbnail);

            filterImageLayout.addView(imgLayout);
        }


    }
    private static Bitmap resize (Bitmap image,int maxWidth,int maxHeight){
        if(maxHeight>0&&maxWidth>0){
            int width = image.getWidth();
            int height = image.getHeight();
            float rationBitmap = (float)width/(float)height;
            float rationMax = (float)maxWidth/(float)maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;

            if(rationMax>1){
                finalWidth =  (int)((float)maxHeight*rationBitmap);
            }else {
                finalHeight= (int)((float)maxWidth/rationBitmap);
            }
            image = Bitmap.createScaledBitmap(image,finalWidth,finalHeight,true);
            return image;
        }else {
            return image;
        }
    }


    private  class MainImageTask extends AsyncTask<View,int[],Bitmap>{
        int styleNo;
        @Override
        protected Bitmap doInBackground(View...v) {

            switch (v[0].getId()){
                case 0  :styleNo=v[0].getId();
                    return bitmap;
                case 1  :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.GRAY_STYLE);
                case 2  :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.RELIEF_STYLE);
                case 3  :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.AVERAGE_BLUR_STYLE,5);
                case 4  :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.OIL_STYLE);
                case 5  :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.NEON_STYLE);
                case 6  :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.PIXELATE_STYLE,10);
                case 7  :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.TV_STYLE);
                case 8  :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.INVERT_STYLE);
                case 9  :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.BLOCK_STYLE);
                case 10 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.OLD_STYLE,5);
                case 11 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.SHARPEN_STYLE);
                case 12 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.LIGHT_STYLE,bitmap.getWidth()/3,bitmap.getHeight()/2,bitmap.getWidth()/2);
                case 13 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.LOMO_STYLE,(bitmap.getWidth() / 2.0) * 95 / 100.0);
                case 14 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.HDR_STYLE);
                case 15 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.GAUSSIAN_BLUR_STYLE,1.2);
                case 16 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.SOFT_GLOW_STYLE,0.6);
                case 17 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.SKETCH_STYLE);
                case 18 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.MOTION_BLUR_STYLE,10,1);
                case 19 :styleNo=v[0].getId();
                    return BitmapFilter.changeStyle(bitmap,BitmapFilter.GOTHAM_STYLE);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
                 Glide.with(getApplicationContext()).load(getImageUri(getApplicationContext(),bitmap)).asBitmap().into(imageView);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            event.startTracking();
            Log.i(LOG_TAG," inside on Key Up  & repeat count is :"+event.getRepeatCount());
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        HorizontalScrollView hsv = (HorizontalScrollView)findViewById(R.id.filters_scrollview);
        Log.i(LOG_TAG," inside on Key Down");
        if(keyCode==KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()){
            Log.i(LOG_TAG,"Performing removal and addition of views");
            Log.i(LOG_TAG,"Repeat count is :"+event.getRepeatCount());
            if(hsv.getVisibility()==View.VISIBLE) {
                System.gc();
                return super.onKeyUp(keyCode, event);
            }

            imageViewsLayout.addView(imageView);
            imageViewsLayout.removeView(rightImageView);
            imageViewsLayout.removeView(leftImageView);
            // CROP BUTTON
            cropButtonLayout.addView(cropButton);
            cropButtonLayout.addView(rotateButton);
            cropButtonLayout.removeView(setButton);
            cropButtonLayout.removeView(sendButton);

            hsv.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

}
