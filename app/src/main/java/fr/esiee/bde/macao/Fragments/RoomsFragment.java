package fr.esiee.bde.macao.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import fr.esiee.bde.macao.DividerItemDecoration;
import fr.esiee.bde.macao.HttpUtils;
import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;
import fr.esiee.bde.macao.Rooms.Room;
import fr.esiee.bde.macao.Rooms.RoomAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Room> roomsList = new ArrayList<Room>();
    private RecyclerView recyclerView;
    private RoomAdapter mAdapter;
    private View view;

    private ProgressBar loader;

    private TextView currentDisplayedTime;
    private int shift;

    public RoomsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoomsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoomsFragment newInstance(String param1, String param2) {
        RoomsFragment fragment = new RoomsFragment();
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
        //Calendar rightNow = Calendar.getInstance();
        //time = rightNow.get(Calendar.HOUR_OF_DAY);
        shift = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rooms, container, false);

        currentDisplayedTime = view.findViewById(R.id.rooms_current_time);

        recyclerView = view.findViewById(R.id.recycler_view_rooms);

        mAdapter = new RoomAdapter(roomsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        loader = getActivity().findViewById(R.id.loader_view);
        loader.setVisibility(View.GONE);

        getRooms(shift);

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

    private void getRooms(int time) {
        loader.setVisibility(View.VISIBLE);
        RequestParams rp = new RequestParams();

        //HttpUtils.getByUrl("https://bde.esiee.fr/api/calendar/rooms/"+time+"h00", rp, new JsonHttpResponseHandler() {
        HttpUtils.getByUrl("https://ade.wallforfry.fr/api/rooms/"+time, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                roomsList.clear();
                Room autre = new Room("", 0);
                Room epi1 = new Room("", 1);
                Room epi2 = new Room("", 2);
                Room epi3 = new Room("", 3);
                Room epi4 = new Room("", 4);
                Room epi5 = new Room("", 5);

                for (int i = 0; i < timeline.length(); i++) {
                    //Log.d("ROOM", "Add " + String.valueOf(i));
                    try {
                        int epi;
                        try {
                            epi = Integer.parseInt(String.valueOf(timeline.get(i)).substring(0, 1));
                        } catch (NumberFormatException e) {
                            epi = 0;
                        }
                        switch (epi) {
                            case 0:
                                if (autre.getRooms().length() == 0) {
                                    autre.setRooms(String.valueOf(timeline.get(i)));
                                } else {
                                    autre.setRooms(autre.getRooms() + ", " + String.valueOf(timeline.get(i)));
                                }
                                break;
                            case 1:
                                if (epi1.getRooms().length() == 0) {
                                    epi1.setRooms(String.valueOf(timeline.get(i)));
                                } else {
                                    epi1.setRooms(epi1.getRooms() + ", " + String.valueOf(timeline.get(i)));
                                }
                                break;
                            case 2:
                                if (epi2.getRooms().length() == 0) {
                                    epi2.setRooms(String.valueOf(timeline.get(i)));
                                } else {
                                    epi2.setRooms(epi2.getRooms() + ", " + String.valueOf(timeline.get(i)));
                                }
                                break;
                            case 3:
                                if (epi3.getRooms().length() == 0) {
                                    epi3.setRooms(String.valueOf(timeline.get(i)));
                                } else {
                                    epi3.setRooms(epi3.getRooms() + ", " + String.valueOf(timeline.get(i)));
                                }
                                break;
                            case 4:
                                if (epi4.getRooms().length() == 0) {
                                    epi4.setRooms(String.valueOf(timeline.get(i)));
                                } else {
                                    epi4.setRooms(epi4.getRooms() + ", " + String.valueOf(timeline.get(i)));
                                }
                                break;
                            case 5:
                                if (epi5.getRooms().length() == 0) {
                                    epi5.setRooms(String.valueOf(timeline.get(i)));
                                } else {
                                    epi5.setRooms(epi5.getRooms() + ", " + String.valueOf(timeline.get(i)));
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                roomsList.add(autre);
                roomsList.add(epi1);
                roomsList.add(epi2);
                roomsList.add(epi3);
                roomsList.add(epi4);
                roomsList.add(epi5);

                mAdapter.notifyDataSetChanged();
                //Log.d("ROOM", "SIZE : " + String.valueOf(roomsList.size()));
                loader.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mListener.makeSnackBar("Oups...");
                loader.setVisibility(View.GONE);
            }
        });

        int time_shifted = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + time;
        currentDisplayedTime.setText("Salles libres à "+time_shifted+"h");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.rooms, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_update_rooms:
                //time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                shift = 0;
                getRooms(shift);
                return true;
            case R.id.action_rooms_plus_hour:
                int now = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                if(now+ shift < 23) {
                    shift++;
                }
                getRooms(shift);
                return true;
            case R.id.action_rooms_minus_hour:
                if(shift > 0) {
                    shift--;
                }
                getRooms(shift);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}