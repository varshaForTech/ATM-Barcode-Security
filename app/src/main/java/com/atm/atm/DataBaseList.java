package com.atm.atm;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class DataBaseList extends AppCompatActivity {

    ListView listView;
    String responseServer;
    ArrayList<HashMap<String, String>> contactList = new ArrayList<>();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base_list);
        AsyncT asyncT = new AsyncT();
        asyncT.execute();

        listView = (ListView) findViewById(R.id.list);

    }


    class AsyncT extends AsyncTask<Object, Object, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DataBaseList.this);
            pDialog.setMessage("Searching......");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Object... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://varshadhoni.000webhostapp.com/ATM/database_list.php");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                InputStream inputStream = response.getEntity().getContent();
                InputStreamToStringExample str = new InputStreamToStringExample();

                responseServer = str.getStringFromInputStream(inputStream);
                Log.e("response", "response -----" + responseServer);


                JSONObject json = new JSONObject(responseServer);
                JSONArray results = json.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject c = results.getJSONObject(i);

                    String nameh = c.getString("HolderName");
                    String accno = c.getString("AccountNumber");
                    String cifno = c.getString("CIFNumber");
                    String accounttp = c.getString("AccountType");
                    String hoderadds = c.getString("HolderAddress");
                    String phoneno = c.getString("PhoneNumber");
                    String emails = c.getString("Email");
                    String atm = c.getString("ATMNo");
                    String bankc = c.getString("BankCode");
                    String issue = c.getString("IssueDate");
                    String pins = c.getString("PIN");
                    String cvvs = c.getString("CVV");
                    String adhar1 = c.getString("AdharCard");

                    HashMap<String, String> contact = new HashMap<>();

                    contact.put("holdername", nameh);
                    contact.put("accountnumber", accno);
                    contact.put("cifnumber", cifno);
                    contact.put("accounttype", accounttp);
                    contact.put("holderaddress", hoderadds);
                    contact.put("pnonenumber", phoneno);
                    contact.put("email", emails);
                    contact.put("atmnumber", atm);
                    contact.put("bankcode", bankc);
                    contact.put("issuedate", issue);
                    contact.put("pin", pins);
                    contact.put("cvv", cvvs);
                    contact.put("adharcard", adhar1);

                    contactList.add(contact);
                }
            } catch (Exception e) {
                Log.e("sister", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    DataBaseList.this, contactList,
                    R.layout.list_db, new String[]{"holdername", "accountnumber", "cifnumber", "accounttype", "holderaddress",
                    "pnonenumber", "email", "atmnumber", "bankcode", "issuedate", "pin", "cvv", "adharcard"},
                    new int[]{R.id.name, R.id.accountno, R.id.cifno, R.id.acctype, R.id.address,
                            R.id.phoneno, R.id.email, R.id.atmno,
                            R.id.bancode, R.id.issuedate, R.id.pinno, R.id.cvvno, R.id.adharno});
            listView.setAdapter(adapter);
        }
    }
}