package com.example.jovan.app3.Fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.ActivityDelegator;
import com.example.jovan.app3.Utilities.TouchImageView;
import com.example.jovan.app3.Utilities.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Jovan on 26-Aug-16.
 */
public class SlideFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
        Bundle args = getArguments();
        ((TextView) rootView.findViewById(R.id.title)).setText(args.getString("title"));
        ((TextView) rootView.findViewById(R.id.author)).setText(args.getString("author"));

        String filesdir = args.getString("filesdir");
        final String id = args.getString("id");
        String source = args.getString("source");
        final String url = args.getString("url");
        Bitmap b = Utilities.loadImageFromStorage(filesdir, id+".jpg");
        TouchImageView image = ((TouchImageView) rootView.findViewById(R.id.image));
        image.setImageBitmap(b);

        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActivityDelegator.execute(v.getContext(), url, id);
            }});

        return rootView;
    }
/*    private Bitmap loadImageFromStorage(String folder ,String name)
    {
        File picture = new File(folder+"/IMAGE"+name+".jpg");
        Bitmap b = null;
        if(picture.exists()) {
            b = BitmapFactory.decodeFile(picture.toString());
            //Log.d("PICE", (((Integer) b.getByteCount()).toString()+":::"+((Integer)index).toString()));
        }
        return b;
    }*/
    /*public static Bitmap loadImageFromStorage(String filesdir ,String name)
    {
        int reqWidth = 540;
        int reqHeight = 960;
        String filepath = filesdir + "/" + name + "." + "jpg";
        File picture = new File(filepath);
        File cached = new File(filesdir+"/CACHE"+name+".jpg");          // the File to save to
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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
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
    }*/
}