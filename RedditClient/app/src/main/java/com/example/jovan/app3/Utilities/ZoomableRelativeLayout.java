package com.example.jovan.app3.Utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * Created by Jovan on 17-Sep-16.
 */
public class ZoomableRelativeLayout extends RelativeLayout {

    public ZoomableRelativeLayout(Context context) {super(context);}
    public ZoomableRelativeLayout(Context context, AttributeSet attrs) {super(context, attrs);}
    public ZoomableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle);}
    public void restore() {this.invalidate();}
    public void release(){}
    public float relativeScale(float scaleFactor, float pivotX, float pivotY) {return 0;}

    //////////////////////////////////...............................\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public float translateX=0, translateY=0;
    public float touchX=0, touchY=0;
    public boolean firstTouch = false, twoFinger = false;
    public float startDistX, startDistY, distX, distY, scaleFactor = 1,scaleFactorPrevious = 1;
    public int pointerCount = -1, lastPointerCount = -1;
    public Matrix M = new Matrix();
    public Matrix P = new Matrix();
    public String text = "", append ="";

    protected void dispatchDraw(Canvas canvas)
    {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.concat(M);
        super.dispatchDraw(canvas);
        canvas.restore();
        /*Paint mp = new Paint();
        mp.setColor(Color.GREEN);
        mp.setTextSize(24);
        mp.setTypeface(Typeface.create("Serif",Typeface.NORMAL));
        canvas.drawText(text, 30, 60, mp);*/

        TextPaint tp = new TextPaint();
        tp.setColor(Color.GREEN);
        tp.setTextSize(24);
        tp.setTypeface(Typeface.create("Serif",Typeface.NORMAL));

        StaticLayout sl = new StaticLayout(text+append, tp, (int)getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);

        sl.draw(canvas);
        canvas.restore();
    }
    public void logF(String source, MotionEvent ev, int rpi){

        //Log.d("PANZOOM", "CP: " + pointerCount + " PP: " + lastPointerCount);
        if(!source.equalsIgnoreCase("MOVE")){
            append = "";
            text = source + ": \n";
            text += "CP:" + pointerCount+ "PP:" + lastPointerCount+'\n';
            int limit = pointerCount;
            for(int i=0;i<limit;i++)
            {
                if(i == rpi){
                    limit+=1;
                    continue;
                }
                String temp =
                        "X: " + (int)ev.getX(ev.findPointerIndex(ev.getPointerId(i))) +
                        "Y: " + (int)ev.getY(ev.findPointerIndex(ev.getPointerId(i))) +'\n';
                //Log.d("PANZOOM", temp);
                text += temp;
            }
        }
        else{
            append = source + ": \n";
            append += "CP:" + pointerCount+ "PP:" + lastPointerCount+'\n';
            int limit = pointerCount;
            for(int i=0;i<limit;i++)
            {
                if(i == rpi){
                    limit+=1;
                    continue;
                }
                String temp =
                        "X: " + (int)ev.getX(ev.findPointerIndex(ev.getPointerId(i))) +
                                "Y: " + (int)ev.getY(ev.findPointerIndex(ev.getPointerId(i))) +'\n';
                //Log.d("PANZOOM", temp);
                append += temp;
            }
        }
    }
    public void setLimits(){

        float[] values = new float[9];
        M.getValues(values);
        float globalX = values[Matrix.MTRANS_X];
        float globalY = values[Matrix.MTRANS_Y];
        float width = values[Matrix.MSCALE_X]*getMeasuredWidth();
        float height = values[Matrix.MSCALE_Y]*getMeasuredHeight();

                    /*Log.d("PANZOOM", " X: "+(int)globalX+
                            " Y: "+(int)globalY+
                            " W: "+(int)width+
                            " H: "+(int)height+
                            " DX: "+(int)(globalX+width)+
                            " DY: "+(int)(globalY+height)+
                            " MW: "+getMeasuredWidth()+
                            " MH: "+getMeasuredHeight());*/

        ////////// SCALE LIMITS ////////
        if(values[Matrix.MSCALE_X] < 1)
            values[Matrix.MSCALE_X] = 1;

        if(values[Matrix.MSCALE_Y] < 1)
            values[Matrix.MSCALE_Y] = 1;

        if(values[Matrix.MSCALE_X] > 100)
            values[Matrix.MSCALE_X] = 100;

        if(values[Matrix.MSCALE_Y] > 100)
            values[Matrix.MSCALE_Y] = 100;
        ////////// SCALE LIMITS ////////

        if(globalX >= 0)
            values[Matrix.MTRANS_X] = 0;
        if(globalY >= 0)
            values[Matrix.MTRANS_Y] = 0;

        if(globalX+width <= getMeasuredWidth())
            values[Matrix.MTRANS_X] = getMeasuredWidth() - width;

        if(width <= getMeasuredWidth())
            values[Matrix.MTRANS_X] = 0;

        if(width > getMeasuredWidth()*100 && globalX < getMeasuredWidth() - getMeasuredWidth()*100)
            values[Matrix.MTRANS_X] = getMeasuredWidth() - getMeasuredWidth()*100;

        if(globalY+height <= getMeasuredHeight())
            values[Matrix.MTRANS_Y] = getMeasuredHeight() - height;

        if(height <= getMeasuredHeight())
            values[Matrix.MTRANS_Y] = 0;

        if(height >= getMeasuredHeight()*100 && globalY < getMeasuredHeight() - getMeasuredHeight()*100)
            values[Matrix.MTRANS_Y] = getMeasuredHeight() - getMeasuredHeight()*100;

        M.setValues(values);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean ret = super.dispatchTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN: {
                Log.d("PANZOOM", String.valueOf(pointerCount));
                if(pointerCount == 2)/*mislam nisto ne pravi*/{
                    P.set(M);
                    firstTouch = false;
                }
                if(pointerCount == 1){
                    P.set(M);
                    firstTouch = false;
                }

                if(pointerCount == -1)
                    pointerCount = 0;
                lastPointerCount = pointerCount;
                pointerCount = ev.getPointerCount();
                logF("POINTER_DOWN", ev, -1);
                break;
            }
            case MotionEvent.ACTION_DOWN: {

                if(pointerCount == -1)
                    pointerCount = 0;
                lastPointerCount = pointerCount;
                pointerCount = ev.getPointerCount();

                logF("DOWN", ev, -1);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float x1, x2, y1, y2;
                logF("MOVE", ev, -1);

                if(ev.getPointerCount() == 1) {
                    x1 = ev.getX(ev.findPointerIndex(ev.getPointerId(0)));
                    y1 = ev.getY(ev.findPointerIndex(ev.getPointerId(0)));
                    if(x1 > 0 && !firstTouch) {
                        touchX = x1;
                        touchY = y1;
                        firstTouch = true;
                    }
                    translateX = x1 - touchX;
                    translateY = y1 - touchY;

                    M.set(P);
                    P.set(M);

                    Matrix T = new Matrix();
                    T.postTranslate(translateX, translateY);
                    M.postConcat(T);
                    setLimits();
                }
                if (ev.getPointerCount() == 2) {
                    x1 = ev.getX(ev.findPointerIndex(ev.getPointerId(0)));
                    x2 = ev.getX(ev.findPointerIndex(ev.getPointerId(1)));
                    y1 = ev.getY(ev.findPointerIndex(ev.getPointerId(0)));
                    y2 = ev.getY(ev.findPointerIndex(ev.getPointerId(1)));
                    if((x1 + x2) / 2 > 0 && !firstTouch) {
                        touchX = (x1 + x2) / 2;
                        touchY = (y1 + y2) / 2;

                        firstTouch = true;
                        startDistX = Math.abs(x1-x2);
                        startDistY = Math.abs(y1-y2);
                        Log.d("PANZOOM", "First touch");
                    }
                    translateX = ((x1 + x2) / 2 - touchX);
                    translateY = ((y1 + y2) / 2 - touchY);
                    Matrix T = new Matrix();
                    M.set(P);
                    P.set(M);

                    distX = Math.abs(x1-x2);
                    distY = Math.abs(y1-y2);
                    scaleFactor = (float) (Math.pow(distX*distX+distY*distY, 0.5)/Math.pow(startDistX*startDistX+startDistY*startDistY, 0.5));

                    Log.d("PANZOOM", String.valueOf(scaleFactor));

                    T = new Matrix();
                    T.postScale(scaleFactor,scaleFactor,touchX,touchY);
                    M.postConcat(T);

                    T = new Matrix();
                    T.postTranslate(translateX, translateY);
                    M.postConcat(T);
                    setLimits();
                }
                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {//TUKA SE POVIKUVA TOA {TO TREBA PO ZAVRSUVAWE O DVIZENJE
                /*P.set(M);
                firstTouch = false;
                scaleFactorPrevious *= scaleFactor;
*/
                if(pointerCount == 1){
                    P.set(M);
                    firstTouch = false;
                }

                int removedPointerIndex = ev.getActionIndex();

                lastPointerCount = pointerCount;
                pointerCount = ev.getPointerCount() - 1;

                logF("UP", ev, removedPointerIndex);

                break;
            }

            case MotionEvent.ACTION_CANCEL: {break;}
            case MotionEvent.ACTION_POINTER_UP: {
                if(pointerCount == 2){
                    P.set(M);
                    firstTouch = false;
                    scaleFactorPrevious *= scaleFactor;
                }


                int removedPointerIndex = ev.getActionIndex();

                lastPointerCount = pointerCount;
                pointerCount = ev.getPointerCount() - 1;

                logF("POINTER_UP", ev, removedPointerIndex);

                break;
            }
        }


        return ret;
    }
}

