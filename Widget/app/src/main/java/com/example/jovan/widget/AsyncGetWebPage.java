package com.example.jovan.widget;

import android.os.AsyncTask;
import android.util.Log;

import com.example.jovan.widget.AsyncWebPageNotifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jovan on 01-Sep-16.
 */
public class AsyncGetWebPage extends AsyncTask<String, Void, String> {
    //private Context context;
    private AsyncWebPageNotifier AWPN;
    private long millis;
    private String tag;

    public AsyncGetWebPage(/*Context context, */AsyncWebPageNotifier AWPN, String tag)
    {
        /*this.context = context;*/
        this.AWPN = AWPN;
        this.tag = tag;
    }

    @Override
    protected String doInBackground(String... URLS) {
        //String webPage;
        //for (String url:URLS) {
            //webPage = getWebPage(url);
        //}

        Log.d("C2TAG","Do in background: " + URLS[0]);
        return getWebPage(URLS[0]); //webPage;
    }

    @Override
    protected void onPostExecute(String webPage){
        //((VideoActivity)context).setWebPage(webPage);
        Log.d("C2TAG","on post execute");
        AWPN.notifyWeb(webPage, tag);
    }

    public String getWebPage(String url){
        String result = null;
        try
        {
            millis = System.currentTimeMillis();


            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            Log.d("ComTim", "open conn: "+ String.valueOf(System.currentTimeMillis()-millis));
            millis = System.currentTimeMillis();
            connection.setReadTimeout(30000);
            connection.setRequestProperty("User-Agent", "Alien V1.0");

            boolean redirect = false;
            // normally, 3xx is redirect
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            while (redirect) {
                Log.d("RDRCTT", "redirecting");
                redirect = false;
                // get redirect url from "location" header field
                String newUrl = connection.getHeaderField("Location");
                // open the new connnection again
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
                connection.setRequestProperty("User-Agent", "Alien V1.0");
                connection.setReadTimeout(30000);

                // normally, 3xx is redirect
                status = connection.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP
                            || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true;
                }
            }



            Log.d("ComTim", "setreqp: "+ String.valueOf(System.currentTimeMillis()-millis));
            millis = System.currentTimeMillis();
            StringBuffer buffer = new StringBuffer(8192);
            String line = "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Log.d("ComTim", "before read: "+ String.valueOf(System.currentTimeMillis()-millis));
            millis = System.currentTimeMillis();
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
                buffer.append("\n");
            }
            Log.d("ComTim", "after read: "+ String.valueOf(System.currentTimeMillis()-millis));
            millis = System.currentTimeMillis();

            reader.close();
            result = buffer.toString();
            Log.d("ComTim", "size of result: "+ String.valueOf(result.getBytes().length/1024)+"KB");

            Log.d("C2TAG","in getWebPage result: \n" + result);
        }
        catch (Exception e)
        {
            //value += "EXCEPTION" + e.toString();
            Log.d("C2TAG","Error: " + e.toString() + " url: " + url);
        }
        Log.d("C2TAG","in getWebPage result: \n" + result);
        return result;
    }

    //Pravi problem na imgur ili so html//mozda treba da se namesti soodvete encoding
    /*public String getWebPage(String url){
        URL u= null;
        String page = null;
        try {
            u = new URL(url);
            page = IOUtils.toString(u.openConnection().getInputStream());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return page;
    }*/
}
