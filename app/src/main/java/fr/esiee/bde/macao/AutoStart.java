package fr.esiee.bde.macao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
            context.startService(new Intent(context, CalendarService.class));
            context.startService(new Intent(context, EventService.class));
            context.startService(new Intent(context, NotificationService.class));
            context.startService(new Intent(context, WidgetUpdateService.class));
        } catch (IllegalStateException e){
            Log.e("AutoStart", e.toString());
        }
    }
}
