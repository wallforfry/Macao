package fr.esiee.bde.macao.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lusfold.spinnerloading.SpinnerLoading;

import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.MainActivity;
import fr.esiee.bde.macao.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView mStatusTextView;
    private View view;

    private SpinnerLoading loader;

    public SignInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
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
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // Button listeners
        view.findViewById(R.id.sign_in_button).setOnClickListener(this);
        view.findViewById(R.id.sign_out_button).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);

        mStatusTextView = (TextView) view.findViewById(R.id.status);

        loader = (SpinnerLoading) getActivity().findViewById(R.id.loader_view);
        loader.setPaintMode(1);
        loader.setCircleRadius(20);
        loader.setItemCount(8);
        loader.setVisibility(View.GONE);

        this.connectUser(((MainActivity) this.getActivity()).isSignedIn());

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

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.sign_in_button:
                ((MainActivity) this.getActivity()).signIn();
                break;
            case R.id.sign_out_button:
                ((MainActivity) this.getActivity()).signOut();
                break;
            /*case R.id.disconnect_button:
                revokeAccess();
                break;*/
        }
    }


    public void connectUser(boolean signedIn){
        if (signedIn) {
            view.findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            view.findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            view.findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
        loader.setVisibility(View.GONE);
    }
}
