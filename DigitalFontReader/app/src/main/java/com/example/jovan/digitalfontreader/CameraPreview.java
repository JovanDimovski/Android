package com.example.jovan.digitalfontreader;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jovan on 25-Apr-17.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera)
    {
        super(context);

        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("CameraDemo", "Error setting camera preview: " + e.getMessage());
        }/*
        mCamera.takePicture();*/
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.



        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        mCamera.setDisplayOrientation(90);


        //////////////////////////////////////////////////////////////////////////

        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        int expcp = parameters.getExposureCompensation();
        //String iso = parameters.get("iso");
        //String sharpness = parameters.get("sharpness");
        Log.d("CameraParams","Exposure Compensation is: " + String.valueOf(expcp));
        //Log.d("CameraParams","ISO is: " + iso);
        //Log.d("CameraParams","Sharpness is: " + sharpness);

        parameters.setExposureCompensation(0);
        parameters.setPictureSize(2048,1536);
        parameters.setJpegQuality(100);
        //parameters.set("iso","ISO1600");//mislam ne raboti

        //parameters.set("denoise", "denoise-on");
        //parameters.set("sharpness", "20");

        mCamera.setParameters(parameters);

        parameters = mCamera.getParameters();
        expcp = parameters.getExposureCompensation();
        //iso = parameters.get("iso");
        //sharpness = parameters.get("sharpness");
        Log.d("CameraParams","New exposure Compensation is: " + String.valueOf(expcp));
        //Log.d("CameraParams","ISO is: " + iso);
        //Log.d("CameraParams","Sharpness is: " + sharpness);

        //Log.d("CameraParams","Camera parametars: " + parameters.flatten());

        /*requestLayout();
        mCamera.setParameters(parameters);

        // Important: Call startPreview() to start updating the preview surface.
        // Preview must be started before you can take a picture.
        mCamera.startPreview();*/

        //////////////////////////////////////////////////////////////////////////



        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d("CameraDemo", "Error starting camera preview: " + e.getMessage());
        }


    }
}