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
    public Long _id;
    private int id;
    private String title, start, end, place = "", image = "https://bde.esiee.fr/bundles/applicationbde/img/couverture.png", content, slug, publicationDate;
    private boolean notified = false;

    public Event(){

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getUrl() {
        String url = "https://bde.esiee.fr/news/";

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.FRANCE);
        Date date = null;
        try {
            date = dateformat.parse(this.publicationDate);
            url += String.valueOf(date.getYear()+1900)+"/"+String.valueOf(date.getMonth()+1)+"/"+String.valueOf(date.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        url += "/"+this.slug;
        return url;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}
