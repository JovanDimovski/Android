package com.example.jovan.app3.Old_Unused;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jovan.app3.Activities.SlideActivity;
import com.example.jovan.app3.RedditApplication;
import com.example.jovan.app3.Utilities.FetchData;
import com.example.jovan.app3.Models.Post;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivityOld extends AppCompatActivity {
    private String url, url_next, q_str;
    private long timer1,timer2;
    private ArrayList<Post> Posts;
    private ArrayList<Post> AsyncPosts = null;
    private Thread t;
    private PostsAdapterOLD adapter;
    private PostsCardAdapterOLD cardsAdapter;
    private int spinner_init [] = new int[2];
    private int lThumb;
    private int ind_next = 0;
    private int thread_running = 0;
    private int thread_finished = 0;
    private int iscontentviewset = 0;
    private Toolbar myToolbar;
    //private int asyncdownloadinprogress;

    FetchData redditdata;
    //*-------------------CHECK IF AT END OF LISTVIEW-----------------------*//
    private int preLast;
    //*-------------------CHECK IF AT END OF LISTVIEW-----------------------*//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        insideOnCreate();
    }
    protected void insideOnCreate() {

        lThumb=0;

        url = "https://www.reddit.com/r/gifs/top.json?sort=top&t=all";//"https://www.reddit.com/r/all/top/.json?sort=top&t=day";

        if (AsyncPosts == null)
        {
            Log.d("LOADTAG","FIRST RUN");
            if(ind_next == 0) {
                q_str = "";
                Posts = new ArrayList<Post>();
                //Clear old thumbnails before downloading new
                File folder = this.getFilesDir();
                for(File file: folder.listFiles())
                    if (!file.isDirectory())
                        file.delete();
            }
            getPosts();
            if(iscontentviewset == 0) {
                /*setContentView(R.layout.loading);
                myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
                setSupportActionBar(myToolbar);*/
                setContentView(R.layout.activity_main);
                Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
                setSupportActionBar(myToolbar);

                RelativeLayout loadingParent = (RelativeLayout) findViewById(R.id.loading);
                loadingParent.setVisibility(View.VISIBLE);

                ListView listView = (ListView) findViewById(R.id.lvItems);
                listView.setVisibility(View.INVISIBLE);

                iscontentviewset = 1;
            }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        insideOnCreate();
                    }
                }, 300);

            return;
        }


        Posts.addAll(AsyncPosts);
        Log.d("LOADTAG","Second RUN Posts size: "+Posts.size()+"AsyncPosts size: "+AsyncPosts.size());

        Utilities.getThumbnails(this, AsyncPosts,ind_next);
        AsyncPosts = null;
        int ind_old = ind_next;
        ind_next = Posts.size();
        //za dva pati da se povtori prvite 25 + uste 25
        if(ind_old == 0){
            insideOnCreate();
            return;
        }

        getPostsStart();
        if(lThumb == 0)
            adapter = new PostsAdapterOLD(this, Posts);
        else
            cardsAdapter = new PostsCardAdapterOLD(this, Posts);

        ListView listView = (ListView) findViewById(R.id.lvItems);// Attach the adapter to a ListView
        listView.setVisibility(View.VISIBLE);

        RelativeLayout loadingParent = (RelativeLayout) findViewById(R.id.loading);
        loadingParent.setVisibility(View.GONE);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.item_header, listView, false);
        listView.addHeaderView(header, null, false);


        if(lThumb == 0)
            listView.setAdapter(adapter);
        else
            listView.setAdapter(cardsAdapter);

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
                timer2 = System.currentTimeMillis();
                if(this.currentFirstVisibleItem >= this.totalItem -25){
                    if (flag == 0){
                        int ind_next = Posts.size();
                        Log.d("THREADTEST", "END"+this.currentFirstVisibleItem+":::::"+this.totalItem);
                        ArrayList<Post> Posts_next = getPostsEnd();
                        Utilities.getThumbnails(MainActivityOld.this, Posts_next, ind_next);
                        Posts.addAll(Posts_next);
                        if(lThumb == 0) adapter.notifyDataSetChanged();
                        else cardsAdapter.notifyDataSetChanged();
                        getPostsStart();
                    }
                    flag = 1;
                }
                else{
                    flag =0;
                }

                timer2 = System.currentTimeMillis() - timer2;
                Log.d("TAGSCROLL", timer1+":::::"+timer2);

            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount && this.currentScrollState == SCROLL_STATE_IDLE) {

                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                //TODO ako pricnam na eden sub pa odma na drug od spinerot za odbiranje index out of bounds exception se javuva
                Log.d("CLICKTAG", "ITEM CLICKED: "+position+": :"+Posts.get(position-1).permalink);
                Intent intent = new Intent(MainActivityOld.this, CommentsActivityOLD.class);
                intent.putExtra("permalink",Posts.get(position-1).permalink);
                intent.putExtra("title",Posts.get(position-1).title);
                startActivity(intent);

            }
        });
        //*-------------------CHECK IF AT END OF LISTVIEW-----------------------*//

    }

    /*@Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //displaySearchBar(menu);
        displayDropDown(menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void displaySearchBar(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        ActionBar action = getSupportActionBar();

        action.setDisplayShowCustomEnabled(true); //enable it to display a custom view in the action bar.
        action.setCustomView(R.layout.search_bar);//add the custom view
        action.setDisplayShowTitleEnabled(false); //hide the title

        final EditText editSearch = (EditText)action.getCustomView().findViewById(R.id.editSearch);

        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //*********************HIDE KEYBOARD**************************//
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    doSearch(v.getText());
                    return true;
                }
                return false;
            }
        });
        //editSearch.requestFocus();
    }

    public void displayDropDown(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        ActionBar action = getSupportActionBar();

        action.setDisplayShowCustomEnabled(true); //enable it to display a custom view in the action bar.
        action.setCustomView(R.layout.custom_actionbar);//add the custom view
        action.setDisplayShowTitleEnabled(false); //hide the title

        final Spinner spinner_sub = (Spinner) findViewById(R.id.subreddits);
        final Spinner spinner_sort = (Spinner) findViewById(R.id.sort);

        // Create an ArrayAdapter using the string array and a default spinner layout
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.subreddits_array, android.R.layout.simple_spinner_item);*/
        //so custom item
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList("All","Android","AskReddit","AskScience","Aww","Earthporn","Food","Gifs","Pics","TodayILearned","Videos","WorldNews","Random"));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  R.layout.spinner_item, list);
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

                    if(parts.length > 1)
                        temp = "https://www.reddit.com/r/"+ spinner_sub.getSelectedItem().toString() + "/"+ parts[0]+ "/.json"+ "?sort="+ parts[0]+ "&t="+ parts[1];
                    else
                        temp = "https://www.reddit.com/r/"+ spinner_sub.getSelectedItem().toString() + "/"+ parts[0]+ "/.json"+ "?sort="+ parts[0];
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
                        temp = "https://www.reddit.com/r/" + sub + "/" + parts[0] + "/.json" + "?sort=" + parts[0] + "&t=" + parts[1];
                    else
                        temp = "https://www.reddit.com/r/" + sub + "/" + parts[0] + "/.json" + "?sort=" + parts[0];
                    Log.d("SpinnerTAG", "::::: " + temp);
                    doSearch(temp);
                }
                spinner_init[1]=1;
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
    }

    private void insideDoSearch(){
        //setContentView(R.layout.activity_main);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);

        lThumb=0;

        //url = "https://www.reddit.com/r/pics/top/.json?sort=top&t=all";
        Log.d("INSIDEDOSEARCH", "po vlez vo insidedoSerch ind next"+String.valueOf(ind_next));
        Log.d("INSIDEDOSEARCH","ASYNCPOSTS NOT NULL");
        if (AsyncPosts == null)
        {
            Log.d("INSIDEDOSEARCH","ASYNCPOSTS IS NULL");
            if(ind_next == 0) {
                q_str = "";
                Posts = new ArrayList<Post>();
                Log.d("INSIDEDOSEARCH","RESET POSTS ARRAY");

            }
            getPosts();
            //setContentView(R.layout.loading);
            //myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            //setSupportActionBar(myToolbar);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    insideDoSearch();
                }
            }, 300);
            return;
        }

        Posts.addAll(AsyncPosts);
        Log.d("LOADTAG","Second RUN Posts size: "+Posts.size()+"AsyncPosts size: "+AsyncPosts.size());

        Utilities.getThumbnails(this, AsyncPosts,ind_next);
        AsyncPosts = null;
        int ind_old = ind_next;
        ind_next = Posts.size();
        if(ind_old == 0){
            Log.d("randtag",url.replaceAll("Random",Posts.get(0).subreddit));
            url = url.replaceAll("Random",Posts.get(0).subreddit);
            insideDoSearch();
            return;
        }
        getPostsStart();
        if(lThumb == 0) adapter = new PostsAdapterOLD(this, Posts);
        else cardsAdapter = new PostsCardAdapterOLD(this, Posts);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvItems);
        if(lThumb == 0) listView.setAdapter(adapter);
        else listView.setAdapter(cardsAdapter);
        /*try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        if(lThumb == 0) adapter.notifyDataSetChanged();
        else cardsAdapter.notifyDataSetChanged();
    }

    private void doSearch(CharSequence text) {
        //Log.d("TAGSEARCH", text.toString());
        //Za da se zavrsi posedno getPostStart()//
        Log.d("INSIDEDOSEARCH", "ind next"+String.valueOf(ind_next));
        getPostsEnd();
        url = text.toString();
        AsyncPosts = null;
        ind_next = 0;
        thread_running = 0;
        thread_finished = 0;

        Log.d("INSIDEDOSEARCH", "pred vlez vo insidedoSerch ind next"+String.valueOf(ind_next));
        insideDoSearch();
    }

/*    public void loadSlides(int setcv)
    {
        if(setcv == 0){

*//*            setContentView(R.layout.activity_main);
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);*//*
            RelativeLayout loadingParent = (RelativeLayout) findViewById(R.id.loading);
            loadingParent.setVisibility(View.VISIBLE);

            ListView listView = (ListView) findViewById(R.id.lvItems);
            listView.setVisibility(View.INVISIBLE);

        }
        if(asyncdownloadinprogress == 0)
        {
            //insideDoSearch();
            getImages(Posts, 5, Posts.size());
            ((RedditApplication)getApplication()).setGlobalVariable(Posts);
            Intent intent = new Intent(MainActivityOld.this, ViewPagerActivity.class);
            startActivity(intent);
            ListView listView = (ListView) findViewById(R.id.lvItems);
            listView.setVisibility(View.VISIBLE);
            RelativeLayout loadingParent = (RelativeLayout) findViewById(R.id.loading);
            loadingParent.setVisibility(View.GONE);
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                loadSlides(1);
            }
        }, 300);

    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action1: {
                ((RedditApplication)getApplication()).setGlobalVariable(Posts);
                Intent intent = new Intent(MainActivityOld.this, SlideActivity.class);
                startActivity(intent);

                //loadSlides(0);

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }*/

    public void getPosts() {
        if(thread_running == 0){
            getPostsStart();
            thread_running = 1;
        }
        if(thread_finished == 0){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    getPosts();
                }
            }, 300);
            return;
        }
        AsyncPosts = getPostsEnd();
        thread_running = 0;
        thread_finished = 0;
    }

    public void getPostsStart() {

        redditdata = new FetchData(url + q_str,"Posts", this, "MainActivityOld");
        t = new Thread(redditdata);
        Log.d("TAGURL",url+"::::"+q_str);
        t.start();
    }

    public ArrayList<Post> getPostsEnd() {
        String after = null, result = null;
        timer1 = System.currentTimeMillis();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //timer1 = System.currentTimeMillis() - timer1;
        ArrayList<Post> posts = redditdata.getPosts();
        after =redditdata.getAfter();
        int q_ex = url.indexOf('?');
        if(q_ex == -1) q_str = "?after="+after;
        else q_str = "&after="+after;
        return posts;
    }




    public void asyncTumbnailDownloadFinished(){
        if(lThumb == 0 ) adapter.notifyDataSetChanged();
        else cardsAdapter.notifyDataSetChanged();
        //asyncdownloadinprogress = 0;
    }
    public void redditDataDownloadFinished(){
        thread_finished = 1;
    }
}
