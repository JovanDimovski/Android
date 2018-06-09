package com.example.jovan.app3.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.AsyncWebPageNotifier;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.AsyncDownloadResource;
import com.example.jovan.app3.Utilities.DownloadFileFromURL;
import com.example.jovan.app3.Utilities.Utilities;
import com.example.jovan.app3.Utilities.ZoomableRelativeLayout;

import java.io.File;

public class ImageActivity extends AppCompatActivity implements AsyncTaskNotifier{
    public ZoomableRelativeLayout mZoomableRelativeLayout;
    public ImageView image;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

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


        image = (ImageView)findViewById(R.id.image);
        String url = getIntent().getExtras().getString("url");
        String id = getIntent().getExtras().getString("id");
        name = id + "thumblarge";
        //ovde javuva greska zatoa sto treba vreme za da se snimi slikata pa da se prikazi go bara nultiot thumbnail namesto to sto treba
        //a nego najcesto go nema
        Log.d("IAT", url);
        //new DownloadFileFromURL(/*this,*/ this, 0, "ImageActivity", new String [] {name}).execute(url);
        new AsyncDownloadResource(this, this, new String [] {name},new String [] {"jpg"}).execute(url);



    }

    @Override
    public void notifyFinished() {
        Bitmap b = Utilities.loadImageFromStorage(getFilesDir().toString(),name+".jpg");
        image.setImageBitmap(b);
    }

    @Override
    public void notifyUpdated() {

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

    /*private Bitmap loadImageFromStorage(String name) {
        File folder = getFilesDir();
        File picture = new File(folder,name+".jpg");
        Bitmap b = null;
        if(picture.exists()) {
            b = BitmapFactory.decodeFile(picture.toString());
            //Log.d("PICE", (((Integer) b.getByteCount()).toString()+":::"+((Integer)index).toString()));
        }
        return b;
    }*/

    public void asyncImageDownloadFinished(){
        ImageView image = (ImageView)findViewById(R.id.image);
        Bitmap b = Utilities.loadImageFromStorage(getFilesDir().toString(), name+".jpg");
        image.setImageBitmap(b);
    }
}

