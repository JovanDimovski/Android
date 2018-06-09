package com.example.jovan.app3.Old_Unused;

import android.util.Log;

import com.example.jovan.app3.Models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jovan on 23/07/2016.
 */
public class ParseJson {
    public String after;
    public ArrayList<Post> parseJson(String webpage){
        JSONObject data= null;
        ArrayList<Post> posts = new ArrayList();
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
        return posts;
    }
    public String getAfter(){return after;}
}
