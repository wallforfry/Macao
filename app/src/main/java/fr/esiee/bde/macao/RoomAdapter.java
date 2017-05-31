package fr.esiee.bde.macao;

/**
 * Created by Wallerand on 31/05/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {

    private List<Room> roomsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView numEpi, rooms;

        public MyViewHolder(View view) {
            super(view);
            numEpi = (TextView) view.findViewById(R.id.numeroEpi);
            rooms = (TextView) view.findViewById(R.id.rooms);
        }
    }


    public RoomAdapter(List<Room> roomsList) {
        this.roomsList = roomsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Room room = roomsList.get(position);
        holder.numEpi.setText(String.valueOf(room.getEpi()));
        holder.rooms.setText(room.getRooms());
    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }
}