package com.example.jovan.app3.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jovan.app3.Models.Comment;
import com.example.jovan.app3.Models.FlatComment;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.ActivityDelegator;

import java.util.ArrayList;

/**
 * Created by Jovan on 10-Sep-16.
 */
public class CommentsAdapter2 extends ArrayAdapter<Comment>{

    Context context;
    ArrayList<Comment> Comments;
    int numComments;

    public CommentsAdapter2(Context context, ArrayList<Comment> Comments, int numComments) {
        super(context,0, Comments);
        this.context = context;
        this.Comments = Comments;
        this.numComments = numComments;
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
        final Comment comment = getCommentFromPosition(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_comment, parent, false);
            viewHolder.body = (TextView) convertView.findViewById(R.id.body);
            viewHolder.header = (TextView) convertView.findViewById(R.id.header);
            viewHolder.numReplies = (TextView) convertView.findViewById(R.id.numReplies);
            viewHolder.itemRoot = (RelativeLayout)  convertView.findViewById(R.id.itemRoot);
            viewHolder.padding = (LinearLayout) convertView.findViewById(R.id.padding);
            convertView.setTag(viewHolder);
        }else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Log.d("C2TAG","getView " + comment.body + "\n");
        viewHolder.body.setText(Html.fromHtml(comment.body));
        viewHolder.body.setMovementMethod (CustomLinkMovementMethod.getInstance());
        //viewHolder.body.setClickable(false);
        viewHolder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.isCollapsed = !comment.isCollapsed;
                Comment root = new Comment();
                root.replies = Comments;
                removeIndexFromTree(root);
                numComments = replaceTreeIndex(root, 0) - 1;
                notifyDataSetChanged();
            }

        });
        viewHolder.header.setText(comment.author+" "+comment.created+" "+comment.score+" points");

        if(comment.isCollapsed) {
            viewHolder.numReplies.setText("+"+String.valueOf(comment.numChildren));
            viewHolder.numReplies.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.numReplies.setVisibility(View.INVISIBLE);
        }

        float hue = (float)(((int)(((float)720)/7*comment.level))%360);
        float sat = (float)0.7;
        float val = (float)0.7;
        viewHolder.padding.setBackgroundColor(Color.HSVToColor(new float []{hue,sat,val}));

        viewHolder.itemRoot.setPadding((comment.level-1-1)*4,0,0,0);

        viewHolder.itemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.isCollapsed = !comment.isCollapsed;
                Comment root = new Comment();
                root.replies = Comments;
                removeIndexFromTree(root);
                numComments = replaceTreeIndex(root, 0) - 1;
                notifyDataSetChanged();
            }

        });

        return convertView;
    }
    private Comment getCommentFromPosition(int position)
    {
        int targetPosition = position + 1;
        Comment root = new Comment();
        Comment atPosition;
        root.hasReplies = true;
        root.replies = Comments;

        atPosition = inOrder(root, targetPosition);

        return atPosition;
    }

    private Comment inOrder(Comment comment, int index)
    {
        Comment result = null;
        if(comment.index == index)
            return comment;
        if(comment.replies != null) {
            for (int i = 0;i < comment.replies.size();i++) {
                result = inOrder(comment.replies.get(i),index);
                if(result != null)
                    return result;
            }
        }
        return  null;
    }
    private void removeIndexFromTree(Comment comment)
    {
        comment.index = -2;
        if(comment.replies != null) {
            for (int i = 0;i < comment.replies.size();i++) {
                removeIndexFromTree(comment.replies.get(i));
            }
        }
        return;
    }
    private int replaceTreeIndex(Comment comment, int index)
    {
        int offset = 0;
        comment.index = index;
        if(comment.isCollapsed == false) {
            if (comment.replies != null) {
                for (int i = 0; i < comment.replies.size(); i++) {
                    offset += replaceTreeIndex(comment.replies.get(i), index + 1 + offset);
                }
            }
        }
        return  offset + 1;
    }

    @Override
    public int getCount() {

        return numComments;
    }

}

class CustomLinkMovementMethod extends LinkMovementMethod {

    private static Context movementContext;

    private static CustomLinkMovementMethod linkMovementMethod = new CustomLinkMovementMethod();

    public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, android.view.MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length != 0) {
                String url = link[0].getURL();
                if (url.startsWith("https") || url.startsWith("http")) {
                    Log.d("Link", url);
                    ActivityDelegator.execute(widget.getContext(), url, ((Integer) url.hashCode()).toString());
                    //Toast.makeText(movementContext, "Link was clicked", Toast.LENGTH_LONG).show();
                }/*else if (url.startsWith("tel")) {
                    Log.d("Link", url);
                    Toast.makeText(movementContext, "Tel was clicked", Toast.LENGTH_LONG).show();
                }else if (url.startsWith("mailto")) {
                    Log.d("Link", url);
                    Toast.makeText(movementContext, "Mail link was clicked", Toast.LENGTH_LONG).show();
                }else if (url.startsWith("http")) {
                    Log.d("Link", url);
                    Toast.makeText(movementContext, "Ordinary link", Toast.LENGTH_LONG).show();
                }*/
                return true;
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    public static android.text.method.MovementMethod getInstance() {
        //Log.d("Link", "??????????????");
        return linkMovementMethod;
    }
}