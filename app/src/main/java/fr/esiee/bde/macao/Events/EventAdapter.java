package fr.esiee.bde.macao.Events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import fr.esiee.bde.macao.R;

/**
 * Created by Wallerand on 01/06/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private List<Event> eventsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView day, date, month, title, time, place;
        private Button link;
        private ImageView image;
        private RelativeLayout place_layout;

        private String url;

        public MyViewHolder(View view) {
            super(view);
            day = (TextView) view.findViewById(R.id.event_day);
            date = (TextView) view.findViewById(R.id.event_date);
            month = (TextView) view.findViewById(R.id.event_month);
            title = (TextView) view.findViewById(R.id.event_title);
            time = (TextView) view.findViewById(R.id.event_time);
            place = (TextView) view.findViewById(R.id.event_place);
            image = (ImageView) view.findViewById(R.id.event_image);
            place_layout = (RelativeLayout) view.findViewById(R.id.event_place_layout);
            link = (Button) view.findViewById(R.id.event_link);

            link.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(this.url));
            context.startActivity(i);
        }

        public void setUrl(String url){
            this.url = url;
        }
    }


    public EventAdapter(List<Event> eventsList, Context context) {
        this.eventsList = eventsList;
        this.context = context;
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
        if(!event.getPlace().equals("")) {
            holder.place.setText(event.getPlace());
        }
        else{
            holder.place_layout.setVisibility(View.GONE);
        }
        holder.setUrl(event.getUrl());
        Picasso.with(context).load(event.getImage()).centerCrop().into(holder.image);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
