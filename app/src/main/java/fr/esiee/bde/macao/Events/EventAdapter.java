package fr.esiee.bde.macao.Events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.esiee.bde.macao.R;

/**
 * Created by Wallerand on 01/06/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<Event> eventsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView day, date, month, title, time, place;

        public MyViewHolder(View view) {
            super(view);
            day = (TextView) view.findViewById(R.id.event_day);
            date = (TextView) view.findViewById(R.id.event_date);
            month = (TextView) view.findViewById(R.id.event_month);
            title = (TextView) view.findViewById(R.id.event_title);
            time = (TextView) view.findViewById(R.id.event_time);
            place = (TextView) view.findViewById(R.id.event_place);
        }
    }


    public EventAdapter(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    @Override
    public EventAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row, parent, false);

        return new EventAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventAdapter.MyViewHolder holder, int position) {
        Event event = eventsList.get(position);
        holder.day.setText(event.getDayStart());
        holder.date.setText(event.getDateStart());
        holder.month.setText(event.getMonthStart());
        holder.title.setText(event.getTitle());
        holder.time.setText(event.getTimeString());
        holder.place.setText(event.getPlace());
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
