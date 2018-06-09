package com.example.jovan.app3.Old_Unused;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jovan.app3.Models.Post;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.ActivityDelegator;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jovan on 08/08/2016.
 */
public class PostsAdapterOLD extends ArrayAdapter<Post> {
    Context context;
    int position;
    boolean dbit = false;
    ArrayList<Post> posts;

    public PostsAdapterOLD(Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
        this.context = context;
        this.posts = posts;
    }

    private static class ViewHolder {
        TextView title;
        TextView author;
        TextView points;
        TextView other;
        ImageView image;
    }
    private void imageExists(String name)
    {
        File folder = context.getFilesDir();
        File picture = new File(folder,"IMAGE"+name+".jpg");
        if(picture.exists()) {
            dbit=true;
        }
        else
            dbit = false;
    }

    private Bitmap loadImageFromStorage(String name)
    {
        File folder = context.getFilesDir();
        File picture = new File(folder,"IMAGE"+name+".jpg");
        Bitmap b = null;
        if(picture.exists()) {
            b = BitmapFactory.decodeFile(picture.toString());
            //Log.d("PICE", (((Integer) b.getByteCount()).toString()+":::"+((Integer)index).toString()));
        }
        return b;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d("VISIBILITYTAG", String.valueOf(dbit));
        // Get the data item for this position
        Post post = getItem(position);
        //**********************ORIGINAL************************************************//
        /*// Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_post, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView author = (TextView) convertView.findViewById(R.id.author);
        TextView points = (TextView) convertView.findViewById(R.id.points);
        //TextView preview = (TextView) convertView.findViewById(R.id.preview);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        // Populate the data into the template view using the data object
        title.setText(post.title);
        author.setText(post.author);
        points.setText(((Integer)post.points).toString());
        //preview.setText(post.preview);

        new DownloadImageTask((ImageView) image).execute(post.thumbnail);*/
        //*****************************************************************************//

        //****************************SO VIEW HOLDER***********************************//
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_post_lt, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.author = (TextView) convertView.findViewById(R.id.author);
            viewHolder.points = (TextView) convertView.findViewById(R.id.points);
            viewHolder.other = (TextView) convertView.findViewById(R.id.other);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);


            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.image.setImageBitmap(null);
        }
        // Populate the data into the template view using the data object
        String title  = "<font color=#ffffff>"+post.title+"</font>";
        String author =
                "<font color=#228080 face=sans-serif-bold>"+post.author+"</font>" +
                "<font color=#999999> in </font>" +
                "<font color=#228080 face=sans-serif-bold>"+post.subreddit+"</font>";

        String other  = new String();
        if(post.flair != "null")
            other ="<font color=#44ff44>"+post.flair+"</font> * ";
        other  +=
                "<font color=#aaaaaa>"+post.numComments+" comments</font> * " +
                "<font color=#aaaaaa>"+post.domain+"</font> *";
        viewHolder.title.setText(Html.fromHtml(title));//setText((position-1)+"."+post.title);
        viewHolder.author.setText(Html.fromHtml(author));
        viewHolder.other.setText(Html.fromHtml(other));
        viewHolder.points.setText(((Integer)post.points).toString());
        imageExists(posts.get(position).id);
        final float scale = context.getResources().getDisplayMetrics().density;
        if(dbit){
            viewHolder.image.setImageBitmap(loadImageFromStorage(posts.get(position).id));
            viewHolder.image.setVisibility(View.VISIBLE);
            viewHolder.other.setPadding((int) (104*scale+0.5f),0,(int) (9*scale+0.5f),(int) (5*scale+0.5f));
        }
        else {
            viewHolder.image.setVisibility(View.GONE);
            viewHolder.other.setPadding((int) (9*scale+0.5f),0,(int) (9*scale+0.5f),(int) (5*scale+0.5f));
        }

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("CLICKTAG", "CLICKED ON IMAGE: "+position);
                ///////////////////////////////////
                //Intent intent = new Intent(v.getContext(), ImgurGaleryActivity.class/*VideoActivity.class*//*GifActivity.class*//*DisplayImageActivity.class*/);
                //intent.putExtra("url",posts.get(position).url);
                //intent.putExtra("id",posts.get(position).id);
                ////////////////////////
                //v.getContext().startActivity(intent);
                ActivityDelegator.execute(v.getContext(), posts.get(position).url, posts.get(position).id);
            }});
        Log.d("GETVIEW", "GETVIEW");
        //new DownloadImageTask((ImageView) viewHolder.image).execute(post.thumbnail);

        // Return the completed view to render on screen
        return convertView;
    }
}