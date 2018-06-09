package com.example.jovan.app3.Utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
public class ZoomableRelativeLayoutMatrixOnlyScaleNoTranslate extends RelativeLayout {

    public ZoomableRelativeLayoutMatrixOnlyScaleNoTranslate(Context context) {super(context);}
    public ZoomableRelativeLayoutMatrixOnlyScaleNoTranslate(Context context, AttributeSet attrs) {super(context, attrs);}
    public ZoomableRelativeLayoutMatrixOnlyScaleNoTranslate(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle);}
    public void restore() {this.invalidate();}
    public void release(){}
    public float relativeScale(float scaleFactor, float pivotX, float pivotY) {return 0;}

    //////////////////////////////////...............................\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public float translateX=0, translateY=0;
    public float touchX=0, touchY=0, touchXPrevious = 0, touchYPrevious = 0;
    public float singleTouchX = 0, singleTouchY = 0, singleTranslateX = 0, singleTranslateY = 0;
    public float touchXVirtual, touchYVirtual, touchXVirtualPrevious = -1, touchYVirtualPrevious = -1, virtualXoffset = 0, virtualYoffset = 0;
    public float translateXPrevious=0, translateYPrevious=0;
    public boolean firstTouch = false,firstTouchS = false, twoFinger = false;
    public float startDistX, startDistY, distX, distY, scaleFactor = 1,scaleFactorPrevious = 1;
    public float postScaleTranslateX = 0, postScaleTranslateY = 0, scaleOffsetX, scaleOffsetY;

    protected void dispatchDrawOld(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        //NE PRAVI NISTO PRIVREMENO //
        canvas.translate(translateXPrevious + translateX, translateYPrevious + translateY);
        //NE PRAVI NISTO PRIVREMENO //

        canvas.translate(scaleOffsetX,scaleOffsetY);

        canvas.translate(virtualXoffset, virtualYoffset);

        canvas.scale(scaleFactorPrevious * scaleFactor,scaleFactorPrevious * scaleFactor, 0, 0);

        /*canvas.translate(virtualXoffset*(2-scaleFactor),
                         virtualYoffset*(2-scaleFactor));*/

        super.dispatchDraw(canvas);

        canvas.restore();

        Paint mypaint = new Paint();
        mypaint.setColor(Color.CYAN);
        canvas.drawRect(0,touchY,getMeasuredWidth(),touchY + 1, mypaint);
        canvas.drawRect(touchX,0,touchX + 1,getMeasuredHeight(), mypaint);

        mypaint.setColor(Color.GREEN);
        canvas.drawRect(0,touchYVirtual,getMeasuredWidth(),touchYVirtual + 1, mypaint);
        canvas.drawRect(touchXVirtual,0,touchXVirtual + 1,getMeasuredHeight(), mypaint);
    }

    //////////////////////////////////////////MATRIX////////////////////////////////////////////////
    public Matrix M = new Matrix();
    public Matrix P = new Matrix();

    protected void dispatchDraw(Canvas canvas)
    {
        canvas.concat(M);
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean ret = super.dispatchTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float x1, x2, y1, y2;
                if(ev.getPointerCount() == 1) {
                    /*twoFinger = false;
                    x1 = ev.getX(ev.findPointerIndex(ev.getPointerId(0)));
                    y1 = ev.getY(ev.findPointerIndex(ev.getPointerId(0)));
                    if(x1 > 0 && !firstTouchS){
                        singleTouchX = x1;
                        singleTouchY = y1;
                        firstTouchS = true;
                    }
                    singleTranslateX = x1 - singleTouchX;
                    singleTranslateY = y1 - singleTouchY;

                    Matrix T = new Matrix();
                    M.set(P);
                    P.set(M);

                    T = new Matrix();
                    T.postTranslate(singleTranslateX, singleTranslateY);
                    M.postConcat(T);
                    Log.d("PANZOOM", " Single finger: "+(int)x1+","+(int)y1+" STX:"+singleTranslateX+" STY:"+singleTranslateY);*/
                }
                else if (ev.getPointerCount() == 2) {
                    twoFinger = true;
                    x1 = ev.getX(ev.findPointerIndex(ev.getPointerId(0)));
                    x2 = ev.getX(ev.findPointerIndex(ev.getPointerId(1)));
                    y1 = ev.getY(ev.findPointerIndex(ev.getPointerId(0)));
                    y2 = ev.getY(ev.findPointerIndex(ev.getPointerId(1)));
                    if((x1 + x2) / 2 > 0 && !firstTouch) {
                        touchX = (x1 + x2) / 2;
                        touchY = (y1 + y2) / 2;
                        touchXVirtual = (touchX - (postScaleTranslateX + translateXPrevious + translateX + virtualXoffset))/(scaleFactorPrevious);
                        touchYVirtual = (touchY - (postScaleTranslateY + translateYPrevious + translateY + virtualYoffset))/(scaleFactorPrevious);
                        virtualXoffset = (touchX - touchXVirtual)/scaleFactorPrevious;
                        virtualYoffset = (touchY - touchYVirtual)/scaleFactorPrevious;

                        firstTouch = true;
                        startDistX = Math.abs(x1-x2);
                        startDistY = Math.abs(y1-y2);
                    }
                    translateX = ((x1 + x2) / 2 - touchX);
                    translateY = ((y1 + y2) / 2 - touchY);
                    Matrix T = new Matrix();
                    M.set(P);
                    P.set(M);


                    //////////PRIVREMENO JA ODSTRANUVAM TRANSLACIJATA////////
                    //translateX = 0; translateY = 0;
                    //////////PRIVREMENO JA ODSTRANUVAM TRANSLACIJATA////////

                    distX = Math.abs(x1-x2);
                    distY = Math.abs(y1-y2);
                    scaleFactor = (float) (Math.pow(distX*distX+distY*distY, 0.5)/Math.pow(startDistX*startDistX+startDistY*startDistY, 0.5));

                    scaleOffsetX = (touchXVirtual)*(1 - scaleFactorPrevious * scaleFactor) +(touchXVirtual-touchX)/(scaleFactorPrevious * scaleFactor)/*- (touchXVirtual - touchX)*/;
                    scaleOffsetY = (touchYVirtual)*(1 - scaleFactorPrevious * scaleFactor) +(touchYVirtual-touchY)/(scaleFactorPrevious * scaleFactor)/*- (touchYVirtual - touchY)*/;

                    /*if(scaleFactor*scaleFactorPrevious < 1)
                        scaleFactor = 1/scaleFactorPrevious;
                    if(scaleFactor*scaleFactorPrevious > 10)
                        scaleFactor = 10/scaleFactorPrevious;*/

                    T = new Matrix();
                    T.postScale(scaleFactor,scaleFactor,touchX,touchY);
                    M.postConcat(T);

                    T = new Matrix();
                    T.postTranslate(translateX, translateY);
                    M.postConcat(T);

                    float[] values = new float[9];
                    M.getValues(values);
                    float globalX = values[Matrix.MTRANS_X];
                    float globalY = values[Matrix.MTRANS_Y];
                    float width = values[Matrix.MSCALE_X]*getMeasuredWidth();
                    float height = values[Matrix.MSCALE_Y]*getMeasuredHeight();

                    Log.d("PANZOOM", " X: "+(int)globalX+
                                     " Y: "+(int)globalY+
                                     " W: "+(int)width+
                                     " H: "+(int)height+
                                     " DX: "+(int)(globalX+width)+
                                     " DY: "+(int)(globalY+height)+
                                     " MW: "+getMeasuredWidth()+
                                     " MH: "+getMeasuredHeight());

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
                setPivotX(getMeasuredWidth());
                setPivotY(getMeasuredHeight());


                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {//TUKA SE POVIKUVA TOA {TO TREBA PO ZAVRSUVAWE O DVIZENJE
                /*if(twoFinger) {*/
                    /////////////////////MATRIX//////////////////
                    P.set(M);
                    firstTouch = false;
                    scaleFactorPrevious *= scaleFactor;
                    /////////////////////MATRIX//////////////////
                /*}else{
                    P.set(M);
                    firstTouchS = false;
                }*/
                /*postScaleTranslateX = scaleOffsetX;
                postScaleTranslateY = scaleOffsetY;

                touchXVirtualPrevious = touchXVirtual;
                touchYVirtualPrevious = touchYVirtual;

                firstTouch = false;
                translateXPrevious += translateX;
                translateYPrevious += translateY;
                translateX = 0;
                translateY = 0;
                scaleFactorPrevious *= scaleFactor;
                scaleFactor = 1;*/
                break;
            }

            case MotionEvent.ACTION_CANCEL: {break;}
            case MotionEvent.ACTION_POINTER_UP: {break;}
        }

        return ret;
    }
}

