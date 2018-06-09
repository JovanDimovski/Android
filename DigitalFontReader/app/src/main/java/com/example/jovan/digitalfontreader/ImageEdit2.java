package com.example.jovan.digitalfontreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jovan on 29-Apr-17.
 */
public final class ImageEdit2 {
    public static void openImage() {
        File f = null;
        try {
            f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/DFR/", "n1.jpg");
        } catch (Exception e) {
            Log.d("ImageTag", "Error: " + e.getMessage());
        }
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            Log.d("ImageTag", "File not found" + e.getMessage());
        }
        int bc = b.getByteCount();
        Log.d("ImageTag", String.valueOf(bc));

        ////////////EDITFILE//////////////
        Bitmap o = b.copy(Bitmap.Config.ARGB_8888, true);
        o = rotateBitmap(o,180);

        int minrow = minrow(o);
        Log.d("ImageTag","Red so najmala vrednost e: " + String.valueOf(minrow));

        o = Bitmap.createBitmap(o,0,minrow,o.getWidth(),o.getHeight()-minrow);

        float avgrow = avgrow(o);

        int faar = firstAboveAvgRow(o,avgrow);
        Log.d("ImageTag","Prv bel red e: " + String.valueOf(faar));

        o = Bitmap.createBitmap(o,0,faar+10,o.getWidth(),o.getHeight()-faar-10);

        float avgcol = avgcol(o);

        int faac = firstAboveAvgCol(o,avgcol);
        Log.d("ImageTag","Prva bela kolona e: " + String.valueOf(faac));

        o = Bitmap.createBitmap(o,faac+10,0,o.getWidth()-faac-10,o.getHeight());

        avgrow = avgrow(o);

        int fbar = firstBelowAvgRow(o,avgrow);
        Log.d("ImageTag","Prv crn red e: " + String.valueOf(fbar));

        o = Bitmap.createBitmap(o,0,fbar,o.getWidth(),o.getHeight()-fbar);

        avgcol = avgcol(o);

        int fbac = firstBelowAvgCol(o,avgcol);
        Log.d("ImageTag","Prva crna kolona e: " + String.valueOf(fbac));

        o = Bitmap.createBitmap(o,fbac,0,o.getWidth()-fbac,o.getHeight());

        avgrow = avgrow(o);

        faar = firstAboveAvgRow(o,avgrow);
        Log.d("ImageTag","Prv bel red e: " + String.valueOf(faar));

        o = Bitmap.createBitmap(o,0,0,o.getWidth(),faar);

        avgcol = avgcol(o);

        faac = firstAboveAvgCol(o,avgcol);
        Log.d("ImageTag","Prva bela kolona e: " + String.valueOf(faac));

        o = Bitmap.createBitmap(o,0,0,faac,o.getHeight());


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/DFR/firstcrop.jpg");
            o.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        }
        catch (Exception e) {
            Log.d("ImageTag",e.getMessage());
        }
    }


    static private int minrow(Bitmap bitmap)
    {
        int minrownum = -1;
        float minrow = 2;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for(int y = 0;y < height;y++) {
            float temp = 0;
            float row = 0;
            for (int x = 0; x < width; x++) {
                float[] hsv = new float[3];                 //array to store HSV values
                Color.colorToHSV(bitmap.getPixel(x, y), hsv); //get original HSV values of pixel
                temp+=hsv[2];
            }
            row = temp/width;
            if(row < minrow) {
                minrow = row;
                minrownum = y;
            }
        }
        return minrownum;
    }

    static private float avgrow(Bitmap bitmap)
    {
        float avgrow = 0;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for(int y = 0;y < height;y++) {
            float temp = 0;
            float row = 0;
            for (int x = 0; x < width; x++) {
                float[] hsv = new float[3];                 //array to store HSV values
                Color.colorToHSV(bitmap.getPixel(x, y), hsv); //get original HSV values of pixel
                temp+=hsv[2];
            }
            row = temp/width;
            avgrow +=row;
        }
        avgrow = avgrow/height;
        return avgrow;
    }

    static private int firstAboveAvgRow(Bitmap bitmap, float avgrow)
    {
        int minrownum = -1;
        float minrow = 2;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for(int y = 0;y < height;y++) {
            float temp = 0;
            float row = 0;
            for (int x = 0; x < width; x++) {
                float[] hsv = new float[3];                 //array to store HSV values
                Color.colorToHSV(bitmap.getPixel(x, y), hsv); //get original HSV values of pixel
                temp+=hsv[2];
            }
            row = temp/width;
            if(row > avgrow) {
                return y;
            }
        }
        return -1;
    }

    static private int firstBelowAvgRow(Bitmap bitmap, float avgrow)
    {
        int minrownum = -1;
        float minrow = 2;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for(int y = 0;y < height;y++) {
            float temp = 0;
            float row = 0;
            for (int x = 0; x < width; x++) {
                float[] hsv = new float[3];                 //array to store HSV values
                Color.colorToHSV(bitmap.getPixel(x, y), hsv); //get original HSV values of pixel
                temp+=hsv[2];
            }
            row = temp/width;
            if(row < avgrow) {
                return y;
            }
        }
        return -1;
    }

    static private float avgcol(Bitmap bitmap)
    {
        float avgcol = 0;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for(int x = 0;x < width;x++) {
            float temp = 0;
            float col = 0;
            for (int y = 0; y < height; y++) {
                float[] hsv = new float[3];                 //array to store HSV values
                Color.colorToHSV(bitmap.getPixel(x, y), hsv); //get original HSV values of pixel
                temp+=hsv[2];
            }
            col = temp/height;
            avgcol +=col;
        }
        avgcol = avgcol/width;
        return avgcol;
    }

    static private int firstAboveAvgCol(Bitmap bitmap, float avgcol)
    {
        int minrownum = -1;
        float minrow = 2;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for(int x = 0;x < width;x++) {
            float temp = 0;
            float col = 0;
            for (int y = 0; y < height; y++) {
                float[] hsv = new float[3];                 //array to store HSV values
                Color.colorToHSV(bitmap.getPixel(x, y), hsv); //get original HSV values of pixel
                temp+=hsv[2];
            }
            col = temp/height;
            if(col > avgcol) {
                return x;
            }
        }
        return -1;
    }

    static private int firstBelowAvgCol(Bitmap bitmap, float avgcol)
    {
        int minrownum = -1;
        float minrow = 2;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for(int x = 0;x < width;x++) {
            float temp = 0;
            float col = 0;
            for (int y = 0; y < height; y++) {
                float[] hsv = new float[3];                 //array to store HSV values
                Color.colorToHSV(bitmap.getPixel(x, y), hsv); //get original HSV values of pixel
                temp+=hsv[2];
            }
            col = temp/height;
            if(col < avgcol) {
                return x;
            }
        }
        return -1;
    }

    static public Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


}
