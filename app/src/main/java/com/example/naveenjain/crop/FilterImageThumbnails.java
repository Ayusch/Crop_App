package com.example.naveenjain.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Naveen Jain on 18-05-2016.
 */
public class FilterImageThumbnails extends ImageView {
    String text;
    Paint paint = new Paint();
    Paint rectPaint = new Paint();

    public FilterImageThumbnails(Context context,String text){
        super(context);
        this.text=text;
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
        //paint.setAlpha(30);

        rectPaint.setColor(Color.BLACK);
        rectPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,canvas.getHeight()-canvas.getHeight()/4,canvas.getWidth(),canvas.getWidth(),rectPaint);
        canvas.drawText(text,10,canvas.getHeight()-5,paint);
    }

    public float getTextWidth(String text){
        Rect bounds = new Rect();
        Paint textPaint = new Paint();
        textPaint.getTextBounds(text,0,0,bounds);
        return bounds.width();
    }
}
