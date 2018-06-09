package com.example.jovan.app3.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jovan.app3.Activities.ImgurGaleryActivity;
import com.example.jovan.app3.R;
import com.example.jovan.app3.Utilities.Utilities;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Jovan on 30-Sep-16.
 */

public class ImgurGifFragment extends Fragment {
    /*Integer imageNumber;
    Integer allImages;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.imgur_gif_fragment, container, false);
        Bundle args = getArguments();
        //((TextView) rootView.findViewById(R.id.title)).setText(args.getString("title"));

        String filesdir = args.getString("filesdir");
        String filepath = args.getString("filepath");
        String description = args.getString("description");
        String title = args.getString("title");
        /*imageNumber = args.getInt("imageNumber");
        allImages = args.getInt("allImages");*/

        Bitmap b = Utilities.loadImageFromStorage(filesdir, filepath);
        ((TextView)rootView.findViewById(R.id.title)).setText(title);
        ((TextView)rootView.findViewById(R.id.description)).setText(description);
        //((ImageView) rootView.findViewById(R.id.image)).setImageBitmap(b);

        GifImageView gif = (GifImageView) rootView.findViewById(R.id.gifview);

        Log.d("GifTAG", "pred citanje od ffajl");
        File gifFile = new File(filesdir,filepath);
        if(gifFile.exists())
            Log.d("GifTAG", "postoi fajlot");
        else
            Log.d("GifTAG", "NE postoi fajlot");

        GifDrawable gifFromFile = null;
        try {
            gifFromFile = new GifDrawable(gifFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("GifTAG", "exception pri mestenje gifDrawable");
        }

        gif.setImageDrawable(gifFromFile);

        return rootView;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ImgurGaleryActivity A = (ImgurGaleryActivity)getActivity();
            A.pageChange();
        }
        else {
        }
    }
}