package fr.esiee.bde.macao.Widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by delevacw on 13/11/17.
 */

public class WidgetUpdateService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("WidgetUpdateService", "Widget Update Started");
        Intent intent_meeting_update=new  Intent(this,MacaoAppWidget.class);
        intent_meeting_update.setAction(MacaoAppWidget.UPDATE_MEETING_ACTION);
        sendBroadcast(intent_meeting_update);

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
                System.currentTimeMillis() + (1000 * 60 * 15),
                PendingIntent.getService(this, 0, new Intent(this, WidgetUpdateService.class), 0)
        );
    }
}
