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

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Jovan on 08/08/2016.
 */
public class PostsCardAdapterOLD extends ArrayAdapter<Post> {
    Context context;
    public PostsCardAdapterOLD(Context context, ArrayList<Post> users) {
        super(context, 0, users);
        this.context = context;
    }

    private static class ViewHolder {
        TextView title;
        TextView author;
        TextView points;
        TextView other;
        ImageView image;
    }
    private Bitmap loadImageFromStorage(Integer index)
    {
        File folder = context.getFilesDir();
        File picture = new File(folder,"IMAGE"+((Integer)index).toString()+".bmp");
        Bitmap b = null;
        if(picture.exists()) {
            b = BitmapFactory.decodeFile(picture.toString());
            //Log.d("PICE", (((Integer) b.getByteCount()).toString()+":::"+((Integer)index).toString()));
        }
        else
        {
            b = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);
        }
        return b;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
            convertView = inflater.inflate(R.layout.item_post_card, parent, false);
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
        viewHolder.image.setImageBitmap(loadImageFromStorage(position));
        Log.d("GETVIEW", "GETVIEW");
        //new DownloadImageTask((ImageView) viewHolder.image).execute(post.thumbnail);

        // Return the completed view to render on screen
        return convertView;
    }
}