package fr.esiee.bde.macao.Events;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import fr.esiee.bde.macao.DataBaseHelper;
import fr.esiee.bde.macao.HttpUtils;
import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;

import static android.app.Notification.DEFAULT_ALL;
import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by delevacw on 08/11/17.
 */

public class EventService extends Service {

    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        try {
            dbHelper =  new DataBaseHelper(this);
            database = dbHelper.getWritableDatabase();

            getEvents();
        }
        catch (SQLiteException e){
            Log.e("EventService", e.toString());
        }

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
                System.currentTimeMillis() + (1000 * 60 * 5 ),
                PendingIntent.getService(this, 0, new Intent(this, EventService.class), 0)
        );
    }

    private void getEvents(){
        Log.i("Events", "Maj des events");
        RequestParams rp = new RequestParams();

        HttpUtils.getByUrl("https://bde.esiee.fr/api/posts.json", rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // Pull out the first event on the public timeline
                JSONArray events = null;
                try {
                    events = (JSONArray) timeline.get("entries");
                    //cupboard().withDatabase(database).delete(Event.class, null);
                    for(int i = 0; i < events.length(); i++) {
                        Event event = new Event();
                        JSONObject newsObject = (JSONObject) events.get(i);
                        try {
                            JSONObject jsonObject = (JSONObject) newsObject.get("event");
                            Log.d("EVENT BDE", String.valueOf(jsonObject.get("title")));
                            event.setId((int) jsonObject.get("id"));
                            event.setTitle(String.valueOf(jsonObject.get("title")));
                            event.setStart(String.valueOf(jsonObject.get("start")));
                            event.setEnd(String.valueOf(jsonObject.get("end")));
                            if(newsObject.has("photo")) {
                                event.setImage(String.valueOf(((JSONObject) newsObject.get("photo")).get("url_thumbnail")));
                            }
                            event.setPublicationDate(String.valueOf(newsObject.get("publication_date_start")));
                            event.setSlug(String.valueOf(newsObject.get("slug")));
                            if(jsonObject.has("place")) {
                                event.setPlace(String.valueOf(jsonObject.get("place")));
                            }

                            Event old = cupboard().withDatabase(database).query(Event.class).withSelection("title = ?", event.getTitle()).get();

                            try {
                                Calendar calendar = Calendar.getInstance();
                                String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'+'SSSS";
                                SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
                                Date now = calendar.getTime();
                                Date date = sdf.parse(event.getStart());
                                Log.d("DATE", date.toString());
                                Log.d("NOW", now.toString());
                                if (now.compareTo(date) <= 0) {
                                    if ((old != null && !old.isNotified()) || old == null) {
                                        createNotification(event);
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(old == null) {
                                cupboard().withDatabase(database).put(event);
                            }

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Events", "Events à jour");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("Event", "Failed to get event form bde website");
            }
        });
    }

    public void createNotification(Event event){
        final NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final Intent launchNotifiactionIntent = new Intent(this, MainActivity.class);
        launchNotifiactionIntent.putExtra("SelectedMenuItem", 0);
        launchNotifiactionIntent.putExtra("SelectedSubMenuItem", 1);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                (int) System.currentTimeMillis(), launchNotifiactionIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder builder = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())
                .setTicker("Titre")
                .setSmallIcon(R.drawable.ic_launcher_transparent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Nouvel évènement")
                .setContentText(event.getTitle())
                .setStyle(new Notification.BigTextStyle().bigText(event.getTitle()+"\n"+event.getTimeString()))
                .setContentIntent(pendingIntent)
                .setDefaults(DEFAULT_ALL)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(getResources().getColor(R.color.colorPrimary));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder.setColor(getColor(R.color.colorPrimary));
        }

        mNotification.notify(event.getId(), builder.build());

        event.setNotified(true);
        cupboard().withDatabase(database).put(event);
    }
}
