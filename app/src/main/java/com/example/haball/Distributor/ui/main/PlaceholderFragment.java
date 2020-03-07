package com.example.haball.Distributor.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.haball.Distribution_Login.Distribution_Login;
import com.example.haball.Distributor.DistributorDashboard;
import com.example.haball.Distributor.DistributorOrdersAdapter;
import com.example.haball.Distributor.DistributorOrdersModel;
import com.example.haball.Distributor.DistributorPaymentsAdapter;

import com.example.haball.Distributor.DistributorPaymentsModel;
import com.example.haball.Distributor.ui.payments.MyJsonArrayRequest;
import com.example.haball.Payment.DistributorPaymentRequestAdaptor;
import com.example.haball.Payment.DistributorPaymentRequestModel;
import com.example.haball.Payment.PaymentLedgerAdapter;
import com.example.haball.Payment.PaymentLedgerModel;
import com.example.haball.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter PaymentsAdapter;
    private RecyclerView.Adapter OrdersAdapter;

    private RecyclerView.LayoutManager layoutManager;
    private String URL_DISTRIBUTOR_DASHBOARD = "http://175.107.203.97:4007/api/dashboard/ReadDistributorDashboard";
    //    private String URL_DISTRIBUTOR_PAYMENTS = "http://175.107.203.97:4007/api/dashboard/ReadDistributorPayments";
    private String URL_DISTRIBUTOR_PAYMENTS = "http://175.107.203.97:4007/api/prepaidrequests/search";
    private String URL_DISTRIBUTOR_PAYMENTS_COUNT = "http://175.107.203.97:4007/api/prepaidrequests/searchCount";
    private String URL_DISTRIBUTOR_ORDERS = "http://175.107.203.97:4007/api/orders/search";
    private String URL_DISTRIBUTOR_ORDERS_COUNT = "http://175.107.203.97:4007/api/orders/searchCount";

    private TextView value_unpaid_amount, value_paid_amount;
    private List<DistributorPaymentsModel> PaymentsList = new ArrayList<>();
    private List<DistributorPaymentRequestModel> PaymentsRequestList = new ArrayList<>();
    private List<DistributorOrdersModel> OrdersList = new ArrayList<>();
    private String Token, DistributorId;

    private PageViewModel pageViewModel;
    private RelativeLayout spinner_container1;
    private Spinner spinner_consolidate;
    private Spinner spinner2;
    private EditText conso_edittext;
    private List<String> consolidate_felter;
    private List<String> filters = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapterPayments;
    private ArrayAdapter<String> arrayAdapterFeltter;
    private Button consolidate;
    private String Filter_selected, Filter_selected_value;
    private RecyclerView.Adapter mAdapter;
    private TextInputLayout search_bar;
    private Button btn_load_more;
    private int pageNumber = 0;
    private double totalPages = 0;
    private double totalEntries = 0;

    private int pageNumberOrder = 0;
    private double totalPagesOrder = 0;
    private double totalEntriesOrder = 0;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        Log.i("SECTION NO", String.valueOf(getArguments().getInt(ARG_SECTION_NUMBER)));
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1: {
                rootView = inflater.inflate(R.layout.fragment_payments, container, false);
                try {
                    fetchPaymentRequests();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                btn_load_more = rootView.findViewById(R.id.btn_load_more);

                SpannableString content = new SpannableString("Load More");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                btn_load_more.setText(content);
                btn_load_more.setVisibility(View.GONE);

                btn_load_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pageNumber++;
                        try {
                            performPagination();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


                recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_fragment_payments);

                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(rootView.getContext());
                recyclerView.setLayoutManager(layoutManager);

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());

                        int visibleItemCount        = layoutManager.getChildCount();
                        int totalItemCount          = layoutManager.getItemCount();
                        int firstVisibleItemPosition= layoutManager.findFirstVisibleItemPosition();

                        // Load more if we have reach the end to the recyclerView
                        if ( (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                            if(totalPages != 0 && pageNumber < totalPages) {
//                                Toast.makeText(getContext(), pageNumber + " - " + totalPages, Toast.LENGTH_LONG).show();
                                btn_load_more.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

                paymentFragmentTask(rootView);

                break;
            }

            case 2: {
                rootView = inflater.inflate(R.layout.fragment_orders, container, false);
                recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_fragment_orders);

                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(rootView.getContext());
                recyclerView.setLayoutManager(layoutManager);

                btn_load_more = rootView.findViewById(R.id.btn_load_more);

                SpannableString content = new SpannableString("Load More");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                btn_load_more.setText(content);
                btn_load_more.setVisibility(View.GONE);

                btn_load_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pageNumberOrder++;
                        try {
                            performPaginationOrder();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());

                        int visibleItemCount        = layoutManager.getChildCount();
                        int totalItemCount          = layoutManager.getItemCount();
                        int firstVisibleItemPosition= layoutManager.findFirstVisibleItemPosition();

                        // Load more if we have reach the end to the recyclerView
                        if ( (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                            if(totalPages != 0 && pageNumberOrder < totalPages) {
//                                Toast.makeText(getContext(), pageNumberOrder + " - " + totalPages, Toast.LENGTH_LONG).show();
                                btn_load_more.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

                try {
                    fetchOrderData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                orderFragmentTask(rootView);

                break;
            }
            case 3: {
                rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
                value_unpaid_amount = rootView.findViewById(R.id.value_unpaid_amount);
                value_paid_amount = rootView.findViewById(R.id.value_paid_amount);
                fetchDashboardData();
                break;
            }
            default:
                break;
        }
        return rootView;
    }
    private void performPaginationOrder() throws JSONException {

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");
        DistributorId = sharedPreferences.getString("Distributor_Id", "");
        Log.i("Token", Token);
        JSONObject map = new JSONObject();
        map.put("Status", -1);
        map.put("OrderState", -1);
        map.put("DistributorId", DistributorId);
        map.put("TotalRecords", 10);
        map.put("PageNumber", pageNumberOrder);

        MyJsonArrayRequest sr = new MyJsonArrayRequest(Request.Method.POST, URL_DISTRIBUTOR_ORDERS, map, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray result) {
                btn_load_more.setVisibility(View.GONE);

                Gson gson = new Gson();
                Type type = new TypeToken<List<DistributorOrdersModel>>() {
                }.getType();
                OrdersList = gson.fromJson(result.toString(), type);
                ((DistributorOrdersAdapter)recyclerView.getAdapter()).addListItem(OrdersList);

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
    private void performPagination() throws JSONException {

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");
        Log.i("Token", Token);

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");
        Log.i("DistributorId ", DistributorId);

        JSONObject map = new JSONObject();
        map.put("DistributorId", Integer.parseInt(DistributorId));
        map.put("TotalRecords", 10);
        map.put("PageNumber", pageNumber);

        MyJsonArrayRequest sr = new MyJsonArrayRequest(Request.Method.POST, URL_DISTRIBUTOR_PAYMENTS, map, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray result) {
                Log.i("Payments Requests", result.toString());
                btn_load_more.setVisibility(View.GONE);

                Gson gson = new Gson();
                Type type = new TypeToken<List<DistributorPaymentRequestModel>>() {
                }.getType();
                PaymentsRequestList = gson.fromJson(result.toString(), type);
                ((DistributorPaymentRequestAdaptor)recyclerView.getAdapter()).addListItem(PaymentsRequestList);

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

    private void paymentFragmentTask(View rootView) {
        search_bar = rootView.findViewById(R.id.search_bar);
        consolidate = rootView.findViewById(R.id.consolidate);
        spinner_container1 = rootView.findViewById(R.id.spinner_container1);
        spinner_consolidate = (Spinner) rootView.findViewById(R.id.spinner_conso);
        spinner2 = (Spinner) rootView.findViewById(R.id.conso_spinner2);
        conso_edittext = (EditText) rootView.findViewById(R.id.conso_edittext);
        spinner_container1.setVisibility(View.GONE);
        conso_edittext.setVisibility(View.GONE);
        consolidate_felter = new ArrayList<>();
        consolidate_felter.add("Select Criteria");
        consolidate_felter.add("Payment ID");
        consolidate_felter.add("Company");
        consolidate_felter.add("Transaction Date");
        consolidate_felter.add("Created Date");
        consolidate_felter.add("Amount");
        consolidate_felter.add("Status");

        arrayAdapterPayments = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, consolidate_felter);

        spinner_consolidate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getContext(), consolidate_felter.get(i), Toast.LENGTH_LONG).show();
                spinner_container1.setVisibility(View.GONE);
                conso_edittext.setVisibility(View.GONE);
                if (i == 0) {
                    try {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.darker_gray));
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Filter_selected = consolidate_felter.get(i);

                    if (!Filter_selected.equals("Status"))
                        spinner2.setSelection(0);
                    if (!conso_edittext.getText().equals(""))
                        conso_edittext.setText("");

                    if (Filter_selected.equals("Payment ID")) {
                        search_bar.setHint("Search by " + Filter_selected);
                        Filter_selected = "PrePaidNumber";
                        conso_edittext.setVisibility(View.VISIBLE);
                    } else if (Filter_selected.equals("Company")) {
                        search_bar.setHint("Search by " + Filter_selected);
                        Filter_selected = "CompanyName";
                        conso_edittext.setVisibility(View.VISIBLE);
                    } else if (Filter_selected.equals("Transaction Date")) {
                        Toast.makeText(getContext(), "Transaction Date selected", Toast.LENGTH_LONG).show();
                    } else if (Filter_selected.equals("Created Date")) {
                        Toast.makeText(getContext(), "Created Date selected", Toast.LENGTH_LONG).show();
                    } else if (Filter_selected.equals("Amount")) {
                        Toast.makeText(getContext(), "Amount selected", Toast.LENGTH_LONG).show();
                    } else if (Filter_selected.equals("Status")) {
                        Filter_selected = "Status";
                        spinner_container1.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        arrayAdapterPayments.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_consolidate.setAdapter(arrayAdapterPayments);

        filters.add("Status");
        filters.add("Processing Payment");
        filters.add("Unpaid ");
        filters.add("Paid");
        arrayAdapterFeltter = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, filters);
        Log.i("aaaa1111", String.valueOf(consolidate_felter));
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    Filter_selected_value = String.valueOf(i - 2);
                    Log.i("Filter_selected_value", String.valueOf(i));

                    if (Filter_selected_value != "") {
                        try {
                            fetchFilteredPaymentRequests();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        arrayAdapterFeltter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        arrayAdapterFeltter.notifyDataSetChanged();
        spinner2.setAdapter(arrayAdapterFeltter);

        conso_edittext.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Log.i("text1", "check");
                Log.i("text", String.valueOf(s));
                Filter_selected_value = String.valueOf(s);
                if(!Filter_selected_value.equals("")) {
                    try {
                        fetchFilteredPaymentRequests();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        consolidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Consolidate clicked", Toast.LENGTH_LONG).show();
//                        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.remove(PaymentRequestDashboard.this);
//                        fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), new CreatePaymentRequestFragment());
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
            }
        });

    }

    private void orderFragmentTask(View rootView) {
        search_bar = rootView.findViewById(R.id.search_bar);
        spinner_container1 = rootView.findViewById(R.id.spinner_container1);
        spinner_consolidate = (Spinner) rootView.findViewById(R.id.spinner_conso);
        spinner2 = (Spinner) rootView.findViewById(R.id.conso_spinner2);
        conso_edittext = (EditText) rootView.findViewById(R.id.conso_edittext);
        spinner_container1.setVisibility(View.GONE);
        conso_edittext.setVisibility(View.GONE);
        consolidate_felter = new ArrayList<>();
        consolidate_felter.add("Select Criteria");
        consolidate_felter.add("Order No");
        consolidate_felter.add("Company");
        consolidate_felter.add("Payment Term");
        consolidate_felter.add("Created Date");
        consolidate_felter.add("Amount");
        consolidate_felter.add("Status");

        arrayAdapterPayments = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, consolidate_felter);

        spinner_consolidate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getContext(), consolidate_felter.get(i), Toast.LENGTH_LONG).show();
                spinner_container1.setVisibility(View.GONE);
                conso_edittext.setVisibility(View.GONE);
                if (i == 0) {
                    try {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.darker_gray));
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Filter_selected = consolidate_felter.get(i);

                    if (!Filter_selected.equals("Status"))
                        spinner2.setSelection(0);
                    if (!conso_edittext.getText().equals(""))
                        conso_edittext.setText("");

                    if (Filter_selected.equals("Order No")) {
                        search_bar.setHint("Search by " + Filter_selected);
                        Filter_selected = "OrderNumber";
                        conso_edittext.setVisibility(View.VISIBLE);
                    } else if (Filter_selected.equals("Company")) {
                        search_bar.setHint("Search by " + Filter_selected);
                        Filter_selected = "CompanyName";
                        conso_edittext.setVisibility(View.VISIBLE);
                    } else if (Filter_selected.equals("Created Date")) {
                        Toast.makeText(getContext(), "Created Date selected", Toast.LENGTH_LONG).show();
                    } else if (Filter_selected.equals("Amount")) {
                        Toast.makeText(getContext(), "Amount selected", Toast.LENGTH_LONG).show();
                    } else if (Filter_selected.equals("Status")) {
                        Filter_selected = "Status";
                        spinner_container1.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        arrayAdapterPayments.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_consolidate.setAdapter(arrayAdapterPayments);

        filters.add("Status");
        filters.add("Pending");
        filters.add("Approved");
        filters.add("Rejected");
        filters.add("Draft");
        filters.add("Cancelled");
        arrayAdapterFeltter = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, filters);
        Log.i("aaaa1111", String.valueOf(consolidate_felter));
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    Filter_selected_value = String.valueOf(i - 1);
                    Log.i("Filter_selected_value", Filter_selected_value);
                    if(!Filter_selected_value.equals("")) {
                        try {
                            fetchFilteredPaymentRequests();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        arrayAdapterFeltter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(arrayAdapterFeltter);

        conso_edittext.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                Log.i("text1", "check");
                Log.i("text", String.valueOf(s));
                Filter_selected_value = String.valueOf(s);
                if(!Filter_selected_value.equals("")) {
                    try {
                        fetchFilteredOrderData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void fetchOrderData() throws JSONException {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");
        DistributorId = sharedPreferences.getString("Distributor_Id", "");
        Log.i("Token", Token);


        JSONObject mapCount = new JSONObject();
        mapCount.put("Status", -1);
        mapCount.put("DistributorId", Integer.parseInt(DistributorId));

        JsonObjectRequest countRequest = new JsonObjectRequest(Request.Method.POST, URL_DISTRIBUTOR_ORDERS_COUNT, mapCount, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    totalEntriesOrder = Double.parseDouble(String.valueOf(response.get("ordersCount")));
                    totalPagesOrder = Math.ceil(totalEntriesOrder / 10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMessage(error);

                error.printStackTrace();
                Log.i("onErrorResponse", "Error");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer "+Token);
                return params;
            }
        };
        countRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(countRequest);


        JSONObject map = new JSONObject();
        map.put("Status", -1);
        map.put("OrderState", -1);
        map.put("DistributorId", DistributorId);
        map.put("TotalRecords", 10);
        map.put("PageNumber", pageNumberOrder);

        MyJsonArrayRequest sr = new MyJsonArrayRequest(Request.Method.POST, URL_DISTRIBUTOR_ORDERS, map, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray result) {
                //                    JSONArray jsonArray = new JSONArray(result);
                Gson gson = new Gson();
                Type type = new TypeToken<List<DistributorOrdersModel>>() {
                }.getType();
                OrdersList = gson.fromJson(result.toString(), type);

                OrdersAdapter = new DistributorOrdersAdapter(getContext(), OrdersList);
                recyclerView.setAdapter(OrdersAdapter);

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

    private void fetchFilteredOrderData() throws JSONException {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");
        DistributorId = sharedPreferences.getString("Distributor_Id", "");
        Log.i("Token", Token);
        JSONObject map = new JSONObject();
        map.put("DistributorId", DistributorId);
        map.put("TotalRecords", 10);
        map.put("PageNumber", 0);
        map.put(Filter_selected, Filter_selected_value);
        Log.i("Map", String.valueOf(map));

        MyJsonArrayRequest sr = new MyJsonArrayRequest(Request.Method.POST, URL_DISTRIBUTOR_ORDERS, map, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray result) {
                //                    JSONArray jsonArray = new JSONArray(result);
                Gson gson = new Gson();
                Type type = new TypeToken<List<DistributorOrdersModel>>() {
                }.getType();
                OrdersList = gson.fromJson(result.toString(), type);

                OrdersAdapter = new DistributorOrdersAdapter(getContext(), OrdersList);
                recyclerView.setAdapter(OrdersAdapter);

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

    private void fetchPaymentRequests() throws JSONException {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");
        Log.i("Token", Token);

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");
        Log.i("DistributorId ", DistributorId);

        JSONObject mapCount = new JSONObject();
        mapCount.put("Status", -1);
        mapCount.put("DistributorId", Integer.parseInt(DistributorId));

        JsonObjectRequest countRequest = new JsonObjectRequest(Request.Method.POST, URL_DISTRIBUTOR_PAYMENTS_COUNT, mapCount, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    totalEntries = Double.parseDouble(String.valueOf(response.get("prepaidrequestsCount")));
                    totalPages = Math.ceil(totalEntries / 10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                printErrorMessage(error);

                error.printStackTrace();
                Log.i("onErrorResponse", "Error");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer "+Token);
                return params;
            }
        };
        countRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getContext()).add(countRequest);

        JSONObject map = new JSONObject();
        map.put("DistributorId", Integer.parseInt(DistributorId));
        map.put("TotalRecords", 10);
        map.put("PageNumber", 0.1);

        MyJsonArrayRequest sr = new MyJsonArrayRequest(Request.Method.POST, URL_DISTRIBUTOR_PAYMENTS, map, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray result) {
                Log.i("Payments Requests", result.toString());
                Gson gson = new Gson();
                Type type = new TypeToken<List<DistributorPaymentRequestModel>>() {
                }.getType();
                PaymentsRequestList = gson.fromJson(result.toString(), type);

                mAdapter = new DistributorPaymentRequestAdaptor(getContext(), PaymentsRequestList);
                recyclerView.setAdapter(mAdapter);
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


    private void fetchFilteredPaymentRequests() throws JSONException {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");
        Log.i("Token", Token);

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");
        Log.i("DistributorId ", DistributorId);

        JSONObject map = new JSONObject();
        map.put("DistributorId", Integer.parseInt(DistributorId));
        map.put("TotalRecords", 10);
        map.put("PageNumber", 0.1);
        map.put(Filter_selected, Filter_selected_value);
        Log.i("Map", String.valueOf(map));

        MyJsonArrayRequest sr = new MyJsonArrayRequest(Request.Method.POST, URL_DISTRIBUTOR_PAYMENTS, map, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray result) {
                Log.i("Payments Requests", result.toString());
                Gson gson = new Gson();
                Type type = new TypeToken<List<DistributorPaymentRequestModel>>() {
                }.getType();
                PaymentsRequestList = gson.fromJson(result.toString(), type);

                mAdapter = new DistributorPaymentRequestAdaptor(getContext(), PaymentsRequestList);
                recyclerView.setAdapter(mAdapter);
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

    private void fetchDashboardData() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");

        StringRequest sr = new StringRequest(Request.Method.POST, URL_DISTRIBUTOR_DASHBOARD, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    DecimalFormat formatter1 = new DecimalFormat("#,###,###,##0.00");
                    String yourFormattedString1 = formatter1.format(Double.parseDouble(jsonObject.get("TotalUnpaidAmount").toString()));
                    DecimalFormat formatter2 = new DecimalFormat("#,###,###,##0.00");
                    String yourFormattedString2 = formatter2.format(Double.parseDouble(jsonObject.get("TotalPrepaidAmount").toString()));
                    value_unpaid_amount.setText(yourFormattedString1);
                    value_paid_amount.setText(yourFormattedString2);
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
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String message = "";
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
//                if (data.get(key) instanceof JSONObject) {
                    Log.i("message", String.valueOf(data.get(key)));
                    if (data.get(key).equals("TokenExpiredError")) {
                        SharedPreferences login_token = getContext().getSharedPreferences("LoginToken",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = login_token.edit();
                        editor.remove("Login_Token");
                        editor.commit();
                        Intent login = new Intent(getActivity(), Distribution_Login.class);
                        startActivity(login);
                        getActivity().finish();

                    }

                    message = message + data.get(key) + "\n";
//                }
                }
//                    if(data.has("message"))
//                        message = data.getString("message");
//                    else if(data. has("Error"))
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}