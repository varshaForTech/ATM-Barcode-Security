package com.atm.atm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class MainActivity extends AppCompatActivity {

    EditText mobile_number;
    Button send;
    String number;
    String responseServer;
    String report;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mobile_number = (EditText) findViewById(R.id.mobileno);
        send = (Button) findViewById(R.id.send);

        number = mobile_number.getText().toString();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = mobile_number.getText().toString();
                if (number.length() == 10) {

                    Toast.makeText(MainActivity.this, number, Toast.LENGTH_SHORT).show();
                    AsyncT asyncT = new AsyncT();
                    asyncT.execute();

                } else {
                    showDialogAlert("Not a Mobile Number");
                    return;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.database) {
            startActivity(new Intent(MainActivity.this, DataBaseList.class));
            return true;
        }
        if (id == R.id.createnew) {
            startActivity(new Intent(MainActivity.this, UpdateDatabase.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void showDialogAlert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Searching......");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Object... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://varshadhoni.000webhostapp.com/ATM/verify.php");


            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("MobileNumber", number));
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

            if (report.equals("found")) {
                startActivity(new Intent(MainActivity.this, EnterCredentials.class));
            } else if (report.equals("not found")) {
                showDialogAlert("Number is not Registered");
            } else {
                showDialogAlert("Not a valid");
            }

        }
    }
}


class InputStreamToStringExample {

    public static void main(String[] args) throws IOException {

        // intilize an InputStream
        InputStream is =
                new ByteArrayInputStream("file content..blah blah".getBytes());

        String result = getStringFromInputStream(is);

        Log.e("result value server:", result);


    }

    // convert InputStream to String
    static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}





