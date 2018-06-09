package com.example.jovan.digitalfontreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Jovan on 01-May-17.
 */
public final class ImageProcess {
    private static Bitmap bmpArr [] = new Bitmap [6];

    public static void openImage() {
        for(int i=0; i<6; i++) {
            File f = null;
            try {
                f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/DFR/", "IMG_2017_05_01_23-48-47_" + String.valueOf(i) + ".jpg");
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
            bmpArr[i] = b.copy(Bitmap.Config.ARGB_8888, true);
        }
        //medianIMG();
        difference(2,3);
    }

    static private void medianIMG() {
        Bitmap average, median;
        average = Bitmap.createBitmap(bmpArr[0]);
        median = Bitmap.createBitmap(bmpArr[0]);

        int nE = 3;

        for(int x = 0;x < average.getWidth();x++) {
            for (int y = 0; y < average.getHeight(); y++) {
                int r[]= new int[nE], g[]= new int[nE], b[]= new int[nE];
                int temp = 0;
                int medA[] = new int[nE];
                int med = 0;
                for (int i = 0; i < nE; i++) {
                    temp = bmpArr[i].getPixel(x,y);
                    medA[i] = (int) bmpArr[i].getPixel(x,y);
                    b[i] = temp%(256);
                    g[i] = ((temp - b[i])/256)%256;
                    r[i] = ((temp - g[i]*256 - b[i])/(256*256))%256;
                }
                int ar=0, ag=0, ab=0, mr=0, mg=0,mb=0;
                for (int i = 0; i < nE; i++) {
                    ar+=r[i];
                    ag+=g[i];
                    ab+=b[i];
                }
                ar=ar/nE;
                ag=ag/nE;
                ab=ab/nE;
                mr = median(r, nE);
                mg = median(g, nE);
                mb = median(b, nE);
                temp =ar*256*256+ag*256+ab;
                average.setPixel(x,y,(int)temp);
                temp =mr*256*256+mg*256+mb;
                median.setPixel(x,y,(int)temp);
            }
        }

        FileOutputStream fos = null;
        FileOutputStream fos2 = null;
        try {
            fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/DFR/average.jpg");
            average.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            fos2 = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/DFR/median.jpg");
            median.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
            fos2.close();
        }
        catch (Exception e) {
            Log.d("ImageTag",e.getMessage());
        }
    }

    static private int median(int arr[], int nE) {
        for(int i =0;i<nE;i++)
        {
            for(int j =0;j<nE;j++)
            {
                int temp = -1;
                if(arr[i]>arr[j])
                {
                    temp = arr[i];
                    arr[i] = arr [j];
                    arr[j] = temp;
                }
            }
        }
        if(nE%2 == 0)
            return (arr[nE/2-1]+arr[nE/2])/2;
        else
            return arr[nE/2];
    }

    static private void difference(int bmp1, int bmp2){
        Bitmap difference = Bitmap.createBitmap(bmpArr[0]);

        int nE = 3, pixel, r1, g1, b1,r2, g2, b2;


        for(int x = 0;x < difference.getWidth();x++) {
            for (int y = 0; y < difference.getHeight(); y++) {
                pixel = bmpArr[bmp1].getPixel(x,y);
                b1 = pixel%(256);
                g1 = ((pixel - b1)/256)%256;
                r1 = ((pixel - g1*256 - b1)/(256*256))%256;

                pixel = bmpArr[bmp2].getPixel(x,y);
                b2 = pixel%(256);
                g2 = ((pixel - b2)/256)%256;
                r2 = ((pixel - g2*256 - b2)/(256*256))%256;

                b1 = Math.abs(b1-b2);
                g1 = Math.abs(g1-g2);
                r1 = Math.abs(r1-r2);
                //Log.d("ImageTag", "R"+String.valueOf(r1)+"G"+String.valueOf(g1)+"B"+String.valueOf(b1));
                pixel = r1*256*256 + g1*256 + b1;
                difference.setPixel(x,y,Color.rgb(r1*g1*b1,r1*g1*b1,r1*g1*b1));
            }
            if(x == 512) Log.d("ImageTag", "512");
            else if(x == 1024) Log.d("ImageTag", "1024");
            else if(x == 1536) Log.d("ImageTag", "1536");
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/DFR/difference.jpg");
            difference.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        }
        catch (Exception e) {
            Log.d("ImageTag",e.getMessage());
        }
    }
}
