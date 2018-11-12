package fr.esiee.bde.macao.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alamkanak.weekview.data.MonthLoader;
import com.alamkanak.weekview.listeners.EmptyViewLongPressListener;
import com.alamkanak.weekview.listeners.EventClickListener;
import com.alamkanak.weekview.listeners.EventLongPressListener;
import com.alamkanak.weekview.model.WeekViewDisplayable;
import com.alamkanak.weekview.model.WeekViewEvent;
import com.alamkanak.weekview.ui.WeekView;
import com.alamkanak.weekview.utils.DateTimeInterpreter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.esiee.bde.macao.Calendar.CalendarEvent;
import fr.esiee.bde.macao.Calendar.CalendarService;
import fr.esiee.bde.macao.DataBaseHelper;
import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.R;
import me.drakeet.materialdialog.MaterialDialog;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements EventClickListener<CalendarEvent>, MonthLoader.MonthChangeListener, EventLongPressListener<CalendarEvent>, EmptyViewLongPressListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    private View view;
    private ProgressBar loader;

    private List<WeekViewDisplayable> events = new ArrayList<WeekViewDisplayable>();

    private DataBaseHelper dbHelper;
    private SQLiteDatabase database;

    private Calendar mCalendar;

    private static int monthTriggered = 0;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        dbHelper =  new DataBaseHelper(this.getContext());
        database = dbHelper.getWritableDatabase();

        mCalendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        // Get a reference for the week view in the layout.
        mWeekView = view.findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(true);

        loader = getActivity().findViewById(R.id.loader_view);
        loader.setVisibility(View.GONE);

        //getGroups();
        //retrieveEvents(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH));
        //getEvents();

        monthTriggered = 0;
        events.clear();
        mWeekView.goToToday();
        mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        getActivity().startService(new Intent(this.getContext(), CalendarService.class));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.calendar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                monthTriggered = 0;
                events.clear();
                mWeekView.goToToday();
                mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(6);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_update_events:
                monthTriggered = 0;
                events.clear();
                //retrieveEvents(mCalendar, mCalendar);
                mWeekView.goToToday();
                mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                getActivity().startService(new Intent(this.getContext(), CalendarService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" d/M", Locale.getDefault());
                //if (shortDate)
                //    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour + "h";
            }

        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(CalendarEvent event, RectF eventRect) {
        //Toast.makeText(this.getActivity().getApplicationContext(), "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
        openDialog(event);
    }

    @Override
    public void onEventLongPress(CalendarEvent event, RectF eventRect) {
        //Toast.makeText(this.getActivity().getApplicationContext(), "Long pressed event: " + event.toWeekViewEvent().getData().getName(), Toast.LENGTH_SHORT).show();
        final Snackbar snack = Snackbar.make(view.getRootView(), "Merci d'avoir demandé à la planif de retirer ce cours. Ils vous recontacteront ultérieurement", Snackbar.LENGTH_INDEFINITE);
        snack.setAction("Cacher", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snack.dismiss();
                }
            });
        ((TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text)).setMaxLines(5);
        snack.show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        //Toast.makeText(this.getActivity().getApplicationContext(), "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this.getActivity().getApplicationContext(), "Ceci n'est pas un EasterEgg", Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    private List<WeekViewDisplayable> retrieveEvents(Calendar startDate, Calendar endDate){
        List<WeekViewDisplayable> matchedEvents = new ArrayList<WeekViewDisplayable>();
        //CalendarEvent calendarEvent = cupboard().withDatabase(database).query(CalendarEvent.class).get();
        Cursor cursor = cupboard().withDatabase(database).query(CalendarEvent.class).getCursor();
        // or we can iterate all results
        Iterable<CalendarEvent> itr = cupboard().withCursor(cursor).iterate(CalendarEvent.class);
        for (CalendarEvent calendarEvent: itr) {
            // do something with book
            //if (eventMatches(calendarEvent.toWeekViewEvent(), startDate, endDate)) {
                WeekViewEvent event = calendarEvent.toWeekViewEvent();
                matchedEvents.add(event);
            //}
        }
        return matchedEvents;
    }

    private boolean eventMatches(WeekViewEvent event, Calendar startDate, Calendar endDate) {
        //noinspection WrongConstant

        return (event.getStartTime().get(Calendar.YEAR) == startDate.get(Calendar.YEAR)
                && event.getStartTime().get(Calendar.MONTH) == startDate.get(Calendar.MONTH))
                && (event.getStartTime().get(Calendar.DAY_OF_MONTH) >= startDate.get(Calendar.DAY_OF_MONTH)
                || event.getStartTime().get(Calendar.DAY_OF_MONTH) <= endDate.get(Calendar.DAY_OF_MONTH));
    }

    private void openDialog(CalendarEvent event){
        int startMinuteValue = event.toWeekViewEvent().getStartTime().get(Calendar.MINUTE);
        String startMinute = String.valueOf(startMinuteValue);
        if(startMinuteValue < 10)
            startMinute = "0"+startMinuteValue;

        int endMinuteValue = event.toWeekViewEvent().getEndTime().get(Calendar.MINUTE);
        String endMinute = String.valueOf(endMinuteValue);
        if(endMinuteValue < 10)
            endMinute = "0"+endMinuteValue;
        else if (endMinuteValue == 59){
            endMinute = "00";
        }
        final MaterialDialog mMaterialDialog = new MaterialDialog(this.getContext());
                mMaterialDialog
                        .setTitle("De "+event.toWeekViewEvent().getStartTime().get(Calendar.HOUR_OF_DAY)+"h"+startMinute+" à "+event.toWeekViewEvent().getEndTime().get(Calendar.HOUR_OF_DAY)+"h"+endMinute)
                        .setMessage(event.getName()+"\n"+event.getRooms()+"\n"+event.getProf()+"\n"+event.getUnite())
                        .setPositiveButton("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        })
                        .setNegativeButton("", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });

        mMaterialDialog.show();
    }

    @Override
    public List<WeekViewDisplayable> onMonthChange(Calendar startDate, Calendar endDate) {
        List<WeekViewDisplayable> e = retrieveEvents(startDate, endDate);

        if(monthTriggered == 0){
            events.addAll(e);
            monthTriggered++;
            return events;
        }

        return new ArrayList<WeekViewDisplayable>();
    }
}

