package com.example.jovan.app3.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.AsyncWebPageNotifier;
import com.example.jovan.app3.Adapters.CommentsAdapter2;
import com.example.jovan.app3.Models.Comment;
import com.example.jovan.app3.Models.Post;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.AsyncDownloadResource;
import com.example.jovan.app3.Utilities.AsyncGetWebPage;
import com.example.jovan.app3.Utilities.Utilities;

import java.util.ArrayList;

public class CommentsActivity2 extends AppCompatActivity implements AsyncWebPageNotifier, AsyncTaskNotifier {
    public ArrayList<Comment> Comments;
    public CommentsAdapter2 adapter;
    public ListView listView;
    public ViewGroup header;
    public long millis;

    public void notifyUpdated(){}
    public void notifyFinished(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        millis = System.currentTimeMillis();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments2);
        String data = getIntent().getExtras().getString("permalink");

        listView = (ListView) findViewById(R.id.lvComments);

        LayoutInflater inflater = getLayoutInflater();

        header = (ViewGroup)inflater.inflate(R.layout.comments_header, listView, false);

        listView.addHeaderView(header, null, false);

        //listView.addHeaderView(getLayoutInflater().inflate(R.layout.comments_header, listView, false), null, false);

        Log.d("C2TAG","on create");

        //Log.d("ComTim", "ASYNCGETWEBPAGE: "+String.valueOf(System.currentTimeMillis()-millis));
        millis = System.currentTimeMillis();
        new AsyncGetWebPage(/*this,*/ this).execute("https://www.reddit.com" + data + ".json?limit=20");//causes strange jump
        new AsyncGetWebPage(/*this,*/ this).execute("https://www.reddit.com" + data + ".json?limit=500");
    }

    public void notifyWeb(String webPage)
    {
        Log.d("ComTim", "IN NOTIFYWEB: "+String.valueOf(System.currentTimeMillis()-millis));
        //millis = System.currentTimeMillis();


        Log.d("C2TAG","notifyWeb");

        Post post = Utilities.submissionFromJson(webPage);
        //Log.d("ComTim", "CALL SUBMISSIONFROMJSON: "+String.valueOf(System.currentTimeMillis()-millis));
        //millis = System.currentTimeMillis();


        ((TextView)header.findViewById(R.id.title)).setText(post.title);
        ((TextView)header.findViewById(R.id.author)).setText(post.author);
        ((TextView)header.findViewById(R.id.points)).setText(String.valueOf(post.points));
        ((TextView)header.findViewById(R.id.selfText)).setText(String.valueOf(post.selfText));

        new AsyncDownloadResource(this, this, new String []{String.valueOf(post.id+"thumb")}, new String []{"jpg"}).execute(new String []{post.thumbnail});

        ((ImageView)header.findViewById(R.id.image)).setImageBitmap(Utilities.loadImageFromStorage(getFilesDir().toString(),String.valueOf(post.id)+"thumb.jpg"));

        Comment root = new Comment();

        Comments= Utilities.commentsFromJson(webPage);

        //Log.d("ComTim", "CALL COMMENTSFROMJSON: "+String.valueOf(System.currentTimeMillis()-millis));
        //millis = System.currentTimeMillis();

        root.replies = Comments;
        int numComments = addIndexToTree(root, 0, 0) - 1;
        //int numComments = inOrder(root);
        Log.d("C2TAG","Number of comments: " + numComments + "\n");
        /*for (Comment c: Comments) {
            Log.d("C2TAG","comment: " + c.body + "\n");
        }*/
        adapter = new CommentsAdapter2(this, Comments, numComments);

        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();


        //Log.d("ComTim", "END: "+String.valueOf(System.currentTimeMillis()-millis));
        //millis = System.currentTimeMillis();
    }
    private int inOrder(Comment comment)
    {
        int allComments = 0;
        Log.d("C2TAG","Comment index: " + comment.index + "\nComment body: " + comment.body + "\n");
        if(comment.replies != null) {
            int i = 0;
            allComments += comment.replies.size();
            while (i < comment.replies.size())
            {
                allComments += inOrder(comment.replies.get(i));
                i++;
            }
        }
        return allComments;
    }


    private int addIndexToTree(Comment comment, int index, int level)
    {
        int offset = 0;
        comment.index = index;
        comment.level = level;
        comment.isCollapsed = false;
        if(comment.replies != null) {
            for (int i = 0;i < comment.replies.size();i++) {
                offset += addIndexToTree(comment.replies.get(i),index + 1 + offset, level + 1);
            }
        }
        comment.numChildren = offset;
        return  offset + 1;
    }

}
