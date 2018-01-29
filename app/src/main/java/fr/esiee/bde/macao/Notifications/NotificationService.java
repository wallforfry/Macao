package fr.esiee.bde.macao.Notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.esiee.bde.macao.Calendar.CalendarEvent;
import fr.esiee.bde.macao.DataBaseHelper;
import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;

import static android.app.Notification.DEFAULT_ALL;
import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by delevacw on 08/11/17.
 */

public class NotificationService extends Service {

    private static List<Integer> notificationId = new ArrayList<Integer>();
    private static List<String> notificationStartString = new ArrayList<String>();
    private List<CalendarEvent> events = new ArrayList<CalendarEvent>();
    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
        try {
            dbHelper = new DataBaseHelper(this);
            database = dbHelper.getWritableDatabase();

            //getEvents();

            retrieveEvents();
            Log.i("Notification", "Start");
            for (CalendarEvent event : events) {
                //if ((!notificationId.contains(event.getId())) && notificationStartString.contains(event.getStartString())) {
                if (!notificationId.contains(event.getId())) {
                    Log.i("Notification", event.getName() + " : " + event.getRooms());
                    boolean notified = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("enable_calendar_notification", true);
                    if(notified) {
                        createNotification(event);
                    }
                /*event.setNotified(true);
                ContentValues values = new ContentValues();
                values.put("notified", true);
                cupboard().withDatabase(this.database).update(CalendarEvent.class, values, "startString = ?", event.getStartString());*/
                }
                else{
                    Log.d("Notification", "Don't notify "+event.getName()+" "+event.getRooms());
                }
            }
        }
        catch (SQLiteException e){
            Log.e("Notification", e.toString());
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

        int minute_before_event = 30;
        try {
            minute_before_event = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("time_before_calendar_notification", "30"));
        } catch (NumberFormatException e){
            Log.e("Settings", "La préférence minute_before_event n'est pas un nombre");
        }

        calendar.add(Calendar.HOUR, -1);

        Date startdate = calendar.getTime();
        Log.i("TIME START", String.valueOf(startdate));

        calendar.add(Calendar.MINUTE, minute_before_event);
        Date startdateminusMinutes = calendar.getTime();
        Log.i("TIME START - MINUTES", String.valueOf(startdateminusMinutes));

        String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        String dateStartMinusMinutes = sdf.format(startdateminusMinutes);
        String dateStart = sdf.format(startdate);


        events.clear();
        Cursor cursor = cupboard().withDatabase(this.database).query(CalendarEvent.class).withSelection("startString >= ? and startString <= ? order by startString asc",  dateStart, dateStartMinusMinutes).getCursor();
        Log.d("Notification", cursor.getCount()+" notifications preloaded");
        // For debug :
        //Cursor cursor = cupboard().withDatabase(this.database).query(CalendarEvent.class).withSelection("startString >= ? order by startString asc", dateStart).getCursor();
        // or we can iterate all results
        Iterable<CalendarEvent> itr = cupboard().withCursor(cursor).iterate(CalendarEvent.class);
        for (CalendarEvent calendarEvent: itr) {
            // do something with book
            events.add(calendarEvent);
        }
    }

    private void createNotification(CalendarEvent event){
        Log.d("Notification", event.getName()+" is notified");
        final NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final Intent launchNotifiactionIntent = new Intent(this, MainActivity.class);
        launchNotifiactionIntent.putExtra("SelectedMenuItem", 1);
        launchNotifiactionIntent.putExtra("SelectedSubMenuItem", 0);
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
            calendar.add(Calendar.HOUR, 1);
            endHour = hdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Notification.Builder builder = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis())
                .setTicker("Titre")
                .setSmallIcon(R.drawable.ic_launcher_transparent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(event.getName()+" : "+event.getRooms())
                .setContentText(startHour+" - "+endHour+" "+event.getId())
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setShowWhen(true);
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setSmallIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        }*/

        notificationId.add(event.getId());
        notificationStartString.add(event.getStartString());
        mNotification.notify(event.getId(), builder.build());
    }
}
