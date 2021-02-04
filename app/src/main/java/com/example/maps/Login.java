package com.example.maps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.example.maps.MapsActivity.APP_PREFERENCES;

public class Login extends AppCompatActivity {
    String sbname = "http://kamonline.r41.ru/Strateg/get_pass_info.php/get.php?nom=";
    String player="http://kamonline.r41.ru/Strateg/get_players_info.php";
    String summary="";
    String get_pass="";
    String alpha="ABCDEFHIKLMNPQRSTUVWXYZ123456789";
    String command="";


    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "mysettings";//filename
    public static final String APP_PREFERENCES_NAME = "Nickname";//player's nickname
    public static final String APP_PREFERENCES_PASSWORD = "Password";//player's system password
    public static final String APP_PREFERENCES_EVENTPASS="Eventpass";//player's event password
    public static final String APP_PREFERENCES_STATUS_ID = "Status_ID";//player's STATUS_ID
    public static final String APP_PREFERENCES_STATUS = "Status";//player's STATUS
    public static final String APP_PREFERENCES_SIDE_ID = "Side_id";//player's side ID
    public static final String APP_PREFERENCES_SIDE = "Side";//player's SIDENAME
    public static final String APP_PREFERENCES_DIVISION_ID = "Division_id";//player's division ID
    public static final String APP_PREFERENCES_DIVISION = "Division";//player's DIVISION
    public static final String APP_PREFERENCES_TEAM_ID = "Team_id";//current team
    public static final String APP_PREFERENCES_TEAM = "Team";//current team
    public static final String APP_PREFERENCES_EVENT_ID = "Event_id"; //current event_ID
    public static final String APP_PREFERENCES_EVENT= "Event"; //current event

    //TextView first_login, first_password;
    EditText first_login,first_password,event_password;
    Button OK, new_login;
    String make_pass="";
    public String have_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        first_login = findViewById(R.id.first_login);
        first_password = findViewById(R.id.first_password);
        event_password=findViewById(R.id.event_password);
        OK = findViewById(R.id.old_login);
        new_login = findViewById(R.id.new_login);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        have_pass = mSettings.getString(APP_PREFERENCES_PASSWORD,"");


        if (mSettings.contains(APP_PREFERENCES_NAME))
            summary += mSettings.getString(APP_PREFERENCES_NAME, "0");
        if (mSettings.contains(APP_PREFERENCES_TEAM))
            summary += mSettings.getString(APP_PREFERENCES_TEAM, "0");
        if (mSettings.contains(APP_PREFERENCES_SIDE))
            summary += mSettings.getString(APP_PREFERENCES_SIDE, "0");
        if (mSettings.contains(APP_PREFERENCES_DIVISION))
            summary += mSettings.getString(APP_PREFERENCES_DIVISION, "0");
        if (mSettings.contains(APP_PREFERENCES_EVENT))
            summary += mSettings.getString(APP_PREFERENCES_EVENT, "0");

        String player = mSettings.getString(APP_PREFERENCES_NAME, "");
        if (player.length() > 0) first_login.setText(player);

        Log.d("Player: ", player + ", " + summary);

        if (have_pass.length()==0) {
            OK.setEnabled(false);
            for (int i = 0; i < 5; i++) {
                Random random = new Random();
                int pos = random.nextInt(32);
                make_pass += alpha.charAt(pos);

            }
            first_password.setText(make_pass);
          //  new CreateUserAsyncTask().execute(first_login.getText().toString(),make_pass);
        }

        else {first_password.setText(have_pass);}

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            //    getJSON(player);
                Intent i;

                i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });


        // getJSON(sbname);


        new_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (first_login.getText().toString().length()==0)
                {
                    Log.d("USER","Не введен позывной");

                }
                if (first_password.getText().toString().length()<5 || event_password.getText().toString().length()<5) {
                    Log.d("USER","Отсутствует корректный пароль");
                }





                if (first_login.getText().toString().length()>0 && first_password.getText().toString().length()==5
                        && event_password.getText().toString().length()==5)
                {
                    SharedPreferences.Editor editor=mSettings.edit();
                    editor.putString(APP_PREFERENCES_PASSWORD,first_password.getText().toString());
                    editor.putString(APP_PREFERENCES_EVENTPASS,event_password.getText().toString());
                    editor.apply();
                    have_pass=mSettings.getString(APP_PREFERENCES_PASSWORD,"");
                    get_pass=sbname+event_password.getText().toString();
                    getJSON(get_pass);

                }

            }
        });
    }

    private void AddJSON(final String urlWebService) {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                Log.d("GetJSON", "PreExecute");
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("GetJSON command",s);

                if (s != null) {
                    try {
                        if (urlWebService==command)

                        {addPlayer(s);
                            Log.d("GetJSON", "loadplayers");}



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


        GetJSON JSON = new GetJSON();
        JSON.execute();
    }



    private void getJSON(final String urlWebService) {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                Log.d("GetJSON", "PreExecute");
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("GetJSON all",s);

                if (s != null) {
                    try {
                           if (urlWebService==get_pass)

                           {loadpassinfo(s);
                            Log.d("GetJSON", "loadplayers");}

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

    private void addPlayer(String json) throws JSONException {}





    private void loadpassinfo(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        //info_s = "Координаты: ";

        if (jsonArray.length()==0) Toast.makeText(this,"Пароль не найден",Toast.LENGTH_LONG);
        Log.d ("GetJSON", "Taking data...");


        String[] id_s = new String[jsonArray.length()];
        String[] status_id_s = new String[jsonArray.length()];
        String[] status_s = new String[jsonArray.length()];
        String[] side_id_s= new String[jsonArray.length()];
        String[] side_s = new String[jsonArray.length()];
        String[] division_id_s = new String[jsonArray.length()];
        String[] division_s = new String[jsonArray.length()];
        String[] team_id_s= new String[jsonArray.length()];
        String[] team_s = new String[jsonArray.length()];
        String[] event_id_s = new String[jsonArray.length()];
        String[] event_s = new String[jsonArray.length()];





        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                id_s[i] = obj.getString("id_s");
                status_id_s[i] = obj.getString("status_id");
                status_s[i] = obj.getString("status");
                side_id_s[i] = obj.getString("side_id");
                side_s[i] = obj.getString("side");
                division_id_s[i] = obj.getString("division_id");
                division_s[i]=obj.getString("division");
                team_id_s[i] = obj.getString("team_id");
                team_s[i]=obj.getString("teamname");
                event_id_s[i] = obj.getString("event_id");
                event_s[i] = obj.getString("event");
                Log.d("GetJSON", "" + id_s[i] + ":" + status_id_s[i] + ":" + status_s[i]
                        + ":" + side_id_s[i] + ":" + side_s[i] + ":" + division_id_s[i]  + ":" + division_s[i] + ":"+
                        team_id_s[i] + ":"+team_s[i]  + ":"+event_id_s[i]+event_s[i]  );

                SharedPreferences.Editor editor=mSettings.edit();
                editor.putString(APP_PREFERENCES_NAME,first_login.getText().toString());
                editor.putString(APP_PREFERENCES_STATUS_ID,status_id_s[i]);
                editor.putString(APP_PREFERENCES_STATUS,status_s[i]);
                editor.putString(APP_PREFERENCES_SIDE_ID,side_id_s[i]);
                editor.putString(APP_PREFERENCES_SIDE,side_s[i]);
                editor.putString(APP_PREFERENCES_DIVISION_ID,division_id_s[i]);
                editor.putString(APP_PREFERENCES_DIVISION,division_s[i]);
                editor.putString(APP_PREFERENCES_TEAM_ID,team_id_s[i]);
                editor.putString(APP_PREFERENCES_TEAM,team_s[i]);
                editor.putString(APP_PREFERENCES_EVENT_ID,event_id_s[i]);
                editor.putString(APP_PREFERENCES_EVENT,event_id_s[i]);
                editor.apply();

                String team_pref=mSettings.getString(APP_PREFERENCES_TEAM,"0");
                String div_pref=mSettings.getString(APP_PREFERENCES_DIVISION,"0");
                String side_pref = mSettings.getString(APP_PREFERENCES_SIDE,"0");
                String name_pref=mSettings.getString(APP_PREFERENCES_NAME,"0");
                Log.d ("PREF",team_pref+","+div_pref+", "+side_pref+","+name_pref);
                String command="http://kamonline.r41.ru/Strateg/check.php/get.php?nickname="+mSettings.getString(APP_PREFERENCES_NAME,"")
                        +"&pass="+mSettings.getString(APP_PREFERENCES_PASSWORD,"")
                        +"&status_id="+mSettings.getString(APP_PREFERENCES_STATUS_ID,"")
                        +"&side_id="+mSettings.getString(APP_PREFERENCES_SIDE_ID,"")
                        +"&division_id="+mSettings.getString(APP_PREFERENCES_DIVISION_ID,"")
                        +"&team_id="+mSettings.getString(APP_PREFERENCES_TEAM_ID,"")
                        +"&event_id="+mSettings.getString(APP_PREFERENCES_EVENT_ID,"");
                AddJSON(command);
                Intent m;
                m = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(m);

                //http://kamonline.r41.ru/Strateg/check.php/get.php?nickname=%D0%A2%D1%83%D0%BF%D0%BE%D0%B9&pass=R&status_id=1&side_id=1&division_id=1&team_id=1&event_id=1



            }

        }
        else
        {
            Log.d("GetJSON","Не найден пароль события");
        }
    }




    private class CreateUserAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(Double result) {

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String nickname, String password) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/Strateg/add_player.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("nickname", nickname));
                nameValuePairs.add(new BasicNameValuePair("passcode", password));

                Log.d("UpdateBase", "nick: " + nickname + " pass: " + password);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {

            } catch (IOException e) {
            }

        }
    }
}