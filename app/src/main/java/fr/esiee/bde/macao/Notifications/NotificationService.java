package fr.esiee.bde.macao.Notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;

import com.alamkanak.weekview.WeekViewEvent;
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
import fr.esiee.bde.macao.Calendar.CalendarEvent;
import fr.esiee.bde.macao.DataBaseHelper;
import fr.esiee.bde.macao.HttpUtils;
import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;

import static android.app.Notification.DEFAULT_ALL;
import static android.app.Notification.DEFAULT_VIBRATE;
import static fr.esiee.bde.macao.Calendar.WeekViewEvent.createWeekViewEvent;
import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by delevacw on 08/11/17.
 */

public class NotificationService extends Service {

    private static List<Integer> notificationId = new ArrayList<Integer>();
    private List<CalendarEvent> events = new ArrayList<CalendarEvent>();
    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.

        dbHelper =  new DataBaseHelper(this);
        database = dbHelper.getWritableDatabase();

        //getEvents();

        retrieveEvents();
        Log.e("Notification", "Start");
        for (CalendarEvent event : events){
            if(!notificationId.contains(event.getId())) {
                Log.e("Notification", event.getName()+" : "+event.getRooms());
                createNotification(event);
                /*event.setNotified(true);
                ContentValues values = new ContentValues();
                values.put("notified", true);
                cupboard().withDatabase(this.database).update(CalendarEvent.class, values, "startString = ?", event.getStartString());*/
            }
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
                System.currentTimeMillis() + (1000 * 10),
                PendingIntent.getService(this, 0, new Intent(this, NotificationService.class), 0)
        );
    }

    private void retrieveEvents(){
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.add(Calendar.HOUR, -1);
        Date today = calendar.getTime();
        Log.i("TIME", String.valueOf(calendar.getTime()));
        //calendar.add(Calendar.HOUR, 10);
        calendar.add(Calendar.HOUR, 1);
        //calendar.add(Calendar.MINUTE, 30);
        Log.i("TIME", String.valueOf(calendar.getTime()));
        Date tomorrow = calendar.getTime();

        String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        String dateStart = sdf.format(today);
        String dateEnd = sdf.format(tomorrow);


        events.clear();
        //CalendarEvent calendarEvent = cupboard().withDatabase(database).query(CalendarEvent.class).get();
        Cursor cursor = cupboard().withDatabase(this.database).query(CalendarEvent.class).withSelection("startString >= ? and startString <= ? order by startString asc", dateStart, dateEnd).getCursor();
        //Cursor cursor = cupboard().withDatabase(this.database).query(CalendarEvent.class).getCursor();
        // or we can iterate all results
        Iterable<CalendarEvent> itr = cupboard().withCursor(cursor).iterate(CalendarEvent.class);
        for (CalendarEvent calendarEvent: itr) {
            // do something with book
            events.add(calendarEvent);
        }
    }

    private void createNotification(CalendarEvent event){
        final NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final Intent launchNotifiactionIntent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, launchNotifiactionIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = Calendar.getInstance();
        String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        String HOUR_FORMAT = "HH:mm";
        SimpleDateFormat hdf = new SimpleDateFormat(HOUR_FORMAT);

        String startHour = event.getStartString();
        String endHour = event.getEndString();
        try {
            calendar.setTime(sdf.parse(startHour));
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            startHour= hdf.format(calendar.getTime());

            calendar.setTime(sdf.parse(endHour));
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            endHour = hdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Notification.Builder builder = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())
                .setTicker("Titre")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(event.getName()+" : "+event.getRooms())
                .setContentText(startHour+" - "+endHour+" "+event.getId())
                .setContentIntent(pendingIntent)
                .setDefaults(DEFAULT_ALL)
                .setOnlyAlertOnce(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setSmallIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        }

        notificationId.add(event.getId());
        mNotification.notify(event.getId(), builder.build());
    }
}
