package com.example.jovan.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
import android.widget.RemoteViews;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.json.*;

public class MainActivity extends AppCompatActivity implements AsyncWebPageNotifier {
    ArrayList<String> daya,mina,maxa,nighta,evea,morna, desca;
    ArrayList<String> HT,HR,HC,HD,HDT;
    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?id=792578&APPID=b55a87a63d665061d8b30fac7c6376bf";
        String tag = "DAYS";
        daya = new ArrayList<>();
        mina = new ArrayList<>();
        maxa = new ArrayList<>();
        desca = new ArrayList<>();

        getApplicationContext().deleteDatabase("db001");

        mydatabase = openOrCreateDatabase("db001",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Days(DayOfWeek VARCHAR,Date VARCHAR,DayTemp VARCHAR,MinTemp VARCHAR,MaxTemp VARCHAR,Description VARCHAR);");

        new AsyncGetWebPage(/*this,*/ this,tag).execute(url);

        url = "http://api.openweathermap.org/data/2.5/forecast?id=792578&APPID=b55a87a63d665061d8b30fac7c6376bf";
        tag = "HOURS";
        HT=new ArrayList<>();
        HC=new ArrayList<>();
        HR=new ArrayList<>();
        HD=new ArrayList<>();
        HDT=new ArrayList<>();

        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Hours(DateTime VARCHAR,Temperature VARCHAR,Description VARCHAR,Clouds VARCHAR,Rain VARCHAR);");
        new AsyncGetWebPage(this,tag).execute(url);

        calendarEvents();

        finish();
    }

    @Override
    public void notifyWeb(String webPage, String tag) {
        switch (tag) {
            case "DAYS":
                fillTableDays(webPage);
                break;
            case "HOURS":
                fillTableHours(webPage);
                break;
        }
    }

    public void fillTableDays(String webPage){
        Log.d("TESTTAG", webPage);
        parseDaysJson(webPage);
        int i = 0;

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        for (String day:daya) {
            Log.d("DATETEST", String.valueOf(new SimpleDateFormat("E", Locale.ENGLISH).format(date.getTime())));
            mydatabase.execSQL("INSERT INTO Days VALUES('"+ new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime())
                    +"','"+ new SimpleDateFormat("MMM d", Locale.ENGLISH).format(date.getTime())
                    +"','"+day
                    +"','"+mina.get(i)
                    + "','"+maxa.get(i)
                    +"','"+desca.get(i)
                    +"');");
            i++;
            calendar.add(Calendar.DATE, 1);  // number of days to add
            date = calendar.getTime();  // dt is now the new date
        }
        
        //******************************DATABASE*TEST******************************//
        Cursor resultSet = mydatabase.rawQuery("Select * from Days",null);
        resultSet.moveToFirst();
        String dbday, dbdate, dbdailyTemperature,dbmin,dbmax,dbdesc;

        for(int j=0;j < resultSet.getCount();j++)
        {
            dbday = resultSet.getString(0);
            dbdate = resultSet.getString(1);
            dbdailyTemperature = resultSet.getString(2);
            dbmin = resultSet.getString(3);
            dbmax = resultSet.getString(4);
            dbdesc = resultSet.getString(5);
            Log.d("SQLITETAG","DAY: " + dbday +"\tDATE: " + dbdate + "\tTemperature: " + dbdailyTemperature + "\tDescription: " + dbdesc+"\t"+dbmin+"/"+dbmax);
            resultSet.moveToNext();
        }
        //******************************DATABASE*TEST******************************//

        Intent intent = new Intent(this, SimpleWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), SimpleWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);

    }

    public void fillTableHours(String webPage){
        Log.d("TESTTAG", webPage);
        parseHoursJson(webPage);

        for (int i = 0; i < HT.size();i++) {
            mydatabase.execSQL("INSERT INTO Hours VALUES('"+ HDT.get(i)
                    +"','"+ HT.get(i)
                    +"','"+HD.get(i)
                    +"','"+HC.get(i)
                    + "','"+HR.get(i)
                    +"');");
        }

        //******************************DATABASE*TEST******************************//
        Cursor resultSet = mydatabase.rawQuery("Select * from Hours",null);
        resultSet.moveToFirst();
        String date, temperature, description, clouds, rain;

        for(int j=0;j < resultSet.getCount();j++)
        {
            date = resultSet.getString(0);
            temperature = resultSet.getString(1);
            description = resultSet.getString(2);
            clouds = resultSet.getString(3);
            rain = resultSet.getString(4);
            Log.d("HOURSTAG","HOUR: " + date +"\tTemperature: " + temperature + "\tDescription: " + description+ "\tClouds%: " + clouds+ "\tRain: " + rain);
            resultSet.moveToNext();
        }
        //******************************DATABASE*TEST******************************//

        Intent intent = new Intent(this, SimpleWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), SimpleWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    void parseDaysJson(String webPage){
        JSONObject obj = null;
        try
        {
            obj = new JSONObject(webPage);
            String cityName = obj.getJSONObject("city").getString("name");
            String cod = obj.getString("cod");
            String msg = obj.getString("message");
            String cnt = obj.getString("cnt");
            JSONArray arr = obj.getJSONArray("list");
            for (int i = 0; i < arr.length(); i++)
            {
                String dt = arr.getJSONObject(i).getString("dt");

                JSONObject temp = arr.getJSONObject(i).getJSONObject("temp");
                Double day = temp.getDouble("day")-273.15;
                daya.add(((Integer) day.intValue()).toString());
                Double min = temp.getDouble("min")-273.15;
                mina.add(((Integer) min.intValue()).toString());
                Double max = temp.getDouble("max")-273.15;
                maxa.add(((Integer) max.intValue()).toString());

                JSONObject weather = arr.getJSONObject(i).getJSONArray("weather").getJSONObject(0);
                String shortDesc = weather.getString("main");
                desca.add(shortDesc);
                String desc = weather.getString("description");
                Log.d("TESTTAG",cityName+", date:"+dt+ ", temperature: "+ day+ ", description: "+ shortDesc);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d("TESTTAG","ERROR: "+e.getMessage());
        }
    }

    void parseHoursJson(String webPage){
        JSONObject obj = null;
        try
        {
            obj = new JSONObject(webPage);
            String cod = obj.getString("cod");
            Log.d("TESTTAG","HOURS COD: "+cod);
            JSONArray arr = obj.getJSONArray("list");

            for (int i = 0; i < arr.length(); i++) {
                String dt = arr.optJSONObject(i).optString("dt");

                long batch_date = Long.parseLong(dt);
                Date date = new Date (batch_date * 1000);

                SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String dateString = sfd.format(date);
                HDT.add(dateString);
                JSONObject main = arr.optJSONObject(i).optJSONObject("main");
                Double temp = main.optDouble("temp")-273.15;
                HT.add(String.valueOf(temp));
                JSONObject weather = arr.optJSONObject(i).optJSONArray("weather").optJSONObject(0);
                String description = weather.optString("description");
                HD.add(description);
                JSONObject cloudsObj = arr.optJSONObject(i).optJSONObject("clouds");
                String clouds = cloudsObj.optString("all");
                HC.add(clouds);
                JSONObject rainObj = arr.optJSONObject(i).optJSONObject("rain");
                String rain = rainObj.optString("3h");
                HR.add(rain);
                Log.d("HOURSTAG", ", temp:" + temp + ", desc: " + description + ", clouds: " + clouds + ", rain: " + rain);
                }
            }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d("TESTTAG","ERROR: "+e.getMessage());
        }
    }


    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
        CalendarContract.Calendars._ID,                           // 0
        CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
        CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public void calendarEvents() {
        Log.d("CALENDARTAG", "In calendar function");

        /*Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, "My House Party");
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Beach House");
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "A Pig Roast on the Beach");

        GregorianCalendar calDate = new GregorianCalendar(2017, 4, 17);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.getTimeInMillis());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDate.getTimeInMillis());

        startActivity(calIntent);*///Raboti so prompt za save

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        ContentUris.appendId(eventsUriBuilder, date.getTime());
        calendar.add(Calendar.DATE,1);
        date = calendar.getTime();
        ContentUris.appendId(eventsUriBuilder, date.getTime());
        Uri eventsUri = eventsUriBuilder.build();
        Cursor cursor = null;
        cursor = getBaseContext().getContentResolver().query(eventsUri, new String[] {CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION,CalendarContract.Events.EVENT_TIMEZONE,CalendarContract.Events.DTSTART}, null, null, CalendarContract.Instances.DTSTART + " ASC");
        if( cursor != null) {
            Log.d("CALENDARTAG","Cursor is not null");
            Log.d("CALENDARTAG","Cursor count is "+String.valueOf(cursor.getCount()));
            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                String EventTitle = cursor.getString(0);
                String description = cursor.getString(1);
                String timezone = cursor.getString(2);
                String startdate = cursor.getString(3);

                Calendar cl = Calendar.getInstance();
                Long millis = Long.parseLong(startdate);
                cl.setTimeInMillis(millis);

                Log.d("CALENDARTAG", "Long to string: " + millis.toString());

                Log.d("CALENDARTAG", "EVENT: " + EventTitle + " descripion: " + description + " timezone: " + timezone + " start: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(cl.getTime()));
                while (cursor.moveToNext()) {
                    EventTitle = cursor.getString(0);
                    description = cursor.getString(1);
                    timezone = cursor.getString(2);
                    startdate = cursor.getString(3);
                    millis = Long.parseLong(startdate);
                    cl.setTimeInMillis(millis);

                    Log.d("CALENDARTAG", "EVENT: " + EventTitle + " descripion: " + description + " timezone: " + timezone + " start: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(cl.getTime()));
                }
            }
        }
    }
}
