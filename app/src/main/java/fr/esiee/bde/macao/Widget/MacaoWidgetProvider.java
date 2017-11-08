package fr.esiee.bde.macao.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.esiee.bde.macao.R;

/**
 * Created by delevacw on 07/11/17.
 */

public class MacaoWidgetProvider extends AppWidgetProvider{

    public static final String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

    public static final String EXTRA_ITEM = "fr.esiee.bde.macao.EXTRA_ITEM";


    public void onReceive(Context context, Intent intent) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(UPDATE_MEETING_ACTION)) {
            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context,MacaoWidgetProvider.class));
            Log.e("received", intent.getAction());
            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_widget);
        }
        super.onReceive(context, intent);
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.e("WIDGET", "UPDATE");

        for (int i = 0; i < appWidgetIds.length; i++) {

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);


            String DATE_FORMAT = "d MMMM yyyy";
            SimpleDateFormat ddf = new SimpleDateFormat(DATE_FORMAT);
            rv.setTextViewText(R.id.widget_title, ddf.format(Calendar.getInstance().getTime()));

            rv.setRemoteAdapter(appWidgetIds[i], R.id.listview_widget, intent);

            //rv.setEmptyView(R.id.list, R.id.empty_view);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

            Log.e("WIDGET", "UPDATE "+i);
        }

    }
}
