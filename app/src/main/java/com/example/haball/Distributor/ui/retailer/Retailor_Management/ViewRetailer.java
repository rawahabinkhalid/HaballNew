package com.example.haball.Distributor.ui.retailer.Retailor_Management;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.haball.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRetailer extends Fragment {
    private String RetailerId;
    private TextInputEditText mg_rt_code, mg_rt_firstname, mg_rt_email, mg_cnic_no, mg_mobile_no, mg_rt_company, mg_tr_address;
    private String URL_RETAILER_DETAILS = "http://175.107.203.97:4013/api/retailer/";
    private String Token, DistributorId;

    public ViewRetailer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_retailer, container, false);
        RetailerId = getArguments().getString("RetailerId");

        mg_rt_code = root.findViewById(R.id.mg_rt_code);
        mg_rt_firstname = root.findViewById(R.id.mg_rt_firstname);
        mg_rt_email = root.findViewById(R.id.mg_rt_email);
        mg_cnic_no = root.findViewById(R.id.mg_cnic_no);
        mg_mobile_no = root.findViewById(R.id.mg_mobile_no);

        mg_rt_company = root.findViewById(R.id.mg_rt_company);
        mg_tr_address = root.findViewById(R.id.mg_tr_address);

        mg_rt_code.setEnabled(false);
        mg_rt_firstname.setEnabled(false);
        mg_rt_email.setEnabled(false);
        mg_cnic_no.setEnabled(false);
        mg_mobile_no.setEnabled(false);
        mg_rt_company.setEnabled(false);
        mg_tr_address.setEnabled(false);



        try {
            fetchRetailerData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    private void fetchRetailerData() throws JSONException {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");
        Log.i("DistributorId ", DistributorId);
        Log.i("Token", Token);

        URL_RETAILER_DETAILS = URL_RETAILER_DETAILS + RetailerId;
        Log.i("URL_RETAILER_DETAILS ", URL_RETAILER_DETAILS);

        JSONObject map = new JSONObject();
        map.put("DistributorId", Integer.parseInt(DistributorId));
        Log.i("Map", String.valueOf(map));

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.GET, URL_RETAILER_DETAILS, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                try {
                    Log.i("result", String.valueOf(result));
                    mg_rt_code.setText(result.getString("RetailerCode"));
//                    mg_rt_firstname.setText(result.getString("ConsolidatedInvoiceNumber"));
//                    mg_rt_email.setText(result.getString("ConsolidatedInvoiceNumber"));
//                    mg_cnic_no.setText(result.getString("ConsolidatedInvoiceNumber"));
//                    mg_mobile_no.setText(result.getString("ConsolidatedInvoiceNumber"));
                    mg_rt_company.setText(result.getString("CompanyName"));
//                    mg_tr_address.setText(result.getString("ConsolidatedInvoiceNumber"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMessage(error);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + Token);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(sr);
    }


    private void printErrorMessage(VolleyError error) {
        if (error instanceof NetworkError) {
            Toast.makeText(getContext(), "Network Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof ServerError) {
            Toast.makeText(getContext(), "Server Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof AuthFailureError) {
            Toast.makeText(getContext(), "Auth Failure Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof ParseError) {
            Toast.makeText(getContext(), "Parse Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof NoConnectionError) {
            Toast.makeText(getContext(), "No Connection Error !", Toast.LENGTH_LONG).show();
        } else if (error instanceof TimeoutError) {
            Toast.makeText(getContext(), "Timeout Error !", Toast.LENGTH_LONG).show();
        }

        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String message = "";
                String responseBody = new String(error.networkResponse.data, "utf-8");
                Log.i("responseBody", responseBody);
                JSONObject data = new JSONObject(responseBody);
                Log.i("data", String.valueOf(data));
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    message = message + data.get(key) + "\n";
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}