package com.example.jovan.app3.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.Fragments.ImgurGifFragment;
import com.example.jovan.app3.Fragments.ImgurImageFragment;

import java.util.ArrayList;

/**
 * Created by Jovan on 29-Aug-16.
 */

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class GaleryAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> Images;
    private ArrayList<String> Descriptions;
    private ArrayList<String> Titles;
    //private ArrayList<Integer> refresh;
    private Context context;
    private int oldposition;
    private AsyncTaskNotifier ATN;
    //private int refresh = 1;

    public GaleryAdapter(Context context, FragmentManager fm, ArrayList<String> Images, ArrayList<String> Titles, ArrayList<String> Descriptions, AsyncTaskNotifier ATN) {
        super(fm);
        this.Images = Images;
        this.Descriptions = Descriptions;
        this.Titles = Titles;
        this.context = context;
        //this.oldposition = -1;
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
        String [] path_parts = Images.get(i).split("\\.");
        Fragment fragment = null;
        if(path_parts[path_parts.length - 1].equalsIgnoreCase("jpg"))
        {
            fragment = new ImgurImageFragment();
        }
        else if (path_parts[path_parts.length - 1].equalsIgnoreCase("gif"))
        {
            fragment = new ImgurGifFragment();
        }
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putString("filepath", Images.get(i));
        args.putString("description", Descriptions.get(i));
        args.putString("title", Titles.get(i));
        args.putString("filesdir", context.getFilesDir().toString());

        Log.d("FRAGTAG", Images.get(i));

        fragment.setArguments(args);
        /*ArrayList<Post> temp = new ArrayList<Post>();
        temp.add(Posts.get(i));
        getThumbnails(temp,i);*/
        Log.d("FRAGTAG", String.valueOf(i));




        return fragment;
    }

    @Override
    public int getCount() {
        return Images.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //if(position != oldposition && position%5 == 0){
            //Utilities.getImages(context, Posts, position+5,position+10, ATN);
            //Log.d("FRAGTAG","Instantiate: "+String.valueOf(position));
            oldposition = position;
        //}
        return super.instantiateItem(container, position);
    }

}