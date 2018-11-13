package fr.esiee.bde.macao.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import fr.esiee.bde.macao.HttpUtils;
import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.Jobs.Jobs;
import fr.esiee.bde.macao.Jobs.JobsAdapter;
import fr.esiee.bde.macao.R;


public class FoundObjectsFragment extends Fragment {


    private List<Jobs> jobsList = new ArrayList<Jobs>();
    private RecyclerView recyclerView;
    private JobsAdapter mAdapter;
    private View view;

    private ProgressBar loader;

    private OnFragmentInteractionListener mListener;

    public FoundObjectsFragment() {
        // Required empty public constructor
    }

    public static JobsFragment newInstance() {
        JobsFragment fragment = new JobsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_found_objects, container, false);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new Main2Activity.SectionsPagerAdapter(getSupportFragmentManager());
*/

        // Set up the ViewPager with the sections adapter.
        final ViewPager mViewPager = (ViewPager) view.findViewById(R.id.tab_container);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return false;
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        //TabLayout.Tab tabs = view.findViewById(R.id.)
        /*recyclerView = view.findViewById(R.id.recycler_view_found_objects);

        mAdapter = new JobsAdapter(jobsList, this.getContext());
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

        getJobs();*/

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

    private void getJobs(){
        RequestParams rp = new RequestParams();

        HttpUtils.getByUrl("https://bde.esiee.fr/objets-trouves.json", rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                for(int i = 0; i < response.length(); i++) {
                    try{
                        Log.d("JOBS", response.get(i).toString());
                        JSONObject obj = (JSONObject) response.get(i);
                        Jobs job = new Jobs(i, obj.get("title").toString(), obj.get("slug").toString(), obj.get("category").toString(), obj.get("color").toString(), obj.get("name").toString(), obj.get("email").toString(), obj.get("telephone").toString(), obj.get("content").toString());
                        jobsList.add(job);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mAdapter.notifyDataSetChanged();
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
