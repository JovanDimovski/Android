package com.example.jovan.app3.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jovan.app3.Adapters.PostsAdapter2;
import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.AsyncWebPageNotifier;
import com.example.jovan.app3.Models.Post;
import com.example.jovan.app3.R;
import com.example.jovan.app3.RedditApplication;
//import com.example.jovan.app3.Utilities.ADRresources;
import com.example.jovan.app3.Utilities.ActionBarCallBack;
import com.example.jovan.app3.Utilities.AsyncDownloadResource;
import com.example.jovan.app3.Utilities.AsyncGetWebPage;
import com.example.jovan.app3.Utilities.BackgroundLoader;
import com.example.jovan.app3.Utilities.BackgroundThread;
import com.example.jovan.app3.Utilities.Refresh;
import com.example.jovan.app3.Utilities.Utilities;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ListActivity extends AppCompatActivity implements AsyncWebPageNotifier, AsyncTaskNotifier{
    private PostsAdapter2 adapter;
    private ArrayList<Post> Posts = new ArrayList<Post>();
    private ListView listView;
    private String after, oldafter;
    private String url = "https://www.reddit.com/r/art/top.json?sort=top&t=all&limit=100";//limit number of posts per request
    private int [] spinner_init = new int [2];
    public boolean fromScroll = false;
    private AsyncGetWebPage AGW;
    private AsyncDownloadResource ADR;
    public ArrayList<Pair<AsyncDownloadResource,String[]>> ADRARR = new ArrayList<Pair<AsyncDownloadResource, String[]>>();;

    private String subreddit = "Art";

    private boolean getNextPageFinished = false;
    private Map<String, Boolean> afterMap = new HashMap<String, Boolean>();
    public Refresh refresh;
    Thread background;

    ArrayList<String> SubredditList = new ArrayList<String>();

    public Spinner spinner_sub = null;
    public Spinner spinner_sort = null;
    public Integer selected_sub = 0;
    public String subreddit_extra = "";

    private int _xDelta;
    private int _yDelta;
    private boolean isClick;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        refresh = new Refresh();
        refresh.refresh = System.currentTimeMillis();
        BackgroundThread BT = new BackgroundThread(this, this, Posts, refresh);
        background = new Thread(BT);
        background.start();

        try
        {
            subreddit_extra = getIntent().getExtras().getString("subreddit");
        } catch (Exception e) {}

        if(subreddit_extra!="") {
            if (subreddit_extra != "null") {
                if (subreddit_extra != null) {
                    SubredditList.add(subreddit_extra);
                    selected_sub = SubredditList.size()-1;
                    url = "https://www.reddit.com/r/" + subreddit_extra + "/top.json?sort=top&t=all&limit=100";
                }
            }
        }
        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        //RelativeLayout contextualActionBar = (RelativeLayout) findViewById(R.id.contextual_toolbar);

        /*contextualActionBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {


                //final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        *//*_xDelta = X - lParams.leftMargin;*//*
                        _yDelta = Y - lParams.height*//*.topMargin*//*;
                        isClick = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isClick)
                            Log.d("CABT","This was CLICK");
                        else
                            Log.d("CABT","This was DRAG");
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        int oldLayoutHeight = layoutParams.height;
                        *//*layoutParams.leftMargin = X - _xDelta;*//*
                        if(Y - _yDelta < 100)
                            layoutParams.height*//*.topMargin*//* = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, getResources().getDisplayMetrics());
                        else if(Y - _yDelta > 500)
                            layoutParams.height*//*.topMargin*//* = 500;
                        else
                            layoutParams.height*//*.topMargin*//* = Y - _yDelta;
                        if(oldLayoutHeight - layoutParams.height < -4 || oldLayoutHeight - layoutParams.height > 4)
                            isClick = false;
                        Log.d("CABT", String.valueOf(Y - _yDelta));
                        Log.d("CABT", String.valueOf(_yDelta));
                        *//*layoutParams.rightMargin = -250;*//*
                        //layoutParams.bottomMargin = -250;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                findViewById(R.id.lvParent).invalidate();
                *//*if(isClick)
                    return false;
                else*//*
                    return true;
            }
        });*/

        listView = (ListView) findViewById(R.id.lvItems);

        //listView.setLongClickable(true);

        listView.addHeaderView(getLayoutInflater().inflate(R.layout.item_header, listView, false), null, false);

        adapter = new PostsAdapter2(this, Posts);

        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentFirstVisibleItem, previousFirstVisibleItem, visibleItemCount, totalItemCount;
            private int currentScrollState, direction;
            private LinearLayout lBelow;
            private int flag = 0, flag2 = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.previousFirstVisibleItem = this.currentFirstVisibleItem;
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
                this.visibleItemCount = visibleItemCount;
                this.totalItemCount = totalItemCount;
                //timer2 = System.currentTimeMillis();
/*                if(this.currentFirstVisibleItem > 0) {
                    if (this.currentFirstVisibleItem >= this.totalItem - 25) {
                        if (flag == 0) {
                            getNextPage();//ni treba znamence deka pominalo ova a goleminata ostanala pomala od pragot
                        }
                        flag = 1;
                    } else {
                        flag = 0;//ako ne dodademe poveke od 25
                        // ne garantira deka ke se povika ova
                        // i zaglavuva nema da pravi sleden povik iako treba
                    }

                }*/
                //timer2 = System.currentTimeMillis() - timer2;
                //Log.d("TAGSCROLL", timer1+":::::"+timer2);
                if (this.currentFirstVisibleItem >= this.totalItemCount - 75)
                {
                    Log.d("SCROLLTAG","FVI: "+String.valueOf(this.currentFirstVisibleItem));
                    if(getNextPageFinished) {
                        Log.d("SCROLLTAG","FVI: "+String.valueOf(getNextPageFinished));
                        getNextPage();
                    }
                }

            }

            private void isScrollCompleted() {
                if (totalItemCount - currentFirstVisibleItem == visibleItemCount && this.currentScrollState == SCROLL_STATE_IDLE) {

                }

            }
        });


/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int pos, long id) {
                Log.v("LONGCLICK","pos: " + pos);
            }
        });*/
/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                //TODO ako pricnam na eden sub pa odma na drug od spinerot za odbiranje index out of bounds exception se javuva
                Log.d("CLICKTAG", "ITEM CLICKED: "+position+": :"+Posts.get(position-1).permalink);
                Intent intent = new Intent(ListActivity.this, CommentsActivityOLD.class);
                intent.putExtra("permalink",Posts.get(position-1).permalink);
                intent.putExtra("title",Posts.get(position-1).title);
                startActivity(intent);

            }
        });*/
        getNextPageFinished = false;
        new AsyncGetWebPage(/*this,*/ this).execute(url);
    }

    private void getNextPage(){
        getNextPageFinished = false;
        afterMap.put(after, false);

        String q_str = "";
        if (url.indexOf('?') == -1)
            q_str = "?after=" + after;
        else
            q_str = "&after=" + after;
        fromScroll = true;
        //new AsyncGetWebPage(ListActivity.this, ListActivity.this).execute(url + q_str);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new AsyncGetWebPage(/*ListActivity.this,*/ ListActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url + q_str);
            Log.d("TAGP", q_str);
        } else {
            new AsyncGetWebPage(/*ListActivity.this, */ListActivity.this).execute(url + q_str);
            Log.d("TAGP", q_str);
        }
    }

    public void doSearch(String temp) {
        afterMap = new HashMap<String, Boolean>();
        for (Pair<AsyncDownloadResource, String[]> adr:ADRARR) {
            //adr.first.cancel(true);
        }
        url = temp;
        after = "";
        oldafter = after;
        fromScroll = false;
        Log.d("DOSEARCH", "FROM SCROLL"+String.valueOf(fromScroll)+"url"+ url);
        new AsyncGetWebPage(/*this,*/ this).execute(url);
        /*if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            AGW = new AsyncGetWebPage(*//*this,*//* this);
            AGW.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }*/
    }

    public void notifyWeb(String webPage) {
        //Parse json webpage
        ArrayList<Post> PostsNew;

        Pair<String, ArrayList<Post>> pair = Utilities.postsFromJSON(webPage);
        after = pair.first;
        getNextPageFinished = true;
        if(oldafter!=null)
            if(oldafter!= "") {
                afterMap.put(oldafter, true);
            }
        Log.d("SCROLLTAG","FVI: "+afterMap);
        /*
        */
        //map.get(after); // returns true|false

        if(after == oldafter && after != "") {
            return;
        }
        oldafter = after;
        PostsNew = pair.second;

        if(fromScroll) {
            Posts.addAll(PostsNew);
        }
        else {
            Posts.clear();
            Posts.addAll(PostsNew);
            //Posts = PostsNew;
            adapter = new PostsAdapter2(this, Posts);
            listView.setAdapter(adapter);
        }

        adapter.notifyDataSetChanged();

        Log.d("DOSEARCH", "url: "+ url+" after: "+after+"zize of posts: "+Posts.size());
        String[] name_array = new String[Posts.size()];
        String[] type_array = new String[Posts.size()];
        String[] url_array  = new String[Posts.size()];
        for (int i = 0; i<Posts.size();i++)
        {
            name_array[i] = Posts.get(i).id+"thumb";
            type_array[i] = "jpg";
            url_array[i]  = Posts.get(i).thumbnail;
        }
        //ADR = new AsyncDownloadResource(this, this, name_array,type_array);
        //ADR.execute(url_array);
        //ADRARR.add(new Pair<AsyncDownloadResource, String[]>(ADR, url_array));

        refresh.refresh = System.currentTimeMillis();
        background.interrupt();

    }

    public void notifyUpdated(){
        Log.d("BTHRD", "notifyUpdated in UI thread");// se povikuva so stara referenca??
        adapter.notifyDataSetChanged();
    }

    public void notifyFinished(){}

    public void displayDropDown(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        ActionBar action = getSupportActionBar();

        action.setDisplayShowCustomEnabled(true); //enable it to display a custom view in the action bar.
        action.setCustomView(R.layout.custom_actionbar);//add the custom view
        action.setDisplayShowTitleEnabled(false); //hide the title

        SubredditList.addAll(Arrays.asList("All","Android","AskReddit","AskScience","Aww","DIY","Earthporn","Food","Gifs","Pics","TodayILearned","Videos","WorldNews"));

        spinner_sub = (Spinner) findViewById(R.id.subreddits);
        if(selected_sub != 0)
            SubredditList.add(subreddit_extra);
        spinner_sub.setSelection(selected_sub);
        spinner_sort = (Spinner) findViewById(R.id.sort);

        // Create an ArrayAdapter using the string array and a default spinner layout
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.subreddits_array, android.R.layout.simple_spinner_item);*/
        //so custom item

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  R.layout.spinner_item, SubredditList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Za poppup menito
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_sub.setAdapter(adapter);
        spinner_init[0] = 0;
        spinner_init[1] = 0;
        spinner_sub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String sub_s = (String)((TextView) selectedItemView).getText(),sort_s = spinner_sort.getSelectedItem().toString();
                SpannableString ss1=  new SpannableString(sub_s +"\n"+ sort_s);
                ss1.setSpan(new RelativeSizeSpan(0.8f),sub_s.length() + 1 ,sub_s.length()+sort_s.length() + 1, 0); // set size
                ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#ffbbbbbb")), sub_s.length() + 1, sub_s.length()+sort_s.length() + 1, 0);// set color
                ((TextView)selectedItemView).setText(ss1);

                //za da ne se selektira avtomatski prviot item treba da se najdi podoba nacin
                if(spinner_init[0] == 1)
                {

                    //((TextView)parentView.getChildAt(0)).setTextColor(Color.BLUE);
                    String sort = spinner_sort.getSelectedItem().toString();
                    String[] parts = sort.split(" ");
                    String temp;
                    subreddit = spinner_sub.getSelectedItem().toString();
                    if(parts.length > 1)
                        temp = "https://www.reddit.com/r/"+ spinner_sub.getSelectedItem().toString() + "/"+ parts[0]+ "/.json"+ "?sort="+ parts[0]+ "&t="+ parts[1]+ "&limit=100";
                    else
                        temp = "https://www.reddit.com/r/"+ spinner_sub.getSelectedItem().toString() + "/"+ parts[0]+ "/.json"+ "?sort="+ parts[0]+ "&limit=100";
                    Log.d("SpinnerTAG","::::: "+temp);
                    doSearch(temp);
                }
                spinner_init[0] = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        ArrayList<String> list_sort = new ArrayList<>();
        list_sort.addAll(Arrays.asList("hot","new","top hour","top day","top week","top month","top year","top all time","controversial hour","controversial day","controversial week","controversial month","controversial year","controversial all time"));

        ArrayAdapter<String> adapter_sort = new ArrayAdapter<String>(this,  R.layout.spinner_item, list_sort);

        //Za poppup menito
        adapter_sort.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_sort.setAdapter(adapter_sort);
        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int sub_pos = spinner_sub.getSelectedItemPosition();
                Log.d("SpinnerTAG", ((Integer) sub_pos).toString());
                String sub_s = (String) spinner_sub.getItemAtPosition(sub_pos);
                Log.d("SpinnerTAG", sub_s);
                String sort_s = (String)((TextView) view).getText();
                SpannableString ss1=  new SpannableString(sub_s +"\n"+ sort_s);
                ss1.setSpan(new RelativeSizeSpan(0.8f),sub_s.length() + 1 ,sub_s.length()+sort_s.length() + 1, 0); // set size
                ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#ffbbbbbb")), sub_s.length() + 1, sub_s.length()+sort_s.length() + 1, 0);// set color
                ((TextView)spinner_sub.getChildAt(0)).setText(ss1);
                ((TextView)view).setText(null);
                //sub_view.setText("LALALALALALA");
                //((TextView)view).setBackgroundResource(R.drawable.btn_dropdown);
                if(spinner_init[1] == 1) {
                    // hide selection text
                    // if you want you can change background here
                    String sort = spinner_sort.getSelectedItem().toString();
                    String[] parts = sort.split(" ");
                    String temp;
                    String sub;
                    if(spinner_sub.getSelectedItem().toString().equalsIgnoreCase("random"))
                        sub = Posts.get(0).subreddit;
                    else
                        sub = spinner_sub.getSelectedItem().toString();
                    if (parts.length > 1)
                        temp = "https://www.reddit.com/r/" + sub + "/" + parts[0] + "/.json" + "?sort=" + parts[0] + "&t=" + parts[1]+ "&limit=100";
                    else
                        temp = "https://www.reddit.com/r/" + sub + "/" + parts[0] + "/.json" + "?sort=" + parts[0]+ "&limit=100";
                    Log.d("SpinnerTAG", "::::: " + temp);
                    doSearch(temp);
                }
                spinner_init[1]=1;
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //displaySearchBar(menu);
        displayDropDown(menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action1: {
                ((RedditApplication)getApplication()).setGlobalVariable(Posts);
                Intent intent = new Intent(ListActivity.this, SlideActivity.class);
                startActivity(intent);

                //loadSlides(0);

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }*/

    /*@Override
    protected void onStop()
    {
        for (Pair<AsyncDownloadResource, String[]> adr:ADRARR) {
            adr.first.cancel(true);
        }
        Log.d("ATST", "OnStop Called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        for (Pair<AsyncDownloadResource, String[]> adr:ADRARR) {
            adr.first.cancel(true);
        }
        Log.d("ATST", "OnDestroy Called");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        for (Pair<AsyncDownloadResource, String[]> adr:ADRARR) {
            adr.first.cancel(true);
        }
        Log.d("ATST", "OnPause Called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Pair<AsyncDownloadResource, String[]> adr:ADRARR) {
            adr.first.execute(adr.second);
        }
        Log.d("ATST", "OnResume Called");
    }*/
}


