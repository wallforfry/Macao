package fr.esiee.bde.macao.Fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import fr.esiee.bde.macao.Clubs.Club;
import fr.esiee.bde.macao.Clubs.ClubAdapter;
import fr.esiee.bde.macao.HttpUtils;
import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClubsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClubsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Club> clubsList = new ArrayList<Club>();
    private RecyclerView recyclerView;
    private ClubAdapter mAdapter;

    private ProgressBar loader;

    public ClubsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClubsFragment newInstance(String param1, String param2) {
        ClubsFragment fragment = new ClubsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_clubs);

        mAdapter = new ClubAdapter(clubsList, this.getContext());
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager;

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }
        else {
            // Portrait
            mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        }
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        loader = getActivity().findViewById(R.id.loader_view);
        loader.setVisibility(View.VISIBLE);

        getEvents();

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

    private void getEvents(){
        RequestParams rp = new RequestParams();

        HttpUtils.getByUrl("https://bde.esiee.fr/clubs.json", rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // Pull out the first event on the public timeline
                clubsList.clear();
                JSONArray events = null;
                try {
                    events = (JSONArray) timeline.get("Associations");

                    for(int i = 0; i < events.length(); i++) {
                        Club club = new Club();
                        JSONObject clubObject = (JSONObject) events.get(i);
                        try {
                            Log.d("CLUBS BDE", String.valueOf(clubObject.get("title")));
                            club.setTitle(String.valueOf(clubObject.get("title")));
                            club.setId((Integer) clubObject.get("id"));
                            club.setShortcode(String.valueOf(clubObject.get("shortcode")));
                            club.setContent(String.valueOf(clubObject.get("abstract")));
                            club.setEmail(String.valueOf(clubObject.get("email")));
                            if(clubObject.has("logo_url")) {
                                club.setImage(String.valueOf(clubObject.get("logo_url")));
                            }
                            clubsList.add(club);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    events = (JSONArray) timeline.get("Clubs du BDS");

                    for(int i = 0; i < events.length(); i++) {
                        Club club = new Club();
                        JSONObject clubObject = (JSONObject) events.get(i);
                        try {
                            Log.d("CLUBS BDE", String.valueOf(clubObject.get("title")));
                            club.setTitle(String.valueOf(clubObject.get("title")));
                            club.setId((Integer) clubObject.get("id"));
                            club.setShortcode(String.valueOf(clubObject.get("shortcode")));
                            club.setContent(String.valueOf(clubObject.get("abstract")));
                            club.setEmail(String.valueOf(clubObject.get("email")));
                            if(clubObject.has("logo_url")) {
                                club.setImage(String.valueOf(clubObject.get("logo_url")));
                            }
                            clubsList.add(club);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    events = (JSONArray) timeline.get("Clubs du BDE");

                    for(int i = 0; i < events.length(); i++) {
                        Club club = new Club();
                        JSONObject clubObject = (JSONObject) events.get(i);
                        try {
                            Log.d("CLUBS BDE", String.valueOf(clubObject.get("title")));
                            club.setTitle(String.valueOf(clubObject.get("title")));
                            club.setId((Integer) clubObject.get("id"));
                            club.setShortcode(String.valueOf(clubObject.get("shortcode")));
                            club.setContent(String.valueOf(clubObject.get("abstract")));
                            club.setEmail(String.valueOf(clubObject.get("email")));
                            if(clubObject.has("logo_url")) {
                                club.setImage(String.valueOf(clubObject.get("logo_url")));
                            }
                            clubsList.add(club);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
                //mListener.makeSnackBar("Events à jour");
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mListener.makeSnackBar("Oups...");
                loader.setVisibility(View.GONE);
            }
        });
    }

}
