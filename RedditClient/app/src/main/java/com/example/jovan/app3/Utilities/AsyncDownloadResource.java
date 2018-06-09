package com.example.jovan.app3.Utilities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Jovan on 01-Sep-16.
 */

public class AsyncDownloadResource extends AsyncTask<String, Float, String> {
    private Context context;
    private String []name_array,type_array;
    private String filesdir, activity_type;
    private long fileLength;
    private AsyncTaskNotifier ATN;
    private int index, oldindex, allurls;
    private int all;

    private boolean isPaused = false;
    private int lastIndex;

    public AsyncDownloadResource(Context context, AsyncTaskNotifier ATN, String[] name_array, String[] type_array)
    {
        this.context = context;
        this.ATN = ATN;
        this.activity_type = activity_type;
        this.name_array = name_array;
        this.type_array = type_array;
        this.filesdir = context.getFilesDir().toString();
        this.index = 0;
        this.oldindex = 0;
        this.allurls = name_array.length;
    }


    @Override
    protected String doInBackground(String... URLS) {
        URLConnection []connections = new URLConnection[allurls];

        Log.d("ATRUNNUNG","Starting AT with PID: "+String.valueOf(android.os.Process.myTid()));
        int count;
        String filepath = null;
        all = URLS.length;
        for (String url_string: URLS) {
            Log.d("AMTTAG", "1. " + String.valueOf(System.currentTimeMillis()));
            if (isCancelled()) {
                Log.d("ATRUNNUNG", "Canceling AT with PID: " + String.valueOf(android.os.Process.myTid()));
                break;
            }
/*            if(isPaused) {
                lastIndex = index - 1;
                break;
            }*/
            //NADVORESNO DA SE RESI
            //ZACUVAJ SITE KOI NE SE ZAVRSENI,
            //ODKAZI GI,
            //PA PRIKLUCI OD POCETOK
            filepath = filesdir + "/" + name_array[index] + "." + type_array[index];
            try {
                File f = new File(filepath);
                if (f.exists() && !f.isDirectory()) {
                    Log.d("FILETAG", "IT EXISTS");
                    index++;
                    continue;
                }
                //Log.d("AMTTAG","2. " + String.valueOf(System.currentTimeMillis()));
                Log.d("FILETAG", "IT DOESNT");
                URL url = new URL(url_string);
                //URLConnection conection = url.openConnection();
                connections[index] = url.openConnection();
                index++;
            } catch (Exception e) {

            }
        }
        index = 0;
        for (String url_string: URLS) {
            Log.d("AMTTAG", "1. " + String.valueOf(System.currentTimeMillis()));
            if (isCancelled()) {
                Log.d("ATRUNNUNG", "Canceling AT with PID: " + String.valueOf(android.os.Process.myTid()));
                break;
            }
            filepath = filesdir + "/" + name_array[index] + "." + type_array[index];
            try {
                File f = new File(filepath);
                if (f.exists() && !f.isDirectory()) {
                    Log.d("FILETAG", "IT EXISTS");
                    index++;
                    continue;
                }
                URL url = new URL(url_string);
                Log.d("AMTTAG","2. " + String.valueOf(System.currentTimeMillis()));//do tuka zanemarlivo malku
                fileLength = /*conection*/connections[index].getContentLength();//Ova trae od 80 - 200ms
                //Log.d("AMTTAG","4. " + String.valueOf(System.currentTimeMillis()));
                /*conection*/connections[index].connect();//ova izgleda nisto ne pravi

                Log.d("AMTTAG","3. " + String.valueOf(System.currentTimeMillis()));//do tuka okolu 80ms, 200ms,90ms,100ms,180ms, 140ms,
                InputStream input = new BufferedInputStream(url.openStream(), 8192);//okolu 180ms
                Log.d("AMTTAG","4. " + String.valueOf(System.currentTimeMillis()));//+180ms,190ms,190ms,160ms,170ms,
                OutputStream output = new FileOutputStream(filepath);
                Log.d("AMTTAG","5. " + String.valueOf(System.currentTimeMillis()));//nimsto do tuka
                byte data[] = new byte[1024];
                int total = 0;
                while ((count = input.read(data)) != -1) {//while trae od 1s do 2s
                    total += count;
                    //publishProgress(total);
                    publishProgress((float)((float)index + ((float)total)/fileLength));
                    output.write(data, 0, count);// writing data to file
                }
                Log.d("AMTTAG","6. " + String.valueOf(System.currentTimeMillis()));//do tuka 2000ms 2s,1000ms,1000ms, ///1600ms,1000ms
                output.flush();// flushing output
                output.close();// closing streams
                input.close();
                Log.d("AMTTAG","7. " + String.valueOf(System.currentTimeMillis()));//do tuka nisto
                /*if(activity_type.equalsIgnoreCase("MainActivityOld")) {
                    this.activity.asyncTumbnailDownloadFinished();
                }*/

            } catch (Exception e) {

                Log.d("TEXTAG", "Exception: " + e.toString() + "url: "+url_string);
                //Log.e("Error: ", e.getMessage());//Ova javuva greska ako nema poraka mozda e.tostring()????
            }
            index++;
        }
        Log.d("ATRUNNUNG","Finished AT with PID: "+String.valueOf(android.os.Process.myTid()));
        return null;
    }
    @Override
    protected void onProgressUpdate(Float... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        //TextView textView = (TextView) ((Activity) context).findViewById(R.id.videolength);
        //int perc = (int)((100*progress[0])/fileLength);
        int perc = (int)(((float)(100*(progress[0]/*+1*/)))/allurls);
        ProgressBar mProgress = (ProgressBar) ((Activity)context).findViewById(R.id.progress);

        try
        {
            //textView.setText(String.valueOf(perc) + "%" + " - " + index + "/" + all);
            mProgress.setProgress(perc);
        }catch (Exception e)
        {

        }
        if(index > oldindex) {
            oldindex = index;
            ATN.notifyUpdated();
        }
    }


    protected void onPostExecute(String result) {
        //((VideoActivity)context).setVideo();
        ATN.notifyFinished();
    }

    @Override
    protected void onCancelled(String result){
        //ATN.notifyFinished();
    }
}