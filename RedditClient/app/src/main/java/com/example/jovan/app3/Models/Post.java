package com.example.jovan.app3.Models;

/**
 * Created by Jovan on 22/07/2016.
 */

public class Post {

    public String subreddit;
    public String title;
    public String author;
    public int points;
    public int numComments;
    public String permalink;
    public String url;
    public String domain;
    public String id;
    public String name;
    public String thumbnail;
    public String source;
    public String flair;
    public int gilded = 0;
    public Boolean locked;
    public Boolean over18;
    public String selfText;

    public String getData(){
        return "Subreddit: "+subreddit+"\nTitle: "+title+"\nAuthor: "+"\nPoints: "+(Integer)points+"\nURL: "+url;
    }
}