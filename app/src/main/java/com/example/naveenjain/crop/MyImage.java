package com.example.naveenjain.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Naveen Jain on 09-04-2016.
 */
public class MyImage extends ImageView {

    float touchX, touchY;
    private Path path=new Path();
    private Paint paint= new Paint();

    public MyImage(Context context) {
        super(context);
        init(null,0);
    }

    public MyImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    public void init(AttributeSet attrs,int defStyleAttr){
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAlpha(150);
        paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(touchX,0,touchX,canvas.getHeight(),paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        touchX = event.getX();

        touchY = event.getY();

/*
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX,touchY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
*/
        invalidate();
        return  true;
        }
    }
