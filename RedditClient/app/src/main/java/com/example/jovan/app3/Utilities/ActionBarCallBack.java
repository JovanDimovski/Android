package com.example.jovan.app3.Utilities;


import android.content.Context;
import android.content.Intent;
import android.support.v7.view.ActionMode;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jovan.app3.Activities.ListActivity;
import com.example.jovan.app3.Activities.SlideActivity;
import com.example.jovan.app3.Activities.TextureActivity;
import com.example.jovan.app3.Models.Post;
import com.example.jovan.app3.R;
import com.example.jovan.app3.RedditApplication;

/**
 * Created by Jovan on 14-Oct-16.
 */

public class ActionBarCallBack implements ActionMode.Callback {
    private Post post;
    private Context context;

    public ActionBarCallBack(Context context, Post post){
        this.post = post;
        this.context = context;
    }
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.subreddit: {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("subreddit",post.subreddit);
                /*for (Pair<AsyncDownloadResource, String[] > adr:((ListActivity)context).ADRARR) {
                    adr.first.cancel(true);
                }*/
                context.startActivity(intent);
                return true;
            }
            default:
                return false;

        }
        //return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        mode.getMenuInflater().inflate(R.menu.context_menu, menu);
        mode.getMenu().findItem(R.id.user).setTitle("u/"+post.author);
        mode.getMenu().findItem(R.id.subreddit).setTitle("r/"+post.subreddit);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub

        //mode.setTitle("CheckBox is Checked");
        return false;
    }

}