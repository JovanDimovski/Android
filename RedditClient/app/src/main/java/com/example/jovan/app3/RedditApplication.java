package com.example.jovan.app3;

import android.app.Application;

import com.example.jovan.app3.Models.Post;

import java.util.ArrayList;

/**
 * Created by Jovan on 26-Aug-16.
 */
public class RedditApplication extends Application {
    private ArrayList<Post> Posts;

    public ArrayList<Post> getGlobalVariable() {
        return Posts;
    }

    public void setGlobalVariable(ArrayList<Post> Posts) {
        this.Posts = Posts;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //reinitialize variable
    }
}
