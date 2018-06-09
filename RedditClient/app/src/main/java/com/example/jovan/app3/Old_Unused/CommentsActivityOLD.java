package com.example.jovan.app3.Old_Unused;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.jovan.app3.Models.Comment;
import com.example.jovan.app3.Utilities.FetchData;
import com.example.jovan.app3.Models.FlatComment;
import com.example.jovan.app3.R;

import java.util.ArrayList;

public class CommentsActivityOLD extends AppCompatActivity {

    private int first_run = 0;
    private int thread_finished = 0;
    private Thread t;
    private FetchData redditdata;
    private String after, result;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insideOnCreate();
    }
    protected void insideOnCreate(){
        if(first_run == 0) {
            setContentView(R.layout.loading);
            toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getIntent().getExtras().getString("title"));

            String data = getIntent().getExtras().getString("permalink");
            //Log.d("PermalinkTAG", data);
            after = null;
            result = null;
            redditdata = new FetchData("https://www.reddit.com" + data + ".json", "Comments", this, "CommentsActivityOLD");
            t = new Thread(redditdata);
            t.start();
            first_run = 1;
        }
        if(thread_finished == 0)
        {
            //so handller
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    insideOnCreate();
                }
            }, 300);
            return;
        }
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_comments);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("title"));


        //timer1 = System.currentTimeMillis() - timer1;

        ArrayList<Comment> comments = redditdata.getComments();
        after =redditdata.getAfter();
        String q_str = "?after="+after;
        final ArrayList<FlatComment> flat = flatenCommentTree(comments, 0);//iterateCommentTree(comments,"->");
        for(int i=0;i<flat.size();i++)flat.get(i).index = i;
        final ArrayList<Integer> hiddenPositions = new ArrayList<Integer>();
        for(int i=0;i<flat.size();i++)
            hiddenPositions.add(1);
        final CommentsAdapterOLD adapter = new CommentsAdapterOLD(this, flat, hiddenPositions);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvComments);

        /*LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.item_header, listView, false);
        listView.addHeaderView(header, null, false);*/

        listView.setAdapter(adapter);
        /*Za odstranuvawe na samo kliknatiot item*/
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                int count =0,realposition = -1;
                for(int i = 0;i < hiddenPositions.size();i++){
                    count += hiddenPositions.get(i);
                    if(count == (position+1))
                    {
                        realposition = i;
                        hiddenPositions.set(i,0);
                        break;
                    }
                }
                Log.d("CLICKTAG",((Integer)position).toString()+"real position: "+((Integer)realposition).toString());
                *//*for(int i = 0;i < hiddenPositions.size();i++){
                    Log.d("CLICKTAG",((Integer)i).toString()+": "+((Integer)hiddenPositions.get(i)).toString());
                }*//*

                adapter.notifyDataSetChanged();
            }
        });*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                int count =0,realposition = -1;
                for(int i = 0;i < hiddenPositions.size();i++){
                    count += hiddenPositions.get(i);
                    if(count == (position+1))
                    {
                        realposition = i;
                        int col = 0;
                        if(realposition + 1 < hiddenPositions.size()) {
                            if (flat.get(realposition + 1).level > flat.get(realposition).level) {
                                if (hiddenPositions.get(realposition + 1) == 0) {
                                    col = 1;
                                }
                            }
                            for (int j = realposition + 1; j < hiddenPositions.size(); j++) {
                                if (flat.get(j).level > flat.get(realposition).level) {
                                    if (col == 0) {
                                        hiddenPositions.set(j, 0);
                                    } else {
                                        hiddenPositions.set(j, 1);
                                    }
                                } else {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                Log.d("CLICKTAG",((Integer)position).toString()+"real position: "+((Integer)realposition).toString());
                /*for(int i = 0;i < hiddenPositions.size();i++){
                    Log.d("CLICKTAG",((Integer)i).toString()+": "+((Integer)hiddenPositions.get(i)).toString());
                }*/

                adapter.notifyDataSetChanged();
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem, previousFirstVisibleItem, direction;
            private int totalItem;
            private LinearLayout lBelow;
            private int flag = 0, flag2 = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.previousFirstVisibleItem = this.currentFirstVisibleItem;

                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.direction = this.previousFirstVisibleItem - this.currentFirstVisibleItem;
                ActionBar ab = getSupportActionBar();
                if (ab == null) {
                    return;
                }
                if (this.direction < 0) {
                    if (ab.isShowing()) {
                        ab.hide();
                    }
                } else if (this.direction > 0) {
                    if (!ab.isShowing()) {
                        ab.show();
                    }
                }
                Log.d("SCROLLSTATE", "::: "+this.previousFirstVisibleItem+"::"+this.currentFirstVisibleItem+"::"+this.direction);
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount && this.currentScrollState == SCROLL_STATE_IDLE) {

                }

            }
        });
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    public String iterateCommentTree(ArrayList <Comment> comments, String pref){
        String temp = "";
        for(Comment comment: comments){
            temp+=pref+comment.body+"\n";
            if(comment.hasReplies){
                temp+= iterateCommentTree(comment.replies,pref+"->")+"\n";
            }
        }
        return temp;
    }

    public ArrayList <FlatComment> flatenCommentTree(ArrayList <Comment> comments, int level){
        ArrayList <FlatComment> flat = new ArrayList<FlatComment>();
        ArrayList <FlatComment> replies = new ArrayList<FlatComment>();
        for(Comment comment: comments){
            FlatComment f = new FlatComment();
            f.comment = comment;
            f.level = level;
            flat.add(f);
            if(comment.hasReplies){
                replies = flatenCommentTree(comment.replies, level+1);
                flat.addAll(replies);
            }
            f.numReplies = replies.size();
        }
        return flat;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    public void redditDataDownloadFinished()
    {
        thread_finished = 1;
    }
}
