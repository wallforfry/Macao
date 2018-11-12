package fr.esiee.bde.macao;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import fr.esiee.bde.macao.Calendar.CalendarService;
import fr.esiee.bde.macao.Events.EventService;
import fr.esiee.bde.macao.Notifications.NotificationService;
import fr.esiee.bde.macao.Widget.WidgetUpdateService;

/**
 * Created by delevacw on 08/11/17.
 */

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        /*DataBaseHelper dbHelper = new DataBaseHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();*/
        try {
            Log.d("AutoStart", "Start Services");
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            JobInfo jobInfo = new JobInfo.Builder(11, new ComponentName(context, NotificationService.class))
                    // only add if network access is required
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(30 * 1000) // wait at least
                    .setOverrideDeadline(3 * 60 * 1000) // maximum delay
                    .build();

            //jobScheduler.schedule(jobInfo);
            //context.getApplicationContext().startService(new Intent(context.getApplicationContext(), NotificationService.class));

            JobInfo jobInfoCalendar = new JobInfo.Builder(12, new ComponentName(context, CalendarService.class))
                    // only add if network access is required
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(10 * 60 * 1000) // wait at least
                    .setOverrideDeadline(30 * 60 * 1000) // maximum delay
                    .build();

            jobScheduler.schedule(jobInfoCalendar);

            JobInfo jobInfoEvent = new JobInfo.Builder(13, new ComponentName(context, EventService.class))
                    // only add if network access is required
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(10 * 60 * 1000) // wait at least
                    .setOverrideDeadline(30 * 60 * 1000) // maximum delay
                    .build();

            jobScheduler.schedule(jobInfoEvent);


            //context.startService(new Intent(context, CalendarService.class));
            //context.startService(new Intent(context, EventService.class));
            context.startService(new Intent(context, WidgetUpdateService.class));
        } catch (IllegalStateException e){
            Log.e("AutoStart", e.toString());
        }
    }
}
