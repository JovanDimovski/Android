package com.example.jovan.app3.Activities;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.DownloadFileFromURL;
import com.example.jovan.app3.Utilities.DownloadGifFromURL;
import com.example.jovan.app3.Utilities.ZoomableRelativeLayout;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GifActivity extends AppCompatActivity {
    public ZoomableRelativeLayout mZoomableRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);

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

        String url = getIntent().getExtras().getString("url");
        String name =getIntent().getExtras().getString("id");
        String [] urls =  /*{"http://i.imgur.com/FG2wcV7.gif"};*/{url};
        Log.d("GifTAG", "url "+urls[0]);

        new DownloadGifFromURL(this, 0, "GifActivity", name).execute(urls);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("GifTAG", "pred findviewbyid");
        GifImageView gif = (GifImageView) findViewById(R.id.gifview);

        Log.d("GifTAG", "pred citanje od ffajl");
        File gifFile = new File(getFilesDir(),"/GIF"+name+".gif");
        if(gifFile.exists())
            Log.d("GifTAG", "postoi fajlot");
        else
            Log.d("GifTAG", "NE postoi fajlot");

        GifDrawable gifFromFile = null;
        try {
            gifFromFile = new GifDrawable(gifFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("GifTAG", "exception pri mestenje gifDrawable");
        }

        gif.setImageDrawable(gifFromFile);
    }

    private class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        float currentSpan;
        float startFocusX;
        float startFocusY;

        public boolean onScaleBegin(ScaleGestureDetector detector) {
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
