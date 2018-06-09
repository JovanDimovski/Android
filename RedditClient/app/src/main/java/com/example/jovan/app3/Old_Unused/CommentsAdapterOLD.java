package com.example.jovan.app3.Old_Unused;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jovan.app3.Models.FlatComment;
import com.example.jovan.app3.R;

import java.util.ArrayList;

/**
 * Created by Jovan on 17-Aug-16.
 */
public class CommentsAdapterOLD extends ArrayAdapter<FlatComment> {
    Context context;
    ArrayList<FlatComment> comments;
    ArrayList<Integer> hiddenPositions;
    public CommentsAdapterOLD(Context context, ArrayList<FlatComment> comments, ArrayList<Integer> hiddenPositions) {
        super(context, 0, comments);
        this.context = context;
        this.comments = comments;
        this.hiddenPositions = hiddenPositions;
    }

    private static class ViewHolder {
        TextView body;
        TextView header;
        TextView numReplies;
        RelativeLayout bodyWrap;
        RelativeLayout itemRoot;
        LinearLayout padding;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*Za odstranuvawe na samo kliknatiot item*/
        /*int count =0, realposition = position;

        for(int i = 0;i < hiddenPositions.size();i++){
            count += hiddenPositions.get(i);
            if(count == (position+1))
            {
                realposition = i;
                break;
            }
        }*/
        int count =0, realposition = position;

        for(int i = 0;i < hiddenPositions.size();i++){
            count += hiddenPositions.get(i);
            if(count == (position+1))
            {
                realposition = i;
                break;
            }
        }
        Log.d("CLICKTAG",((Integer)position).toString()+"real position: "+((Integer)realposition).toString());
        FlatComment comment = getItem(realposition);

        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_comment, parent, false);
            viewHolder.body = (TextView) convertView.findViewById(R.id.body);
            viewHolder.header = (TextView) convertView.findViewById(R.id.header);
            viewHolder.numReplies = (TextView) convertView.findViewById(R.id.numReplies);
            //viewHolder.bodyWrap = (RelativeLayout) convertView.findViewById(R.id.bodyWrap);
            viewHolder.itemRoot = (RelativeLayout) convertView.findViewById(R.id.itemRoot);
            viewHolder.padding = (LinearLayout) convertView.findViewById(R.id.padding);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        String header =
                "<font color=#228080 face=sans-serif-bold>"+comment.index+" </font>" +
                "<font color=#228080 face=sans-serif-bold>"+comment.comment.author+" </font>" +
                "<font color=#999999 face=sans-serif-bold>"+comment.comment.score+"points </font>" +
                "<font color=#5080bb face=sans-serif-bold>"+comment.comment.created+" </font>";
        viewHolder.body.setText(comment.comment.body);
        viewHolder.header.setText(Html.fromHtml(header));
        viewHolder.header.setTypeface(null, Typeface.BOLD);

        if (realposition+1 < hiddenPositions.size()) {
            if (hiddenPositions.get(realposition + 1) == 0) {
                String numReplies =
                        "<font color=#ffffffff face=sans-serif-bold> +" + comment.numReplies + " </font>";
                viewHolder.numReplies.setText(Html.fromHtml(numReplies));
                viewHolder.numReplies.setVisibility(View.VISIBLE);
            } else {
                viewHolder.numReplies.setVisibility(View.INVISIBLE);
            }
        }
        else {
            viewHolder.numReplies.setVisibility(View.INVISIBLE);
        }
        //viewHolder.numReplies.setText(String.valueOf(comment.numReplies));

        //int color = Color.parseColor("#ff111111");;
        /*if(comment.level == 0)
            color = Color.parseColor("#ffbb4444");
        else if(comment.level == 1)
            color = Color.parseColor("#ff4466cc");
        else if(comment.level == 2)
            color = Color.parseColor("#ff229977");
        else if(comment.level == 3)
            color = Color.parseColor("#ffddbb44");
        else if(comment.level == 4)
            color = Color.parseColor("#ffbb44aa");
        else if(comment.level == 5)
            color = Color.parseColor("##ffbb6622");*/
        //viewHolder.body.setBackgroundColor(color);
        viewHolder.itemRoot.setPadding(comment.level*4,0,0,0);
        float hue = (float)(((int)(((float)720)/7*comment.level))%360);
        float sat = 1;
        float val = (float)0.7;
        viewHolder.padding.setBackgroundColor(Color.HSVToColor(new float []{hue,sat,val}));
        if(realposition == 0 )
            viewHolder.itemRoot.setPadding(comment.level*4,90,0,0);
        // Return the completed view to render on screen
        return convertView;
    }

    /*@Override
    public FlatComment getItem(int position){
        //int offset = b_flat.get(position);
        //Log.d("CLICKTAG", ((Integer) position).toString()+" : "+b_flat.get(position)+"VRATI ELEMENT BR:"+((Integer)(position + b_flat.get(position))).toString());
        //return comments.get(position + b_flat.get(position));
        return comments.get(position + b_flat.get(position + b_flat.get(position + b_flat.get(position + b_flat.get(position)))));
    }*/
    @Override
    public int getCount() {
        int count = 0;
        for(int i= 0;i<hiddenPositions.size();i++){
            //Log.d("CLICKTAG","::: "+hiddenPositions.get(i));
            count += hiddenPositions.get(i);
        }
        //Log.d("CLICKTAG","ListLegngth"+count);
        return count;
    }

}
