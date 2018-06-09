package com.example.jovan.app3.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.Models.Post;
import com.example.jovan.app3.Fragments.SlideFragment;
import com.example.jovan.app3.Utilities.Utilities;

import java.util.ArrayList;

/**
 * Created by Jovan on 29-Aug-16.
 */

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class SlideAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Post> Posts;
    //private ArrayList<Integer> refresh;
    private Context context;
    private int oldposition;
    private AsyncTaskNotifier ATN;
    //private int refresh = 1;

    public SlideAdapter(Context context, FragmentManager fm, ArrayList<Post> Posts, AsyncTaskNotifier ATN) {
        super(fm);
        this.Posts = Posts;
        this.context = context;
        this.oldposition = -1;
        this.ATN = ATN;
    }

/*

    @Override
    public void notifyDataSetChanged() {
        //refresh = 0;
        super.notifyDataSetChanged();
    }
*/

    /*@Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
    */

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
        //return POSITION_UNCHANGED;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new SlideFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putString("title", Posts.get(i).title);
        args.putString("author", Posts.get(i).author);
        args.putInt("points", Posts.get(i).points);
        args.putString("id", Posts.get(i).id);
        args.putString("source", Posts.get(i).source);
        args.putString("filesdir", context.getFilesDir().toString());
        args.putString("url", Posts.get(i).url);
        fragment.setArguments(args);
        /*ArrayList<Post> temp = new ArrayList<Post>();
        temp.add(Posts.get(i));
        getThumbnails(temp,i);*/
        Log.d("FRAGTAG", String.valueOf(i));
        return fragment;
    }

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(position != oldposition && position%5 == 0){
            Utilities.getImages(context, Posts, position+5,position+10, ATN);
            Log.d("FRAGTAG","Instantiate: "+String.valueOf(position));
            oldposition = position;
        }
        return super.instantiateItem(container, position);
    }

/*    public void getThumbnails(ArrayList<Post> Posts, int start) {
        *//*if(lThumb == 1) {
            getLargeThumbnails(Posts, start);
            return;
        }*//*
        String [] url_array = new String[Posts.size()];
        String [] name_array = new String[Posts.size()];
        int i = 0;
        for (Post post: Posts) {
            url_array[i] = post.source;
            name_array[i] = post.id+"large";
            i++;
        }
        File f = new File(context.getFilesDir().toString() + "/IMAGE" + name_array[0] + ".jpg");
        if(f.exists() && !f.isDirectory()) {
            Log.d("VPIMTAG", "Exists");
        }
        else
        {
            Log.d("VPIMTAG", "Does Not Exist");
            new DownloadFileFromURL(context, start, "SlideActivity",name_array).execute(url_array);
        }
    }*/
}