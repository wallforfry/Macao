package fr.esiee.bde.macao.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.graphics.Color.parseColor;

/**
 * Created by delevacw on 07/11/17.
 */

public class WeekViewEvent {

    public static com.alamkanak.weekview.WeekViewEvent createWeekViewEvent(int id, String title, String startString, String endString, String name){
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
            com.alamkanak.weekview.WeekViewEvent event = new com.alamkanak.weekview.WeekViewEvent(id, title, startTime, endTime);
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
