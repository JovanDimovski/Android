package com.example.jovan.gravity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by Jovan on 04-Nov-16.
 */
public class RelativeLayoutCustom extends RelativeLayout {
    public static final int MAX = 240/1;
    public float xc = 270, yc = 460, fc = 0.05f;
    public float xo = 600, yo = 900, fo = 0.05f;
    public float gc = 0.05f*30;
    public float x = 0, y = 0, vx = 5, vy = 1;
    public float ax[] = new float[MAX],
            ay[] = new float[MAX],
            avx[] = new float[MAX],
            avy[] = new float[MAX],
            af[] = new float[MAX];
    public boolean pe[] = new boolean[MAX];


    public Path path[] = new Path[MAX];
    public int whoToFollow = 0;

    public ArrayList<Float> HX[]= new ArrayList[MAX];
    public ArrayList<Float> HY[]= new ArrayList[MAX];

    public RelativeLayoutCustom(Context context) {
        super(context);
        //Log.d("TEST", "TEST");
    }

    public RelativeLayoutCustom(Context context, AttributeSet attrs) {
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
            avx[i] = (float)(fy*d/(540*2));//(float)(Math.random()-0.5)*0.1f;
            avy[i] = (float)(fx*d/(540*2));//(float)(Math.random()-0.5)*0.1f;
            af[i] = 0.05f;
            pe[i] = true;
            path[i] = new Path();
            path[i].moveTo(ax[i], ay[i]);
            HX[i] = new ArrayList<>();
            HY[i] = new ArrayList<>();
        }
    }

    public RelativeLayoutCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //Log.d("TEST", "TEST");
    }

    /*public RelativeLayoutCustom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.scale(0.3f,0.3f);
        //canvas.translate(500,500);
        Paint myPaint = new Paint();/*
        myPaint.setColor(Color.RED);
        canvas.drawCircle(xc, yc, 20, myPaint);
        myPaint.setColor(Color.YELLOW);
        canvas.drawCircle(xo, yo, 40, myPaint);*/
        myPaint.setColor(Color.RED);
        for(int i = 0; i < MAX; i++)
        {
            if(pe[i])
                canvas.drawCircle(ax[i], ay[i], 2*(float)Math.pow(af[i]*20,0.33), myPaint);
        }


        ///////
        //super.onDraw(canvas);
        Paint paint = new Paint();

        //Path path = new Path();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
        /*for (int i = 50; i < 100; i++) {
            path.lineTo(3*i, 10*i);
        }*/
        //path.close();
        paint.setStrokeWidth(1);
        paint.setPathEffect(null);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        /*for(int i = 0; i < MAX; i++)
        {
            if(pe[i])
                canvas.drawPath(path[i], paint);
        }*/
        //////
    }
    public void runEvery40ms(){
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
                    /*if(i == whoToFollow)
                        path.lineTo(ax[i], ay[i]);*/
                    /*HX[i].add(ax[i]);
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
                    }*/


                    d = (float) Math.pow(Math.pow(tx, 2) + Math.pow(ty, 2), 0.5);
                    fx = -tx / d;
                    fy = -ty / d;
                    avx[i] = avx[i] + gc * (af[j] / (d)) * fx;
                    avy[i] = avy[i] + gc * (af[j] / (d)) * fy;

                    if(d < 2*(float)Math.pow(af[i]*20,0.33)) {
                        pe[j] = false;
                        /*if(whoToFollow == j)
                            whoToFollow = i;*/
                        avx[i] = avx[i]*((af[i])/(af[i]+af[j]))+ avx[j]*((af[j])/(af[i]+af[j]));
                        avy[i] = avy[i]*((af[i])/(af[i]+af[j]))+ avy[j]*((af[j])/(af[i]+af[j]));
                        af[i] += af[j];
                    }
                }
                /*float tx = ax[i] - xc, ty = ay[i] - yc, d, fx, fy;
                ax[i] = ax[i] + avx[i];
                ay[i] = ay[i] + avy[i];
                d = (float) Math.pow(Math.pow(tx, 2) + Math.pow(ty, 2), 0.5);
                fx = -tx / d;
                fy = -ty / d;
                avx[i] = avx[i] + 500 * (fc / (d)) * fx;
                avy[i] = avy[i] + 500 * (fc / (d)) * fy;*/
/*
            tx = ax[i] - xo;
            ty = ay[i] - yo;
            d = (float) Math.pow(Math.pow(tx, 2) + Math.pow(ty, 2), 0.5);
            fx = -tx / d;
            fy = -ty / d;
            avx[i] = avx[i] + 500 * (fo / (d)) * fx;
            avy[i] = avy[i] + 500 * (fo / (d)) * fy;

            ax[i] = ax[i] + avx[i];
            ay[i] = ay[i] + avy[i];*/
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
    }
}
