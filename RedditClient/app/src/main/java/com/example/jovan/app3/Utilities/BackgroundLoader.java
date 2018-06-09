package com.example.jovan.app3.Utilities;

import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import com.example.jovan.app3.Adapters.PostsAdapter2;
import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.AsyncWebPageNotifier;
import com.example.jovan.app3.Models.Post;

import java.util.ArrayList;

/**
 * Created by Jovan on 28-Sep-16.
 */
public class BackgroundLoader implements Runnable, AsyncWebPageNotifier, AsyncTaskNotifier{
    private PostsAdapter2 adapter;
    private ArrayList<Post> Posts;
    private ArrayList<String> url_array;
    private AsyncDownloadResource ADR;
    private AsyncGetWebPage AGW;
    private Context context;
    private Integer sizeof_array;

    public BackgroundLoader(ArrayList<String> url_array, Context context, PostsAdapter2 adapter)
    {
        this.adapter = adapter;
        this.context = context;
        this.url_array = url_array;
        this.sizeof_array = url_array.size();
        this.Posts = new ArrayList<>();
    }

    public void run(){
/*        for (String name: name_array) {
            new AsyncGetWebPage(*//*this, *//*this).execute("https://www.reddit.com/" + name + ".json?limit=0");
        }*/
        for (String url: url_array) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                AGW = new AsyncGetWebPage(/*this,*/ this);
                //AGW.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://www.reddit.com/" + url + ".json?limit=0");
                AGW.execute("https://www.reddit.com/" + url + ".json?limit=0");
            }
        }
    }

    public void notifyWeb(String webPage) {
        if(webPage != null) {
            //ArrayList<Post> PostsNew;
            Post post = Utilities.submissionFromJson(webPage);
            Posts.add(post);
        }
        sizeof_array--;
        if(sizeof_array == 0)
            downloadThumbnails();
    }

    public void downloadThumbnails(){
        //adapter.notifyDataSetChanged();
        //Log.d("DOSEARCH", "url: "+ url+" after: "+after+"zize of posts: "+Posts.size());

        String[] name_array = new String[Posts.size()];
        String[] type_array = new String[Posts.size()];
        String[] url_array  = new String[Posts.size()];
        for (int i = 0; i<Posts.size();i++)
        {
            name_array[i] = Posts.get(i).id+"thumb";
            type_array[i] = "jpg";
            url_array[i]  = Posts.get(i).thumbnail;
        }
        String t1 ="";
        for (String t2:url_array) {
            t1+=t2;
        }
        ADR = new AsyncDownloadResource(context, this, name_array,type_array);
        ADR.execute(url_array);
    }
    public void notifyUpdated(){adapter.notifyDataSetChanged();}
    public void notifyFinished(){}
}
