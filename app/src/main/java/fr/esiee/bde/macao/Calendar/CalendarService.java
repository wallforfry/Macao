package fr.esiee.bde.macao.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.alamkanak.weekview.*;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import fr.esiee.bde.macao.DataBaseHelper;
import fr.esiee.bde.macao.HttpUtils;
import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.Notifications.NotificationService;
import fr.esiee.bde.macao.R;

import static android.app.Notification.DEFAULT_ALL;
import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by delevacw on 08/11/17.
 */

public class CalendarService extends Service {

    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.

        dbHelper =  new DataBaseHelper(this);
        database = dbHelper.getWritableDatabase();

        getEvents();

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 5),
                PendingIntent.getService(this, 0, new Intent(this, CalendarService.class), 0)
        );
    }

    private void getEvents(){
        SharedPreferences sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String mail = sharedPref.getString("mail", "");
        if(mail.equals("")){
            Log.e("Agenda", "Non connecté");
        }
        else {
            Log.i("Agenda", "Maj des events");
            RequestParams rp = new RequestParams();
            rp.add("mail", mail);

            HttpUtils.postByUrl("http://ade.wallforfry.fr/api/ade-esiee/agenda", rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.d("asd", "---------------- this is response : " + response);
                    try {
                        JSONObject serverResp = new JSONObject(response.toString());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                    // Pull out the first event on the public timeline
                    try {
                        if(!((JSONObject) timeline.get(0)).has("error")) {
                            cupboard().withDatabase(database).delete(CalendarEvent.class, null);
                            Log.i("Agenda", timeline.get(0).toString());
                            for (int i = 0; i < timeline.length(); i++) {
                                JSONObject obj = (JSONObject) timeline.get(i);

                                String start = obj.get("start").toString();
                                String end = obj.get("end").toString();

                                String title = obj.get("name") + "\n" + obj.get("rooms") + "\n" + obj.get("prof") + "\n" + obj.get("unite");
                                String name = obj.get("name").toString();

                                CalendarEvent calendarEvent = new CalendarEvent(i, title, start, end, name);
                                calendarEvent.setRooms(obj.getString("rooms"));
                                calendarEvent.setProf(obj.getString("prof"));
                                calendarEvent.setUnite(obj.getString("unite"));
                                calendarEvent.setColor();

                                cupboard().withDatabase(database).put(calendarEvent);

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("Agenda", "Agenda à jour");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("Failed: ", ""+statusCode);
                    Log.d("Error : ", "" + throwable);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject){
                    super.onFailure(statusCode, headers, throwable, jsonObject);
                    Log.e("AGENDA", ""+statusCode+" "+throwable);
                }

            });
        }
    }
}
