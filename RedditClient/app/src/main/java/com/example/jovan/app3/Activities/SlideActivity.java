package com.example.jovan.app3.Activities;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.Models.Post;
import com.example.jovan.app3.R;
import com.example.jovan.app3.RedditApplication;
import com.example.jovan.app3.Adapters.SlideAdapter;
import com.example.jovan.app3.Utilities.Utilities;

import java.util.ArrayList;

public class SlideActivity extends FragmentActivity implements AsyncTaskNotifier{
    // When requested, this adapter returns a SlideFragment,
    // representing an object in the collection.
    SlideAdapter adapter;
    ViewPager mViewPager;

    private ArrayList<Post> Posts;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        Posts =((RedditApplication)getApplication()).getGlobalVariable();

        Utilities.getImages(this, Posts, 0, 5/*Posts.size()*/, this);

        Log.d("VIEWPAGER", "title " + Posts.get(0).title + "author " + Posts.get(0).author + "points " + Posts.get(0).points);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        adapter = new SlideAdapter(this, getSupportFragmentManager(), Posts, this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setAdapter(adapter);
    }
    public void notifyFinished(){asyncImageDownloadFinished();}
    public void notifyUpdated(){asyncImageDownloadFinished();  }

    public void asyncImageDownloadFinished(){
        adapter.notifyDataSetChanged();
        Log.d("ImgRefTAG","true");
    }
    @Override
    public void onStop(){
        //ADR.cancel(true);
        if(Utilities.ADR == null)
            Log.d("IGTAG", "FILEDOWNLOADER IS NULL");
        else
            Log.d("IGTAG", "FILEDOWNLOADER IS NOTNOTNOTNOTNOTNOTNOT NULL");
        Log.d("IGTAG", String.valueOf(Utilities.ADR.getStatus()));
        Utilities.ADR.cancel(true);
        super.onStop();
    }
}

class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}




// Instances of this class are fragments representing a single
// object in our collection.
