package com.example.jovan.app3.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.jovan.app3.Utilities.DownloadFileFromURL;
import com.example.jovan.app3.R;

import java.io.File;

public class DisplayImageActivity extends AppCompatActivity {
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        ImageView image = (ImageView)findViewById(R.id.image);
        String url = getIntent().getExtras().getString("url");
        String id = getIntent().getExtras().getString("id");
        name = id + "thumblarge";
        //ovde javuva greska zatoa sto treba vreme za da se snimi slikata pa da se prikazi go bara nultiot thumbnail namesto to sto treba
        //a nego najcesto go nema
        Log.d("URLTAG", url);
        new DownloadFileFromURL(/*this,*/ this, 0, "DisplayImageActivity", new String [] {name}).execute(url);
        Bitmap b = loadImageFromStorage(name);
        image.setImageBitmap(b);

    }

    private Bitmap loadImageFromStorage(String name)
    {
        File folder = getFilesDir();
        File picture = new File(folder,"IMAGE"+name+".jpg");
        Bitmap b = null;
        if(picture.exists()) {
            b = BitmapFactory.decodeFile(picture.toString());
            //Log.d("PICE", (((Integer) b.getByteCount()).toString()+":::"+((Integer)index).toString()));
        }
        return b;
    }
    public void asyncImageDownloadFinished(){
        ImageView image = (ImageView)findViewById(R.id.image);
        Bitmap b = loadImageFromStorage(name);
        image.setImageBitmap(b);
    }
}
