package fr.esiee.bde.macao.Notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.esiee.bde.macao.AutoStart;
import fr.esiee.bde.macao.Calendar.CalendarEvent;
import fr.esiee.bde.macao.DataBaseHelper;
import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;

import static android.app.Notification.DEFAULT_ALL;
import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by delevacw on 08/11/17.
 */

public class NotificationService extends JobService {

    private static List<Integer> notificationId = new ArrayList<Integer>();
    private static List<String> notificationStartString = new ArrayList<String>();
    private List<CalendarEvent> events = new ArrayList<CalendarEvent>();
    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;

    private static String NOTIFICATION_CHANNEL_NAME = "Agenda";
    private static String NOTIFICATION_CHANNEL_DESCRIPTION = "Notification de l'agenda";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
        doJob();
        //stopSelf();
        //setRestart();
        return START_STICKY;
    }

    private void doJob(){
        createNotificationChannel();
        try {
            dbHelper = new DataBaseHelper(this);
            database = dbHelper.getWritableDatabase();

            //getEvents();

            retrieveEvents();
            Log.i("Notification", "Start");
            for (CalendarEvent event : events) {
                //if ((!notificationId.contains(event.getId())) && notificationStartString.contains(event.getStartString())) {
                if (!notificationId.contains(event.getTitle().hashCode())) {
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
    }
    private void setRestart(){
        // I want to restart this service again in one hour
        Log.d("Notification", "Restart Scheduled");
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 5),
                PendingIntent.getService(this, 0, new Intent(this, AutoStart.class), 0)
                //PendingIntent.getService(this, 0, new Intent(this, NotificationService.class), 0)
        );
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        super.onTaskRemoved(rootIntent);
        //startService(new Intent(this, NotificationService.class));
        //sendBroadcast(new Intent(this, AutoStart.class));
    }

    /*@Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/

    @Override
    public boolean onStartJob(JobParameters params) {
        doJob();
        //setRestart();
        sendBroadcast(new Intent(this, AutoStart.class));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        //sendBroadcast(new Intent(this, AutoStart.class));
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        setRestart();
        //sendBroadcast(new Intent(this, AutoStart.class));
        //startService(new Intent(this, NotificationService.class));
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
                .setTicker(event.getName())
                .setSmallIcon(R.drawable.fuego_notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(event.getName()+" : "+event.getUnite())
                .setContentText(startHour+" - "+endHour)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.BigTextStyle().bigText(startHour+" - "+endHour+"\n"+event.getRooms()+"\n"+event.getProf()))
                .setDefaults(Notification.DEFAULT_ALL)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_NAME);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setColor(getColor(R.color.colorPrimary));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(getResources().getColor(R.color.colorPrimary));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setShowWhen(true);
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setSmallIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        }*/

        notificationId.add(event.getTitle().hashCode());
        notificationStartString.add(event.getStartString());
        mNotification.notify(event.getId(), builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = NOTIFICATION_CHANNEL_NAME;
            String description = NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_NAME, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
