package com.example.jovan.app3.Models;

import java.util.ArrayList;

/**
 * Created by Jovan on 17-Aug-16.
 */
public class Comment {
    public int index;
    public int level;
    public boolean isCollapsed;
    public int numChildren;

    public String body;
    public String created;
    public String author;
    public String score;
    public String gilded;
    public ArrayList<Comment> replies;
    public Boolean hasReplies;
}
