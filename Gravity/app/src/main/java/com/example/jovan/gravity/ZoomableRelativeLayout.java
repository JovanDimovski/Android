package com.example.jovan.gravity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by Jovan on 17-Sep-16.
 */
public class ZoomableRelativeLayout extends RelativeLayout {
    public static final int MAX = 900;
    public float xc = 270, yc = 460, fc = 0.05f;
    public float xo = 600, yo = 900, fo = 0.05f;
    public float gc = 0.05f*30;
    public float x = 0, y = 0, vx = 5, vy = 1;
    public float ax[] = new float[MAX],
            ay[] = new float[MAX],
            avx[] = new float[MAX],
            avy[] = new float[MAX],
            af[] = new float[MAX];
    public int pe[] = new int[MAX];

    public float grid[][] = new float[10][10];


    public Path path[] = new Path[MAX];
    public int whoToFollow = 0;

    public ArrayList<Float> HX[]= new ArrayList[MAX];
    public ArrayList<Float> HY[]= new ArrayList[MAX];

    public boolean aclt30 = false;

    public boolean click = false;
    public boolean moveF = true;
    public float curentScale = 1;

    public ZoomableRelativeLayout(Context context) {super(context);}
    public ZoomableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d("TEST", "NEW TEST");
        for(int i =0; i < MAX; i++)
        {
            ax[i] = (float)(Math.random())*540;//270+135;
            ay[i] = (float)(Math.random())*540;//270+135;
            double tx = ax[i]-270;
            double ty = ay[i]-270;
            double d = (float) Math.pow(Math.pow(tx, 2) + Math.pow(ty, 2), 0.5);
            double fx = -tx / d;
            double fy = ty / d;
            avx[i] = (float)(fy*d/(540*20));//(float)(Math.random()-0.5)*0.1f;
            avy[i] = (float)(fx*d/(540*20));//(float)(Math.random()-0.5)*0.1f;
            af[i] = 0.05f;
            pe[i] = 1;
            path[i] = new Path();
            path[i].moveTo(ax[i], ay[i]);
            HX[i] = new ArrayList<>();
            HY[i] = new ArrayList<>();
        }
    }
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
        //////////////////////////////////////////
        //////////////SQUARE GRID/////////////////
        /*Paint gp = new Paint();
        for(int i=0;i<10;i++)
            for(int j=0;j<10;j++)
            {
                //gp.setColor(Color.rgb((int)(8*grid[i][j]),(int)(8*grid[i][j]),(int)(8*grid[i][j])));
                if(grid[i][j] > 0)
                    gp.setColor(Color.BLUE);
                else
                    gp.setColor(Color.BLACK);
                canvas.drawRect(
                        this.getMeasuredWidth()/10*i,
                        this.getMeasuredWidth()/10*j+230,
                        this.getMeasuredWidth()/10*(i+1),
                        this.getMeasuredWidth()/10*(j+1)+230,gp);
            }*/


        //////////////SQUARE GRID/////////////////
        //////////////////////////////////////////

        Paint myPaint = new Paint();
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
        Log.d("OBJP", "cs: "+curentScale);
        paint.setStrokeWidth(1/curentScale);
        paint.setPathEffect(null);
        paint.setStyle(Paint.Style.STROKE);

        for(int i = 0; i < MAX; i++)
        {
            int r = 255, g =255-(int)(af[i]*5), b =220-(int)(af[i]*10);
            if(g<0) g=0;
            if(b<0) b=0;
            myPaint.setColor(Color.rgb(r,g,b));
            if(pe[i] == 1) {
                canvas.drawCircle(ax[i], ay[i], (float) Math.pow(af[i] * 20, 0.25)*2, myPaint);
                if(aclt30) {
                    paint.setColor(Color.rgb(r, g, b));
                    canvas.drawPath(path[i], paint);
                }
            }
        }


        //////////////////////////////////////////

        canvas.restore();

/*
        TextPaint tp = new TextPaint();
        tp.setColor(Color.GREEN);
        tp.setTextSize(24);
        tp.setTypeface(Typeface.create("Serif",Typeface.NORMAL));

        StaticLayout sl = new StaticLayout(text+append, tp, (int)getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1, 1, false);

        sl.draw(canvas);*/
        canvas.restore();
    }

    /*public void runEvery40ms(){
        for(int j = 0; j < MAX; j++) {
            if(!pe[j])
                continue;
            for (int i = 0; i < MAX; i++) {
                if(!pe[i])
                    continue;
                if(i!=j)
                {
                    float tx = ax[i] - ax[j], ty = ay[i] - ay[j], d, fx, fy;
                    ax[i] = ax[i] + avx[i];
                    ay[i] = ay[i] + avy[i];
                    *//*if(i == whoToFollow)
                        path.lineTo(ax[i], ay[i]);*//*
                    *//*HX[i].add(ax[i]);
                    HY[i].add(ay[i]);
                    Log.d("TEST", String.valueOf(HX[i].size()));
                    if(HX[i].size() > 500) {
                        HX[i].remove(0);
                        HY[i].remove(0);
                    }
                    //path[i].lineTo(ax[i], ay[i]);
                    path[i] = new Path();
                    path[i].moveTo(HX[i].get(0),HY[i].get(0));
                    for(int k = 0; k < HX[i].size(); k++)
                    {
                        path[i].lineTo(HX[i].get(k), HY[i].get(k));
                    }*//*


                    d = (float) Math.pow(Math.pow(tx, 2) + Math.pow(ty, 2), 0.5);
                    fx = -tx / d;
                    fy = -ty / d;
                    avx[i] = avx[i] + gc * (af[j] / (d)) * fx;
                    avy[i] = avy[i] + gc * (af[j] / (d)) * fy;

                    if(d < 2*(float)Math.pow(af[i]*20,0.33)) {
                        pe[j] = false;
                        *//*if(whoToFollow == j)
                            whoToFollow = i;*//*
                        avx[i] = avx[i]*((af[i])/(af[i]+af[j]))+ avx[j]*((af[j])/(af[i]+af[j]));
                        avy[i] = avy[i]*((af[i])/(af[i]+af[j]))+ avy[j]*((af[j])/(af[i]+af[j]));
                        af[i] += af[j];
                    }
                }
                *//*float tx = ax[i] - xc, ty = ay[i] - yc, d, fx, fy;
                ax[i] = ax[i] + avx[i];
                ay[i] = ay[i] + avy[i];
                d = (float) Math.pow(Math.pow(tx, 2) + Math.pow(ty, 2), 0.5);
                fx = -tx / d;
                fy = -ty / d;
                avx[i] = avx[i] + 500 * (fc / (d)) * fx;
                avy[i] = avy[i] + 500 * (fc / (d)) * fy;*//*
*//*
            tx = ax[i] - xo;
            ty = ay[i] - yo;
            d = (float) Math.pow(Math.pow(tx, 2) + Math.pow(ty, 2), 0.5);
            fx = -tx / d;
            fy = -ty / d;
            avx[i] = avx[i] + 500 * (fo / (d)) * fx;
            avy[i] = avy[i] + 500 * (fo / (d)) * fy;

            ax[i] = ax[i] + avx[i];
            ay[i] = ay[i] + avy[i];*//*
            }
        }
        Log.d("adas", "vx: " + avx[0] + " vy: " + avy[0]);

        this.invalidate();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runEvery40ms();
            }
        }, 1);
    }*/

    public void runEvery40ms(float ax[], float ay[], float af[],int pe[]){
        this.ax = ax;
        this.ay = ay;
        this.af = af;
        this.pe = pe;

        float []m = new float[9];
        M.getValues(m);
        curentScale = m[Matrix.MSCALE_X];

        if(!aclt30) {
            int activeCount = 0;
            for (int i = 0; i < MAX; i++) {
                if (pe[i] == 1)
                    activeCount++;
            }
            if(activeCount < 100)
                aclt30 = true;
        }
        else{
            for(int i = 0;i < MAX; i ++) {
                if(pe[i] == 1) {
                    if(click){
                        Log.d("CLICK", "Clearing Paths");
                        Log.d("CLICK", "HX[0] before" + HX[0]);
                        HX[i].clear();
                        Log.d("CLICK", "HX[0] after" + HX[0]);
                        HY[i].clear();
                    }
                    HX[i].add(ax[i]);
                    HY[i].add(ay[i]);
                    Log.d("CLICK", String.valueOf(HX[i].size()));
                    if (HX[i].size() > 1200/*300*/) {
                        HX[i].remove(0);
                        HY[i].remove(0);
                    }
                    path[i] = new Path();
                    path[i].moveTo(HX[i].get(0), HY[i].get(0));
                    for (int k = 0; k < HX[i].size(); k++) {
                        path[i].lineTo(HX[i].get(k), HY[i].get(k));
                    }
                }
            }
            click = false;
        }
        for(int i=0;i<10;i++)
            for(int j=0;j<10;j++)
            {
                grid[i][j] = 0;
            }
        for(int i=0;i<MAX;i++)
        {
            int x = (int)(ax[i]/54);
            int y = (int)((ay[i]-230)/54);
            if(x >= 0 && x < 10)
                if(y >= 0 && y < 10)
                    grid[x][y] += af[i];
        }
        for(int i=0;i<10;i++)
            Log.d("GRID",(int)(grid[i][0])+"\t"
                    +(int)(grid[i][1])+"\t"
                    +(int)(grid[i][2])+"\t"
                    +(int)(grid[i][3])+"\t"
                    +(int)(grid[i][4])+"\t"
                    +(int)(grid[i][5])+"\t"
                    +(int)(grid[i][6])+"\t"
                    +(int)(grid[i][7])+"\t"
                    +(int)(grid[i][8])+"\t"
                    +(int)(grid[i][9])+"\t");

        this.invalidate();
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

    public void setMoveFlag(boolean flag)
    {
        moveF = flag;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean ret = super.dispatchTouchEvent(ev);
        if(moveF)
            panZoom(ev);
        /*else
            addObjectInOrbit(ev);*/
        return ret;
    }

    public void panZoom(MotionEvent ev){
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
                click = false;
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
                    //setLimits();
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
                    //setLimits();
                }
                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {//TUKA SE POVIKUVA TOA {TO TREBA PO ZAVRSUVAWE O DVIZENJE
                /*P.set(M);
                firstTouch = false;
                scaleFactorPrevious *= scaleFactor;
*/
                Log.d("CLICK", String.valueOf(translateX));
                if(Math.abs(translateX) < 5){
                    click = true;
                    Log.d("CLICK", "TRUE");
                }
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
    }
    public int ltx=0, lty=0;


    /*public void addObject(MotionEvent ev){
        final int action = ev.getAction();
        Log.d("NEWITEM", "in view");
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                Log.d("NEWITEM", "action DOWN");
                float x, y;
                x = ev.getX(ev.findPointerIndex(ev.getPointerId(0)));
                y = ev.getY(ev.findPointerIndex(ev.getPointerId(0)));
                Log.d("NEWITEM", "X: "+x+" Y: " + y);

                float []m = new float[9];
                M.getValues(m);
                float tx = m[Matrix.MTRANS_X] * -1;
                float ty = m[Matrix.MTRANS_Y] * -1;
                float sx = m[Matrix.MSCALE_X];
                float sy = m[Matrix.MSCALE_Y];
                ltx = (int) ((x + tx) / sx);
                lty = (int) ((y + ty) / sy);
                *//*ltx = Math.abs(ltx);
                lty = Math.abs(lty);*//*

                Log.d("NEWITEM", "X: "+ltx+" Y: " + lty);

                break;
            }
            case MotionEvent.ACTION_UP:{
                Log.d("NEWITEM", "action UP");
                Log.d("NEWITEM", "X: "+ltx+" Y: " + lty);

                float x, y, cx, cy, vx, vy;
                x = ev.getX(ev.findPointerIndex(ev.getPointerId(0)));
                y = ev.getY(ev.findPointerIndex(ev.getPointerId(0)));

                float []m = new float[9];
                M.getValues(m);
                float tx = m[Matrix.MTRANS_X] * -1;
                float ty = m[Matrix.MTRANS_Y] * -1;
                float sx = m[Matrix.MSCALE_X];
                float sy = m[Matrix.MSCALE_Y];
                cx = (int) ((x + tx) / sx);
                cy = (int) ((y + ty) / sy);

                vx = -(ltx - cx)/2000;
                vy = -(lty - cy)/2000;
                Log.d("OBJP", "X: "+ltx+" Y: " + lty + " VX: "+vx+" VY: " + vy);
                ((MainActivity)getContext()).addObjectMain(ltx,lty,vx,vy);
                break;
            }
        }
    }
*/
    /*public void addObjectInOrbit(MotionEvent ev){
        final int action = ev.getAction();
        Log.d("NEWITEM", "in view");
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                Log.d("NEWITEM", "action DOWN");
                float x, y;
                x = ev.getX(ev.findPointerIndex(ev.getPointerId(0)));
                y = ev.getY(ev.findPointerIndex(ev.getPointerId(0)));
                Log.d("NEWITEM", "X: "+x+" Y: " + y);

                float []m = new float[9];
                M.getValues(m);
                float tx = m[Matrix.MTRANS_X] * -1;
                float ty = m[Matrix.MTRANS_Y] * -1;
                float sx = m[Matrix.MSCALE_X];
                float sy = m[Matrix.MSCALE_Y];
                ltx = (int) ((x + tx) / sx);
                lty = (int) ((y + ty) / sy);

                float shortestDist = 1000000;
                int sdIndex = -1;

                for(int i = 0;i < MAX;i++)
                {
                    float distance;
                    distance = (float) Math.sqrt(Math.pow(ax[i] - ltx,2)+Math.pow(ay[i] - lty,2));
                    *//*if(af[i]!=0)*//*if(pe[i] == 1)
                    {
                        if(distance < shortestDist)
                        {
                            shortestDist = distance;
                            sdIndex = i;
                        }
                    }
                }
                float dx, dy, d, fx,fy, acc, f;
                dx = ltx - ax[sdIndex];
                dy = lty - ay[sdIndex];


                d = (float) Math.sqrt(dx*dx+dy*dy);

                fx = dx / d;
                fy = dy / d;

                acc = (float) (5*0.05 * (af[sdIndex] / (d*0.1)));

                float lvx =0,lvy=0;


                lvx = avx[sdIndex] - 50/(dy)*Math.abs(fy*dy)/(5*(float)Math.sqrt(d));

                lvy = avy[sdIndex] + 50/(dx)*Math.abs(fx*dx)/(5*(float)Math.sqrt(d));

                *//*if(dy > 0)
                    lvx = *//**//*avx[sdIndex]*//**//* - 1.5f;//acc * fx;
                else
                    lvx = *//**//*avx[sdIndex]*//**//* 1.5f;//+ acc * fx;*//*
                *//*if(dx > 0)
                    vy = *//**//*avy[sdIndex]*//**//* - 1.5f;//acc * fy;
                else
                    vy = *//**//*avy[sdIndex]*//**//* 1.5f;//+ acc * fy;*//*

                f= 0.0f;//af[sdIndex]/10;

                float v = (float)Math.sqrt(Math.pow(lvx,2)+Math.pow(lvy,2));

                //Log.d("ORBIT", "fX: "+fx+" fY: " + fy + " lVX: "+lvx+" lVY: " + lvy);
                Log.d("ORBIT", "speed: "+v);
                ((MainActivity)getContext()).addObjectMain(ltx,lty,lvx,lvy, f);

                break;
            }
        }
    }*/
}

