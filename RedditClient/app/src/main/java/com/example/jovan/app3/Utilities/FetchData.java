package com.example.jovan.app3.Utilities;

import android.content.Context;
import android.util.Log;

import com.example.jovan.app3.Old_Unused.CommentsActivityOLD;
import com.example.jovan.app3.Old_Unused.MainActivityOld;
import com.example.jovan.app3.Models.Comment;
import com.example.jovan.app3.Models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jovan on 23/07/2016.
 */

public class FetchData implements Runnable {
    //private volatile String json_result_string;
    private String url, type;
    private ArrayList<Post> posts;
    private ArrayList<Comment> comments;
    public String after;
    public String [] childrenlist;
    public String childrenstring;
    public String link;
    private Context context;
    private String activity_type;

    public FetchData(String url, String type,Context context, String activity_type)
    {
        this.url =url;
        this.type =type;
        this.context = context;
        this.activity_type = activity_type;
    }

    @Override
    public void run() {
        String json_result = getWebPage(url);
        if(type == "Posts") {
            parseJson(json_result);
        }
        else if(type == "Comments"){
            Log.d("COMMENTSTAG","ADSADASD");
            parseCommentsJson(json_result);
        }
        if(this.activity_type == "MainActivityOld")
            ((MainActivityOld)context).redditDataDownloadFinished();
        if(this.activity_type == "CommentsActivityOLD")
            ((CommentsActivityOLD)context).redditDataDownloadFinished();
    }
    public String getWebPage(String address){
        String json_result="";
        try
        {
            HttpURLConnection hcon = null;
            hcon = (HttpURLConnection) new URL(address).openConnection();
            hcon.setReadTimeout(30000); // Timeout at 30 seconds
            hcon.setRequestProperty("User-Agent", "Alien V1.0");

            StringBuffer sb = new StringBuffer(8192);
            String tmp = "";
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            hcon.getInputStream()
                    )
            );
            while ((tmp = br.readLine()) != null) {sb.append(tmp).append("\n");}
            br.close();

            json_result = sb.toString();
        }
        catch (Exception e)
        {
            //value += "EXCEPTION" + e.toString();
        }
        return json_result;
    }



    public void parseJson(String webpage){
        JSONObject data= null;
        posts = new ArrayList();
        try {
            data = new JSONObject(webpage).getJSONObject("data");
            JSONArray children=data.getJSONArray("children");
            //Using this property we can fetch the next set of
            //posts from the same subreddit
            after=data.getString("after");

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
                    posts.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Comment> generateCommentTree(JSONArray children) {
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
                        elapsed = elapsed/7;
                        if(elapsed < 31) {
                            c.created = Long.toString(elapsed) + " weeks";
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

    public void parseCommentsJson(String webpage){
        JSONObject data= null;
        //comments = new ArrayList();
        try
        {
            data = new JSONArray(webpage).getJSONObject(1);
            JSONObject data1 = data.getJSONObject("data");

            after = data1.getString("after");
            JSONArray children = data1.getJSONArray("children");

            Log.d("CJASON", after);
            comments = generateCommentTree(children);

            JSONObject last = children.getJSONObject(children.length()-1).getJSONObject("data");
            link = last.optString("parent_id");
            String childrenoflast = last.optString("children");
            //String cdchildren="";
            /*for (int i = 0; i < childrenoflast.length(); i++){
                cdchildren+=childrenoflast.getJSONObject(i);
            }*/
            childrenlist = childrenoflast.split(",");
            //Log.d("MORECHILDREN", childrenlist[0]);
            if(childrenlist.length > 1) {
                childrenstring = "";

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
    }

    public String getAfter(){return after;}

    public ArrayList<Post> getPosts(){return posts;}
    
    public String getChildComments(){return childrenstring;}

    public ArrayList<Comment> getComments(){return comments;}
};