package com.example.jovan.app3.Utilities;

import android.content.Context;
import android.util.Log;

import com.example.jovan.app3.Activities.ListActivity;
import com.example.jovan.app3.Adapters.PostsAdapter2;
import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.Models.Post;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Ref;
import java.util.ArrayList;

/**
 * Created by Jovan on 16-Oct-16.
 */
public class BackgroundThread implements Runnable {
    Context context;
    AsyncTaskNotifier ATN;
    ArrayList<Post> P;
    ArrayList<String> name_array,type_array,url_array;
    String filesdir;/*
    PostsAdapter2 adapter;*/
    Refresh refresh;
    long oldRefresh;

    public BackgroundThread(Context context, AsyncTaskNotifier ATN, ArrayList<Post> P, Refresh refresh){
        this.context = context;
        this.ATN = ATN;
        this.P = P;
        this.refresh = refresh;
        this.oldRefresh = this.refresh.refresh;
        /*this.adapter = adapter;*/
        filesdir = context.getFilesDir().toString();

        createArrays();
    }

    private void createArrays() {
        name_array = new ArrayList<>();
        type_array = new ArrayList<>();
        url_array = new ArrayList<>();
        for (int i = 0;i<P.size();i++)
        {
            name_array.add(P.get(i).id+"thumb");
            type_array.add("jpg");
            url_array.add(P.get(i).thumbnail);
        }
    }


    @Override
    public void run() {
        while(true){
            Log.d("BTHRD", "Cycling");
            if(oldRefresh == refresh.refresh) {
                try {
                    Log.d("BTHRD", "Sleeping");
                    Thread.sleep(60*1000);
                } catch (InterruptedException e) {
                    Log.d("BTHRD", "Cant Sleep");
                    e.printStackTrace();
                }
                continue;
            }
            oldRefresh = refresh.refresh;
            Log.d("BTHRD", "Working");
            createArrays();
            doInBackground();
        }
    }

    protected void doInBackground() {
        int allurls = url_array.size(), index;
        URLConnection[]connections = new URLConnection[allurls];

        Log.d("ATRUNNUNG","Starting AT with PID: "+String.valueOf(android.os.Process.myTid()));
        int count;
        String filepath = null;
        if(oldRefresh != refresh.refresh)
            return;
        for (index = 0;index <url_array.size();index++) {
            if(oldRefresh != refresh.refresh)
                return;
            Log.d("AMTTAG", "1. " + String.valueOf(System.currentTimeMillis()));

            filepath = filesdir + "/" + name_array.get(index) + "." + type_array.get(index);
            try {
                File f = new File(filepath);
                if (f.exists() && !f.isDirectory()) {
                    Log.d("FILETAG", "IT EXISTS");
                    continue;
                }
                //Log.d("AMTTAG","2. " + String.valueOf(System.currentTimeMillis()));
                Log.d("FILETAG", "IT DOESNT");
                URL url = new URL(url_array.get(index));
                //URLConnection conection = url.openConnection();
                connections[index] = url.openConnection();
            } catch (Exception e) {

            }
        }
        for (index = 0;index <url_array.size();index++) {
            if(oldRefresh != refresh.refresh)
                return;
            filepath = filesdir + "/" + name_array.get(index) + "." + type_array.get(index);
            try {
                File f = new File(filepath);
                if (f.exists() && !f.isDirectory()) {
                    Log.d("FILETAG", "IT EXISTS");
                    continue;
                }
                URL url = new URL(url_array.get(index));
                Log.d("AMTTAG","2. " + String.valueOf(System.currentTimeMillis()));//do tuka zanemarlivo malku
                //fileLength = /*conection*/connections[index].getContentLength();//Ova trae od 80 - 200ms
                //Log.d("AMTTAG","4. " + String.valueOf(System.currentTimeMillis()));
                /*conection*/connections[index].connect();//ova izgleda nisto ne pravi

                //Log.d("AMTTAG","3. " + String.valueOf(System.currentTimeMillis()));//do tuka okolu 80ms, 200ms,90ms,100ms,180ms, 140ms,
                InputStream input = new BufferedInputStream(url.openStream(), 8192);//okolu 180ms
                //Log.d("AMTTAG","4. " + String.valueOf(System.currentTimeMillis()));//+180ms,190ms,190ms,160ms,170ms,
                OutputStream output = new FileOutputStream(filepath);
                //Log.d("AMTTAG","5. " + String.valueOf(System.currentTimeMillis()));//nimsto do tuka
                byte data[] = new byte[1024];
                int total = 0;
                while ((count = input.read(data)) != -1) {//while trae od 1s do 2s
                    total += count;
                    //publishProgress(total);
                    //publishProgress((float)((float)index + ((float)total)/fileLength));
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

                ((ListActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //adapter.notifyDataSetChanged();
                        ATN.notifyUpdated();
                    }
                });

            } catch (Exception e) {

                Log.d("TEXTAG", "Exception: " + e.toString() + "url: "+url_array.get(index));
                //Log.e("Error: ", e.getMessage());//Ova javuva greska ako nema poraka mozda e.tostring()????
            }
        }
        Log.d("ATRUNNUNG","Finished AT with PID: "+String.valueOf(android.os.Process.myTid()));
    }
}
