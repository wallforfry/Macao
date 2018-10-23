package fr.esiee.bde.macao.Calendar;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.graphics.Color.parseColor;

/**
 * Created by Wallerand on 06/06/2017.
 */

public class CalendarEvent implements WeekViewDisplayable {
    private Long _id;
    private int eventId;
    private String title, startString,endString, name, rooms, prof, unite;
    private int color;
    private boolean notified;

    public CalendarEvent(){

    }

    public CalendarEvent(int eventId, String title, String startString, String endString, String name){
        this._id = (long) eventId;
        this.eventId = eventId;
        this.title = title;
        this.startString = startString;
        this.endString = endString;
        this.name = name;
        this.notified = false;
    }

    public int getId() {
        return eventId;
    }

    public void setId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartString() {
        return startString;
    }

    public void setStartString(String startString) {
        this.startString = startString;
    }

    public String getEndString() {
        return endString;
    }

    public void setEndString(String endString) {
        this.endString = endString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public int getColor() {
        return color;
    }

    public void setColor() {
        if(name.contains("CTRL")){
            this.color = parseColor("#e74c3c");
        }
        else if(name.contains("TD")){
            this.color = parseColor("#f39c12");
        }
        else if(name.contains("PERS")){
            this.color = parseColor("#95a5a6");
        }
        else if(name.contains("TP")){
            this.color = parseColor("#27ae60");
        }
        else {
            this.color = parseColor("#35a9fb");
        }
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    @Override
    public WeekViewEvent toWeekViewEvent() {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRANCE);
        dateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date start = null;
        Date end = null;
        try {
            start = dateformat.parse(startString);
            end = dateformat.parse(endString);
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, start.getDate());
            startTime.set(Calendar.HOUR_OF_DAY, start.getHours());
            startTime.set(Calendar.MINUTE, start.getMinutes());
            startTime.set(Calendar.MONTH, start.getMonth());
            startTime.set(Calendar.YEAR, start.getYear()+1900);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, end.getHours());
            endTime.set(Calendar.MINUTE, end.getMinutes()-1);
            endTime.set(Calendar.MONTH, end.getMonth());
            endTime.set(Calendar.YEAR, start.getYear()+1900);
            com.alamkanak.weekview.WeekViewEvent event = new com.alamkanak.weekview.WeekViewEvent(_id, title, startTime, endTime);
            if(name.contains("CTRL")){
                event.setColor(parseColor("#e74c3c"));
            }
            else if(name.contains("TD")){
                event.setColor(parseColor("#f39c12"));
            }
            else if(name.contains("PERS")){
                event.setColor(parseColor("#95a5a6"));
            }
            else if(name.contains("TP")){
                event.setColor(parseColor("#27ae60"));
            }
            else {
                event.setColor(parseColor("#35a9fb"));
            }
            return event;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
