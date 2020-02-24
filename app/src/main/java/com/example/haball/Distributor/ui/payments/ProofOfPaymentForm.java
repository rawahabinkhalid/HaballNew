package com.example.haball.Distributor.ui.payments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.haball.Payment.ConsolidatePaymentsModel;
import com.example.haball.Payment.Consolidate_Fragment_Adapter;
import com.example.haball.Payment.ProofOfPaymentAdapter;
import com.example.haball.Payment.ProofOfPaymentModel;
import com.example.haball.Payment.ProofOfPaymentsFormAdapter;
import com.example.haball.Payment.ProofOfPaymentsFormModel;
import com.example.haball.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProofOfPaymentForm extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final int REQUEST_CAMERA = 2;
    private static final int SELECT_FILE = 1;

    private String Token;
    private String URL_PROOF_OF_PAYMENTS = "http://175.107.203.97:4008/api/prepaidrequests/PrepaidPOP/";
    private String URL_MODE_OF_PAYMENTS = "http://175.107.203.97:4008/api/lookup/PROOF_OF_PAYMENT";
    private String URL_PROOF_OF_PAYMENTS_SUBMIT = "http://175.107.203.97:4008/api/proofofpayment/save";

    private Spinner spinner_payment_id, spinner_mode_of_payments;
    private ArrayAdapter<String> arrayAdapterPayments;
    private ArrayAdapter<String> arrayAdapterPaymentModes;
    private List<ProofOfPaymentModel> proofOfPaymentsList = new ArrayList<>();
    private HashMap<String, String> PaymentIdNumber = new HashMap<>();
    private HashMap<String, String> PaymentModeAndID = new HashMap<>();
    private HashMap<String, String> FileTypeIdValue = new HashMap<>();

    private List<String> payment_ids = new ArrayList<>();
    private List<String> paymentID = new ArrayList<>();
    private List<String> payment_modes = new ArrayList<>();
    private String DistributorId, selected_paymentid, selected_paymentmode;
    private Button btn_upload, btn_finish;
    private static int RESULT_LOAD_IMAGE = 1;
    private ArrayList<String> ImageFileTypes = new ArrayList<>();
    private ArrayList<String> DocumentNames = new ArrayList<>();
    private ArrayList<String> selectedImageFileTypes = new ArrayList<>();
    private ArrayList<String> imageBitmapBase64 = new ArrayList<>();

    private ProgressDialog progressDialog;
    private CountDownTimer CDT;
    private int i=8;
    private String selectedFileType, imageName;
    private TextView FileName;
    private EditText txt_bank, txt_branch, txt_transaction;

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] b = baos.toByteArray();

        String imageEncoded = Base64.encodeToString(baos.toByteArray(),Base64.NO_WRAP);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECT_FILE:{
                if (resultCode == RESULT_OK && data != null) {
                    final Uri imageUri = data.getData();
                    System.out.println("data"+data.getData());

                    Bundle extras = data.getExtras();
                    Bitmap bmp = (Bitmap) extras.get("data");
                    System.out.println("bmp "+bmp);

                    InputStream imageStream = null;
                    try {
                        imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println("imageStream  "+imageStream);

                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    System.out.println("yourSelectedImage  "+yourSelectedImage);

                    imageBitmapBase64.add(encodeTobase64(yourSelectedImage));
                    imageName = getRealPathFromURI(imageUri);
                    FileName.setText(imageName);
                    Toast.makeText(getContext(), imageName, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
                }
                break;

            }
            case REQUEST_CAMERA:{
                if (resultCode == RESULT_OK && data != null) {
                    Bundle extras = data.getExtras();
                    // Get the returned image from extra
                    Bitmap bmp = (Bitmap) extras.get("data");
                    imageBitmapBase64.add(encodeTobase64(bmp));
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyyyyhhmmss");
                    String datetime = dateformat.format(c.getTime());
                    System.out.println(datetime);

                    imageName = datetime+".jpg";
                    FileName.setText(imageName);
                    Toast.makeText(getContext(), imageName, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
                }
                break;
            }

        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_proof__of__payment__form, container, false);

        payment_ids.add("Payment ID *");
        payment_modes.add("Payment Mode *");

        ImageFileTypes.add("File Type *");
        ImageFileTypes.add("Deposit-slip");
        ImageFileTypes.add("CNIC");

        FileTypeIdValue.put("Deposit-slip", "0");
        FileTypeIdValue.put("Cheque", "1");
        FileTypeIdValue.put("CNIC", "2");

        spinner_payment_id = root.findViewById(R.id.spinner_id);
        spinner_mode_of_payments = root.findViewById(R.id.payment_mode);
        btn_upload = root.findViewById(R.id.btn_upload);
        btn_finish = root.findViewById(R.id.btn_finish);
        txt_bank = root.findViewById(R.id.txt_bank);
        txt_branch = root.findViewById(R.id.txt_branch);
        txt_transaction = root.findViewById(R.id.txt_transaction);

        progressDialog = new ProgressDialog(getContext());

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(getContext(), "Requesting please wait", Toast.LENGTH_LONG).show();
                    makePOPRequest();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageDialogue();
            }
        });

        arrayAdapterPayments = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_spinner_dropdown_item, payment_ids);
        arrayAdapterPaymentModes = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_spinner_dropdown_item, payment_modes);

        spinner_payment_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
                else{
                    selected_paymentid = payment_ids.get(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_mode_of_payments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
                else{
                    selected_paymentmode = payment_modes.get(i);
                    if(selected_paymentmode.equals("OTC"))
                        ImageFileTypes.add("Cheque");
                    else{
                        if(ImageFileTypes.contains("Cheque"))
                            ImageFileTypes.remove("Cheque");
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recyclerView = root.findViewById(R.id.rv_upload_doc);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        fetchPaymentsId();
        fetchPaymentModes();
        return root;
    }

    private void makePOPRequest() throws JSONException{
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token","");
        DistributorId = sharedPreferences.getString("Distributor_Id","");

        JSONArray array = new JSONArray();
        for(int i=0;i<DocumentNames.size();i++){
            JSONObject obj = new JSONObject();
            obj.put("ID", 0);
            obj.put("ImageType", "image/png");
            obj.put("Title", DocumentNames.get(i));
            obj.put("FileType", FileTypeIdValue.get(selectedImageFileTypes.get(i)));
            obj.put("FileTypeValue", selectedImageFileTypes.get(i));
            obj.put("ImageData", "data:image/png;base64,"+imageBitmapBase64.get(i));
            array.put(obj);
        }

        System.out.println("JSON ARRAY"+array);
        JSONObject map = new JSONObject();
        map.put("ID",0);
        map.put("Status",0);
        map.put("CompanyId",null);
        map.put("DistributorId",DistributorId);
        map.put("PaymentId",PaymentIdNumber.get(selected_paymentid));
        map.put("ModeOfPayment",PaymentModeAndID.get(selected_paymentmode));
        map.put("Bank",txt_bank.getText().toString());
        map.put("BranchCode",txt_branch.getText().toString());
        map.put("ChecqueNo",txt_transaction.getText().toString());
        map.put("ProofOfPaymentDetails",array);

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL_PROOF_OF_PAYMENTS_SUBMIT, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                Log.e("RESPONSE", result.toString());
                try {
                    Toast.makeText(getContext(), "Proof Of payment ID "+result.getString("POPNumber")+" has been created successfully.", Toast.LENGTH_SHORT).show();
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

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "bearer " + Token);
                params.put("Content-Type", "application/json; charset=UTF-8");
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(sr);
    }

    private void uploadImageDialogue() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view_popup = inflater.inflate(R.layout.upload_file_layout, null);
        alertDialog.setView(view_popup);
        Spinner spinner_upload_types;
        final Button btn_choose, btn_attach;
        TextView filename;

        btn_choose = view_popup.findViewById(R.id.btn_choose);
        spinner_upload_types = view_popup.findViewById(R.id.spinner_upload_types);
        filename = view_popup.findViewById(R.id.filename);
        btn_attach = view_popup.findViewById(R.id.btn_attach);
        FileName = filename;
        ArrayAdapter arrayAdapterFileTypes = new ArrayAdapter<>(view_popup.getContext(),
                android.R.layout.simple_dropdown_item_1line, ImageFileTypes);
        arrayAdapterFileTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterFileTypes.notifyDataSetChanged();
        spinner_upload_types.setAdapter(arrayAdapterFileTypes);
        spinner_upload_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.darker_gray));
                    selectedFileType = "";
                }
                else{
                    selectedFileType = ImageFileTypes.get(i);
                    selectedImageFileTypes.add(selectedFileType);
                    btn_choose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!selectedFileType.isEmpty()) {
                                openImageChooserDialog();
//                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                                photoPickerIntent.setType("image/*");
//                                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                            }
                        }
                    });
                    Toast.makeText(getContext(), selectedFileType, Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DocumentNames != null){

                    alertDialog.dismiss();
                    progressDialog.setTitle("Uploading ... ");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.setProgress(i);
                    progressDialog.show();

                    CDT = new CountDownTimer(8000, 1000)
                    {
                        public void onTick(long millisUntilFinished)
                        {
                            progressDialog.setMessage("Please wait...");
                            i--;
                        }

                        public void onFinish()
                        {
                            i=8;
                            DocumentNames.add(imageName);
                            mAdapter = new ProofOfPaymentsFormAdapter(getContext(),DocumentNames, selectedImageFileTypes, imageBitmapBase64);
                            recyclerView.setAdapter(mAdapter);
                            progressDialog.dismiss();
                            //Your Code ...
                        }
                    }.start();
                }
            }
        });

        alertDialog.show();

    }

    private void openImageChooserDialog() {
        final CharSequence[] items = {"Take Photo", "Gallery", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void fetchPaymentModes() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("Login_Token", "");

        SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                Context.MODE_PRIVATE);
        DistributorId = sharedPreferences1.getString("Distributor_Id", "");
        Log.i("DistributorId ", DistributorId);

        URL_PROOF_OF_PAYMENTS = URL_PROOF_OF_PAYMENTS + DistributorId;
        Log.i("URL_PROOF_OF_PAYMENTS ", URL_PROOF_OF_PAYMENTS);

        Log.i("Token", Token);

        JsonArrayRequest sr = new JsonArrayRequest(Request.Method.GET, URL_MODE_OF_PAYMENTS, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray result) {
                try {
                    JSONObject jsonObject = null;
                    for (int i = 0; i < result.length(); i++) {
                        jsonObject = result.getJSONObject(i);
                        payment_modes.add(jsonObject.getString("value"));
                        PaymentModeAndID.put(jsonObject.getString("value"), jsonObject.getString("key"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("RESPONSE PAYMENT MODES", result.toString());
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
                params.put("rightid", "-1");
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(sr);
        arrayAdapterPaymentModes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapterPaymentModes.notifyDataSetChanged();
        spinner_mode_of_payments.setAdapter(arrayAdapterPaymentModes);
    }

    private void fetchPaymentsId() {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginToken",
                    Context.MODE_PRIVATE);
            Token = sharedPreferences.getString("Login_Token", "");

            SharedPreferences sharedPreferences1 = this.getActivity().getSharedPreferences("LoginToken",
                    Context.MODE_PRIVATE);
            DistributorId = sharedPreferences1.getString("Distributor_Id", "");
            Log.i("DistributorId ", DistributorId);

            URL_PROOF_OF_PAYMENTS = URL_PROOF_OF_PAYMENTS + DistributorId;
            Log.i("URL_PROOF_OF_PAYMENTS ", URL_PROOF_OF_PAYMENTS);

            Log.i("Token", Token);

            JsonArrayRequest sr = new JsonArrayRequest(Request.Method.GET, URL_PROOF_OF_PAYMENTS, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray result) {
                    try {
                        JSONObject jsonObject = null;
                        for (int i = 0; i < result.length(); i++) {
                            jsonObject = result.getJSONObject(i);
                            payment_ids.add(jsonObject.getString("PaymentNumber"));
                            PaymentIdNumber.put(jsonObject.getString("PaymentNumber"), jsonObject.getString("PaymentId"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("RESPONSE OF PAYMENT ID", result.toString());
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
            Volley.newRequestQueue(getContext()).add(sr);
            arrayAdapterPayments.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            arrayAdapterPayments.notifyDataSetChanged();
            spinner_payment_id.setAdapter(arrayAdapterPayments);
        }

    private String getRealPathFromURI(Uri contentURI) {

        String thePath = "no-path-found";
        String[] filePathColumn = {MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = getContext().getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            thePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return  thePath;
    }


    private void printErrorMessage(VolleyError error) {
        try {
            String message = "";
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Iterator<String> keys = data.keys();
            while(keys.hasNext()) {
                String key = keys.next();
//                if (data.get(key) instanceof JSONObject) {
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
