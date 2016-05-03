package com.example.naveenjain.crop;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private String LOG_TAG="ayusch";

    private String selectedImagePath;
    MyImage imageView;
    Button loadButton,cameraButton;
    LinearLayout buttonsLayout;
    Uri selectedImage;

    private static int RESULT_LOAD_IMAGE = 1;
    private static int REQUEST_CAMERA_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (MyImage) findViewById(R.id.custom_image_view);
        loadButton= (Button)findViewById(R.id.load_button);
        cameraButton=(Button)findViewById(R.id.camera_button);
        buttonsLayout= (LinearLayout) findViewById(R.id.buttons_layout);
    }

    public void selectButtonClicked(View v) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void cameraButtonClicked(View v){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i,REQUEST_CAMERA_CAPTURE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.desert);
        if(resultCode==RESULT_OK){
            if(requestCode==RESULT_LOAD_IMAGE){
                if (data.getData()==null){
                    bmp = (Bitmap)data.getExtras().get("data");
                }else{
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if(requestCode==REQUEST_CAMERA_CAPTURE){
                Bundle extras = data.getExtras();
                bmp = (Bitmap)extras.get("data");
            }
        }
        Log.i(LOG_TAG,"This is the bitmap in mainActivity :"+bmp);

           // bmp = scaleDownBitmap(bmp,150,this);

        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG,100,ostream);
        byte[] bytes = ostream.toByteArray();


            Intent i = new Intent(MainActivity.this,Crop.class);

            i.putExtra("bitmap",bytes);
            startActivity(i);


    }



}
