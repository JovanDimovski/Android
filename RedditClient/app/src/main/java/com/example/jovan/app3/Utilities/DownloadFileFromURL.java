package com.example.jovan.app3.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jovan.app3.Activities.DisplayImageActivity;
import com.example.jovan.app3.Activities.ImageActivity;
import com.example.jovan.app3.Old_Unused.MainActivityOld;
import com.example.jovan.app3.Activities.SlideActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Jovan on 12/08/2016.
 */
public class DownloadFileFromURL extends AsyncTask<String, String, String> {

    /*public MainActivityOld activity;*/
    private MainActivityOld activity;
    private ImageActivity di_activity;
    private SlideActivity vp_activity;
    private Context context;
    private Integer start;
    private String []name_array;
    private String filesdir;
    private String activity_type;

    public DownloadFileFromURL(/*MainActivityOld activity,*/ Context context, Integer start, String activity_type, String[] name_array)
    {
        this.activity_type = activity_type;
        if(activity_type.equalsIgnoreCase("MainActivityOld")) {
            this.activity = (MainActivityOld) context;
        }
        if(activity_type.equalsIgnoreCase("DisplayImageActivity")) {
            this.di_activity = (ImageActivity) context;
        }
        if(activity_type.equalsIgnoreCase("SlideActivity")) {
            this.vp_activity = (SlideActivity) context;
        }
        this.context = context;
        this.start=start;
        this.name_array = name_array;
        this.filesdir = context.getFilesDir().toString();
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        int index = 0;
        int position;
        for (String url_string: f_url) {
            position = start + index;
            try {
                File f = new File(filesdir + "/IMAGE" + name_array[index] + ".jpg");
                if(f.exists() && !f.isDirectory()) {
                    Log.d("FILETAG", "IT EXISTS");
                    index++;
                    continue;
                }
                Log.d("FILETAG", "IT DOESNT");
                URL url = new URL(url_string);
                URLConnection conection = url.openConnection();
                conection.connect();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(filesdir + "/IMAGE" + name_array[index] + ".jpg");
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);// writing data to file
                }
                output.flush();// flushing output
                output.close();// closing streams
                input.close();
                /*if(activity_type.equalsIgnoreCase("MainActivityOld")) {
                    this.activity.asyncTumbnailDownloadFinished();
                }*/

            } catch (Exception e) {
                //Log.e("Error: ", e.getMessage());//Ova javuva greska ako nema poraka mozda e.tostring()????
            }
            index++;
        }
        return "AAA";
    }
    protected void onPostExecute(String result) {
        if(activity_type.equalsIgnoreCase("MainActivityOld")) {
            this.activity.asyncTumbnailDownloadFinished();
        }
        if(activity_type.equalsIgnoreCase("DisplayImageActivity")) {
            this.di_activity.asyncImageDownloadFinished();
        }
        if(activity_type.equalsIgnoreCase("SlideActivity")) {
            try {
                this.vp_activity.asyncImageDownloadFinished();
            }
            catch (Exception e){
                Log.d("ErrorTag", e.toString());
            }
        }
    }
}