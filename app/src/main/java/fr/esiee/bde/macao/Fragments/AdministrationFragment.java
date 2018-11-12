package fr.esiee.bde.macao.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import fr.esiee.bde.macao.BuildConfig;
import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class AdministrationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Administration";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View view;
    private OkHttpClient client;

    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public AdministrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdministrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdministrationFragment newInstance(String param1, String param2) {
        AdministrationFragment fragment = new AdministrationFragment();
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

        client = new OkHttpClient();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_administration, container, false);

        FloatingActionButton fab = view.findViewById(R.id.administration_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openNotificationDialog();
            }
        });

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

    private void openNotificationDialog(){
        final LayoutInflater factory = LayoutInflater.from(getContext());
        final View dialogView = factory.inflate(R.layout.notification_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Nouvelle notification");
        builder.setView(dialogView);
        builder.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String title = ((EditText) dialogView.findViewById(R.id.administration_notification_dialog_title)).getText().toString();
                final String body = ((EditText) dialogView.findViewById(R.id.administration_notification_dialog_message)).getText().toString();
                final String topic = ((Spinner) dialogView.findViewById(R.id.administration_notification_dialog_topic)).getSelectedItem().toString();

                final AlertDialog.Builder confirmation = new AlertDialog.Builder(getActivity());
                confirmation.setTitle("Confirmer l'envoi");
                confirmation.setMessage("Titre : "+title+"\nMessage : "+body+"\nTopic : "+topic);
                confirmation.setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendMessage(title, body, topic, null);
                    }
                });
                confirmation.setNegativeButton("Annuler", null);
                confirmation.show();
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    private void sendMessage(final String title, final String body, final String topic, final String icon) {

            try {
                JSONObject root = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("title", title);
                notification.put("body", body);
                //notification.put("icon", icon);

                JSONObject data = new JSONObject();
                //data.put("message", message);
                root.put("to", "/topics/"+topic);
                root.put("notification", notification);
                //root.put("data", data);
                //root.put("registration_ids", recipients);

                postToFCM(root.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Snackbar.make(getView(), "L'envoi a échoué", Snackbar.LENGTH_SHORT).show();
                        Log.d(TAG, "Error");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, response.body().string());
                        Snackbar.make(getView(), "Notification envoyée", Snackbar.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }


    }

    private Call postToFCM(String bodyString, Callback callback) throws IOException {
        String SERVER_KEY = BuildConfig.ApiKey;

        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
