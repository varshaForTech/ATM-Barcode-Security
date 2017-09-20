package com.atm.atm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class EnterCredentials extends AppCompatActivity {

    EditText adharcard, cvv, pin;
    Button check;
    String adhar_str, cvv_str, pin_str;
    private ProgressDialog pDialog;
    String responseServer, report, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_credentials);

        check = (Button) findViewById(R.id.check);

        adharcard = (EditText) findViewById(R.id.adharCard);
        cvv = (EditText) findViewById(R.id.cvv);
        pin = (EditText) findViewById(R.id.pin);

        adhar_str = adharcard.getText().toString();
        cvv_str = cvv.getText().toString();
        pin_str = pin.getText().toString();


        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adhar_str = adharcard.getText().toString();
                cvv_str = cvv.getText().toString();
                pin_str = pin.getText().toString();

                int count = 0;
                if (adhar_str.length() != 16) {
                    count++;
                    showDialogAlert("AdharCard Invalid");
                }
                if (pin_str.length() != 4) {
                    count++;
                    showDialogAlert("Pin Invalid");
                }
                if (cvv_str.length() != 3) {
                    count++;
                    showDialogAlert("CVV Invalid");
                }
                if (count == 0) {
                    Toast.makeText(EnterCredentials.this, "All are Correct", Toast.LENGTH_SHORT).show();
                    Server server = new Server();
                    server.execute();
                }
            }
        });
    }

    void showDialogAlert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(EnterCredentials.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.error);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    class Server extends AsyncTask<Object, Object, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(EnterCredentials.this);
            pDialog.setMessage("Searching......");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Object... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://varshadhoni.000webhostapp.com/ATM/getDetails.php");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("Adhar", adhar_str));
                nameValuePairs.add(new BasicNameValuePair("pin", pin_str));
                nameValuePairs.add(new BasicNameValuePair("cvv", cvv_str));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                InputStream inputStream = response.getEntity().getContent();
                InputStreamToStringExample str = new InputStreamToStringExample();

                responseServer = str.getStringFromInputStream(inputStream);
                Log.e("response", "response -----" + responseServer);

            } catch (Exception e) {
                Log.e("sister", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                JSONObject json = new JSONObject(responseServer);
                JSONArray results = json.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject c = results.getJSONObject(i);
                    report = c.getString("message");
                    code = c.getString("barcodeValue");
                    Log.e("Report", report);
                }
            } catch (Exception e) {
                Log.e("varsha__sister", e.toString());
                e.printStackTrace();
            }

            if (pDialog.isShowing())
                pDialog.dismiss();

            if (report.equals("Deny")) {
                showDialogAlert("Invalide Adhar/Pin/CVV");
            } else if (report.equals("Grant")) {
                barcodeGenerate(code);
            } else {
                showDialogAlert("Something Wrong");
            }

        }
    }

    void barcodeGenerate(String text2Qr) {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {

            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Intent intent = new Intent(EnterCredentials.this, QrActivity.class);
            intent.putExtra("pic", bitmap);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
