package com.example.jovan.app3.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.AsyncWebPageNotifier;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.AsyncDownloadResource;
import com.example.jovan.app3.Utilities.AsyncGetWebPage;
import com.example.jovan.app3.Utilities.ScalableVideoView;
import com.example.jovan.app3.Utilities.ZoomableRelativeLayout;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoActivity extends AppCompatActivity implements AsyncTaskNotifier, AsyncWebPageNotifier{

    private String webPage, url, name, filesdir;
    private String [] name_array, type_array;
    private int format = 0;
    public long duration;

    private boolean bVideoIsBeingTouched = false;
    private Handler mHandler = new Handler();


    TextView text;
    ScalableVideoView video;
    RelativeLayout video_parent;
    public ZoomableRelativeLayout mZoomableRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

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

        url = getIntent().getExtras().getString("url");
        name = getIntent().getExtras().getString("id");
        filesdir = this.getFilesDir().toString();

        Log.d("VideoTag","Reddit url: "+url);

        new AsyncGetWebPage(/*this, */this).execute(url);

        //text = (TextView) findViewById(R.id.videolength);


        //text.setText("Video no yet loaded");
    }
    public void notifyWeb(String webPage){setWebPage(webPage);}
    public void setWebPage(String webPage){
        this.webPage = webPage;
        String source = null;
        //"http://i.imgur.com/jxY7Ywz.gifv"
        Pattern url_pattern = Pattern.compile("https?://(i.)?imgur.com/.*?\\.gifv");
        Matcher url_matcher = url_pattern.matcher(url);
        if(url_matcher.find()) {

            Pattern pattern = Pattern.compile("<source src=\"(.*?)\" type=\"video/mp4\">");
            Matcher matcher = pattern.matcher(webPage);
            if (matcher.find()) {
                source = matcher.group(1);
            }
            source = "http:" + source;
        }
        //"http://gfycat.com/FondSmartBaiji"
        url_pattern = Pattern.compile("https*://gfycat.com/.*?");
        url_matcher = url_pattern.matcher(url);
        if(url_matcher.find()) {
            //"<source id=\"mp4Source\" src=\"https://giant.gfycat.com/FondSmartBaiji.mp4\" type=\"video/mp4\">"
            //"<source id=\"mp4Source\" src=\"https://giant.gfycat.com/BarrenTornIchneumonfly.mp4\" type=\"video/mp4\">"
            //"<source id=\"webmSource\" src=\"https://fat.gfycat.com/FondSmartBaiji.webm\" type=\"video/webm\">"
            Pattern pattern;
            if(format == 0)
                pattern = Pattern.compile("<source id=\"mp4Source\" src=\"(.*?)\" type=\"video/mp4\">");
            else
                pattern = Pattern.compile("<source id=\"webmSource\" src=\"(.*?)\" type=\"video/webm\">");
            Matcher matcher = pattern.matcher(webPage);
            if (matcher.find()) {
                source = matcher.group(1);
            }
            Log.d("VideoTag", "Gfycat video url: " + source);
        }

        name_array = new String[]{name};
        type_array = new String[1];
        if(format == 0)
            type_array[0] = "mp4";
        else
            type_array[0] = "webm";
        Log.d("VideoTag", "Url before download is called" + source);
        new AsyncDownloadResource(this,this, name_array, type_array).execute(source);
    }

    public void notifyFinished(){setVideo();}
    public void notifyUpdated(){}
    public void setVideo(){
        File mydir = this.getFilesDir();//.getDir("Videos", Context.MODE_PRIVATE);
        File fileWithinMyDir = new File(mydir, name_array[0] + "." + type_array[0]);
        if(fileWithinMyDir.exists()) {
            Log.d("VideoTag", "Video exists. File size: "+(((long)(fileWithinMyDir.length()))/1024)+"kB");
            if(fileWithinMyDir.length()/1024 < 100) {
                fileWithinMyDir.delete();
                if(format == 0)
                {
                    format = 1;
                    setWebPage(webPage);
                }
                return;
            }
        }
        else
            Log.d("VideoTag", "Video does NOT exist");
        fileWithinMyDir.setReadable(true, false);
        String videoResource = fileWithinMyDir.getPath();

        Log.d("VideoTag", "Video path: " + videoResource);

        final Uri video_uri = Uri.fromFile(new File(videoResource));

        Log.d("VideoTag", "Video URI: "+video_uri.toString());

        RelativeLayout video_parent = (RelativeLayout) findViewById(R.id.video_parent);
        /*video = new VideoView(this);
        ViewGroup.LayoutParams params = video.getLayoutParams();
        //video.setScaleY(2);

        video_parent.addView(video);*/

        video = (ScalableVideoView) findViewById(R.id.video);//original ako veke postoi vo layoutot
        video_parent = (RelativeLayout) findViewById(R.id.video_parent);

        /*MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(video);
        video.setMediaController(mediaController);*/
        video.setVideoURI(video_uri);

        video.start();

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                }
            }
        });

/*        video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(bVideoIsBeingTouched == false) {
                    Log.d("VidTag", String.valueOf(video.isPlaying()));
                    bVideoIsBeingTouched = true;
                    if (video.isPlaying()) {
                        video.pause();
                    } else {
                        video = (VideoView) findViewById(R.id.video);
                        video.setVideoURI(video_uri);
                        video.start();
                    }
                }
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        bVideoIsBeingTouched = false;
                    }
                }, 100);
                return true;
            }
        });*/

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this, video_uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Long.parseLong(time)/1000;
        //text.setText("0/"+String.valueOf(duration));

        updateTime();
    }
    public void updateTime()
    {
        //final int j=i+1;
        //video.getCurrentPosition();


        //text.setText(String.valueOf(video.getCurrentPosition()/1000)+"/"+String.valueOf(duration));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                updateTime();
            }
        }, 490);
    }
    private class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        float currentSpan;
        float startFocusX;
        float startFocusY;
        float currentScale;
        int mW,mH;

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            currentSpan = detector.getCurrentSpan();
            startFocusX = detector.getFocusX();
            startFocusY = detector.getFocusY();
            mW=video.getWidth();
            mH=video.getHeight();
            currentScale = 1;
            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            mZoomableRelativeLayout.relativeScale(detector.getCurrentSpan() / currentSpan, startFocusX, startFocusY);
            currentSpan = detector.getCurrentSpan();
            currentScale = currentScale * detector.getScaleFactor();
            video.setFixedVideoSize((int)(mW*currentScale),(int)(mH*currentScale));
            video_parent = (RelativeLayout) findViewById(R.id.video_parent);
            ViewGroup.LayoutParams parameters = video_parent.getLayoutParams();
            parameters.width = mW;
            parameters.height = mH;
            video_parent.setLayoutParams(parameters);
            Log.d("onScale", "scale = "+String.valueOf(currentScale));
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            mZoomableRelativeLayout.release();

            video.setFixedVideoSize((int)(mW*currentScale),(int)(mH*currentScale));
            ViewGroup.LayoutParams parameters = video_parent.getLayoutParams();
            parameters.width = (int)(mW*currentScale);
            parameters.height = (int)(mH*currentScale);
            video_parent.setLayoutParams(parameters);
            Log.d("onScale", "scaleEnd = "+String.valueOf(currentScale));

            /*video.stopPlayback();
            RelativeLayout video_parent = (RelativeLayout) findViewById(R.id.video_parent);
            video_parent.removeAllViews();
            setVideo();*/
        }
    }
}
