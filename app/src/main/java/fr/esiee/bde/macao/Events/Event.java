package fr.esiee.bde.macao.Events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Wallerand on 01/06/2017.
 */

public class Event {
    private String title, start, end, place;

    public Event(){

    }

    public Event(String title, String start, String end, String place){
        this.title = title;
        this.start = start;
        this.end = end;
        this.place = place;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDayStart(){
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE);
        SimpleDateFormat dayformat = new SimpleDateFormat("E");
        try {
            calendar.setTime(dateformat.parse(this.start));
            return dayformat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDateStart(){
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE);
        Date datestart = null;
        try {
            datestart = dateformat.parse(this.start);
            return String.valueOf(datestart.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getMonthStart(){
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE);
        SimpleDateFormat monthformat = new SimpleDateFormat("MMMM", Locale.FRANCE);
        try {
            calendar.setTime(dateformat.parse(this.start));
            return monthformat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getHourStart(){
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE);
        SimpleDateFormat hoursformat = new SimpleDateFormat("H'h'm");
        try {
            calendar.setTime(dateformat.parse(this.start));
            return hoursformat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDayEnd(){
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE);
        SimpleDateFormat dayformat = new SimpleDateFormat("E");
        try {
            calendar.setTime(dateformat.parse(this.end));
            return dayformat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDateEnd(){
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE);
        Date dateend = null;
        try {
            dateend = dateformat.parse(this.end);
            return String.valueOf(dateend.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getMonthEnd(){
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE);
        SimpleDateFormat monthformat = new SimpleDateFormat("MMMM", Locale.FRANCE);
        try {
            calendar.setTime(dateformat.parse(this.end));
            return monthformat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getHourEnd(){
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE);
        SimpleDateFormat hoursformat = new SimpleDateFormat("H'h'm");
        try {
            calendar.setTime(dateformat.parse(this.end));
            return hoursformat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getTimeString(){
        if(getDateStart().equals(getDateEnd())){
            return "Le "+getDateStart()+" "+getMonthStart()+" de "+getHourStart()+" à "+getHourEnd();
        }
        else {
            return "Du "+getDateStart()+" "+getMonthStart()+" "+getHourStart()+" au "+getDateEnd()+" "+getMonthEnd()+" "+getHourEnd();
        }
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
