package fr.esiee.bde.macao.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.esiee.bde.macao.R;

/**
 * Implementation of App Widget functionality.
 */
public class MacaoAppWidget extends AppWidgetProvider {

    public static final String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String WIDGET_INCREMENT_DAY = "android.appwidget.action.APPWIDGET_INCREMENT_DAY";
    public static final String WIDGET_DECREMENT_DAY = "android.appwidget.action.APPWIDGET_DECREMENT_DAY";
    public static final String WIDGET_RESET_DAY= "android.appwidget.action.APPWIDGET_RESET_DAY";

    public static final String EXTRA_ITEM = "fr.esiee.bde.macao.EXTRA_ITEM";

    public static Calendar calendar;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

            Log.i("Widget", "Widget is up to date");
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.macao_app_widget);

            updateDate(context, rv);

            Intent intentUpdate = new Intent(context, MacaoAppWidget.class);
            intentUpdate.setAction(MacaoAppWidget.WIDGET_INCREMENT_DAY);
            int[] idArray = new int[]{appWidgetId};
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
            PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.widget_right_arrow, pendingUpdate);

            intentUpdate = new Intent(context, MacaoAppWidget.class);
            intentUpdate.setAction(MacaoAppWidget.WIDGET_DECREMENT_DAY);
            idArray = new int[]{appWidgetId};
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
            pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.widget_left_arrow, pendingUpdate);

            intentUpdate = new Intent(context, MacaoAppWidget.class);
            intentUpdate.setAction(MacaoAppWidget.WIDGET_RESET_DAY);
            idArray = new int[]{appWidgetId};
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
            pendingUpdate = PendingIntent.getBroadcast(
                    context, appWidgetId, intentUpdate,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.widget_title, pendingUpdate);

            rv.setRemoteAdapter(R.id.listview_widget, intent);

            //rv.setEmptyView(appWidgetId, R.id.widget_empty_view);
            appWidgetManager.updateAppWidget(appWidgetId, rv);
    }


    public void onReceive(Context context, Intent intent) {
        if(calendar == null){
            calendar = Calendar.getInstance();
        }
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if(intent.getAction() != null) {
            switch (intent.getAction()) {
                case UPDATE_MEETING_ACTION: {
                    int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, MacaoAppWidget.class));
                    mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_widget);
                    break;
                }
                case MacaoAppWidget.WIDGET_INCREMENT_DAY: {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.macao_app_widget);
                    updateDate(context, rv);

                    int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, MacaoAppWidget.class));
                    mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_widget);
                    break;
                }
                case MacaoAppWidget.WIDGET_DECREMENT_DAY: {
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.macao_app_widget);
                    updateDate(context, rv);

                    int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, MacaoAppWidget.class));
                    mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_widget);
                    break;
                }
                case MacaoAppWidget.WIDGET_RESET_DAY: {
                    calendar = Calendar.getInstance();
                    RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.macao_app_widget);
                    updateDate(context, rv);

                    int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, MacaoAppWidget.class));
                    mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_widget);
                    break;
                }
            }

        }
        super.onReceive(context, intent);
    }

    private static void updateDate(Context context, RemoteViews rv){
        String DATE_FORMAT = "d MMMM yyyy";
        SimpleDateFormat ddf = new SimpleDateFormat(DATE_FORMAT);
        rv.setTextViewText(R.id.widget_title, ddf.format(calendar.getTime()));
        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, MacaoAppWidget.class), rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if(calendar == null){
            calendar = Calendar.getInstance();
        }
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        if(calendar == null){
            calendar = Calendar.getInstance();
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

