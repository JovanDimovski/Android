package com.example.jovan.app3.Utilities;

import android.content.Context;
import android.graphics.Canvas;
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
public class ZoomableRelativeLayoutOld extends RelativeLayout {
    float mScaleFactor = 1;
    float mPivotX;
    float mPivotY;
    int MIN_SCALE = 1;
    int MAX_SCALE = 10;

    ///////////////////////////////Ovaj del e za panning////////////////////////////
    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;
    ///////////////////////////////Ovaj del e za panning////////////////////////////


    //////////////////////////////Test//////////////////////////////////////////////
    private boolean oneFinger = false;
    private int twoFingerStart = 0;
    private double distStart, distEnd, distScale;
    //////////////////////////////Test//////////////////////////////////////////////





    public ZoomableRelativeLayoutOld(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ZoomableRelativeLayoutOld(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ZoomableRelativeLayoutOld(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    protected void dispatchDraw(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        //if(oneFinger)
        canvas.translate(mPosX, mPosY);
        //else
        canvas.scale(mScaleFactor, mScaleFactor);

        Log.d("PANZOOM", "OneFinger: "+oneFinger);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    /*public void scale(float scaleFactor, float pivotX, float pivotY) {
        mScaleFactor = scaleFactor;
        mPivotX = pivotX;
        mPivotY = pivotY;
        this.invalidate();
    }*/

    public void restore() {
        //mScaleFactor = 1;
        this.invalidate();
    }

    public float relativeScale(float scaleFactor, float pivotX, float pivotY)
    {

        mScaleFactor *= scaleFactor;
        if(mScaleFactor < 1) mScaleFactor = 1;
        if(mScaleFactor > 10) mScaleFactor = 10;
        if(scaleFactor >= 1)
        {
            mPivotX = mPivotX + (pivotX - mPivotX) * (1 - 1 / scaleFactor);
            mPivotY = mPivotY + (pivotY - mPivotY) * (1 - 1 / scaleFactor);
        }
        else
        {
            pivotX = getWidth()/2;
            pivotY = getHeight()/2;

            mPivotX = mPivotX + (pivotX - mPivotX) * (1 - scaleFactor);
            mPivotY = mPivotY + (pivotY - mPivotY) * (1 - scaleFactor);
        }

        this.invalidate();
        return mScaleFactor;
    }

    /*public void release() {
        *//*if(mScaleFactor < MIN_SCALE)
        {
            final float startScaleFactor = mScaleFactor;

            Animation a = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t)
                {
                    scale(startScaleFactor + (MIN_SCALE - startScaleFactor)*interpolatedTime,mPivotX,mPivotY);
                }
            };

            a.setDuration(300);
            startAnimation(a);
        }
        else if(mScaleFactor > MAX_SCALE)
        {
            final float startScaleFactor = mScaleFactor;

            Animation a = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t)
                {
                    scale(startScaleFactor + (MAX_SCALE - startScaleFactor)*interpolatedTime,mPivotX,mPivotY);
                }
            };

            a.setDuration(300);
            startAnimation(a);
        }*//*
    }*/

    ///////////////////////////////Ovaj del e za panning////////////////////////////
    ///////////////////////////////Ovaj del e za panning////////////////////////////
    ///////////////////////////////Ovaj del e za panning////////////////////////////

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean ret = super.dispatchTouchEvent(ev);

        Log.d("PANZOOM", "ON TOUCH EVENT");
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                //oneFinger = !oneFinger;
                final float x = ev.getX();
                final float y = ev.getY();

                // Remember where we started
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                //oneFinger = true;
                Log.d("PANZOOM", "ONE FINGER");
                final float x = ev.getX();
                final float y = ev.getY();

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                // Move the object
                mPosX += dx;
                mPosY += dy;

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                // Invalidate to request a redraw
                invalidate();
                break;
            }
            *//*default:
                oneFinger = false;*//*
        }

        return ret;
    }*/
    private static final int INVALID_POINTER_ID = -1;

    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean ret = super.dispatchTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer
                mActivePointerId = ev.getPointerId(0);
                Log.d("PANZOOM", "Pointer ID: "+mActivePointerId);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                double x1= -1,x2=-1,y1=-1,y2=-1, dist=0, SF=1;
                Log.d("PANZOOM", "Number of pointers: "+ev.getPointerCount());
                if(ev.getPointerCount() == 1) {
                    oneFinger = true;
                    if(twoFingerStart == 0)
                        twoFingerStart = 1;
                }
                else if (ev.getPointerCount() == 2) {
                    x1=ev.getX(ev.findPointerIndex(ev.getPointerId(0)));
                    y1=ev.getY(ev.findPointerIndex(ev.getPointerId(0)));
                    x2=ev.getX(ev.findPointerIndex(ev.getPointerId(1)));
                    y2=ev.getY(ev.findPointerIndex(ev.getPointerId(1)));
                    dist = Math.pow(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2),0.5);
                    oneFinger = false;
                    if(twoFingerStart == 1)
                        distStart = dist;
                    distEnd = dist;
                    if(dist > 0)
                        twoFingerStart = 2;
                }
                distScale = distEnd/distStart;

                //Log.d("PANZOOM", "P1: "+x1+", "+y1+"P2: "+x2+", "+y2+"DISTANCE: "+dist);

                Log.d("PANZOOM", "START: "+distStart+"END: "+distEnd+"SCALE: "+distScale);

                // Find the index of the active pointer and fetch its position
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                final float dx = (x - mLastTouchX)/mScaleFactor;
                final float dy = (y - mLastTouchY)/mScaleFactor;

                mPosX += dx;
                mPosY += dy;

                if(mPosX > 0) mPosX = 0;
                if(mPosY > 0) mPosY = 0;
                if(mPosX + (540 - 540/mScaleFactor)< 0) mPosX = -540 + 540/mScaleFactor;
                if(mPosY + (922 - 922/mScaleFactor)< 0) mPosY = -922 + 922/mScaleFactor;

                int tx = getWidth();//getMeasuredWidth();
                int ty = getHeight();

                //Log.d("PANZOOM", "X: "+mPosX+" Y: "+mPosY+" SCALE: "+mScaleFactor);

                mLastTouchX = x;
                mLastTouchY = y;

                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return ret;
    }
}

