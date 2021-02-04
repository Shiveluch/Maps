package com.example.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class master extends AppCompatActivity {
TextView create_event,master_login,master_password;
Button create;
String sbname="http://kamonline.r41.ru/Strateg/get_event.php";
public String currentEvent;
public String mlog="";
public String mpass="";
String alpha="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
String newpassword="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        create_event = findViewById(R.id.event_name);
        create = findViewById(R.id.event);
        master_login=findViewById(R.id.master_login);
        master_password=findViewById(R.id.master_password);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlog=master_login.getText().toString();
                mpass=master_password.getText().toString();

                new MyAsyncTask().execute(create_event.getText().toString());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getJSON(sbname);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("pass","event:"+currentEvent);

            }
        });

    }




    private class AddPasscodesAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0]);
            return null;
        }

        protected void onPostExecute(Double result) {

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String name) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/Strateg/add_strateg_passcodes.php/get.php?log="+mlog+"&pass="+mpass);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("name", name));
                Log.d("UpdateEvent", "Event: " + name);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {

            } catch (IOException e) {
            }

        }
    }

    private class ClearEventAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0]);
            return null;
        }

        protected void onPostExecute(Double result) {

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String name) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/Strateg/clear_passcodes.php/get.php?log="+mlog+"&pass="+mpass);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("name", name));
                Log.d("UpdateEvent", "Event: " + name);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {

            } catch (IOException e) {
            }

        }
    }



    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0]);
            return null;
        }

        protected void onPostExecute(Double result) {

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String name) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/Strateg/add_event.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("name", name));
               Log.d("UpdateEvent", "Event: " + name);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {

            } catch (IOException e) {
            }

        }
    }

    private void getJSON(final String urlWebService) {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    try {
                        if (urlWebService == sbname) {
                            loadevent(s);
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected String doInBackground(Void... voids) {


                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }


        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadevent(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        //info_s = "Координаты: ";
        Log.d ("GetJSON", "Taking data...");


        String[] id_s = new String[jsonArray.length()];
        String[] name_s = new String[jsonArray.length()];
        Log.d ("pass", ""+jsonArray.length());

        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d ("pass", "Parsing data from JSON");

                JSONObject obj = jsonArray.getJSONObject(i);
                id_s[i] = obj.getString("id");
                Log.d ("pass", ""+id_s[i]);

                name_s[i] = obj.getString("name");
                Log.d ("pass", ""+name_s[i]);

                currentEvent=id_s[0];
                Log.d("logging",mlog+", "+mpass);


                Log.d("GetJSON", "" + currentEvent + ":" + name_s[i]);
                new ClearEventAsyncTask().execute("0");
                new AddPasscodesAsyncTask().execute("0");


            }

        }



    }



    private class CreatePasscodeAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0], params[1], params[2],params[3],params[4],params[5]);
            return null;
        }

        protected void onPostExecute(Double result) {

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String password, String status, String side, String  division, String team, String event) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/Strateg/add_passcodes.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("password", password));
                nameValuePairs.add(new BasicNameValuePair("status_id", status));
                nameValuePairs.add(new BasicNameValuePair("side_id", side));
                nameValuePairs.add(new BasicNameValuePair("division_id", division));
                nameValuePairs.add(new BasicNameValuePair("team_id", team));
                nameValuePairs.add(new BasicNameValuePair("event_id",event));
               // Log.d("UpdateEvent", "Event: " + name);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {

            } catch (IOException e) {
            }

        }
    }

}