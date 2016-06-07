package com.example.naveenjain.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.Ragnarok.BitmapFilter;

/**
 * Created by Naveen Jain on 18-05-2016.
 */
public class FilterImageThumbnails extends ImageView {
                  String text;
                  int styleNo;
   public   Paint paint = new Paint();
   public   Paint rectPaint = new Paint();

    public FilterImageThumbnails(Context context,String text,int styleNo){
        super(context);
        this.text=text;
        this.styleNo = styleNo;
        init(null,0);
    }

    public FilterImageThumbnails(Context context) {
        super(context);
        init(null,0);
    }

    public FilterImageThumbnails(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public FilterImageThumbnails(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,0);
    }

    public void init(AttributeSet attrs,int defStyleAttr){
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(25);

        rectPaint.setColor(Color.BLACK);
        rectPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(styleNo == BitmapFilter.OIL_STYLE || styleNo == BitmapFilter.NEON_STYLE || styleNo == BitmapFilter.OLD_STYLE || styleNo == BitmapFilter.LOMO_STYLE || styleNo == BitmapFilter.MOTION_BLUR_STYLE || styleNo == BitmapFilter.GOTHAM_STYLE || styleNo == BitmapFilter.TV_STYLE || styleNo == BitmapFilter.BLOCK_STYLE || styleNo == BitmapFilter.SKETCH_STYLE){
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD,Typeface.BOLD));
            paint.setColor(Color.RED);
            paint.setTextSize(20);
            String prem = "PREMIUM";
           // setTextSizeForWidth(paint,canvas.getWidth(),prem);
            drawText(canvas,paint,prem);
        }
      //  setTextSizeForWidth(paint,canvas.getWidth(),text);
        canvas.drawRect(0, canvas.getHeight() - canvas.getHeight() / 4, canvas.getWidth(), canvas.getWidth(), rectPaint);
        canvas.drawText(text, 10, canvas.getHeight() - 5, paint);
    }

    public static void drawText(Canvas canvas,Paint paint, String text){
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,text.length(),bounds);
        int x = (canvas.getWidth()/2)-(bounds.width()/2);
        int y = (canvas.getHeight()/2)-(bounds.height()/2);
        canvas.drawText(text,x,y,paint);
    }

    public static void setTextSizeForWidth(Paint paint,float maxWidth,String text){
        final float testSize =1f;
        paint.setTextSize(testSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,text.length(),bounds);

        float desiredSize = testSize * (maxWidth/bounds.width());
        paint.setTextSize(desiredSize);

    }
}
