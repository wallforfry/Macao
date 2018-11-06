package fr.esiee.bde.macao.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lusfold.spinnerloading.SpinnerLoading;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import fr.esiee.bde.macao.Annales.Annale;
import fr.esiee.bde.macao.Annales.AnnaleAdapter;
import fr.esiee.bde.macao.DividerItemDecoration;
import fr.esiee.bde.macao.Events.EventAdapter;
import fr.esiee.bde.macao.HttpUtils;
import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnnalesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnalesFragment extends Fragment implements AnnaleAdapter.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Annale> annalesList = new ArrayList<Annale>();
    private RecyclerView recyclerView;
    private AnnaleAdapter mAdapter;
    private static String annalesToken;

    private MaterialSearchView searchView;
    private WebView webView;
    private MenuItem back;

    private ProgressBar loader;

    public AnnalesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnnalesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnalesFragment newInstance(String param1, String param2) {
        AnnalesFragment fragment = new AnnalesFragment();
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
        annalesSignin();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_annales, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_annales);

        mAdapter = new AnnaleAdapter(annalesList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        searchView = (MaterialSearchView) this.getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                searchAnnale(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        webView = (WebView) view.findViewById(R.id.annales_webview);
        //webView.setWebViewClient(new annalesWebView());
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);

        webView.setWebViewClient( new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsById('left-menu')[0].style.width=\"0px\";" +
                        "})()");
                loader.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });

        loader = (ProgressBar) getActivity().findViewById(R.id.loader_view);
        loader.setVisibility(View.VISIBLE);

        annalesSignin();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.annales, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        back = menu.findItem(R.id.annale_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.annale_back:
                hideAnnale();
        }
        return true;
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

    public void annalesSignin(){
        RequestParams rp = new RequestParams();
        rp.add("grant_type", "social_access_token");
        rp.add("social_id", ((MainActivity) this.getActivity()).getId());
        rp.add("social_token", ((MainActivity) this.getActivity()).getIdToken());
        rp.add("client_id", getResources().getString(R.string.annales_client_id));
        rp.add("client_secret", getResources().getString(R.string.annales_secret));

        HttpUtils.postByUrl("https://bde.esiee.fr/annales/oauth/v2/token/google", rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // Pull out the first event on the public timeline
                try {
                    annalesToken = String.valueOf(timeline.get("access_token"));
                    searchAnnale("habib");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                Log.d("ANNALES ERROR",String.valueOf(jsonObject));
            }
        });
    }

    public void searchAnnale(String search){
        loader.setVisibility(View.VISIBLE);
        RequestParams rp = new RequestParams();
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Accept", "application/json");
        header.put("Content-Type", "application/json");
        header.put("Authorization", "Bearer "+ this.annalesToken);
        HttpUtils.getByUrl("https://bde.esiee.fr/annales/api/document/search.json?s="+search+"&page=1", header, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // Pull out the first event on the public timeline
                try {
                    JSONArray documents = (JSONArray) timeline.get("documents");
                    annalesList.clear();
                    for(int i = 0; i < documents.length(); i++){
                        Annale annale = new Annale();
                        JSONObject annaleJson = (JSONObject) documents.get(i);
                        annale.setId((Integer) annaleJson.get("id"));
                        annale.setPromo((String) annaleJson.get("class"));
                        annale.setField((String) annaleJson.get("field"));
                        annale.setUnit((String) annaleJson.get("unit"));
                        annale.setYear((String) annaleJson.get("year"));
                        annale.setTeacher((String) annaleJson.get("teacher"));
                        annale.setSubject((String) annaleJson.get("subject"));
                        annalesList.add(annale);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("ANNALE S", String.valueOf(timeline));
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                Log.d("ERROR", String.valueOf(jsonObject));
            }
        });
    }

    public void fetchAnnale(int id){
        loader.setVisibility(View.VISIBLE);
        RequestParams rp = new RequestParams();
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Accept", "application/json");
        header.put("Content-Type", "application/json");
        header.put("Authorization", "Bearer "+ annalesToken);
        HttpUtils.getByUrl("https://bde.esiee.fr/annales/api/document/document/"+id, header, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // Pull out the first event on the public timeline
                try {
                    JSONObject document = (JSONObject) timeline.get("document");
                    JSONArray files = (JSONArray) document.get("files");
                    JSONObject file = (JSONObject) files.get(0);
                    String url = "https://docs.google.com/gview?url=https://bde.esiee.fr"+file.get("download_path")+"&embedded=true";
                    displayAnnale(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                Log.d("ERROR", String.valueOf(jsonObject));
            }
        });
    }

    private void displayAnnale(String url){
        webView.loadUrl(url);
        back.setVisible(true);
    }

    private void hideAnnale(){
        webView.setVisibility(View.GONE);
        back.setVisible(false);
    }

}
