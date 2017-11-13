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
 * Implementation of App Widget functionality.
 */
public class MacaoAppWidget extends AppWidgetProvider {

    public static final String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

    public static final String EXTRA_ITEM = "fr.esiee.bde.macao.EXTRA_ITEM";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

            Log.i("Widget", "Widget is up to date");
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.macao_app_widget);


            String DATE_FORMAT = "d MMMM yyyy";
            SimpleDateFormat ddf = new SimpleDateFormat(DATE_FORMAT);
            rv.setTextViewText(R.id.widget_title, ddf.format(Calendar.getInstance().getTime()));

            rv.setRemoteAdapter(appWidgetId, R.id.listview_widget, intent);

            //rv.setEmptyView(R.id.list, R.id.empty_view);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
    }


    public void onReceive(Context context, Intent intent) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(UPDATE_MEETING_ACTION)) {
            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context,MacaoAppWidget.class));
            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_widget);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

