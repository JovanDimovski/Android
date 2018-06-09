package com.example.jovan.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

/**
 * Created by Jovan on 08-Apr-17.
 */

/**
 * This is the service that provides the factory to be bound to the collection service.
 */

public class WeatherRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("ListTag","In service");
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}


/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;
    private Bitmap myBitmap;
    private ArrayList<RemoteViews> LRV;

    //******************************DATABASE*TEST******************************//
    SQLiteDatabase mydatabase;
    Cursor resultSet;
    //******************************DATABASE*TEST******************************//

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;

        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        //AppWidgetManager.getInstance(mContext).notifyAppWidgetViewDataChanged(mAppWidgetId,0);

        //******************************DATABASE*TEST******************************//
        mydatabase = SQLiteDatabase.openDatabase(context.getDatabasePath("db001").toString(), null, 0);
        resultSet = mydatabase.rawQuery("Select * from Days",null);
        //******************************DATABASE*TEST******************************//

        myBitmap = Bitmap.createBitmap(700, 160, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        myCanvas.drawRGB(235, 0, 0);

        LRV = new ArrayList<>();
    }
    public void onCreate() {
        // Since we reload the cursor in onDataSetChanged() which gets called immediately after
        // onCreate(), we do nothing here.
        Log.d("ListTag","On create");
    }
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }
    public int getCount() {
        //return mCursor.getCount();
        Log.d("ListTag","Get Count: "+String.valueOf(resultSet.getCount()));
        //treba da vrakja tocen broj
        return resultSet.getCount()+1+1;//+header+footer
    }

    public void createRemoteViewArray()
    {
        for(int position = 0; position < getCount(); position++)
        {
            if(position == 0){
                RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.header);
                final Intent fillInIntent = new Intent();
                rv.setOnClickFillInIntent(R.id.listItemImage, fillInIntent);
                //return rv;
                LRV.add(rv);
            }
            else if(position>0&&position<getCount()-1) {
                //******************************DATABASE*TEST******************************//*/
                resultSet.moveToPosition(position-1);
                String day = resultSet.getString(0);
                String date = resultSet.getString(1);
                String dailyTemperature = resultSet.getString(2);
                String min = resultSet.getString(3);
                String max = resultSet.getString(4);
                String description = resultSet.getString(5);
                Log.d("SQLITETAG", "DAY: " + day + " Temperature: " + dailyTemperature);
                //******************************DATABASE*TEST******************************//*/
                int temp = Integer.parseInt(dailyTemperature);

                Log.d("ListTag", "Get View");
                // Get the data for this position from the content provider


                // Return a proper item with the proper day and temperature
                final String formatStr = mContext.getResources().getString(R.string.item_format_string);
                int itemId;

                itemId = R.layout.list_item_day;

                RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);

                /******************************************************************************************/

                Bitmap myBitmap = Bitmap.createBitmap(700, 160, Bitmap.Config.ARGB_8888);
                Canvas myCanvas = new Canvas(myBitmap);
                if (day.equalsIgnoreCase("Saturday") || day.equalsIgnoreCase("Sunday")) {
                    myCanvas.drawRGB(235, 235, 235);
                } else {
                    myCanvas.drawRGB(255, 255, 255);
                }
                Paint paint = new Paint();

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#ff737373"));
                paint.setFlags(Paint.ANTI_ALIAS_FLAG);


                Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf");
                paint.setTypeface(font);
                paint.setTextSize(34);

                myCanvas.drawText(day, 40, 60, paint);


                font = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Semibold.ttf");
                paint.setTypeface(font);
                paint.setTextSize(26);

                myCanvas.drawText(description, 41, 98, paint);

                paint.setTextAlign(Paint.Align.RIGHT);

                myCanvas.drawText(date, 662, 50, paint);

                font = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Regular.ttf");
                paint.setTypeface(font);
                paint.setTextSize(34);
                paint.setColor(Color.parseColor("#ff000000"));

                myCanvas.drawText(min + "/" + max + "\u00B0C", 662, 104, paint);

                rv.setImageViewBitmap(R.id.listItemImage, myBitmap);


                /******************************************************************************************/
                // Set the click intent so that we can handle it and show a toast message
                //final Intent fillInIntent = new Intent();
                /*final Bundle extras = new Bundle();
                extras.putString(SimpleWidgetProvider.EXTRA_DAY_ID, day);
                fillInIntent.putExtras(extras);*/

                /*Bundle extras = new Bundle();
                extras.putInt("123", position);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                // Make it possible to distinguish the individual on-click
                // action of a given item

                rv.setOnClickFillInIntent(R.id.listview, fillInIntent);*/

                // Next, set a fill-intent, which will be used to fill in the pending intent template
                // that is set on the collection view in StackWidgetProvider.
                Bundle extras = new Bundle();
                extras.putInt(SimpleWidgetProvider.EXTRA_ITEM, position);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                // Make it possible to distinguish the individual on-click
                // action of a given item
                rv.setOnClickFillInIntent(R.id.listview, fillInIntent);


                //return rv;
                LRV.add(rv);
            }
            else{
                RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.footer);//so footer ne se prikazvaat denovite so header se prikazuvaat
                final Intent fillInIntent = new Intent();
                rv.setOnClickFillInIntent(R.id.listItemImage, fillInIntent);
                //return rv;
                LRV.add(rv);
            }
        }
    }

    public RemoteViews getViewAt(int position) {
        if(position - 1> LRV.size())
            return getLoadingView();
        else
            return LRV.get(position);
    }

    public RemoteViews WITHREDRECTANGLESgetViewAt(int position) {
        int itemId = R.layout.list_item_day;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        rv.setImageViewBitmap(R.id.listItemImage, myBitmap);

        final Intent fillInIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.listItemImage, fillInIntent);
        return rv;
    }
    public RemoteViews getLoadingView() {
        int itemId = R.layout.loading;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        final Intent fillInIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.listItemImage, fillInIntent);
        return rv;
    }
    public int getViewTypeCount() {
        // Technically, we have two types of views (the dark and light background views)
        return 3;
    }
    public long getItemId(int position) {
        return position;
    }
    public boolean hasStableIds() {
        return true;
    }
    public void onDataSetChanged() {
        Log.d("ListTag","On DatasetChanged");
        resultSet = mydatabase.rawQuery("Select * from Days",null);

        LRV = new ArrayList<>();
        createRemoteViewArray();
    }
}