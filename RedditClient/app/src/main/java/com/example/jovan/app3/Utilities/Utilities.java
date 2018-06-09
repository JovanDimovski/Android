package com.example.jovan.app3.Utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import com.example.jovan.app3.Activities.ImgurGaleryActivity;
import com.example.jovan.app3.AsyncTaskNotifier;
import com.example.jovan.app3.Models.Comment;
import com.example.jovan.app3.Models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jovan on 31-Aug-16.
 */
public final class Utilities {
    public static AsyncDownloadResource ADR;
    private Utilities(){}
    //TODO type_array treba da se popolni za sekoj element posebno so soodveten format jpg za sliki za video mp4 ili webm i za gif gif
    public static void getImages(Context context, ArrayList<Post> Posts, int start, int end, AsyncTaskNotifier ATN) {

        String [] url_array = new String[Posts.size()];
        String [] name_array = new String[Posts.size()];
        String [] type_array = new String[Posts.size()];
        int i = 0, j = 0;
        for (Post post: Posts) {
            if(j >= start && j < end) {
                /*Pattern url_pattern = Pattern.compile("http://i.imgur.com/.*?\\.gifv");
                Matcher url_matcher = url_pattern.matcher(post.url);
                if(url_matcher.find()) {
                    //Od tip gifv
                }
                url_pattern = Pattern.compile("https*://gfycat.com/.*?");
                url_matcher = url_pattern.matcher(post.url);
                if(url_matcher.find()) {
                    //Od tip gfycat
                }
                url_pattern = Pattern.compile("http://i.imgur.com/.*?\\.gif");
                url_matcher = url_pattern.matcher(post.url);
                if(url_matcher.find()) {
                    //Od tip gif
                }
                url_pattern = Pattern.compile("http://imgur.com/[a-zA-Z0-9]*");
                url_matcher = url_pattern.matcher(post.url);
                if(url_matcher.find()) {
                    //treba da se proveri sto e tocno slika gif gifv ????
                    //getwebpage pa da se pobara
                    // <div class="post-images"> pa
                    // <div class="post-image-container"> pa
                    // <div class="post-image" i //na isto nivo so ova ima <class="post-image-meta" vnatre ima caption za slikata
                    // <a class="zoom">
                    // <img
                    // src="//i.imgur.com/LTSxX43.jpg"
                    // alt="Humor me before I have to go"
                    //</a>
                    // vo nego src
                }*/
                url_array[i] = post.url;
                name_array[i] = post.id;

                String type = "DEFAULT";
                //Odredi tip na resurs
                Pattern pattern;
                Matcher matcher;

                pattern = Pattern.compile("https?://(www.)?i.imgur.com/.*?",Pattern.DOTALL);
                matcher  = pattern.matcher(post.url);
                if(matcher.find())
                    type = "IMAGE";

                pattern = Pattern.compile("https?://(www.)?i.redd.it/.*?",Pattern.DOTALL);
                matcher  = pattern.matcher(post.url);
                if(matcher.find())
                    type = "IMAGE";

                pattern = Pattern.compile("https?:.*?[.]gif",Pattern.DOTALL);
                matcher  = pattern.matcher(post.url);
                if(matcher.find())
                    type = "GIF";

                pattern = Pattern.compile("https?:.*?[.]gifv",Pattern.DOTALL);
                matcher  = pattern.matcher(post.url);
                if(matcher.find())
                    type = "VIDEO";

                pattern = Pattern.compile("https?://(www.)?gfycat.com/.*?", Pattern.DOTALL);
                matcher  = pattern.matcher(post.url);
                if(matcher.find())
                    type = "VIDEO";

                if(type.equalsIgnoreCase("IMAGE")) {
                    type_array[i] = "jpg";
                }
                if(type.equalsIgnoreCase("GIF")) {
                    type_array[i] = "gif";
                }
                if(type.equalsIgnoreCase("VIDEO")) {
                    type_array[i] = "mp4";//TODO privremeno moze da bide i dr
                }
                if(type.equalsIgnoreCase("DEFAULT")) {
                    type_array[i] = "";
                    url_array[i] = "";
                }
                i++;
            }
            j++;
        }
        ADR = new AsyncDownloadResource(context, ATN, name_array,type_array);
        ADR.execute(url_array);
    }

    public static void getThumbnails(Context context, ArrayList<Post> Posts,int start) {
        /*if(lThumb == 1) {
            getLargeThumbnails(Posts, start);
            return;
        }*/
        String [] url_array = new String[Posts.size()];
        String [] name_array = new String[Posts.size()];
        int i = 0;
        for (Post post: Posts) {
            url_array[i] = post.thumbnail;
            name_array[i] = post.id;
            i++;
        }
        new DownloadFileFromURL(context, start, "MainActivityOld",name_array).execute(url_array);
    }

    //Prefrleno e od slide fragment mozno e da se javi problem
    public static Bitmap loadImageFromStorage(String filesdir , String name) {
        int reqWidth = 540;
        int reqHeight = 960;
        String filepath = filesdir + "/" + name;

        Log.d("FRAGTAG", "Folder: "+filesdir+" filename: " + name);

        File picture = new File(filepath);
        File cached = new File(filesdir+"/CACHE"+name);          // the File to save to
        if(cached.exists() && !cached.isDirectory()) {
            Log.d("FRAGTAG","Cached version already loaded"+cached.toString());
            return BitmapFactory.decodeFile(cached.toString());
        }
        if(picture.exists() && !picture.isDirectory()) {
            Log.d("FRAGTAG","Picture already loaded");
        }
        else
        {
            Log.d("FRAGTAG","Picture does not exist");
            Bitmap b = null;
            return b;
        }
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picture.toString(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap b = BitmapFactory.decodeFile(picture.toString(), options);
        OutputStream fOut = null;

        try {
            fOut = new FileOutputStream(cached);
            b.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush();
            fOut.close(); // do not forget to close the stream
        }
        catch (Exception e)
        {

        }
        return b;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Pair<String, ArrayList<Post>> postsFromJSON(String webPage){
        JSONObject data= null;
        ArrayList <Post> temp = new ArrayList();
        String after = null;
        try {
            data = new JSONObject(webPage).getJSONObject("data");
            JSONArray children=data.getJSONArray("children");

            after = data.getString("after");

            Log.d("tagindex", ((Integer)children.length()).toString());
            for(int i=0;i<children.length();i++){
                Log.d("tagindex", ((Integer)children.length()).toString());
                JSONObject cur=children.getJSONObject(i)
                        .getJSONObject("data");
                Post p=new Post();
                p.title=cur.optString("title");
                p.url=cur.optString("url");
                p.numComments=cur.optInt("num_comments");
                p.points=cur.optInt("score");
                p.author=cur.optString("author");
                p.subreddit=cur.optString("subreddit");
                p.permalink=cur.optString("permalink");
                p.domain=cur.optString("domain");
                p.flair=cur.optString("link_flair_text");
                p.id=cur.optString("id");
                p.over18 = cur.optBoolean("over_18");
                p.gilded = cur.optInt("gilded");
                p.locked = cur.optBoolean("locked");
                p.name = cur.optString("name");
                try {
                    JSONObject source = cur.getJSONObject("preview");
                    JSONArray images = source.getJSONArray("images");
                    ArrayList<String> list = new ArrayList<String>();
                    for (int j = 0; j < images.length(); j++) {
                        list.add(images.getJSONObject(j).getJSONObject("source").optString("url"));
                    }
                    //Log.d("myURL", images.getJSONObject(0).getString("url"));

                    p.source = list.get(0);
                    Log.d("SOURCETAG", p.source);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                p.thumbnail = cur.optString("thumbnail");

                if(p.title!=null)
                    temp.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Pair<String, ArrayList<Post>>(after, temp);
        //return temp;
    }

    public static ArrayList<Comment> commentsFromJson(String webpage){
        JSONObject data= null;
        ArrayList<Comment> comments = new ArrayList<Comment>();
        //comments = new ArrayList();
        try
        {
            data = new JSONArray(webpage).getJSONObject(1);
            JSONObject data1 = data.getJSONObject("data");

            String after = data1.getString("after");
            JSONArray children = data1.getJSONArray("children");

            Log.d("CJASON", after);
            comments = generateCommentTree(children);

            JSONObject last = children.getJSONObject(children.length()-1).getJSONObject("data");
            String link = last.optString("parent_id");
            String childrenoflast = last.optString("children");
            //String cdchildren="";
            /*for (int i = 0; i < childrenoflast.length(); i++){
                cdchildren+=childrenoflast.getJSONObject(i);
            }*/
            String [] childrenlist = childrenoflast.split(",");
            //Log.d("MORECHILDREN", childrenlist[0]);
            if(childrenlist.length > 1) {
                String childrenstring = "";

                childrenlist[0] = childrenlist[0].substring(1, childrenlist[0].length() - 1);
                childrenlist[childrenlist.length - 1] = childrenlist[childrenlist.length - 1].substring(0, childrenlist[childrenlist.length - 1].length() - 2);
                Log.d("MORECHILDREN", String.valueOf(childrenlist.length));
                int num_children = childrenlist.length/100+1;
                String [] children_page = new String[num_children];
                for (int i = 0; i < childrenlist.length; i++) {
                    childrenlist[i] = childrenlist[i].replaceAll("\"", "");}
                for (int i = 0; i < num_children; i++)
                {
                    children_page[i] = "";
                }
                for (int i = 0; i < childrenlist.length; i++) {
                    children_page[i/100] += childrenlist[i];
                    if (i + 1 < childrenlist.length)
                        if(i%100 != 99)
                            children_page[i/100] += ",";
                }
                for (int i = 0; i < num_children; i++) {
                    Log.d("CHILDRENPAGE: ", children_page[i]);
                    String address = "https://www.reddit.com/api/morechildren?link_id="+link+"&children="+children_page[i];
                    //String json_result = getWebPage(address);
                    //Log.d("CHILDRENPAGE: ", json_result);
                }
                //childrenstring.substring(0, childrenstring.length() - 2);
                //childrenlist[childrenlist.length-1] = childrenlist[childrenlist.length-1].replaceAll("\\]", "");
                Log.d("MORECHILDREN", link + "->->->" + childrenstring);
            }
        }catch (JSONException e) {
            e.printStackTrace();
            Log.d("CJASON", e.toString());
        }
        return comments;
    }

    public static Post submissionFromJson(String webpage){
        JSONObject data= null;
        Post p=new Post();
        try
        {
            JSONArray array = new JSONArray(webpage);
            data = array.getJSONObject(0);
            JSONObject data1 = data.getJSONObject("data");
            JSONArray children = data1.getJSONArray("children");
            JSONObject cur = children.getJSONObject(0).getJSONObject("data");

            p.title=cur.optString("title");
            p.selfText=cur.optString("selftext");
            p.url=cur.optString("url");
            p.numComments=cur.optInt("num_comments");
            p.points=cur.optInt("score");
            p.author=cur.optString("author");
            p.subreddit=cur.optString("subreddit");
            p.permalink=cur.optString("permalink");
            p.domain=cur.optString("domain");
            p.flair=cur.optString("link_flair_text");
            p.id=cur.optString("id");
            p.over18 = cur.optBoolean("over_18");
            p.gilded = cur.optInt("gilded");
            p.locked = cur.optBoolean("locked");
            p.name = cur.optString("name");
            try {
                JSONObject source = cur.getJSONObject("preview");
                JSONArray images = source.getJSONArray("images");
                ArrayList<String> list = new ArrayList<String>();
                for (int j = 0; j < images.length(); j++) {
                    list.add(images.getJSONObject(j).getJSONObject("source").optString("url"));
                }
                //Log.d("myURL", images.getJSONObject(0).getString("url"));

                p.source = list.get(0);
                Log.d("SOURCETAG", p.source);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            p.thumbnail = cur.optString("thumbnail");
        }catch (JSONException e) {
            e.printStackTrace();
            Log.d("CJASON", e.toString());
        }
        return p;
    }

    private static ArrayList<Comment> generateCommentTree(JSONArray children) {
        ArrayList<Comment> comments2 = new ArrayList<Comment>();
        long timestamp = System.currentTimeMillis()/1000;
        Log.d("TESTTAG", Long.toString(timestamp));
        try
        {
            for (int i = 0; i < children.length(); i++) {
                Log.d("tagindex", ((Integer) children.length()).toString());
                JSONObject cur = children.getJSONObject(i).getJSONObject("data");
                Comment c = new Comment();
                c.body = cur.optString("body");


                c.body = c.body.replaceAll("([^\\(]|^)(https?:.*?)([\\s]|$)","<a href=\\\"$2\\\">$2</a>");//ne raboti

                c.body = c.body.replaceAll("\\[(.*?)\\]\\((https?:.*?)\\)","<a href=\\\"$2\\\">$1</a>");




                long created = cur.getLong("created_utc");
                long elapsed = (timestamp - created)/60;
                if(elapsed < 60) {
                    c.created = Long.toString(elapsed) + " minutes";
                }
                else{
                    elapsed = elapsed/60;
                    if(elapsed == 1) {
                        c.created = Long.toString(elapsed) + " hour";
                    }
                    else if(elapsed < 24) {
                        c.created = Long.toString(elapsed) + " hours";
                    }
                    else{
                        elapsed = elapsed/24;
                        if(elapsed < 7) {
                            c.created = Long.toString(elapsed) + " days";
                        }
                        else{
                            if(elapsed < 31) {
                                c.created = Long.toString((int)(elapsed/7)) + " weeks";
                            }
                            else{
                                elapsed = elapsed/31;
                                if(elapsed < 12) {
                                    c.created = Long.toString(elapsed) + " months";
                                }
                                else{
                                    elapsed = elapsed/12;
                                    c.created = Long.toString(elapsed) + " years";
                                }
                            }
                        }
                    }
                }
                c.author = cur.optString("author");
                c.score = cur.optString("score");
                c.gilded = cur.optString("gilded");
                c.hasReplies = false;
                if(cur.has("replies")) {
                    Object aObj = cur.get("replies");
                    if (aObj instanceof JSONObject)
                    {
                        JSONObject replies = cur.getJSONObject("replies");
                        JSONObject data2 = replies.getJSONObject("data");
                        JSONArray children2 = data2.getJSONArray("children");
                        //ArrayList<Comment> coments2;
                        if (children2.length() > 0) {
                            c.replies = generateCommentTree(children2);
                            c.hasReplies = true;
                        } else
                            c.hasReplies = false;
                    }
                    else
                        c.hasReplies = false;
                }
                comments2.add(c);
            }
        }catch (JSONException e) {
            e.printStackTrace();
            Log.d("CJASON", e.toString());
        }
        return comments2;
    }

}
