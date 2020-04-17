package com.example.haball.Distributor.ui.main.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.example.haball.Distributor.StatusKVP;
import com.example.haball.Distributor.ui.main.ViewOrdersAdapter.ViewOrderProductAdapter;
import com.example.haball.Distributor.ui.main.ViewOrdersModel.ViewOrderProductModel;
import com.example.haball.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private String orderID;
    private String URL_Order_Data = "http://175.107.203.97:4013/api/Orders/";
    private PageViewModel pageViewModel;
    private TextInputEditText txt_orderID, txt_company_order, txt_created_date_order, txt_status_order, txt_comments;
    private TextInputEditText txt_companyName, txt_paymentID, txt_created_date, txt_transaction_date, txt_bank, txt_authorization_id, txt_settlement_id, txt_status, txt_amount, txt_transaction_charges, txt_total_amount;
    private RecyclerView rv_fragment_retailer_order_details;
    private TextView tv_shipment_no_data;
    private RecyclerView.Adapter rv_productAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ViewOrderProductModel> invo_productList = new ArrayList<>();
    private String Token;
    private String DistributorId;
    private HashMap<String, String> OrderStatusKVP = new HashMap<>();
    private HashMap<String, String> InvoiceStatusKVP = new HashMap<>();
    private StatusKVP StatusKVPClass;
    private TextInputLayout layout_txt_created_date, layout_transaction_date, layout_txt_bank, layout_txt_authorization_id, layout_txt_settlement_id, layout_txt_status, layout_txt_amount, layout_txt_transaction_charges, layout_txt_total_amount;


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        SharedPreferences sharedPreferences3 = getContext().getSharedPreferences("OrderId",
                Context.MODE_PRIVATE);


        orderID = sharedPreferences3.getString("OrderId", "");
        Log.i("OrderId", orderID);
        if (!URL_Order_Data.contains(orderID)) {
            URL_Order_Data = URL_Order_Data + orderID;
            Log.i("URL_Order_Data", URL_Order_Data);
        }
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");
//        Log.i("StatusKVP", String.valueOf(Token));

        StatusKVPClass = new StatusKVP(getContext(), Token);
        OrderStatusKVP = StatusKVPClass.getOrderStatus();
        InvoiceStatusKVP = StatusKVPClass.getInvoiceStatus();
//        Log.i("StatusKVP1", String.valueOf(StatusKVP));

        View rootView = null;
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1: {
                rootView = inflater.inflate(R.layout.fragment_retailer_orders_tab, container, false);
                txt_orderID = rootView.findViewById(R.id.txt_orderID);
                txt_company_order = rootView.findViewById(R.id.txt_company_order);
                txt_created_date_order = rootView.findViewById(R.id.txt_created_date_order);
                txt_status_order = rootView.findViewById(R.id.txt_status_order);
                txt_comments = rootView.findViewById(R.id.txt_comments);

                txt_orderID.setEnabled(false);
                txt_company_order.setEnabled(false);
                txt_created_date_order.setEnabled(false);
                txt_status_order.setEnabled(false);
                txt_comments.setEnabled(false);

                getOrderData();
                break;
            }
            case 2: {
                rootView = inflater.inflate(R.layout.fragment_retailer_orders_details_tab, container, false);
                rv_fragment_retailer_order_details = rootView.findViewById(R.id.rv_fragment_retailer_order_details);
                rv_fragment_retailer_order_details.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(rootView.getContext());
                rv_fragment_retailer_order_details.setLayoutManager(layoutManager);


                getOrderDetailsData(rootView);
                break;
            }
            case 3: {
                rootView = inflater.inflate(R.layout.fragment_retailer_payment_tab, container, false);
                txt_companyName = rootView.findViewById(R.id.txt_companyName);
                txt_paymentID = rootView.findViewById(R.id.txt_paymentID);
                txt_created_date = rootView.findViewById(R.id.txt_created_date);
                txt_transaction_date = rootView.findViewById(R.id.txt_confirm);
                txt_bank = rootView.findViewById(R.id.txt_bank);
                txt_authorization_id = rootView.findViewById(R.id.txt_authorization_id);
                txt_settlement_id = rootView.findViewById(R.id.txt_settlement_id);
                txt_status = rootView.findViewById(R.id.txt_status);
                txt_amount = rootView.findViewById(R.id.txt_amount);
                txt_transaction_charges = rootView.findViewById(R.id.txt_transaction_charges);
                txt_total_amount = rootView.findViewById(R.id.txt_total_amount);
                layout_txt_created_date = rootView.findViewById(R.id.layout_txt_created_date);
                layout_transaction_date = rootView.findViewById(R.id.layout_transaction_date);
                layout_txt_bank = rootView.findViewById(R.id.layout_txt_bank);
                layout_txt_authorization_id = rootView.findViewById(R.id.layout_txt_authorization_id);
                layout_txt_settlement_id = rootView.findViewById(R.id.layout_txt_settlement_id);
                layout_txt_status = rootView.findViewById(R.id.layout_txt_status);
                layout_txt_amount = rootView.findViewById(R.id.layout_txt_amount);
                layout_txt_transaction_charges = rootView.findViewById(R.id.layout_txt_transaction_charges);
                layout_txt_total_amount = rootView.findViewById(R.id.layout_txt_total_amount);

                layout_txt_created_date.setVisibility(View.GONE);
                layout_transaction_date.setVisibility(View.GONE);
                layout_txt_bank.setVisibility(View.GONE);
                layout_txt_authorization_id.setVisibility(View.GONE);
                layout_txt_settlement_id.setVisibility(View.GONE);
                layout_txt_status.setVisibility(View.GONE);
                layout_txt_amount.setVisibility(View.GONE);
                layout_txt_transaction_charges.setVisibility(View.GONE);
                layout_txt_total_amount.setVisibility(View.GONE);

                txt_companyName.setEnabled(false);
                txt_paymentID.setEnabled(false);
                txt_created_date.setEnabled(false);
                txt_transaction_date.setEnabled(false);
                txt_bank.setEnabled(false);
                txt_authorization_id.setEnabled(false);
                txt_settlement_id.setEnabled(false);
                txt_status.setEnabled(false);
                txt_amount.setEnabled(false);
                txt_transaction_charges.setEnabled(false);
                txt_total_amount.setEnabled(false);


                getPaymentData();
                break;
            }
        }
        return rootView;
    }

    private void getOrderData() {
        Log.i("DistributorId invoice", DistributorId);
        Log.i("Token invoice", Token);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, URL_Order_Data, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Order Data response", String.valueOf(response));
                try {
                    txt_orderID.setText(String.valueOf(response.get("OrderNumber")));
                    txt_company_order.setText(String.valueOf(response.get("CompanyName")));
                    txt_created_date_order.setText(String.valueOf(response.get("CreatedDate")).split("T")[0]);
                    txt_status_order.setText(OrderStatusKVP.get(String.valueOf(response.get("Status"))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        printErrorMessage(error);

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + Token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(stringRequest);

    }

    private void getOrderDetailsData(View rootView) {
        tv_shipment_no_data = rootView.findViewById(R.id.tv_shipment_no_data);
        tv_shipment_no_data.setVisibility(View.GONE);

        Log.i("DistributorId invoice", DistributorId);
        Log.i("Token invoice", Token);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, URL_Order_Data, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<ViewOrderProductModel>>() {
                }.getType();
                try {
                    invo_productList = gson.fromJson(response.get("OrderDetails").toString(), type);
                    Log.i("OrderDetails", String.valueOf(response.get("OrderDetails")));
                    ViewOrderProductAdapter productAdapter = new ViewOrderProductAdapter(getContext(), invo_productList);
                    rv_fragment_retailer_order_details.setAdapter(productAdapter);
                    if (invo_productList.size() != 0) {
                        tv_shipment_no_data.setVisibility(View.GONE);
                    } else {
                        tv_shipment_no_data.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        printErrorMessage(error);

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + Token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void getPaymentData() {
        Log.i("DistributorId invoice", DistributorId);
        Log.i("Token invoice", Token);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, URL_Order_Data, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Order Data response2", String.valueOf(response));
                try {
                    if (response.has("Invoice")) {
                        JSONObject responseInv = response.getJSONObject("Invoice");
                        txt_companyName.setText(String.valueOf(response.get("CompanyName")));
                        txt_paymentID.setText(String.valueOf(responseInv.get("InvoiceNumber")));
                        setTextAndShow(layout_txt_created_date, txt_created_date, String.valueOf(responseInv.get("PaidDate")).split("T")[0]);
                        setTextAndShow(layout_txt_amount, txt_amount, String.valueOf(responseInv.get("PaidAmount")));
                        setTextAndShow(layout_txt_status, txt_status, String.valueOf(InvoiceStatusKVP.get(responseInv.getString("Status"))));
                        setTextAndShow(layout_txt_amount, txt_amount, String.valueOf(responseInv.get("PaidAmount")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        printErrorMessage(error);

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + Token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(stringRequest);

    }

    private void setTextAndShow(TextInputLayout layout, TextInputEditText editText, String value) {
        if (!value.equals("null")) {
            layout.setVisibility(View.VISIBLE);
            editText.setText(value);
        }
    }

    private void printErrorMessage(VolleyError error) {
        if (getContext() != null) {
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
}