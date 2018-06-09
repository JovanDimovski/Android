package com.example.jovan.digitalfontreader;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private static int nE = 0;
    private static String timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageProcess.openImage();

        //ImageEdit2.openImage();

        nE = 0;

        Button captureButton = (Button) findViewById(R.id.captureFront);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss").format(new Date());
                        takeAPicture();
                    }
                }
        );
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d("CameraDemo", "Camera not available: "+ e.getMessage());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {

            Log.d("CameraDemo", "On picture taken");
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("CameraDemo", "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("CameraDemo", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("CameraDemo", "Error accessing file: " + e.getMessage());
            }


            camera.release();
            camera = null;
            if(nE++<5)
            {
                takeAPicture();
            }
        }
    };

    private Camera.PictureCallback mRAW = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {

            Log.d("CameraDemo", "On picture taken");
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("CameraDemo", "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("CameraDemo", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("CameraDemo", "Error accessing file: " + e.getMessage());
            }


            camera.release();
            camera = null;
            if(nE++<5)
            {
                takeAPicture();
            }
        }
    };

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){

        Log.d("CameraDemo", "In getOutputMediaFile");
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DFR");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        // Create a media file name

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp+ "_" + String.valueOf(nE) + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp+ "_" + String.valueOf(nE) + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void takeAPicture()
    {
        mCamera = getCameraInstance();
        if(mCamera == null)
            Log.d("CameraDemo", "No camera instance");
        mPreview = new CameraPreview(this, mCamera);
        //mPreview.setRotation(90);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        Log.d("CameraDemo", "In takePicture");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCamera.takePicture(null, null, mPicture);//JPEG
                //mCamera.takePicture(null,mRAW,null);//RAW ne povikuvaj ke generira ogromni fajlovi


/*                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;*/
            }
        }, 200);
    }
}