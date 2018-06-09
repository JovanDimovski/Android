package com.example.jovan.digitalfontreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jovan on 25-Apr-17.
 */
public final class ImageEdit {
    public static void openImage(){
        File f = null;
        try{
            f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/DFR/", "s1.jpg");
        }
        catch (Exception e)
        {
            Log.d("ImageTag","Error: " + e.getMessage());
        }
        Bitmap b = null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            Log.d("ImageTag","File not found" + e.getMessage());
        }
        int bc = b.getByteCount();
        Log.d("ImageTag",String.valueOf(bc));

        ////////////EDITFILE//////////////
        Bitmap o = b.copy(Bitmap.Config.ARGB_8888, true);

        //o = Bitmap.createScaledBitmap(o, 640, 480, false);//za pogolema slika b1 b2

        o = rotateBitmap(o,92);
        //o = skewBitmap(o,0,-0.08f);//za tempimg vrednosti
        //o = Bitmap.createBitmap(o, 287,321,26,100);//za tempimg coordinati

        //o = skewBitmap(o,-0.032f,-0.04f);//za t1 vrednosti
        //o = Bitmap.createBitmap(o, 332,278,26,100);//za t1 koordinati

        //o = Bitmap.createBitmap(o, 339,284,26,100);//za b2 koordinati
        //float scale = 3.2f;
        //o = Bitmap.createBitmap(o, 1088,931,83,336);//za b2 koordinati

        o = Bitmap.createBitmap(o, 928,931,83,336);//za s1 koordinati
        o = getResizedBitmap(o,83,380);//za s1 koordinati
        o = Bitmap.createBitmap(o, 0,0,83,336);//za s1 koordinati

        Bitmap bp = o;

        o = skewBitmap(o,0,-0.04f);

        float avgv, var;
        Bitmap ba[] = new Bitmap[8];
        for(int i = 0; i < 8; i++)
        {
            ba[i] = Bitmap.createBitmap(o, 0,42*i,83,42);
            avgv = calculateAverageValue(ba[i]);
            Log.d("ImageTag","Sredna vrednost na pixel e: " + String.valueOf(avgv));
            var = calculateVariance(ba[i],avgv);
            Log.d("ImageTag","Varira za +/-: " + String.valueOf(var));
            ba[i] = adjustBitmap(ba[i],avgv,var,i);
        }

        o = combineImages(combineImages(combineImages(ba[0],ba[1]),combineImages(ba[2],ba[3])),combineImages(combineImages(ba[4],ba[5]),combineImages(ba[6],ba[7])));

        o = rotateBitmap(o, 90);

        //o = Bitmap.createScaledBitmap(o, 50, 13, false);

        float top,mid,bot;
        float topl,botl;
        float topr,botr;

        Canvas canvas = new Canvas(o);
        Paint paint = new Paint();
        paint.setStrokeWidth(1);

        paint.setColor(Color.argb(180,0,255,255));
        float step = 12.334f;

        int digit = 0,value = 0;
        float result =0;

        for(int i=0;i<7;i++){
            float temp = 2;
            for(int  j=0;j<3;j++) {
                top = isPixelActive(o, (int) (18 + step * i), (int) (21 + step * i), 3+j, 4+j);
                if(top < temp)
                    temp = top;
            }
            top = temp;
            canvas.drawLine(18+step*i,4,21+step*i,4,paint);

            temp = 2;
            for(int  j=0;j<3;j++) {
                mid = isPixelActive(o,(int)(18+step*i),(int)(21+step*i),13+j,14+j);
                if(mid < temp)
                    temp = mid;
            }
            mid = temp;
            canvas.drawLine(18+step*i,14,21+step*i,14,paint);

            temp = 2;
            for(int  j=0;j<3;j++) {
                bot = isPixelActive(o,(int)(18+step*i),(int)(21+step*i),23+j,24+j);
                if(bot < temp)
                    temp = bot;
            }
            bot=temp;
            canvas.drawLine(18+step*i,24,21+step*i,24,paint);

            Log.d("ImageTag", "Column: "+String.valueOf(i)+" Top: " + String.format("%.2f", top)+" Mid: " + String.format("%.2f", mid)+" bot: " + String.format("%.2f", bot));

            temp = 2;
            for(int  j=0;j<3;j++) {
                topl = isPixelActive(o,(int)(15+j+step*i),(int)(16+j+step*i),5,13);
                if(topl < temp)
                    temp = topl;
            }
            topl=temp;
            canvas.drawLine(16+step*i,5,16+step*i,13,paint);

            temp = 2;
            for(int  j=0;j<3;j++) {
                topr = isPixelActive(o,(int)(22+j+step*i),(int)(23+j+step*i),5,13);
                if(topr < temp)
                    temp = topr;
            }
            topr=temp;
            canvas.drawLine(23+step*i,5,23+step*i,13,paint);

            temp = 2;
            for(int  j=0;j<3;j++) {
                botl = isPixelActive(o,(int)(15+j+step*i),(int)(16+j+step*i),15,23);
                if(botl < temp)
                    temp = botl;
            }
            botl=temp;
            canvas.drawLine(16+step*i,15,16+step*i,23,paint);

            temp = 2;
            for(int  j=0;j<3;j++) {
                botr = isPixelActive(o,(int)(22+j+step*i),(int)(23+j+step*i),15,23);
                if(botr < temp)
                    temp = botr;
            }
            botr=temp;
            canvas.drawLine(23+step*i,15,23+step*i,23,paint);

            if(top<0.5)
                Log.d("ImageTag"," _ ");
            else
                Log.d("ImageTag"," ");
            if(topl<0.5&&topr<0.5)
                Log.d("ImageTag","| |");
            else if(topl>=0.5&&topr<0.5)
                Log.d("ImageTag","  |");
            else if(topl>=0.5&&topr>=0.5)
                Log.d("ImageTag","   ");
            else if(topl<0.5&&topr>=0.5)
                Log.d("ImageTag","|  ");
            if(mid<0.5)
                Log.d("ImageTag"," _ ");
            else
                Log.d("ImageTag"," ");
            if(botl<0.5&&botr<0.5)
                Log.d("ImageTag","| |");
            else if(botl>=0.5&&botr<0.5)
                Log.d("ImageTag","  |");
            else if(botl>=0.5&&botr>=0.5)
                Log.d("ImageTag","   ");
            else if(botl<0.5&&botr>=0.5)
                Log.d("ImageTag","|  ");
            if(bot<0.5)
                Log.d("ImageTag"," _ ");
            else
                Log.d("ImageTag"," ");
            digit = 0;
            if(top < 0.5)
                digit +=1;
            if(topr <0.5)
                digit +=2;
            if(botr <0.5)
                digit +=4;
            if(bot <0.5)
                digit +=8;
            if(botl <0.5)
                digit +=16;
            if(topl <0.5)
                digit +=32;
            if(mid <0.5)
                digit +=64;
            Log.d("ImageTag","DIGIT: " + String.valueOf(digit));
            if(digit == 63) {
                Log.d("ImageTag", "DIGIT VALUE: 0");
                value = 0;
            }
            else if(digit == 6){
                Log.d("ImageTag","DIGIT VALUE: 1");
                value = 1;
            }
            else if(digit == 91){
                Log.d("ImageTag","DIGIT VALUE: 2");
                value = 2;
            }
            else if(digit == 79){
                Log.d("ImageTag","DIGIT VALUE: 3");
                value = 3;
            }
            else if(digit == 102){
                Log.d("ImageTag","DIGIT VALUE: 4");
                value = 4;
            }
            else if(digit == 109){
                Log.d("ImageTag","DIGIT VALUE: 5");
                value = 5;
            }
            else if(digit == 125){
                Log.d("ImageTag","DIGIT VALUE: 6");
                value = 6;
            }
            else if(digit == 7) {
                Log.d("ImageTag", "DIGIT VALUE: 7");
                value = 7;
            }
            else if(digit == 127){
                Log.d("ImageTag","DIGIT VALUE: 8");
                value = 8;
            }
            else if(digit == 111) {
                Log.d("ImageTag", "DIGIT VALUE: 9");
                value = 9;
            }
            else {
                Log.d("ImageTag", "DIGIT IS NOT RECOGNIZED");
                value = 0;
            }
            result = result * 10 + value;
            value = 0;
        }
        result = result/10;
        Log.d("RESULTTAG","::: " + String.format("%.1f",result));
/*

        Canvas canvas = new Canvas(o);
        Paint paint = new Paint();
        paint.setStrokeWidth(1);

        paint.setColor(Color.RED);
        canvas.drawLine(0,4,100,4,paint);
        canvas.drawLine(0,14,100,14,paint);
        canvas.drawLine(0,24,100,24,paint);



        paint.setColor(Color.YELLOW);
        canvas.drawLine(97,0,97,26,paint);
        canvas.drawLine(85,0,85,26,paint);
        canvas.drawLine(73,0,73,26,paint);
        canvas.drawLine(61,0,61,26,paint);

        canvas.drawLine(48,0,48,26,paint);
        canvas.drawLine(36,0,36,26,paint);

        canvas.drawLine(23,0,23,26,paint);

        paint.setColor(Color.CYAN);
        canvas.drawLine(90,0,90,26,paint);
        canvas.drawLine(78,0,78,26,paint);
        canvas.drawLine(66,0,66,26,paint);
        canvas.drawLine(54,0,54,26,paint);

        canvas.drawLine(41,0,41,26,paint);
        canvas.drawLine(29,0,29,26,paint);

        canvas.drawLine(16,0,16,26,paint);
*/


        ////////////EDITFILE//////////////
        FileOutputStream fos = null;
        FileOutputStream fos2 = null;
        //FileOutputStream fos3 = null;
        try {
            fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/DFR/output.jpg");
            fos2 = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/DFR/output_not_processed.jpg");
            //fos3 = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/DFR/output_denoised.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        o.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        bp.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
        //bdn.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
        try {
            fos.close();
            fos2.close();
            //fos3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static private float calculateAverageValue(Bitmap o)
    {
        float sum = 0;
        float avg = -1;
        Bitmap srca = o;
        Bitmap bitmap = srca.copy(Bitmap.Config.ARGB_8888, true);
        for(int x = 0;x < bitmap.getWidth();x++)
            for(int y = 0;y < bitmap.getHeight();y++){
                float[] hsv = new float[3];       //array to store HSV values
                Color.colorToHSV(bitmap.getPixel(x,y),hsv); //get original HSV values of pixel
                sum+=hsv[2];
            }
        avg = sum/(bitmap.getWidth()*bitmap.getHeight());
        return avg;
    }

    static private float calculateVariance(Bitmap o, float avg)
    {
        float sum = 0;
        float var = -1;
        Bitmap srca = o;
        Bitmap bitmap = srca.copy(Bitmap.Config.ARGB_8888, true);
        for(int x = 0;x < bitmap.getWidth();x++)
            for(int y = 0;y < bitmap.getHeight();y++){
                float[] hsv = new float[3];       //array to store HSV values
                Color.colorToHSV(bitmap.getPixel(x,y),hsv); //get original HSV values of pixel
                sum+=Math.abs(hsv[2]-avg);
            }
        var = sum/(bitmap.getWidth()*bitmap.getWidth());
        return var;
    }

    static private float isPixelActive(Bitmap o, int startx,int stopx,int starty,int stopy)
    {
        Bitmap srca = o;
        Bitmap bitmap = srca.copy(Bitmap.Config.ARGB_8888, true);
        float sum = 0, avg = 0;
        for(int x = startx;x < stopx;x++)
            for(int y = starty;y < stopy;y++) {
                float[] hsv = new float[3];
                Color.colorToHSV(bitmap.getPixel(x, y), hsv);
                sum+=hsv[2];
            }
        avg = sum / ((stopx-startx)*(stopy-starty));
        return avg;
    }

    static private Bitmap adjustBitmap(Bitmap o, float avg,float var,int i)
    {
        Bitmap srca = o;
        Bitmap bitmap = srca.copy(Bitmap.Config.ARGB_8888, true);
        for(int x = 0;x < bitmap.getWidth();x++)
            for(int y = 0;y < bitmap.getHeight();y++){
                int newPixel = adjustPixel(bitmap.getPixel(x,y),avg,var,i);
                bitmap.setPixel(x, y, newPixel);
            }

        return bitmap;
    }

    static private int adjustPixel(int startpixel,float avg, float var,int i){
        float[] hsv = new float[3];       //array to store HSV values
        Color.colorToHSV(startpixel,hsv); //get original HSV values of pixel
        //hsv[0]=hsv[0]+h;                //add the shift to the HUE of HSV array
        //hsv[0]=hsv[0]%360;                //confines hue to values:[0,360]

        //hsv[2] = hsv[2]*2;

        /*hsv[2]=(hsv[2]-0.25f);
        if(hsv[2]<0)
            hsv[2]=0;
        hsv[2]=hsv[2]*2;
        if(hsv[2]>1)
            hsv[2]=1;*/

        //hsv[2]=hsv[2]+(0.5f-avg);
        /*hsv[2]=(hsv[2]-(avg-var))/(avg+var);
        if(hsv[2]<0)
            hsv[2]=0;
        if(hsv[2]>1)
            hsv[2]=1;*/
        //hsv[2]=hsv[2]*2;

        //hsv[2]=hsv[2]+(1-avg);
        if(hsv[2]<(avg+0.000)) {
            hsv[0] = 360;
            hsv[1] = 1;
            hsv[2] = 0;
        }/*else if(hsv[2]>(avg+0.000) && hsv[2]<(avg+0.000))
        {
            hsv[0] = 180;
            hsv[1] = 0;
            hsv[2] = 1;
        }*/
        else{
            hsv[0] = 40*i;
            hsv[1] = 1;//dali e vo boja pozadinata
            hsv[2] = 1;
        }

        return Color.HSVToColor(Color.alpha(startpixel),hsv);
    }

    static public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        width = c.getWidth();
        height = c.getHeight()+s.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0, c.getHeight(), null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/

        return cs;
    }

    static public Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    static private  Bitmap skewBitmap(Bitmap src, float xSkew, float ySkew){
        Matrix matrix = new Matrix();
        matrix.postSkew(xSkew, ySkew);
        Bitmap skewedBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return skewedBitmap;
    }
    static public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
