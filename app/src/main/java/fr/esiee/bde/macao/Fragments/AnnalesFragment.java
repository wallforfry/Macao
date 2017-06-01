package fr.esiee.bde.macao.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import fr.esiee.bde.macao.HttpUtils;
import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AnnalesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnnalesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnnalesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchAnnale("plop");
        return inflater.inflate(R.layout.fragment_annales, container, false);
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

    public void searchAnnale(String search){
        /*RequestParams rp = new RequestParams();
        /*rp.add("Accept", "application/json");
        rp.add("Content-Type", "application/json");
        rp.add("Authorization", "Bearer " + ((MainActivity) this.getActivity()).getIdToken());*
        rp.add("grant_type", "social_access_token");
        rp.add("social_id", ((MainActivity) this.getActivity()).getIdToken());
        rp.add("social_token", "");
        rp.add("client_id", getResources().getString(R.string.annales_client_id));
        rp.add("client_secret", getResources().getString(R.string.annales_secret));
        //Log.d("TOKEN", ((MainActivity) this.getActivity()).getIdToken());

        //HttpUtils.postByUrl("https://bde.esiee.fr/annales/api/document/search.json", rp, new JsonHttpResponseHandler() {
        HttpUtils.postByUrl("https://bde.esiee.fr/annales/oauth/v2/token/google", rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // Pull out the first event on the public timeline
                mListener.makeSnackBar("SUCCES OBJECT");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                mListener.makeSnackBar("SUCCES ARRAY");
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mListener.makeSnackBar("Oups...");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                mListener.makeSnackBar(String.valueOf(jsonObject));
            }
        });*/
        RequestParams rp = new RequestParams();
        rp.add("idToken", ((MainActivity) this.getActivity()).getIdToken());
        rp.add("grant_type", "social_access_token");
        rp.add("client_id", getResources().getString(R.string.annales_client_id));

        HttpUtils.postByUrl("https://bde.esiee.fr/annales/oauth/v2/token/google", rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject timeline) {
                // Pull out the first event on the public timeline
                mListener.makeSnackBar("SUCCES OBJECT");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                mListener.makeSnackBar("SUCCES ARRAY");
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mListener.makeSnackBar("Oups...");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                Log.d("FAILE",String.valueOf(jsonObject));
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void makeSnackBar(String text);
    }
}
