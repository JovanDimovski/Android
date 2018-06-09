package com.example.jovan.app3.Adapters;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jovan.app3.Activities.CommentsActivity2;
import com.example.jovan.app3.Activities.ListActivity;
import com.example.jovan.app3.Animations.CustomColapseAnimation;
import com.example.jovan.app3.Models.Post;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.ActionBarCallBack;
import com.example.jovan.app3.Utilities.ActivityDelegator;
import com.example.jovan.app3.Utilities.ResizeAnimation;

import java.io.File;
import java.io.LineNumberReader;
import java.util.ArrayList;

/**
 * Created by Jovan on 06-Sep-16.
 */

public class PostsAdapter2 extends ArrayAdapter<Post> {
    Context context;
    int position;
    boolean dbit = false;
    ArrayList<Post> posts;
    ArrayList<Boolean> imgT;
    int long_pressed_position = -1;

    private ActionMode mActionMode;

    public boolean isClick;
    public int _yDelta;
    public boolean animationStarted;
    public int oldLayoutHeight;
    public long oldTime;
    public long timeDif;
    public float speed = 0;

    public PostsAdapter2(Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
        this.context = context;
        this.posts = posts;
        this.imgT = new ArrayList<Boolean>();
        for(int i = 0;i < posts.size();i++)
            imgT.add(true);
    }

    private static class ViewHolder {
        TextView title;
        TextView author;
        TextView points;
        TextView other;
        TextView star_count;
        ImageView image;
        ImageView star;
        RelativeLayout postholder;
        RelativeLayout footer;
    }
    private void imageExists(String name)
    {
        File folder = context.getFilesDir();
        File picture = new File(folder,name+"thumb.jpg");
        if(picture.exists()) {
            dbit=true;
        }
        else
            dbit = false;
    }

    private Bitmap loadImageFromStorage(String name)
    {
        File folder = context.getFilesDir();
        File picture = new File(folder,name+"thumb.jpg");
        Bitmap b = null;
        if(picture.exists()) {
            b = BitmapFactory.decodeFile(picture.toString());
        }
        return b;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Log.d("VISIBILITYTAG", String.valueOf(dbit));
        final Post post = getItem(position);
        while(imgT.size()<=position)
        {
            imgT.add(true);
        }
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_post_lt, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.author = (TextView) convertView.findViewById(R.id.author);
            viewHolder.points = (TextView) convertView.findViewById(R.id.points);
            viewHolder.other = (TextView) convertView.findViewById(R.id.other);
            viewHolder.star_count = (TextView) convertView.findViewById(R.id.star_count);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.star = (ImageView) convertView.findViewById(R.id.star);
            viewHolder.postholder = (RelativeLayout) convertView.findViewById(R.id.postholder);
            viewHolder.footer = (RelativeLayout) convertView.findViewById(R.id.footer);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.image.setImageBitmap(null);
        }
        // Populate the data into the template view using the data object
        String title  = "<font color=#ffffff>"+post.title+"</font>";
        String author = "<font color=#77c0c0 face=sans-serif-bold>"+post.author+"</font>" +
                        "<font color=#bbbbbb> in </font>" +
                        "<font color=#77c0c0 face=sans-serif-bold>"+post.subreddit+"</font>";

        String other  = new String();
        if(post.over18)
            other +="<font color=#ff0000>NSFW </font> * ";
        if(post.locked)
            other +="<font color=#ff0000>LOCKED </font> * ";
        if(post.flair != "null")
            other +="<font color=#77c0c0>"+post.flair+"</font> * ";
        other  += "<font color=#888888>"+post.numComments+" comments</font> * " +
                "<font color=#888888>"+post.domain+"</font>";
        viewHolder.title.setText(Html.fromHtml(title));//setText((position-1)+"."+post.title);
        viewHolder.author.setText(Html.fromHtml(author));
        viewHolder.other.setText(Html.fromHtml(other));
        if(post.gilded > 1) {
            viewHolder.star_count.setText(String.valueOf(post.gilded));
            viewHolder.star.setVisibility(View.VISIBLE);
            viewHolder.star.setImageResource(R.drawable.gold_star32);
        }
        else
        {
            viewHolder.star_count.setText("");
            viewHolder.star.setVisibility(View.GONE);
        }
        viewHolder.points.setText(((Integer)post.points).toString());
        imageExists(posts.get(position).id);
        final float scale = context.getResources().getDisplayMetrics().density;
        /*if(imgT.get(position)) {
            if(position < 7)
                viewHolder.postholder.animate().setDuration(position*50);
            else
                viewHolder.postholder.animate().setDuration(250);
            viewHolder.postholder.setTranslationY(200);
            viewHolder.postholder.setTranslationX(-50);
            viewHolder.postholder.animate().translationY(0);
            viewHolder.postholder.animate().translationX(0);
            viewHolder.postholder.setScaleX(0.8f);
            viewHolder.postholder.animate().scaleX(1);
            viewHolder.postholder.setScaleY(0.8f);
            viewHolder.postholder.animate().scaleY(1);

        }*/

        if(dbit){
            Resources r = context.getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 95, r.getDisplayMetrics());
            viewHolder.image.getLayoutParams().width = (int)px;

            viewHolder.image.setImageBitmap(loadImageFromStorage(posts.get(position).id));
            if(imgT.get(position)) {
/*                viewHolder.postholder.setTranslationY(100);
                //viewHolder.postholder.setTranslationX(-40);
                viewHolder.postholder.animate().translationY(0);
                viewHolder.postholder.setScaleX(0.8f);
                viewHolder.postholder.animate().scaleX(1);
                viewHolder.postholder.setScaleY(0.8f);
                viewHolder.postholder.animate().scaleY(1);
                //viewHolder.postholder.animate().translationX(0);*/
                viewHolder.image.setVisibility(View.INVISIBLE);
                Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
                //myFadeInAnimation.setDuration(2000);
                viewHolder.image.startAnimation(myFadeInAnimation); //Set animation to your ImageView
                myFadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        // let make your image visible
                        viewHolder.image.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                });
            }
            else {
                viewHolder.image.setVisibility(View.VISIBLE);
            }
            //viewHolder.other.setPadding((int) (104*scale+0.5f),0,(int) (9*scale+0.5f),(int) (5*scale+0.5f));
        }
        else {
            //viewHolder.other.setPadding((int) (9*scale+0.5f),0,(int) (9*scale+0.5f),(int) (5*scale+0.5f));
            viewHolder.image.getLayoutParams().width = 0;
        }
        if(post.thumbnail.length() > 5 || post.over18 == true) {
            Resources r = context.getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 95, r.getDisplayMetrics());
            viewHolder.image.getLayoutParams().width = (int)px;
        }
        else
        {
            viewHolder.image.getLayoutParams().width = 0;
        }
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("CLICKTAG", "CLICKED ON IMAGE: "+position);
                ActivityDelegator.execute(v.getContext(), posts.get(position).url, posts.get(position).id);
            }});
        Log.d("GETVIEW", "GETVIEW");

        viewHolder.postholder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(Main.this, "Long click!", Toast.LENGTH_SHORT).show();
                Log.d("LONGCLICK","CLICK pos: " + position);
                Intent intent = new Intent(context, CommentsActivity2.class);
                intent.putExtra("permalink",post.permalink);
                intent.putExtra("title",post.title);
                context.startActivity(intent);
            }

        });

        viewHolder.postholder.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                /*//Toast.makeText(Main.this, "Long click!", Toast.LENGTH_SHORT).show();
                Log.d("LONGCLICK", "LONG pos: " + position);
                if (viewHolder.footer.getChildAt(0) == null) {
                    long_pressed_position = position;
                    //addFooter(viewHolder.footer, post);
                    //viewHolder.footer.addView(txt1);
                }
                else{
                    long_pressed_position = -1;
                    //viewHolder.footer.removeViewAt(0);
                }
                *//*for(int i = 0;i< posts.size();i++)
                    if(parent.getChildAt(i) != null)
                        Log.d("LONGCLICK","Visible position: "+i);*/


                //mActionMode = ((ListActivity)context).startSupportActionMode(new ActionBarCallBack(context, post));

                final RelativeLayout contextualActionBar = (RelativeLayout)((ListActivity)context).findViewById(R.id.contextual_toolbar);
                int visibility = contextualActionBar.getVisibility();
                if(visibility == View.VISIBLE) {
                    contextualActionBar.setVisibility(View.INVISIBLE);
                }
                else {
                    ((TextView)((ListActivity)context).findViewById(R.id.subreddit)).setText(post.subreddit);
                    //contextualActionBar.invalidate();
                    /*((RelativeLayout)((ListActivity)context).findViewById(R.id.subreddit_button)).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(final View v) {
                            //Toast.makeText(Main.this, "Long click!", Toast.LENGTH_SHORT).show();
                            //Log.d("LONGCLICK","YOU CLICKED ON THE FIRST BUTTON ");
                            //v.setBackgroundColor(Color.CYAN);
                            int colorFrom = Color.argb(127,255,255,255);
                            int colorTo = Color.argb(0,255,255,255);
                            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                            colorAnimation.setDuration(250); // milliseconds
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    v.setBackgroundColor((int) animator.getAnimatedValue());
                                }

                            });
                            colorAnimation.start();

                        }

                    });*/
                    ((RelativeLayout)((ListActivity)context).findViewById(R.id.subreddit_button)).setOnTouchListener(new OnCABButtonTouch());


                    ((TextView)((ListActivity)context).findViewById(R.id.user)).setText(post.author);
                    ((RelativeLayout)((ListActivity)context).findViewById(R.id.user_button)).setOnTouchListener(new View.OnTouchListener() {

                        RelativeLayout cab = (RelativeLayout) ((ListActivity)context).findViewById(R.id.contextual_toolbar);
                        @Override
                        public boolean onTouch(View view, MotionEvent event) {


                            //final int X = (int) event.getRawX();
                            final int Y = (int) event.getRawY();
                            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                                case MotionEvent.ACTION_DOWN:
                                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) cab.getLayoutParams();
                        /*_xDelta = X - lParams.leftMargin;*/
                                    _yDelta = Y - lParams.height/*.topMargin*/;
                                    isClick = true;
                                    break;
                                case MotionEvent.ACTION_UP:
                                    if(isClick)
                                    {
                                        Log.d("CABT","Button This was CLICK");
                                        onClick(view);
                                    }
                                    else
                                        Log.d("CABT","Button This was DRAG");
                                    break;
                                case MotionEvent.ACTION_POINTER_DOWN:
                                    break;
                                case MotionEvent.ACTION_POINTER_UP:
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cab.getLayoutParams();
                                    int oldLayoutHeight = layoutParams.height;
                        /*layoutParams.leftMargin = X - _xDelta;*/
                                    if(Y - _yDelta < 100)
                                        layoutParams.height/*.topMargin*/ = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, context.getResources().getDisplayMetrics());
                                    else if(Y - _yDelta > 500)
                                        layoutParams.height/*.topMargin*/ = 500;
                                    else
                                        layoutParams.height/*.topMargin*/ = Y - _yDelta;
                                    if(oldLayoutHeight - layoutParams.height < -4 || oldLayoutHeight - layoutParams.height > 4)
                                        isClick = false;
                                    Log.d("CABT", "Button "+String.valueOf(Y - _yDelta));
                                    Log.d("CABT", "Button "+String.valueOf(_yDelta));
                        /*layoutParams.rightMargin = -250;*/
                                    //layoutParams.bottomMargin = -250;
                                    cab.setLayoutParams(layoutParams);
                                    break;
                            }
                            ((ListActivity)context).findViewById(R.id.lvParent).invalidate();
                            /*if(isClick) {
                                return true;
                            }
                            else*///koj se spravuva //treba tuka ne vo roditel zatoa samo return true;
                                return true;
                        }

                        /*@Override*/
                        public void onClick(final View v) {
                            //Toast.makeText(Main.this, "Long click!", Toast.LENGTH_SHORT).show();
                            //Log.d("LONGCLICK","YOU CLICKED ON THE FIRST BUTTON ");
                            //v.setBackgroundColor(Color.CYAN);
                            int colorFrom = Color.argb(127,255,255,255);
                            int colorTo = Color.argb(0,255,255,255);
                            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                            colorAnimation.setDuration(250); // milliseconds
                            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                                @Override
                                public void onAnimationUpdate(ValueAnimator animator) {
                                    v.setBackgroundColor((int) animator.getAnimatedValue());
                                }

                            });
                            colorAnimation.start();

                        }

                    });

                    contextualActionBar.setVisibility(View.VISIBLE);
                }
                //final Toolbar t = (Toolbar)((ListActivity)context).findViewById(R.id.back_button_container_toolbar);
                RelativeLayout BackButton = (RelativeLayout) ((ListActivity)context).findViewById(R.id.back_button_container);
                //BackButton.setBackgroundResource(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

                BackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        int colorFrom = Color.argb(127,255,255,255);
                        int colorTo = Color.argb(0,255,255,255);
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(250); // milliseconds
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                v.setBackgroundColor((int) animator.getAnimatedValue());
                            }

                        });
                        colorAnimation.start();
                        colorAnimation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {}
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                contextualActionBar.setVisibility(View.INVISIBLE);

                            }
                            @Override
                            public void onAnimationCancel(Animator animation) {}
                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        });

                    }
                });
                /*t.setNavigationIcon(ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
                t.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        int colorFrom = Color.argb(127,255,255,255);
                        int colorTo = Color.argb(0,255,255,255);
                        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                        colorAnimation.setDuration(250); // milliseconds
                        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animator) {
                                v.setBackgroundColor((int) animator.getAnimatedValue());
                            }

                        });
                        colorAnimation.start();
                        colorAnimation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {}
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                contextualActionBar.setVisibility(View.INVISIBLE);

                            }
                            @Override
                            public void onAnimationCancel(Animator animation) {}
                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        });

                    }
                });*/
                notifyDataSetChanged();
                return true;
            }

        });
        /*if(long_pressed_position == position) {

            addFooter(viewHolder.footer, post);
            viewHolder.postholder.setBackgroundColor(Color.rgb(27,90,90));
            //viewHolder.footer.setScaleY(0);
            *//*viewHolder.footer.setElevation(-10);
            viewHolder.footer.setTranslationZ(0);*//*
            viewHolder.footer.invalidate();
            CustomColapseAnimation a = new CustomColapseAnimation(viewHolder.footer, 150, CustomColapseAnimation.EXPAND);
            Resources r = context.getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, r.getDisplayMetrics());
            a.setHeight((int)px);
            viewHolder.footer.startAnimation(a);

            //viewHolder.footer.animate().scaleY(1);

            *//*for(int i = 0;i< posts.size();i++)
                if(parent.getChildAt(i) != null)
                    Log.d("LONGCLICK","Visible position: "+i);*//*
            //Log.d("LONGCLICK",)
        }
        else {
            if (viewHolder.footer.getChildAt(0) != null) {
                viewHolder.footer.removeViewAt(0);
                viewHolder.postholder.setBackgroundColor(Color.rgb(0,0,0));
                CustomColapseAnimation a = new CustomColapseAnimation(viewHolder.footer, 150, CustomColapseAnimation.COLLAPSE);
                Resources r = context.getResources();
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, r.getDisplayMetrics());
                a.setHeight((int)px);
                viewHolder.footer.startAnimation(a);
                //viewHolder.footer.invalidate();
            }
        }*/

        if(dbit)
            imgT.set(position,false);
        else
            imgT.set(position,true);

        return convertView;
    }

    private ViewGroup mLinearLayout;

    //@Override
    protected void addFooter(ViewGroup view, final Post post) {
        /*super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);*/
        //mLinearLayout = (ViewGroup) findViewById(R.id.linear_layout);
        //addLayout("This is text 1", "This is first button", "This is second Button");
/*    }

    private void addLayout(String textViewText, String buttonText1, String buttonText2) {*/
        View footer = LayoutInflater.from(context).inflate(R.layout.post_footer, view, false);

        RelativeLayout button1 = (RelativeLayout) footer.findViewById(R.id.user);
        RelativeLayout button2 = (RelativeLayout) footer.findViewById(R.id.subreddit);
        //Button button3 = (Button) footer.findViewById(R.id.button3);

        ((TextView)button1.findViewById(R.id.userName)).setText("/u/"+post.author);
        ((TextView)button2.findViewById(R.id.subredditName)).setText("/r/"+post.subreddit);
        //button3.setText("TEMP");

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(Main.this, "Long click!", Toast.LENGTH_SHORT).show();
                Log.d("LONGCLICK","YOU CLICKED ON THE FIRST BUTTON ");
            }

        });
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(Main.this, "Long click!", Toast.LENGTH_SHORT).show();
                Log.d("LONGCLICK","YOU CLICKED ON THE SECOND BUTTON ");
                //((ListActivity)context).doSearch("https://www.reddit.com/r/"+post.subreddit);
            }

        });

        view.addView(footer);
    }
    private class OnCABButtonTouch implements View.OnTouchListener{

            RelativeLayout cab = (RelativeLayout) ((ListActivity)context).findViewById(R.id.contextual_toolbar);
            @Override
            public boolean onTouch(View view, MotionEvent event) {


                //final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) cab.getLayoutParams();
                        /*_xDelta = X - lParams.leftMargin;*/
                        _yDelta = Y - lParams.height/*.topMargin*/;
                        isClick = true;
                        //animationStarted = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isClick)
                        {
                            Log.d("CABT","Button This was CLICK");
                            onClick(view);
                        }
                        else
                            Log.d("CABT","Button This was DRAG");
                        /*if(animationStarted == false){
                            animationStarted = true;*/
                        ResizeAnimation resizeAnimation = new ResizeAnimation(cab, 500, cab.getLayoutParams().height);
                        resizeAnimation.setDuration(500);
                        cab.startAnimation(resizeAnimation);
                        //}
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cab.getLayoutParams();
                        oldLayoutHeight = layoutParams.height;
                        int newLayoutHeight;
                        /*layoutParams.leftMargin = X - _xDelta;*/
                        if(Y - _yDelta < 100)
                            newLayoutHeight = layoutParams.height/*.topMargin*/ = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, context.getResources().getDisplayMetrics());
                        else if(Y - _yDelta > 500)
                            newLayoutHeight = layoutParams.height/*.topMargin*/ = 500;
                        else{
                            newLayoutHeight = layoutParams.height/*.topMargin*/ = Y - _yDelta;
                        }
                        int heightDif = newLayoutHeight -oldLayoutHeight;
                        timeDif = System.currentTimeMillis() - oldTime;
                        oldTime = System.currentTimeMillis();
                        if(((float)heightDif)/((float)timeDif) != 0)
                            speed = ((float)heightDif)/((float)timeDif);//0.05 - 1.0 mozi i pojke i pomalce
                        Log.d("CABT", "Speed "+String.valueOf(speed));
                        if(oldLayoutHeight - layoutParams.height < -4 || oldLayoutHeight - layoutParams.height > 4)
                            isClick = false;
                        Log.d("CABT", "Button "+String.valueOf(Y - _yDelta));
                        Log.d("CABT", "Button "+String.valueOf(_yDelta));
                        /*layoutParams.rightMargin = -250;*/
                        //layoutParams.bottomMargin = -250;
                        cab.setLayoutParams(layoutParams);
                        break;
                }
                ((ListActivity)context).findViewById(R.id.lvParent).invalidate();
                            /*if(isClick) {
                                return true;
                            }
                            else*///koj se spravuva //treba tuka ne vo roditel zatoa samo return true;
                return true;
            }

                        /*@Override*/
        public void onClick(final View v) {
            //Toast.makeText(Main.this, "Long click!", Toast.LENGTH_SHORT).show();
            //Log.d("LONGCLICK","YOU CLICKED ON THE FIRST BUTTON ");
            //v.setBackgroundColor(Color.CYAN);
            int colorFrom = Color.argb(127,255,0,0);
            int colorTo = Color.argb(0,255,255,255);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(250); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    v.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();

        }
    }

}