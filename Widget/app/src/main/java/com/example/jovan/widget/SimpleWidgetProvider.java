package com.example.jovan.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Jovan on 03-Apr-17.
 */
public class SimpleWidgetProvider extends AppWidgetProvider {
    public static final String TOAST_ACTION = "com.example.jovan.widget.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.example.jovan.widget.EXTRA_ITEM";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        Intent listIntent = new Intent(context, WeatherRemoteViewsService.class);
        listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[0]);
        remoteViews.setRemoteAdapter(R.id.listview, listIntent);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[0], R.id.listview);
        appWidgetManager.updateAppWidget(appWidgetIds[0], remoteViews);

        //**********************************************************
/*        RemoteViews newView = new RemoteViews(context.getPackageName(), R.layout.day_view);
        /*//**********************************************************

        Bitmap myBitmap = Bitmap.createBitmap(700, 180, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#ff737373"));
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        paint.setTypeface(font);
        paint.setTextSize(34);

        myCanvas.drawText("Wednesday", 40, 60, paint);


        font = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        paint.setTypeface(font);
        paint.setTextSize(26);

        myCanvas.drawText("Rain", 41, 98, paint);

        paint.setTextAlign(Paint.Align.RIGHT);

        myCanvas.drawText("Apr 5", 662, 50, paint);

        font = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        paint.setTypeface(font);
        paint.setTextSize(34);
        paint.setColor(Color.parseColor("#ff000000"));

        myCanvas.drawText("2/12\u00B0C", 662, 104, paint);

        remoteViews.setImageViewBitmap(R.id.myImageView, myBitmap);

        /*//**********************************************************
        newView.setImageViewBitmap(R.id.dynamicImage, myBitmap);
        remoteViews.addView(R.id.daylist,newView);*/
        //**********************************************************

        //************************************HOURLYWEATHER***************************************//
        //************************************HOURLYWEATHER***************************************//

        RemoteViews remoteHourlyImageView = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        Bitmap myBitmap = Bitmap.createBitmap(700, 600, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setColor(Color.parseColor("#ff000000"));
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        Paint fontPaint = new Paint();
        fontPaint.setColor(Color.parseColor("#ff777777"));
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        fontPaint.setTypeface(font);
        fontPaint.setTextSize(26);
        fontPaint.setTextAlign(Paint.Align.CENTER);

        /*Path mPath;
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.cubicTo(90, 90, 90, 90, 180, 20); *//*the anchors you want, the curve will tend to reach these anchor points; look at the wikipedia article to understand more *//*
        mPath.cubicTo(270, 120, 270, 120, 360, 50);

        myCanvas.drawPath(mPath, paint);*/
        //draw_path(myCanvas,paint);

        Path pathT = new Path();
        Path pathR = new Path();
        Path pathC = new Path();
        int off = 460;
        SQLiteDatabase mydatabase = SQLiteDatabase.openDatabase(context.getDatabasePath("db001").toString(), null, 0);
        Cursor resultSet = mydatabase.rawQuery("Select * from Hours",null);
        if(resultSet.getCount()>0) {
            resultSet.moveToFirst();

            int i = 0;
            float step = 77.5f, tstep = 72.5f;
            int scaleY = 1;

            pathT.moveTo(40, off - scaleY*10 * Float.parseFloat(resultSet.getString(1)));
            pathR.moveTo(40, off - scaleY*20 * Float.parseFloat(resultSet.getString(4)));
            pathC.moveTo(40, off - scaleY*1.8f * Float.parseFloat(resultSet.getString(3)));
            myCanvas.drawText(String.valueOf(Math.round(Float.parseFloat(resultSet.getString(1))/* * 100.0*/)/* / 100.0*/), 60+i*tstep, 600 - 40, fontPaint);
            myCanvas.drawText(String.valueOf(Math.round(Float.parseFloat(resultSet.getString(4))/* * 100.0*/)/* / 100.0*/), 60+i*tstep, 600 - 90, fontPaint);


            while (resultSet.moveToNext() && i < 8) {
                i++;
                pathT.lineTo(40 + i * step, off - scaleY*10 * Float.parseFloat(resultSet.getString(1)));
                if(i%2 == 0)
                    myCanvas.drawText(String.valueOf(Math.round(Float.parseFloat(resultSet.getString(1))/* * 100.0*/)/* / 100.0*/), 60+i*tstep, 600 - 40, fontPaint);
                try {
                    pathR.lineTo(40+i * step, off - scaleY*20 * Float.parseFloat(resultSet.getString(4)));
                    if(i%2 == 0)
                        myCanvas.drawText(String.valueOf(Math.round(Float.parseFloat(resultSet.getString(4))/* * 100.0*/)/* / 100.0*/), 60+i*tstep, 600 - 90, fontPaint);
                }
                catch(NumberFormatException e)
                {
                    pathR.lineTo(40 + i * step, off);
                    if(i%2 == 0)
                        myCanvas.drawText("NA", 60+i*tstep, 600 - 90, fontPaint);
                }
                try {
                    pathC.lineTo(40 + i * step, off - scaleY*1.8f * Float.parseFloat(resultSet.getString(3)));
                    //myCanvas.drawText(String.valueOf(Math.round(Float.parseFloat(resultSet.getString(3)) * 100.0) / 100.0), i*step, 600 - 80, fontPaint);
                }
                catch(NumberFormatException e)
                {
                    pathC.lineTo(40 + i * step, off);
                    //myCanvas.drawText("NA", i*step, 50, fontPaint);
                }
            }

            paint.setColor(Color.parseColor("#ffff0000"));
            paint.setStrokeWidth(1);
            myCanvas.drawPath(pathT, paint);

            float radius = 50.0f;
            CornerPathEffect cornerPathEffect = new CornerPathEffect(radius);
            ComposePathEffect composePathEffect = new ComposePathEffect(cornerPathEffect, cornerPathEffect);
            paint.setPathEffect(composePathEffect);

            myCanvas.drawPath(pathT, paint);
            paint.setColor(Color.parseColor("#ff0066ff"));
            myCanvas.drawPath(pathR, paint);
            paint.setColor(Color.parseColor("#ffaaaaaa"));
            myCanvas.drawPath(pathC, paint);

            //for(int h = 0; h <10;h++) {
                myCanvas.drawLine(40, off, 660, off, fontPaint);
                //myCanvas.drawLine(step*h, 0, step*h, 600, fontPaint);
            //}

        }
        //TODO vrati go ova remoteHourlyImageView.setImageViewBitmap(R.id.hourlyImageView, myBitmap);

        //************************************HOURLYWEATHER***************************************//
        //************************************HOURLYWEATHER***************************************//

        /*Intent intent = new Intent(context, SimpleWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds[0], remoteViews);*/

        // Sets up the intent that points to the StackViewService that will
        // provide the views for this collection.
        Intent intent = new Intent(context, WeatherRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[0]);
        // When intents are compared, the extras are ignored, so we need to embed the extras
        // into the data so that the extras will not be ignored.
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));


        // This section makes it possible for items to have individualized behavior.
        // It does this by setting up a pending intent template. Individuals items of a collection
        // cannot set up their own pending intents. Instead, the collection as a whole sets
        // up a pending intent template, and the individual items set a fillInIntent
        // to create unique behavior on an item-by-item basis.
        Intent toastIntent = new Intent(context, SimpleWidgetProvider.class);
        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect of
        // broadcasting TOAST_ACTION.
        toastIntent.setAction(SimpleWidgetProvider.TOAST_ACTION);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[0]);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.listview, toastPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds[0], remoteViews);
        appWidgetManager.updateAppWidget(appWidgetIds[0], remoteHourlyImageView);



        /*for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            String number = "ABCD";//String.format("%03d", (new Random().nextInt(900) + 100));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.simple_widget);
            remoteViews.setTextViewText(R.id.textView, number);

            Intent intent = new Intent(context, SimpleWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }*/

        //////////////////////////////////////CLICKTEST/////////////////////////////////////////////
        /*Intent clickIntent = new Intent(context, MainActivity.class);

        PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        widget.setPendingIntentTemplate(R.id.listview, clickPI);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[0], R.id.listview);*/
        //////////////////////////////////////CLICKTEST/////////////////////////////////////////////
    }

    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is TOAST_ACTION. If it is, the app widget
    // displays a Toast message for the current item.
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CLICKTAG","CLICK RECEIVED");
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }

    class Point {
        float x, y;
        float dx, dy;

        @Override
        public String toString() {
            return x + ", " + y;
        }
    }

    public Canvas draw_path(Canvas canvas,Paint paint) {
        Path path = new Path();
        ArrayList<Point> points= new ArrayList<>();
        for(int i=0;i<10;i++){
            Point point = new Point();
            point.x= 100*i;
            point.y = (float)Math.random()*90+45;
            points.add(point);
        }
        if(points.size() > 1){
            for(int i = 0; i < points.size(); i++){
                if(i >= 0){
                    Point point = points.get(i);

                    if(i == 0){
                        Point next = points.get(i + 1);
                        point.dx = ((next.x - point.x) / 3);
                        point.dy = ((next.y - point.y) / 3);
                    }
                    else if(i == points.size() - 1){
                        Point prev = points.get(i - 1);
                        point.dx = ((point.x - prev.x) / 3);
                        point.dy = ((point.y - prev.y) / 3);
                    }
                    else{
                        Point next = points.get(i + 1);
                        Point prev = points.get(i - 1);
                        point.dx = ((next.x - prev.x) / 3);
                        point.dy = ((next.y - prev.y) / 3);
                    }
                }
            }
        }

        boolean first = true;
        for(int i = 0; i < points.size(); i++){
            Point point = points.get(i);
            if(first){
                first = false;
                path.moveTo(point.x, point.y);
            }
            else{
                Point prev = points.get(i - 1);
                path.cubicTo(prev.x + prev.dx, prev.y + prev.dy, point.x - point.dx, point.y - point.dy, point.x, point.y);
            }
        }
        canvas.drawPath(path, paint);
        return canvas;
    }
}
