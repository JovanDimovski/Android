package com.example.jovan.gravity;

import android.graphics.Path;
import android.os.Handler;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
/*import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.Allocation;*/
import android.renderscript.ScriptC;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int MAX = 900;
    public ZoomableRelativeLayout mZoomableRelativeLayout;

    /*float INPUTARRAY[] = new float[MAX*MAX];
    float OUTPUTARRAY[] = new float[MAX*MAX];*/

    /*float ax[] = new float[MAX];
    float ay[] = new float[MAX];*/
    float avx[] = new float[MAX];
    float avy[] = new float[MAX];
    float af[] = new float[MAX];
    int pe[] = new int[MAX];

    float oax[] = new float[MAX];
    float oay[] = new float[MAX];
    float oavx[] = new float[MAX];
    float oavy[] = new float[MAX];
    float oaf[] = new float[MAX];
    int ope[] = new int[MAX];

    Allocation aAx;
    Allocation aAy;
    Allocation aAvx;
    Allocation aAvy;
    Allocation aAf;
    Allocation aPe;

    Allocation alloc;
    Allocation input;
    Allocation output;

    ScriptC_foo foo;

    public double runTime=0,runCycles=0;
    public int tOffset = 40;

    public double ls = 0;

    float oOax[] = new float[MAX];
    float oOay[] = new float[MAX];
    float iOax[] = new float[MAX];
    float iOay[] = new float[MAX];
    boolean skipC = true;
    int step = 0, frame = 0;
    private void pCalcHelperOriginal(final ZoomableRelativeLayout rlc){
        double br = System.nanoTime();

        //Log.d("RenderScriptCycle", frame + ". Outside Function ms: " + (int) ((br - ls) / 1000000));
        frame++;

        ls = br;
        foo.forEach_root(input, output, MAX);
        double ar = System.nanoTime();


        Log.d("SEGTAG", "before copyTo in pcalchelper");

        aAx.copyTo(oax);

        Log.d("SEGTAG", "after first copy");
        aAy.copyTo(oay);

        Log.d("SEGTAG", "after second copy");
        aAf.copyTo(oaf);
        Log.d("SEGTAG", "after third copy");
        aPe.copyTo(ope);
        Log.d("SEGTAG", "after fourth copy");


        Log.d("SEGTAG", "after copyTo");

        double aa = System.nanoTime();

        rlc.runEvery40ms(oax,oay,oaf,ope);

        Log.d("SEGTAG", "after runEvery40ms");

        double ad = System.nanoTime();
        int cTOffset = (int)((ad-br)/1000000);
        if(cTOffset > 40)
            tOffset = 1;
        else
            tOffset = 40 - cTOffset;
        Log.d("RenderScriptCycle", frame + ". Inside Function ms: "+cTOffset);

        final Handler handler = new Handler();
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                pCalcHelperOriginal(rlc);
            }
        }, (int)(br/1000000) + tOffset);
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pCalcHelper(rlc);
            }
        }, tOffset);*/

        double ah = System.nanoTime();
        //Log.d("RenderScriptCycle", frame + ". Time for handler ms: "+(int)((ah-ad)/1000000));
    }
    /*private void pCalcHelper(final ZoomableRelativeLayout rlc) {
        double br = System.nanoTime();

        if(step == 0) {
            Log.d("RenderScriptCycle", "Outside Function ms: " + (int) ((br - ls) / 1000000));
            ls = br;
            foo.forEach_root(input, output, MAX);
            double ar = System.nanoTime();

            System.arraycopy(oax, 0, oOax, 0, MAX);
            System.arraycopy(oay, 0, oOay, 0, MAX);
            Log.d("ARRAY", "Same: " + String.valueOf(oOax[5]) + " ... " + String.valueOf(oax[5]));
            aAx.copyTo(oax);
            aAy.copyTo(oay);
            Log.d("ARRAY", "Diff: " + String.valueOf(oOax[5]) + " ... " + String.valueOf(oax[5]));

            aAf.copyTo(oaf);
            aPe.copyTo(ope);

            double aa = System.nanoTime();

        }
        for (int i = 0; i < MAX; i++) {
            iOax[i] = (oax[i]*(step) + oOax[i]*(1-step)) / 1;
            iOay[i] = (oay[i]*(step) + oOay[i]*(1-step)) / 1;
        }
        step++;
        if(step == 2)
            step = 0;
        rlc.runEvery40ms(iOax,iOay,oaf,ope);

        double ad = System.nanoTime();
        int cTOffset = (int)((ad-br)/1000000);
        if(cTOffset > 40)
            tOffset = 1;
        else
            tOffset = 40 - cTOffset;
        Log.d("RenderScriptCycle", "Inside Function ms: "+cTOffset);

        final Handler handler = new Handler();
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                pCalcHelper(rlc);
            }
        }, (int)(br/1000000) + tOffset);
        *//*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pCalcHelper(rlc);
            }
        }, tOffset);*//*

        double ah = System.nanoTime();
        Log.d("RenderScriptCycle", "Time for handler ms: "+(int)((ah-ad)/1000000));
    }*/
    /*public void addObjectMain(int x, int y, float vx, float vy, float f){
        Log.d("NEWITEM", "new item");
        int first = -1;
        int count = 0;
        for(int i=0;i<MAX;i++)
        {
            if(ope[i] == 0)
            {
                first = i;
                *//*count++;
                if(count > 1)*//*
                    break;
            }
        }
        Log.d("NEWITEM", "Found a zero index=" + first);
                *//*oax[i] = 100;
                oay[i] = 100;
                oaf[i] = 5.0f;
                oavx[i] = 0;
                oavy[i] = 0;*//*
        float temp[] = new float[1];
        temp[0] = x;
        aAx.copy1DRangeFrom(first,1,temp);
        temp[0] = y;
        aAy.copy1DRangeFrom(first,1,temp);
        temp[0] = f;
        aAf.copy1DRangeFrom(first,1,temp);
        temp[0] = vx;
        aAvx.copy1DRangeFrom(first,1,temp);
        temp[0] = vy;
        aAvy.copy1DRangeFrom(first,1,temp);
        int temp2[] = new int [1];
        temp2[0] = 1;
        aPe.copy1DRangeFrom(first,1,temp2);
        //OVA FUNKCIONIRA
        *//*float temp[] = new float[MAX];
        for(int i =0;i <MAX;i++){
            temp[i] = 10;
        }
        aAf.copy1DRangeFrom(0,MAX,temp);*//*
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final ZoomableRelativeLayout rlc = (ZoomableRelativeLayout)findViewById(R.id.zoomview);
        RelativeLayout button = (RelativeLayout)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlc.setMoveFlag(!rlc.moveF);
            }
        });
        /*for(int j = 0; j <100;j++) {
            double t1 = System.nanoTime();
            double res = 0;
            for (int i = 0; i < 10000; i++) {
                res = 300.3124515 * 300.3124515;
            }
            double t2 = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                res = (long) Math.pow(300.3124515, 2);
            }
            double t3 = System.nanoTime();
            Log.d("SPEED", "Mult: " + (int) ((t2 - t1) / 10000) + " Pow: " + (int) ((t3 - t2) / 10000));
        }*/


        for(int i =0; i < MAX; i++)
        {
            double r,a;
            double R = 270; //16;
            r = Math.sqrt(Math.random())*R;//289 - Math.pow(Math.random()*17,2);
            a = (Math.random())*2*3.14159265358979323846264338;

            oax[i] = (float)(r*Math.sin(a)+270);//(float)(Math.random())*540;
            oay[i] = (float)(r*Math.cos(a)+460);//(float)(Math.random())*540;
            double tx = oax[i]-270;
            double ty = oay[i]-460;
            double d = (float) Math.pow(Math.pow(tx, 2) + Math.pow(ty, 2), 0.5);
            double fx = -tx / d;
            double fy = ty / d;
            avx[i] = (float)((1000 * 0.05 *((r*r)/(R*R)))*(-ty)*0.000001);//(float)(fy*d/(540*8*6));//(float)(Math.random()-0.5)*0.1f;
            avy[i] = (float)((1000 * 0.05 *((r*r)/(R*R)))*tx*0.000001);//(float)(fx*d/(540*8*6));//(float)(Math.random()-0.5)*0.1f;


            af[i] = 0.05f/*0.05f*100*10*/;
            pe[i] = 1;
            /*path[i] = new Path();
            path[i].moveTo(ax[i], ay[i]);
            HX[i] = new ArrayList<>();
            HY[i] = new ArrayList<>();*/
        }
        ////////////////////RENDERSCRIPT//////////////////////
        double start = System.currentTimeMillis();
        RenderScript mRs;
        mRs = RenderScript.create(this);
        Log.d("SEGTAG", "before allocation");
        aAx = Allocation.createSized(mRs, Element.F32(mRs) ,MAX);
        Log.d("SEGTAG", "after first allocation");
        aAy = Allocation.createSized(mRs, Element.F32(mRs) ,MAX);
        aAvx = Allocation.createSized(mRs, Element.F32(mRs) ,MAX);
        aAvy = Allocation.createSized(mRs, Element.F32(mRs) ,MAX);
        aAf = Allocation.createSized(mRs, Element.F32(mRs) ,MAX);
        //Premnogu resursi koristi za dzabe moze da bide samo 1 ili 0
        aPe = Allocation.createSized(mRs, Element.I32(mRs) ,MAX);
        Log.d("SEGTAG", "after all allocations");

        aAx.copyFrom(oax);
        aAy.copyFrom(oay);
        aAvx.copyFrom(avx);
        aAvy.copyFrom(avy);
        aAf.copyFrom(af);
        aPe.copyFrom(pe);

        Log.d("SEGTAG", "after sopy to Allocations");

        //alloc = Allocation.createSized(mRs, Element.F32(mRs) ,MAX);
        input = Allocation.createSized(mRs, Element.F32(mRs) ,MAX*MAX);
        output = Allocation.createSized(mRs, Element.F32(mRs) ,MAX*MAX);

        Log.d("SEGTAG", "after input output allocation");
        //alloc.copyFrom(in);
        foo = new ScriptC_foo(mRs);
        //foo.bind_data(alloc);

        foo.bind_ax(aAx);
        foo.bind_ay(aAy);
        foo.bind_avx(aAvx);
        foo.bind_avy(aAvy);
        foo.bind_af(aAf);
        foo.bind_pe(aPe);

        Log.d("SEGTAG", "after binding");

        pCalcHelperOriginal(rlc);


        Log.d("SEGTAG", "after pCalcHelperOriginal");

        /*int size = MAX;
        double setupTime = System.currentTimeMillis();
        int numC = 20;
        float hxp[] = new float[numC];
        float hxs[] = new float[numC];

        for(int n = 0; n < numC;n++) {
            double br = System.currentTimeMillis();
            foo.forEach_root(input, output, size);
            double ar = System.currentTimeMillis();
            aAx.copyTo(oax);
            aAy.copyTo(oay);
            aAvx.copyTo(oavx);
            aAvy.copyTo(oavy);
            aAf.copyTo(oaf);
            alloc.copyTo(out);
            hxp[n] = oax[1];
            *//*for (int i = 0; i < 1; i++) {
                *//**//*Log.d("RenderScript", "INX: " + String.valueOf(ax[i]));*//**//*
                //Log.d("RenderScript", "OTX: " + String.valueOf(oax[i]));
                *//**//*Log.d("RenderScript", "INY: "+String.valueOf(ay[i]));
                Log.d("RenderScript", "OTY: "+String.valueOf(oay[i]));
                Log.d("RenderScript", "INVX: "+String.valueOf(avx[i]));*//**//*
                //Log.d("RenderScript", "OTVX: "+String.valueOf(oavx[i]));
                *//**//*Log.d("RenderScript", "INVY: "+String.valueOf(avy[i]));
                Log.d("RenderScript", "OTVY: "+String.valueOf(oavy[i]));*//**//*
            }*//*
            *//*Log.d("RenderScript", "Cycle-"+ n +": "+ String.valueOf((ar - br)) + "ms");*//*
        }
        double end = System.currentTimeMillis();
        //Log.d("RenderScript", "All: " + String.valueOf((end-start))+"ms");
        //Log.d("RenderScript", "Setup: " + String.valueOf(setupTime - start) +"ms");
        Log.d("RenderScript", "Ncalc: " + String.valueOf((end-setupTime))+"ms");
        Log.d("RenderScript", "PerCycle: " + String.valueOf((end-setupTime)/numC)+"ms");*/
        ////////////////////RENDERSCRIPT//////////////////////
        //////////////////NOT_RENDERSCRIPT////////////////////
        /*start = System.currentTimeMillis();
        setupTime = System.currentTimeMillis();
        for(int n = 0; n < numC;n++) {
            for (int x = 0; x < MAX; x++)
                for (int j = 0; j < MAX; j++) {
                    if(pe[x]!=1)
                        continue;
                    if(x!=j) {
                        float tx, ty, d, fx, fy, gc = 1.5f;
                        ax[x] = ax[x] + avx[x];
                        ay[x] = ay[x] + avy[x];


                        tx = ax[x] - ax[j];
                        ty = ay[x] - ay[j];

                        //Log.d("TQ", tx + ", " + ty);
                        d = (float) Math.pow((Math.pow(tx, 2) + Math.pow(ty, 2)), 0.5f);
                        fx = -tx / d;
                        fy = -ty / d;

                        avx[x] = avx[x] + gc * (af[j] / (d)) * fx;
                        avy[x] = avy[x] + gc * (af[j] / (d)) * fy;

                        if (d < 2 * (float) Math.pow(af[x] * 20, 0.33)) {
                            pe[j] = 0;
                            avx[x] = avx[x] * ((af[x]) / (af[x] + af[j])) + avx[j] * ((af[j]) / (af[x] + af[j]));
                            avy[x] = avy[x] * ((af[x]) / (af[x] + af[j])) + avy[j] * ((af[j]) / (af[x] + af[j]));
                            af[x] += af[j];
                        }
                    }
                }

            hxs[n] = ax[1];
            *//*Log.d("RenderScript", "OTX: " + String.valueOf(ax[0]));
            Log.d("RenderScript", "OTVX: "+String.valueOf(avx[0]));*//*
        }
        end = System.currentTimeMillis();

        //Log.d("RenderScript", "All: " + String.valueOf((end-start))+"ms");
        //Log.d("RenderScript", "Setup: " + String.valueOf(setupTime - start) +"ms");
        Log.d("RenderScript", "Ncalc: " + String.valueOf((end-setupTime))+"ms");
        Log.d("RenderScript", "PerCycle: " + String.valueOf((end-setupTime)/numC)+"ms");
        //////////////////NOT_RENDERSCRIPT////////////////////
*/
        /*for (int i = 0; i < 2; i++) {
            Log.d("RenderScript", "1: " + String.valueOf(ax[i]) + "\t2: " + String.valueOf(oax[i]));
            Log.d("RenderScript", "1: " + String.valueOf(ay[i]) + "\t2: " + String.valueOf(oay[i]));
            Log.d("RenderScript", "______________________________________________________________");
        }*/

        /*for (int i = 0; i < numC; i++) {
            Log.d("RenderScript", "1: " + String.valueOf(hxp[i]) + "\t2: " + String.valueOf(hxs[i]));
        }*/



        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new OnPinchListener());

        mZoomableRelativeLayout = (ZoomableRelativeLayout)findViewById(R.id.zoomview);

        mZoomableRelativeLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }


        });

        //rlc.runEvery40ms();
    }

    private class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        float currentSpan;
        float startFocusX;
        float startFocusY;

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            /*mZoomableRelativeLayout.setPivotX(detector.getFocusX());
            mZoomableRelativeLayout.setPivotY(detector.getFocusY());*///za centrirawe okolu odredena tocka
            currentSpan = detector.getCurrentSpan();
            startFocusX = detector.getFocusX();
            startFocusY = detector.getFocusY();
            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            mZoomableRelativeLayout.relativeScale(detector.getCurrentSpan() / currentSpan, startFocusX, startFocusY);
            currentSpan = detector.getCurrentSpan();
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {mZoomableRelativeLayout.release();}
    }

}
