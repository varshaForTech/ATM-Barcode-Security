package com.atm.atm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class UpdateDatabase extends AppCompatActivity {

    EditText hname, accno, cifnio, acct, haddress, phoneno, email, atmno, bankcode, issuedate, pin, cvv, adharno;
    Button send;
    String hname_s, accno_s, cifno_s, acct_s, haddress_s, phoneno_s, emai_s, atmno_s, bancode_s, issuedate_s, pin_s, cvv_s, adharno_s;
    private ProgressDialog pDialog;
    String responseServer, report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_update_database);

        hname = (EditText) findViewById(R.id.name);
        accno = (EditText) findViewById(R.id.accno);
        cifnio = (EditText) findViewById(R.id.cif);
        acct = (EditText) findViewById(R.id.accounttype);
        haddress = (EditText) findViewById(R.id.haddress);
        phoneno = (EditText) findViewById(R.id.phonen);
        email = (EditText) findViewById(R.id.emails);
        atmno = (EditText) findViewById(R.id.atmnos);
        bankcode = (EditText) findViewById(R.id.bankcode);
        issuedate = (EditText) findViewById(R.id.issued);
        pin = (EditText) findViewById(R.id.pins);
        cvv = (EditText) findViewById(R.id.cvvs);
        adharno = (EditText) findViewById(R.id.adharc);

        send = (Button) findViewById(R.id.update);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hname_s = hname.getText().toString();
                accno_s = accno.getText().toString();
                cifno_s = cifnio.getText().toString();
                acct_s = acct.getText().toString();
                haddress_s = haddress.getText().toString();
                phoneno_s = phoneno.getText().toString();
                emai_s = email.getText().toString();
                atmno_s = atmno.getText().toString();
                bancode_s = bankcode.getText().toString();
                issuedate_s = issuedate.getText().toString();
                pin_s = pin.getText().toString();
                cvv_s = cvv.getText().toString();
                adharno_s = adharno.getText().toString();

                int count = 0;
                if (hname.length() == 0) {
                    showDialogAlert("Require Holder Name Filed");
                    count++;
                }
                if (accno.length() != 11) {
                    showDialogAlert("Invaide Account Number");
                    count++;
                }
                if (cifno_s.length() != 11) {
                    showDialogAlert("Invaide CIF Number");
                    count++;
                }
                if (acct_s.length() == 0) {
                    showDialogAlert("Invaide AccountType");
                    count++;
                }
                if (haddress_s.length() == 0) {
                    showDialogAlert("Required Holder Address");
                    count++;
                }
                if (phoneno_s.length() != 10) {
                    showDialogAlert("Invalide Mobile Number");
                    count++;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emai_s).matches()) {
                    showDialogAlert("Invalide Email Address");
                    count++;
                }
                if (atmno_s.length() != 16) {
                    showDialogAlert("Invalide ATM Number");
                    count++;
                }
                if (bancode_s.length() != 7) {
                    showDialogAlert("Invalide Branch Code");
                    count++;
                }
                if (issuedate_s.length() == 0) {
                    showDialogAlert("Invalide Issue Date");
                    count++;
                }
                if (pin_s.length() != 4) {
                    showDialogAlert("Invalide Pin");
                    count++;
                }
                if (cvv_s.length() != 3) {
                    showDialogAlert("Invalide CVV");
                    count++;
                }
                if (adharno_s.length() != 16) {
                    showDialogAlert("Invalide Adhar Number");
                    count++;
                }
                if (count == 0) {
                    Toast.makeText(UpdateDatabase.this, "All are verified", Toast.LENGTH_SHORT).show();
                    AsyncT asyncT = new AsyncT();
                    asyncT.execute();
                }

            }
        });


    }


    void showDialogAlert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(UpdateDatabase.this).create();
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


    class AsyncT extends AsyncTask<Object, Object, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UpdateDatabase.this);
            pDialog.setMessage("Updating......");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Object... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://varshadhoni.000webhostapp.com/ATM/Update.php");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", hname_s));
                nameValuePairs.add(new BasicNameValuePair("accountNo", accno_s));
                nameValuePairs.add(new BasicNameValuePair("cifno", cifno_s));
                nameValuePairs.add(new BasicNameValuePair("accounttype", acct_s));
                nameValuePairs.add(new BasicNameValuePair("address", haddress_s));
                nameValuePairs.add(new BasicNameValuePair("phoneno", phoneno_s));
                nameValuePairs.add(new BasicNameValuePair("email", emai_s));
                nameValuePairs.add(new BasicNameValuePair("atmno", atmno_s));
                nameValuePairs.add(new BasicNameValuePair("bankcode", bancode_s));
                nameValuePairs.add(new BasicNameValuePair("issuedate", issuedate_s));
                nameValuePairs.add(new BasicNameValuePair("pin", pin_s));
                nameValuePairs.add(new BasicNameValuePair("cvv", cvv_s));
                nameValuePairs.add(new BasicNameValuePair("adharcard", adharno_s));

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
                    Log.e("Report", report);
                }
            } catch (Exception e) {
                Log.e("varsha__sister", e.toString());
                e.printStackTrace();
            }

            if (pDialog.isShowing())
                pDialog.dismiss();

            if (report.equals("Updated")) {
                Toast.makeText(UpdateDatabase.this, "Updated Details", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateDatabase.this, DataBaseList.class));
            } else if (report.equals("not updated")) {
                showDialogAlert("Not updated Something Wrong");
            } else {
                showDialogAlert("Problem with Updating");
            }

        }
    }


}
