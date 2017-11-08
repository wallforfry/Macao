package fr.esiee.bde.macao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;

import fr.esiee.bde.macao.Calendar.CalendarService;
import fr.esiee.bde.macao.Events.EventService;
import fr.esiee.bde.macao.Notifications.NotificationService;

/**
 * Created by delevacw on 08/11/17.
 */

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationService.class));
        context.startService(new Intent(context, CalendarService.class));
        context.startService(new Intent(context, EventService.class));
    }
}
