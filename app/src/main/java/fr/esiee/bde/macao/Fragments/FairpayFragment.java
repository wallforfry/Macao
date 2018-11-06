package fr.esiee.bde.macao.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import fr.esiee.bde.macao.HttpUtils;
import fr.esiee.bde.macao.Interfaces.OnFragmentInteractionListener;
import fr.esiee.bde.macao.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FairpayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FairpayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView name;
    private TextView balance;
    private ImageView pictureStudent;
    private ImageView barcodeImage;
    private TextView id;


    private ProgressBar loader;

    private String pictureBaseUrl = "https://bde.esiee.fr/fairpay/api/students/photo/by-email/";

    public FairpayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FairpayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FairpayFragment newInstance(String param1, String param2) {
        FairpayFragment fragment = new FairpayFragment();
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
        View view = inflater.inflate(R.layout.fragment_fairpay, container, false);

        name = view.findViewById(R.id.fairpayUserName);
        balance = view.findViewById(R.id.fairpayUserBalance);
        pictureStudent = view.findViewById(R.id.fairpayUserImage);
        barcodeImage = view.findViewById(R.id.fairpayBarcode);
        id = view.findViewById(R.id.fairpayUserId);

        loader = getActivity().findViewById(R.id.loader_view);
        loader.setVisibility(View.GONE);

        getStudentInfo();

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


    private void getStudentInfo(){

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String mail = sharedPref.getString("mail", "");
        if(mail.equals("")){
            Log.e("Agenda", "Non connecté");
            name.setText("Tu dois te connecter pour accéder à FairPay");
            loader.setVisibility(View.GONE);
        }
        else {
            loader.setVisibility(View.VISIBLE);
            Log.i("Agenda", "Maj de la balance fairpay");
            RequestParams rp = new RequestParams();
            //rp.add("mail", mail);

            HttpUtils.getByUrl("https://bde.esiee.fr/fairpay/api/students/"+mail, rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    Log.d("asd", "---------------- this is response : " + response);
                    try {
                        JSONObject serverResp = new JSONObject(response.toString());
                        if((boolean) serverResp.get("has_fairpay")) {
                            getStudentBalance(serverResp);
                        }
                        else{
                            //Pas de compte fairpay
                            setValue(serverResp, null);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.i("Agenda", "FairPay à jour");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("Failed: ", ""+statusCode);
                    Log.d("Error : ", "" + throwable);
                }

            });
        }
    }

    private void getStudentBalance(final JSONObject studentInfo) throws JSONException {
        Log.i("Agenda", "Maj de la balance fairpay");
        RequestParams rp = new RequestParams();
        //rp.add("mail", mail);

        HttpUtils.getByUrl("https://bde.esiee.fr/fairpay/api/student/balance?client_id="+studentInfo.get("id"), rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("asd", "---------------- this is response : " + response);
                try {
                    JSONObject balance = new JSONObject(response.toString());
                    setValue(studentInfo, balance);
                    loader.setVisibility(View.GONE);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.i("Agenda", "FairPay à jour");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Failed: ", ""+statusCode);
                Log.d("Error : ", "" + throwable);
            }

        });
    }

    private void setValue(JSONObject studentInfo, JSONObject studentBalance){
        String balanceValue = "Tu n'as pas encore de compte FairPay";
        if(studentBalance != null){
            try {
                balanceValue = "Ton solde FairPay : "+studentBalance.get("balance")+"€";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            name.setText(studentInfo.get("first_name")+ " " + studentInfo.get("last_name"));
            balance.setText(balanceValue);
            setPicture(pictureBaseUrl+studentInfo.get("email"));
            barcodeImage.setImageBitmap(createBarcode(studentInfo.get("id").toString()));
            id.setText(studentInfo.get("id").toString());
            loader.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPicture(String pictureUrl){
        if (pictureUrl != null) {
            Picasso.with(this.getContext()).load(pictureUrl).into(pictureStudent);
        } else {
            pictureStudent.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private Bitmap createBarcode(String id){
        Bitmap barcodeBitmap = null;
        try{
            barcodeBitmap = encodeAsBitmap(id, BarcodeFormat.CODE_39, barcodeImage.getWidth(), 150);
        }
        catch (WriterException e){
            e.printStackTrace();
        }
        return barcodeBitmap;
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException
    {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

}
