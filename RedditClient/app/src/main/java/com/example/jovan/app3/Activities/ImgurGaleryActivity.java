package com.example.jovan.app3.Activities;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.AsyncWebPageNotifier;
import com.example.jovan.app3.Adapters.GaleryAdapter;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.AsyncDownloadResource;
import com.example.jovan.app3.Utilities.AsyncGetWebPage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImgurGaleryActivity extends FragmentActivity implements AsyncWebPageNotifier, AsyncTaskNotifier {
    private String url, name;
    private List<String> allImages = new ArrayList<String>();
    private ArrayList<String> descriptions = new ArrayList<String>();
    private ArrayList<String> titles = new ArrayList<String>();
    //private ArrayList <String> text = new ArrayList<String>();
    private ArrayList <String> Images = new ArrayList<String>();
    private String [] type_array, name_array;
    private boolean url_is_gal;
    private ListView listView;
    private int curent, all;
    //private GaleryAdapter adapter;
    private AsyncDownloadResource ADR;

    GaleryAdapter adapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgur_galery);


        url = getIntent().getExtras().getString("url");
        name = getIntent().getExtras().getString("id");
        url = url.split("#")[0];
        Pattern pattern = Pattern.compile("http://(m\\.)?imgur.com/a/.*?",Pattern.DOTALL);
        Matcher matcher  = pattern.matcher(url);
        if (matcher.find()) {
            url_is_gal = true;
            url = url + "/layout/grid";
        }
        else
        {
            url_is_gal = false;
        }
        Log.d("IGTAG", "Is a galery" + String.valueOf(url_is_gal));
        new AsyncGetWebPage(/*this, */this).execute(url);
    }
    @Override
    public void onStop(){
        //ADR.cancel(true);
        if(ADR == null)
            Log.d("IGTAG", "FILEDOWNLOADER IS NULL");
        else{
            Log.d("IGTAG", "FILEDOWNLOADER IS NOTNOTNOTNOTNOTNOTNOT NULL");
            ADR.cancel(true);
        }
        //Log.d("IGTAG", String.valueOf(ADR.getStatus()));
        super.onStop();
    }

    public void notifyWeb(String webPage){
        if(url_is_gal)
            parseHtml(webPage);
        else
            parseHtmlOLD(webPage);
    }

    public void parseHtmlOLD(String webPage){
        // <div class="post-images"> pa
        // <div class="post-image-container"> pa
        // <div class="post-image" i //na isto nivo so ova ima <class="post-image-meta" vnatre ima caption za slikata
        // <a class="zoom">
        // <img
        // src="//i.imgur.com/LTSxX43.jpg"
        // alt="Humor me before I have to go"
        //</a>
        // vo nego src
        String post_images;
        Pattern pattern = Pattern.compile("class=\"post-images\".*",Pattern.DOTALL);
        Log.d("IGTAG", "INSIDE OLD HTMLPARSE");
        Matcher matcher = pattern.matcher(webPage);
        if (matcher.find()) {
            post_images = matcher.group(0);
            //TextView text = (TextView)findViewById(R.id.p1);
            //text.setText(post_images);

            List<String> allMatches = new ArrayList<String>();

            //Pattern pattern2 = Pattern.compile("class=\"post-image-container\".*?<meta",Pattern.DOTALL);
            Pattern pattern2 = Pattern.compile("class=\"post-image-container.*?<meta",Pattern.DOTALL);
            Matcher matcher2 = pattern2.matcher(post_images);
            while (matcher2.find()) {
                allMatches.add(matcher2.group());
            }
            Log.d("IGTAG", "number of image containers found " + allMatches.size());
            //text.setText(allMatches.get(0)+""+allMatches.get(1));
            //Pattern pattern3 = Pattern.compile("<a href=\"(.*?)\"",Pattern.DOTALL);
            Pattern pattern3 = Pattern.compile("((<img src=\")|(<a href=\"))(.*?)\"",Pattern.DOTALL);

            Pattern pattern4 = Pattern.compile("\"description\">(.*?)</p",Pattern.DOTALL);
            Matcher matcher3, matcher4;
            for (String temp: allMatches) {
                matcher3 = pattern3.matcher(temp);
                if (matcher3.find()) {
                    //allImages.add(matcher3.group(1));
                    allImages.add(matcher3.group(4));
                }
                matcher4 = pattern4.matcher(temp);
                if (matcher4.find()) {
                    descriptions.add(matcher4.group(1));
                }
            }
            String all = "";
            Log.d("IGTAG", "number of links found " + allImages.size());
            for (int i = 0; i < allImages.size();i++) {
                allImages.set(i, "http:"+allImages.get(i));
                all+=allImages.get(i)+"\n";
            }
            Log.d("IGTAG", "Link list" + all);
            for (int i = 0; i < descriptions.size();i++) {
                all+=descriptions.get(i)+"\n";
            }
            //text.setText(all);
            type_array = new String[allImages.size()];
            name_array = new String[allImages.size()];
            String [] url_array = new String[allImages.size()];

            for (int i = 0; i < allImages.size();i++) {
                type_array[i] = "jpg";
                name_array[i] = name+String.valueOf(i);
                String []url_split = allImages.get(i).split("\\.");
                url_split[url_split.length-2]+="l";
                String new_url = "";
                for (String s:url_split) {new_url+=s+".";}
                url_array[i] = new_url;//allImages.get(i);
                Log.d("IGTAG", url_array[i]);
            }
            for(int i = 0; i <name_array.length; i++)
            {
                Images.add(i, name_array[i]+"."+type_array[i]);
            }
            for(int i = descriptions.size(); i <Images.size(); i++)
            {
                descriptions.add("");
            }
            for(int i = titles.size(); i <Images.size(); i++)
            {
                titles.add("");
            }
            Log.d("IGTAG", "::::"+type_array[0]+":"+name_array[0]+":"+url_array[0]);
            ADR = new AsyncDownloadResource(this, this, name_array, type_array);
            ADR.execute(url_array);
            /*Za paralelno prezemawe na resursi samo ne e mnogu pobrzo ogranicuvaweto e brzina na mreza i seriski i prarlelno prezemanje*/
            /*if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                for(int i = 0;i< Images.size();i++) {
                    ADR = new AsyncDownloadResource(this, this, new String[]{name_array[i]}, new String[] {type_array[i]});
                    ADR.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url_array[i]);
                }
            }*/
        }
        //setUpListView();
        setUpPageView();
    }

    public void notifyFinished(){
        asyncImageDownloadFinished();
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    public void notifyUpdated(){
        asyncImageDownloadFinished();
        curent = mViewPager.getCurrentItem() + 1;
        ((TextView)findViewById(R.id.imageNumber)).setText(String.valueOf(curent)+"/"+String.valueOf(all));
    }

    public void asyncImageDownloadFinished(){
        adapter.notifyDataSetChanged();
        Log.d("ImgRefTAG","true");
    }


    public void parseHtml(String webPage){

        String posts, ids = "", links = "";
        ArrayList <String> id = new ArrayList<String>();
        ArrayList <String> link = new ArrayList<String>();
        Pattern pattern = Pattern.compile("<div class=\"posts\">(.*?)<div class=\"clear\"></div>",Pattern.DOTALL);

        Log.d("IGTAG", url);
        Log.d("IGTAG", webPage);

        //Log.d("IGTAG", webPage);

        Matcher matcher = pattern.matcher(webPage);
        //TextView p1 = (TextView) findViewById(R.id.p1);
        //p1.setText(webPage);

//        Log.d("IGTAG", matcher.group(1));
        if (matcher.find())
        {
            posts = matcher.group(1);
            Log.d("IGTAG", posts);
            pattern = Pattern.compile("<div id=\"(.*?)\" class=\"post\">.*?<a href=\"(.*?)\"",Pattern.DOTALL);
            matcher = pattern.matcher(posts);

            while (matcher.find()) {
                id.add(matcher.group(1));
                link.add(matcher.group(2));
                ids+=matcher.group(1)+"\n";
                links+=matcher.group(2)+"\n";
            }
            Log.d("IGTAG", ids);

            Log.d("IGTAG", links);
            /*"<div id=\"kVhgkbE\" class=\"post\">\n" +
                    "\t\t\t\t\t<a href=\"//i.imgur.com/kVhgkbE.jpg\">\n" +
                    "\t\t\t\t\t\t<img alt=\"\" src=\"//i.imgur.com/kVhgkbEb.jpg\"  />\n" +
                    "\t\t\t\t\t</a>\n" +
                    "\t\t\t\t</div>"*/

            type_array = new String[id.size()];
            name_array = new String[id.size()];
            String [] url_array = new String[id.size()];

            for (int i = 0; i < link.size();i++) {
                name_array[i] = name+String.valueOf(i);
                String []url_split = link.get(i).split("\\.");
                if (url_split[url_split.length-1].equalsIgnoreCase("jpg"))
                {
                    type_array[i] = "jpg";
                    url_split[url_split.length-2]+="l";
                }
                else if (url_split[url_split.length-1].equalsIgnoreCase("gif"))
                {
                    type_array[i] = "gif";
                }
                String new_url = "";
                for (String s:url_split) {new_url+=s+".";}
                url_array[i] = "http:"+new_url;
                Log.d("IGTAG", url_array[i]);
                //url_array[i] =  "http:"+link.get(i);
            }
            for(int i = 0; i <name_array.length; i++)
            {
                Images.add(i, name_array[i]+"."+type_array[i]);
            }

            Pattern pattern2 = Pattern.compile(".*_item:.*");
            Matcher matcher2 = pattern2.matcher(webPage);
            String jsonstr;
            if (matcher2.find()) {
                Log.d("IGTAG", matcher2.group(0));
                jsonstr = matcher2.group(0);
                Pattern pattern3;
                Matcher matcher3;
                for (String str: id) {
                    String ptrn = "\"hash\":\""+str+"\",\"title\":\"?(.*?)\"?,\"description\":\"?(.*?)\"?,";
                    pattern3 = Pattern.compile(ptrn);
                    matcher3 = pattern3.matcher(jsonstr);
                    if (matcher3.find()) {
                        String match = "";
                        String title = "";
                        String description = "";
                        if(!matcher3.group(1).equalsIgnoreCase("null"))
                            title = matcher3.group(1);
                        if(!matcher3.group(2).equalsIgnoreCase("null"))
                            description = matcher3.group(2);
                        descriptions.add(description);
                        titles.add(title);
                        Log.d("IGTAG", match+"\n");
                    }
                }
            for(int i = descriptions.size(); i <Images.size();i++){descriptions.add("");}
            for(int i = titles.size(); i <Images.size();i++){titles.add("");}
            }
            Log.d("IGTAG", "::::"+type_array[0]+":"+name_array[0]+":"+url_array[0]);
            ADR = new AsyncDownloadResource(this, this, name_array, type_array);
            ADR.execute(url_array);
            /*Za paralelno prezemawe na resursi samo ne e mnogu pobrzo ogranicuvaweto e brzina na mreza i seriski i prarlelno prezemanje*/
            /*if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                for(int i = 0;i< Images.size();i++) {
                    ADR = new AsyncDownloadResource(this, this, new String[]{name_array[i]}, new String[] {type_array[i]});
                    ADR.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url_array[i]);
                }
            }*/
        }
        else
        {
            Log.d("IGTAG", "Did not find a match for "+pattern.toString());
        }
        //setUpListView();
        setUpPageView();
    }
    /*public void setUpListView(){
        Log.d("IGTAG", "IN SETUPLISTVIEW ");
        listView = (ListView) findViewById(R.id.lvimages);
        int listViewHeight = listView.getMeasuredHeight();
        adapter = new GaleryAdapter(this, Images, descriptions, listViewHeight);
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public int oldFirstVisibleItem = 0, flag = 1, flag2 =1;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                //if (scrollState == SCROLL_STATE_FLING) {
                *//*    View itemView = view.getChildAt(0);
                    int top = Math.abs(itemView.getTop());
                    int bottom = Math.abs(itemView.getBottom());
                    int scrollBy = top >= bottom ? bottom : -top;
                    Log.d("FLINGTAG", String.valueOf(top)+" : "+String.valueOf(bottom)+" ------- "+String.valueOf(scrollBy));
                    Log.d("FLINGTAG", "First item"+String.valueOf(view.getFirstVisiblePosition()));
                    if (scrollBy == 0) {
                        return;
                    }
                    int position = view.getFirstVisiblePosition();
                    view.scrollTo(0,position);
                    if(top >= bottom)
                        position+=1;
                    else
                        position-=1;
                    smoothScrollDeferred(position, (ListView)view);*//*
                //}
            }

            private void smoothScrollDeferred(final int scrollByF,
                                              final ListView viewF) {
                final Handler h = new Handler();
                h.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        viewF.smoothScrollToPosition(scrollByF);
                    }
                });
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                final int fvi = firstVisibleItem;
                final AbsListView v = view;
                if(oldFirstVisibleItem > firstVisibleItem && flag2 == 1) {
                    //Handler handler = new Handler();
                    //handler.postDelayed(new Runnable() {
                    //    public void run() {
                    flag2 = 2;
                    v.smoothScrollBy(0, 0);
                    v.smoothScrollToPosition(fvi);
                    //    }
                    //}, 100);
                    oldFirstVisibleItem = firstVisibleItem;
                }
                else*//*if(oldFirstVisibleItem <= firstVisibleItem)*//* {
                    if (visibleItemCount > 1 && flag == 1) {
                    *//*    Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {*//*
                        flag = 2;
                        v.smoothScrollBy(0, 0);
                        v.smoothScrollToPosition(fvi + 1);
                        oldFirstVisibleItem = firstVisibleItem + 1;
                    *//*        }
                        }, 300);*//*
                    }
                }
                if(visibleItemCount == 1)
                {
                    flag = 1;
                    flag2 = 1;
                }

            }
        });
    }*/
    public void setUpPageView(){
        adapter = new GaleryAdapter(this, getSupportFragmentManager(),Images, titles, descriptions,  this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);

        curent = mViewPager.getCurrentItem() + 1;
        all = Images.size();
        ((TextView)findViewById(R.id.imageNumber)).setText(String.valueOf(curent)+"/"+String.valueOf(all));
    }

    /*public void setImageNumber(int curent, int all){
        if(curent == -1 && all == -1)
        {
            //
        }
        else
        {
            this.curent = curent;
            this.all = all;
        }

        ((TextView)findViewById(R.id.imageNumber)).setText(String.valueOf(this.curent)+"/"+String.valueOf(this.all));
    }
    public int getCurent(){return curent;}

    public int getAll(){return all;}*/
    public  void pageChange(){
        curent = mViewPager.getCurrentItem() + 1;
        ((TextView)findViewById(R.id.imageNumber)).setText(String.valueOf(curent)+"/"+String.valueOf(all));
    }
}
