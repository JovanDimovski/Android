package com.example.jovan.app3.Activities;

import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.AsyncWebPageNotifier;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.AsyncDownloadResource;
import com.example.jovan.app3.Utilities.AsyncGetWebPage;
import com.example.jovan.app3.Utilities.ScalableVideoView;
import com.example.jovan.app3.Utilities.ZoomableRelativeLayout;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextureActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener,AsyncWebPageNotifier,AsyncTaskNotifier {
    /*--------------------------------------------------------------------------------------*/
    private String webPage, url, name, filesdir;
    private String [] name_array, type_array;
    private int format = 0;
    private String videoResource;
    private AsyncDownloadResource ADR;
    /*--------------------------------------------------------------------------------------*/



    // Original video size, in our case 640px / 360px
    private float mVideoWidth;
    private float mVideoHeight;
    private float currentScale;

    // Log tag
    private static final String TAG = TextureActivity.class.getName();

    // Asset video file name
    private static final String FILE_NAME = "waterfall.mp4";

    // MediaPlayer instance to control playback of video file.
    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;
    private RelativeLayout frameParent;
    private RelativeLayout rlRoot;
    public ZoomableRelativeLayout mZoomableRelativeLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture);

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

        Log.d("TEXTAG","End of onCreate: "+url);
        new AsyncGetWebPage(/*this, */this).execute(url);
    }

    private void initView() {
        mTextureView = (TextureView) findViewById(R.id.textureView);
        // SurfaceTexture is available only after the TextureView
        // is attached to a window and onAttachedToWindow() has been invoked.
        // We need to use SurfaceTextureListener to be notified when the SurfaceTexture
        // becomes available.
        Log.d("TEXTAG","In initView: "+url);
        mTextureView.setSurfaceTextureListener(this);
        if (mTextureView.isAvailable()) {
            onSurfaceTextureAvailable(mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
        }

        int mW = mTextureView.getMeasuredWidth();
        float iS = (float)(mVideoWidth)/(float)(mW);


        FrameLayout rootView = (FrameLayout) findViewById(R.id.rootView);
        //rlRoot =(RelativeLayout) findViewById(R.id.rl_root);

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

        /*rlRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        updateTextureViewSize((int) motionEvent.getX(), (int) motionEvent.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateTextureViewSize((int) motionEvent.getX(), (int) motionEvent.getY());
                        break;
                }
                return true;
            }
        });*/
    }

    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = mTextureView.getWidth();
        int viewHeight = mTextureView.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;
        Log.v(TAG, "video=" + videoWidth + "x" + videoHeight +
                " view=" + viewWidth + "x" + viewHeight +
                " newView=" + newWidth + "x" + newHeight +
                " off=" + xoff + "," + yoff);

        Matrix txform = new Matrix();
        mTextureView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight / viewHeight);
        //txform.postRotate(10);          // just for fun
        txform.postTranslate(xoff, yoff);
        mTextureView.setTransform(txform);
    }

    @Override
    public void notifyFinished() {
        Log.d("TEXTAG","Notify Finished: "+url);
        //calculateVideoSize();
        initView();
    }



    @Override
    public void notifyUpdated() {

    }

    @Override
    public void notifyWeb(String webPage) {
        setWebPage(webPage);
    }
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

            //"<source id=\"webmSource\" src=\"https://giant.gfycat.com/FineEminentDinosaur.webm\" type=\"video/webm\">"
            //"<source id=\"mp4Source\" src=\"https://giant.gfycat.com/FineEminentDinosaur.mp4\" type=\"video/mp4\">"
            Pattern pattern;
            String [] url_array=url.split("/");
            String name = url_array[url_array.length-1];
            Log.d("PTRNTG", "Url: " + url);
            Log.d("PTRNTG", "WebPage:\n" + webPage);
            Log.d("PTRNTG", "Name: " + name);
            if(format == 0) {
                pattern = Pattern.compile("<source id=\"mp4Source\" src=\"(.*?)\" type=\"video/mp4\">");
                //pattern = Pattern.compile("\"((.*?)"+name+"\\.mp4)\"");
            }
            else {
                pattern = Pattern.compile("<source id=\"webmSource\" src=\"(.*?)\" type=\"video/webm\">");
                //pattern = Pattern.compile("\"((.*?)"+name+"\\.webm)\"");
            }
            Matcher matcher = pattern.matcher(webPage);
            if (matcher.find()) {
                source = matcher.group(1);
            }
            Log.d("PTRNTG", "Source: " + source);

            Log.d("TEXTAG", "Gfycat video url: " + source);
        }

        name_array = new String[]{name};
        type_array = new String[1];
        if(format == 0)
            type_array[0] = "mp4";
        else
            type_array[0] = "webm";
        Log.d("TEXTAG", "Url before download is called" + source);
        ADR = new AsyncDownloadResource(this,this, name_array, type_array);
        ADR.execute(source);
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
            currentScale = mZoomableRelativeLayout.relativeScale(detector.getCurrentSpan() / currentSpan, startFocusX, startFocusY);
            currentSpan = detector.getCurrentSpan();
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            //updateTextureViewSize(200, 200);
            updateTextureViewSize();
            mZoomableRelativeLayout.release();
        }
    }
    private void updateTextureViewSize(){

        frameParent = (RelativeLayout) findViewById(R.id.frame_parent);
        //ViewGroup.LayoutParams params = frameParent.getLayoutParams();
        Log.d("TEXTAG","w: "+String.valueOf(frameParent.getMeasuredWidth()*currentScale)+"h: "+String.valueOf(frameParent.getMeasuredHeight()*currentScale));
        //mTextureView.setLayoutParams(new FrameLayout.LayoutParams((int)(frameParent.getMeasuredWidth()*currentScale), (int)(frameParent.getMeasuredHeight()*currentScale)));
        //mTextureView.invalidate();

    }
    /*private void updateTextureViewSize(int viewWidth, int viewHeight) {
        ViewGroup.LayoutParams params = mTextureView.getLayoutParams();
        Log.d("TEXTAG","w: "+String.valueOf(viewWidth)+"h: "+String.valueOf(viewHeight));
        int tw = 320;*//*(int)mVideoHeight;*//*//params.width;
        int th = 480;*//*(int)mVideoWidth;*//*//params.height;
        float sf = ((float)viewHeight)/((float)th);

        mTextureView.setLayoutParams(new FrameLayout.LayoutParams((int)(tw*sf), (int)(th*sf)));
        //frameParent.setLayoutParams(new RelativeLayout.LayoutParams((int)(tw*sf), (int)(th*sf)));//ne mozi vaka

        //mTextureView.
*//*
        float scaleX = 1.0f;
        float scaleY = 1.0f;

        if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
            scaleX = mVideoWidth / viewWidth;
            scaleY = mVideoHeight / viewHeight;
        } else if (mVideoWidth < viewWidth && mVideoHeight < viewHeight) {
            scaleY = viewWidth / mVideoWidth;
            scaleX = viewHeight / mVideoHeight;
        } else if (viewWidth > mVideoWidth) {
            scaleY = (viewWidth / mVideoWidth) / (viewHeight / mVideoHeight);
        } else if (viewHeight > mVideoHeight) {
            scaleX = (viewHeight / mVideoHeight) / (viewWidth / mVideoWidth);
        }
        Log.d("TEXTAG","scalex: "+String.valueOf(scaleX)+"scaley: "+String.valueOf(scaleY));

        // Calculate pivot points, in our case crop from center
        int pivotPointX = 0;// viewWidth / 2;
        int pivotPointY = 0;//viewHeight / 2;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);

        mTextureView.setTransform(matrix);
        mTextureView.setLayoutParams(new FrameLayout.LayoutParams(viewWidth, viewHeight));*//*
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            // Make sure we stop video and release resources when activity is destroyed.
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        ADR.cancel(true);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        Log.d("TEXTAG","On SurfaceTextureAvailable: "+url);
        Surface surface = new Surface(surfaceTexture);

        try {
            AssetFileDescriptor afd = getAssets().openFd(FILE_NAME);
            mMediaPlayer = new MediaPlayer();
            /*------------------------------------------------------------------------------------------------*/
            //mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            File mydir = this.getFilesDir();//.getDir("Videos", Context.MODE_PRIVATE);
            File fileWithinMyDir = new File(mydir, name_array[0] + "." + type_array[0]);
            if(fileWithinMyDir.exists()) {
                Log.d("TEXTAG", "Video exists. File size: "+(((long)(fileWithinMyDir.length()))/1024)+"kB");
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
                Log.d("TEXTAG", "Video does NOT exist");
            fileWithinMyDir.setReadable(true, false);
            videoResource = fileWithinMyDir.getPath();

            Log.d("TEXTAG", "Video path: " + videoResource);
            calculateVideoSize();
            final Uri video_uri = Uri.fromFile(new File(videoResource));
            /*------------------------------------------------------------------------------------------------*/
            mMediaPlayer.setDataSource(this,video_uri);
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(true);
            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer.prepareAsync();

            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }

        adjustAspectRatio((int)mVideoWidth, (int)mVideoHeight);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    private void calculateVideoSize() {
        try {
            final Uri video_uri = Uri.fromFile(new File(videoResource));
            //AssetFileDescriptor afd = getAssets().openFd(FILE_NAME);
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(this, video_uri);
            String height = metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            mVideoHeight = Float.parseFloat(height);
            mVideoWidth = Float.parseFloat(width);

        } /*catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }*/ catch (NumberFormatException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    protected void onStop()
    {
        ADR.cancel(true);
        super.onStop();
    }

    @Override
    protected void onPause() {
        ADR.cancel(true);
        super.onPause();
    }
}