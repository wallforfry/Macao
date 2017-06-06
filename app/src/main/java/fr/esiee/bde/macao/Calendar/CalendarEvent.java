package fr.esiee.bde.macao.Calendar;

/**
 * Created by Wallerand on 06/06/2017.
 */

public class CalendarEvent {
    private int id;
    private String title, startString,endString, name;

    public CalendarEvent(){

    }

    public CalendarEvent(int id, String title, String startString, String endString, String name){
        this.id = id;
        this.title = title;
        this.startString = startString;
        this.endString = endString;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
