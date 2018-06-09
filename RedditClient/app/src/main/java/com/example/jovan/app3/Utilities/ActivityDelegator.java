package com.example.jovan.app3.Utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.jovan.app3.Activities.DisplayImageActivity;
import com.example.jovan.app3.Activities.GifActivity;
import com.example.jovan.app3.Activities.ImageActivity;
import com.example.jovan.app3.Activities.ImgurGaleryActivity;
import com.example.jovan.app3.Activities.TextureActivity;
import com.example.jovan.app3.Activities.VideoActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jovan on 05-Sep-16.
 */
public final  class ActivityDelegator {
    private ActivityDelegator(){}
    public static void execute(Context context, String url, String id){
        String type = "DEFAULT";

        //Odredi tip na resurs

        Pattern pattern = Pattern.compile("https?://(www.)?(m\\.)?imgur.com/[\\.]*?",Pattern.DOTALL);
        Matcher matcher  = pattern.matcher(url);
        if(matcher.find())
            type = "IMGURGALERY";

        pattern = Pattern.compile("https?://(www.)?i.imgur.com/.*?",Pattern.DOTALL);
        matcher  = pattern.matcher(url);
        if(matcher.find())
            type = "IMAGE";

        pattern = Pattern.compile("https?://(www.)?i.redd.it/.*?",Pattern.DOTALL);
        matcher  = pattern.matcher(url);
        if(matcher.find())
            type = "IMAGE";

        /*pattern = Pattern.compile("https?://(www.)?i.reddituploads.com/.*?",Pattern.DOTALL);
        matcher  = pattern.matcher(url);
        if(matcher.find())
            type = "IMAGE";*///ne Funkcionira ne e od toj tip

        pattern = Pattern.compile("https?:.*?[.]gif",Pattern.DOTALL);
        matcher  = pattern.matcher(url);
        if(matcher.find())
            type = "GIF";

        pattern = Pattern.compile("https?:.*?[.]gifv",Pattern.DOTALL);
        matcher  = pattern.matcher(url);
        if(matcher.find())
            type = "VIDEO";

        pattern = Pattern.compile("https?://(www.)?gfycat.com/.*?", Pattern.DOTALL);
        matcher  = pattern.matcher(url);
        if(matcher.find())
            type = "VIDEO";

        pattern = Pattern.compile("https?://(www.)?youtube.com/.*?", Pattern.DOTALL);
        matcher  = pattern.matcher(url);
        if(matcher.find())
            type = "YOUTUBE";

        pattern = Pattern.compile("https?://(www.)?youtu.be/.*?", Pattern.DOTALL);
        matcher  = pattern.matcher(url);
        if(matcher.find())
            type = "YOUTUBE";


        Log.d("ERRORTAG", "FINALMATCH IS " + type + "URL " + url);
        //Od tip na resurs povikaj soodvetna klasa

        Intent intent = null;
        if(type.equalsIgnoreCase("IMGURGALERY"))
            intent = new Intent(context, ImgurGaleryActivity.class);

        if(type.equalsIgnoreCase("IMAGE"))
            intent = new Intent(context, /*DisplayImageActivity*/ImageActivity.class);

        if(type.equalsIgnoreCase("GIF"))
            intent = new Intent(context, GifActivity.class);

        if(type.equalsIgnoreCase("VIDEO"))
            intent = new Intent(context, TextureActivity.class);

        if(type.equalsIgnoreCase("YOUTUBE"))
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        if(type.equalsIgnoreCase("DEFAULT"))
            //intent = new Intent(context, VideoActivity.class);
            //Log.d("ERRORTAG", "DEFAULT ne e poznat tip na resurs");
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        intent.putExtra("url",url);
        intent.putExtra("id",id);
        context.startActivity(intent);
    };
}
/*


    Intent intent = new Intent(context, ImgurGaleryActivity.class*/
/*VideoActivity.class*//*
*/
/*GifActivity.class*//*
*/
/*DisplayImageActivity.class*//*
);
intent.putExtra("url",url);
        intent.putExtra("id",id);*/
