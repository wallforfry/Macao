package fr.esiee.bde.macao.Widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fr.esiee.bde.macao.Calendar.CalendarEvent;
import fr.esiee.bde.macao.DataBaseHelper;
import fr.esiee.bde.macao.R;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by delevacw on 07/11/17.
 */

class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;

        private ArrayList<CalendarEvent> events;
        private DataBaseHelper dbHelper;
        private SQLiteDatabase database;



        ListViewRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            dbHelper =  new DataBaseHelper(mContext);
            database = dbHelper.getWritableDatabase();
        }

        // Initialize the data set.

        public void onCreate() {

            // In onCreate() you set up any connections / cursors to your data source. Heavy lifting,
            // for example downloading or creating content etc, should be deferred to onDataSetChanged()
            // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

            events=new ArrayList<CalendarEvent>();
            retrieveEvents();
        }
        // Given the position (index) of a WidgetItem in the array, use the item's text value in
        // combination with the app widget item XML file to construct a RemoteViews object.

        public RemoteViews getViewAt(int position) {
            // position will always range from 0 to getCount() - 1.
            // Construct a RemoteViews item based on the app widget item XML file, and set the
            // text based on the position.

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_row);

            // feed row
            Calendar calendar = Calendar.getInstance();

            String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);

            String HOUR_FORMAT = "HH:mm";
            SimpleDateFormat hdf = new SimpleDateFormat(HOUR_FORMAT);

            String data = events.get(position).getName();
            String room = events.get(position).getRooms();
            String startTime = events.get(position).getStartString();
            String endTime = events.get(position).getEndString();
            String prof = events.get(position).getProf();

            TimeZone timezone = TimeZone.getTimeZone("CET");

            try {
                calendar.setTime(sdf.parse(startTime));
                long offSet = timezone.getOffset(calendar.getTimeInMillis());
                calendar.add(Calendar.MILLISECOND, (int) offSet);
                startTime = hdf.format(calendar.getTime());

                calendar.setTime(sdf.parse(endTime));
                calendar.add(Calendar.MILLISECOND, (int) offSet);
                endTime = hdf.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            rv.setTextViewText(R.id.widget_item_text, data);
            rv.setTextViewText(R.id.widget_prof, prof);
            rv.setTextViewText(R.id.widget_room, room);
            rv.setTextViewText(R.id.widget_starttime, startTime);
            rv.setTextViewText(R.id.widget_endtime, endTime);

            rv.setInt(R.id.widget_recycler, "setBackgroundColor", events.get(position).getColor());
            // end feed row
            // Next, set a fill-intent, which will be used to fill in the pending intent template
            // that is set on the collection view in ListViewWidgetProvider.
            Bundle extras = new Bundle();
            extras.putInt(MacaoAppWidget.EXTRA_ITEM, position);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra("homescreen_meeting",data);
            fillInIntent.putExtras(extras);
            // Make it possible to distinguish the individual on-click
            // action of a given item
            //rv.setOnClickFillInIntent(R.id.item_layout, fillInIntent);
            // Return the RemoteViews object.
            return rv;

        }

        public int getCount(){

            Log.d("size=",events.size()+"");

            return events.size();

        }

        public void onDataSetChanged(){
            // Fetching JSON data from server and add them to records arraylist
            retrieveEvents();
        }

        public int getViewTypeCount(){
            return 1;
        }

        public long getItemId(int position) {
            return position;
        }

        public void onDestroy(){
            events.clear();
        }

        public boolean hasStableIds() {
            return true;
        }

        public RemoteViews getLoadingView() {
            return null;
        }

    private void retrieveEvents(){
        Calendar calendar;
        try {
            calendar = MacaoAppWidget.calendar;
        } catch (NullPointerException e) {
            calendar = Calendar.getInstance();
            MacaoAppWidget.calendar = calendar;
        }

        Log.d("RV", calendar.getTime().toString());
        //calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date today = calendar.getTime();
        Calendar calendar_tomorrow = (Calendar) calendar.clone();
        calendar_tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar_tomorrow.getTime();

        String ISO_FORMAT = "yyyy-MM-dd'T00:00:00.000Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        String dateStart = sdf.format(today);
        String dateEnd = sdf.format(tomorrow);


        events.clear();
        //CalendarEvent calendarEvent = cupboard().withDatabase(database).query(CalendarEvent.class).get();
        Cursor cursor = cupboard().withDatabase(this.database).query(CalendarEvent.class).withSelection("startString >= ? and endString < ? order by startString asc", dateStart, dateEnd).getCursor();
        //Cursor cursor = cupboard().withDatabase(this.database).query(CalendarEvent.class).getCursor();
        // or we can iterate all results
        Iterable<CalendarEvent> itr = cupboard().withCursor(cursor).iterate(CalendarEvent.class);
        for (CalendarEvent calendarEvent: itr) {
            // do something with book
            events.add(calendarEvent);
        }
    }
}
