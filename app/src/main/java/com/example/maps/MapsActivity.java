package com.example.maps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocListenerInterface {

    private GoogleMap mMap;
    public static final int RequestPermissionCode = 1;
    Button button, button2, button3, dataset, timeset;
    Context context;
    Location location;
    EditText nick, password;
    public String Date, Time;
    String team, side, event;
    String sbname = "http://kamonline.r41.ru/Strateg/get_players_info.php";
    String strateg_getinfo = "http://kamonline.r41.ru/Strateg/get_team_strateg.php/get.php?nom=";
    String strateg_getplayer="http://kamonline.r41.ru/Strateg/get_strateg_player_byname.php/get.php?nom=";
    String getinfo="";
    String getTeamInfo="";
    String _password;

    public int teammate;
    private LocationManager locationManager;
    private Location LastLocation;
    private MyLocListener myLocListener;
    public double lat, lon, touch_lat, touch_lon;
    public int radiation = 0;
    boolean GpsStatus = false;
    Criteria criteria;
    public int counter = 0, dang_counter = 0;
    Calendar dateAndTime = Calendar.getInstance();
String currentName,currentTeam,currentSide;
    double center_lat = 53.067530;
    double center_lon = 158.622869;
    int sam_radius = 2000;
    String name,teamname,sidename;
    String summary="";
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    public static final String APP_PREFERENCES = "mysettings";//filename
    public static final String APP_PREFERENCES_NAME = "Nickname";//player's nickname
    public static final String APP_PREFERENCES_TEAM = "Team";//current team
    public static final String APP_PREFERENCES_SIDE = "Side"; //current side
    public static final String APP_PREFERENCES_ID = "ID"; //current side
    SharedPreferences mSettings;
    public double[] anomaly_latitudes =
            {60.667923,//1
                    60.674902,//2
                    60.681165,//3
                    55.527917,//4
                    55.5275223,//5
                    55.3133,//6
                    53.093370,//7
                    53.056837
            };
    public double[] anomaly_longitudes =
            {29.177713,//1
                    29.173336,//2
                    29.161963,//3
                    28.6753678,//4
                    28.6707544,//5
                    28.4018,//6
                    158.851072,//7
                    158.663521//8
            };
    public int[] anomaly_draw_res =
            {
                    R.drawable.rad, //1
                    R.drawable.rad1,//2
                    R.drawable.rad2,//3
                    R.drawable.rad,//4
                    R.drawable.rad1,//5
                    R.drawable.rad2,//6
                    R.drawable.rad, //7
                    R.drawable.rad //7
            };
    public String[] anomaly_titles =
            {"Радиация. Пятно 100 метров",//1
                    "Радиация. Пятно 20 метров", //2
                    "Радиация. Пятно 300 метров",//3
                    "Радиация. Пятно 50 метров",//4
                    "Радиация. Пятно 30 метров",//5
                    "Радиация. Пятно 100 метров",//6
                    "Радиация. Пятно 50 метров",//7
                    "Радиация. Пятно 50 метров"

            };
    public int[] anomaly_radius = {100, 20, 300, 50, 30, 100, 50, 50};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EnableRuntimePermission();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        myLocListener = new MyLocListener();
        dataset = findViewById(R.id.dateset);
        timeset = findViewById(R.id.timeset);
        nick = findViewById(R.id.nick);
        password = findViewById(R.id.password);

        criteria = new Criteria();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.coord);
        if (mSettings.contains(APP_PREFERENCES_NAME))
        summary+=mSettings.getString(APP_PREFERENCES_NAME,"0");
        if (mSettings.contains(APP_PREFERENCES_TEAM))
            summary+=mSettings.getString(APP_PREFERENCES_TEAM,"0");
        if (mSettings.contains(APP_PREFERENCES_SIDE))
            summary+=mSettings.getString(APP_PREFERENCES_SIDE,"0");
        if (mSettings.contains(APP_PREFERENCES_ID))
            summary+=mSettings.getString(APP_PREFERENCES_ID,"0");
        String player=mSettings.getString(APP_PREFERENCES_NAME,"0");
        Log.d("Player",player);
        getinfo=strateg_getplayer+player;

        getJSON(getinfo);

        //button.setText(summary);




        setInitialDate();
        setInitialTime();


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng anomaly1 = new LatLng(60.667923, 29.177713);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(anomaly1));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String new_codename = password.getText().toString();
                if (new_codename.length()>=3){
                team = "" + new_codename.charAt(0);
                side = "" + new_codename.charAt(1);
                event = "" + new_codename.charAt(2);}
                int _nick = nick.getText().toString().length();

                Log.d("Nick", "" + _nick);

                if (_nick != 0) {
                    summary="";
                    new StrategAsyncTask().execute(nick.getText().toString(), team, side, "0", "0", event);

                    currentName=nick.getText().toString();
                    getinfo=strateg_getplayer+currentName;
                    getJSON(getinfo);
                    currentTeam=team;
                    currentSide=side;
                    SharedPreferences.Editor editor=mSettings.edit();
                    editor.putString(APP_PREFERENCES_NAME,currentName);
                    editor.putString(APP_PREFERENCES_TEAM,currentTeam);
                    editor.putString(APP_PREFERENCES_SIDE,currentSide);
                    editor.apply();
                    if (mSettings.contains(APP_PREFERENCES_NAME))
                        summary+=mSettings.getString(APP_PREFERENCES_NAME,"0");

                    if (mSettings.contains(APP_PREFERENCES_TEAM))
                        summary+=mSettings.getString(APP_PREFERENCES_TEAM,"0");
                    if (mSettings.contains(APP_PREFERENCES_SIDE))
                        summary+=mSettings.getString(APP_PREFERENCES_SIDE,"0");
                    Toast.makeText(MapsActivity.this, summary, Toast.LENGTH_LONG).show();
                    //button.setText(summary);

                }}});





        CheckGpsStatus();

        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {

                    try {
                        Thread.sleep(1000);  //1000ms = 1 sec

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                CheckGpsStatus();
                                if (GpsStatus == true) {
                                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                            && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        Log.d("Permisns", "недостаточно разрешений");
                                        Toast.makeText(MapsActivity.this, "Недостаточно разрешений", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, myLocListener);
                                    Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                                    //dataset.setText(Date);

                                    if (location != null) {


                                        double cLat = location.getLatitude();
                                        double cLon = location.getLongitude();
                                        lat=cLat;
                                        lon=cLon;
                                        LatLng position = new LatLng(cLat, cLon);

                                        button3.setText("" + cLat + "," + cLon);
                                        Log.d("GPSpos", "Timer: -" + position.latitude + ", " + position.longitude);
                                        dang_counter++;
                                        if (dang_counter > 30 && sam_radius > 100) {
                                            sam_radius = sam_radius - 100;
                                            dang_counter = 0;
                                        }


                                        int zone = (int) (distanceInKmBetweenEarthCoordinates(center_lat, center_lon, position.latitude, position.longitude) * 1000);
                                        if (zone <= sam_radius) {
                                            //button3.setText(button3.getText().toString() + "\n" + "Расстояние до границ опасной зоны " + (sam_radius - zone) + " метров");
                                        }
                                        if (zone > sam_radius) {
                                            //button3.setText(button3.getText().toString() + "\n" + "За границами опасной зоны на " + (Math.abs((sam_radius - zone))) + " метров");
                                        }

                                        for (int i = 0; i < anomaly_radius.length; i++) {
                                            int dist = (int) (distanceInKmBetweenEarthCoordinates(cLat, cLon, anomaly_latitudes[i], anomaly_longitudes[i]) * 1000); //в метрах
                                            if (dist < anomaly_radius[i]) {
                                                radiation++;
                                                button3.setText(button3.getText().toString() + "\n" + "Опасность в " + dist + " метрах. " + "\n" + anomaly_titles[i]
                                                        + "\n" + "Накопленная радиация: " + radiation);


                                            }

                                        }

                                        counter++;
                                        if (counter > 10) {
                                            mMap.clear();
                                            String id=mSettings.getString(APP_PREFERENCES_ID,"0");
                                            new MyAsyncTask().execute(id, "" + cLat, "" + cLon);
                                            counter = 0;
                                            //getJSON(sbname);
                                            mMap.addMarker(new MarkerOptions().position(position).icon(getBitmapHighDescriptor(R.drawable.stalker)).title("I'm here"));
                                        }

                                        mMap.setMyLocationEnabled(true);

                                        LatLng[] anomaly = new LatLng[anomaly_latitudes.length];
                                                                               for (int i = 0; i < anomaly_latitudes.length; i++) {
                                            anomaly[i] = new LatLng(anomaly_latitudes[i], anomaly_longitudes[i]);
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(anomaly[i])
                                                    .title(anomaly_titles[i])
                                                    .icon(getBitmapDescriptor(anomaly_draw_res[i])));
                                        }
                                        LatLng touch = new LatLng(touch_lat, touch_lon);

                                        mMap.addMarker(new MarkerOptions()
                                                .position(touch)
                                                .title("Точка")
                                        );

                                    }
                                }
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        t.start();
    }

    private void setInitialDate() {
        dataset.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));


    }

    private void setInitialTime() {
        timeset.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));

    }

    public void setDate(View v) {
        new DatePickerDialog(MapsActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();


    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(MapsActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();

    }


    // установка начальных даты и времени
    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            Time = "" + dateAndTime.get(Calendar.HOUR_OF_DAY) + ":" + (dateAndTime.get(Calendar.MINUTE));
            //timeset.setText(Time);
            setInitialTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Date = "" + dateAndTime.get(Calendar.YEAR) + "/" + (dateAndTime.get(Calendar.MONTH) + 1) + "/" + dateAndTime.get(Calendar.DAY_OF_MONTH);
            //dataset.setText(Date);
            setInitialDate();
        }
    };

    public void sending(View v) {
        String t_lat = "" + touch_lat;
        String t_lon = "" + touch_lon;
        String radius = "" + sam_radius;

        new SendAsyncTask().execute("5115", Date, Time, t_lat, t_lon, radius);

    }

    public String codename;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {

            } else {

                String new_codename = result.getContents();
                team = "" + new_codename.charAt(0);
                side = "" + new_codename.charAt(1);
                event = "" + new_codename.charAt(2);
                int _nick = nick.getText().toString().length();

                Log.d("Nick", "" + _nick);

                if (_nick != 0) {
                    summary="";
                    new StrategAsyncTask().execute(nick.getText().toString(), team, side, "0", "0", event);

                    currentName=nick.getText().toString();
                    getinfo=strateg_getplayer+currentName;
                    getJSON(getinfo);
                    currentTeam=team;
                    currentSide=side;
                    SharedPreferences.Editor editor=mSettings.edit();
                    editor.putString(APP_PREFERENCES_NAME,currentName);
                    editor.putString(APP_PREFERENCES_TEAM,currentTeam);
                    editor.putString(APP_PREFERENCES_SIDE,currentSide);
                    editor.apply();
                    if (mSettings.contains(APP_PREFERENCES_NAME))
                        summary+=mSettings.getString(APP_PREFERENCES_NAME,"0");

                    if (mSettings.contains(APP_PREFERENCES_TEAM))
                        summary+=mSettings.getString(APP_PREFERENCES_TEAM,"0");
                    if (mSettings.contains(APP_PREFERENCES_SIDE))
                        summary+=mSettings.getString(APP_PREFERENCES_SIDE,"0");
                    Toast.makeText(MapsActivity.this, summary, Toast.LENGTH_LONG).show();
                    //button.setText(summary);

                } else
                    Toast.makeText(MapsActivity.this, "Не введен позывной", Toast.LENGTH_LONG).show();


            }


        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    final Activity activity = this;

    public void showCurrent(MenuItem item)
    {
        Toast.makeText(MapsActivity.this, "Мои координаты: " + lat+ ", " + lon, Toast.LENGTH_LONG).show();
        LatLng current=new LatLng(lat,lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));


    }

    public void info(MenuItem item)
    {
        Toast.makeText(MapsActivity.this, "Позывной: " + name + "\n" + "Команда: "+ teamname
                +"\n"+"Сторона: "+sidename, Toast.LENGTH_LONG).show();


    }

    public void autologin (MenuItem item)
    {
        new StrategAsyncTask().execute("Лаврик", "3", "3", "0", "0", "3");

        currentName="Лаврик";
        getinfo=strateg_getplayer+currentName;
        getJSON(getinfo);
        currentTeam="3";
        currentSide="3";
        SharedPreferences.Editor editor=mSettings.edit();
        editor.putString(APP_PREFERENCES_NAME,currentName);
        editor.putString(APP_PREFERENCES_TEAM,currentTeam);
        editor.putString(APP_PREFERENCES_SIDE,currentSide);
        editor.apply();
        if (mSettings.contains(APP_PREFERENCES_NAME))
            summary+=mSettings.getString(APP_PREFERENCES_NAME,"0");

        if (mSettings.contains(APP_PREFERENCES_TEAM))
            summary+=mSettings.getString(APP_PREFERENCES_TEAM,"0");
        if (mSettings.contains(APP_PREFERENCES_SIDE))
            summary+=mSettings.getString(APP_PREFERENCES_SIDE,"0");
       //Toast.makeText(MapsActivity.this, summary, Toast.LENGTH_LONG).show();


    }

    public void teamInfo(MenuItem item)
    {
        currentTeam=mSettings.getString(APP_PREFERENCES_TEAM,"0");
        getTeamInfo=strateg_getinfo+currentTeam;
        getJSON(getTeamInfo);

    }

    public void scanner(MenuItem item) {

        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt(" ");
        integrator.setCameraId(0);
        integrator.autoWide();
        //         integrator.setBeepEnabled(false);
        //     integrator.setBarcodeImageEnabled(false);
        try {
            integrator.initiateScan();
        } catch (Exception e) {

        }
        //f_but.setVisibility(View.GONE);
        // b_but.setVisibility(View.GONE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        LatLng anomaly1 = new LatLng(60.667923, 29.177713);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(anomaly1));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
                touch_lat = latLng.latitude;
                touch_lon = latLng.longitude;
                Toast.makeText(MapsActivity.this, "" + latLng.latitude + "," + latLng.longitude, Toast.LENGTH_LONG).show();

            }

        });

    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        Drawable vectorDrawable = ContextCompat.getDrawable(getApplicationContext(), id);
        vectorDrawable.setBounds(0, 0, 50, 50);
        Bitmap bm = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    private BitmapDescriptor getBitmapHighDescriptor(int id) {
        Drawable vectorDrawable = ContextCompat.getDrawable(getApplicationContext(), id);
        vectorDrawable.setBounds(0, 0, 50, 90);
        Bitmap bm = Bitmap.createBitmap(50, 90, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public double distanceInKmBetweenEarthCoordinates(double lat1, double lon1, double lat2, double lon2) {
        int earthRadiusKm = 6371;
        double dLat = degreesToRadians(lat2 - lat1);
        double dLon = degreesToRadians(lon2 - lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    public void CheckGpsStatus() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(MapsActivity.this, "ACCESS_FINE_LOCATION permission allows us to Access GPS in app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MapsActivity.this, "есть доступ к gps", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MapsActivity.this, "нет доступа к gps.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void OnLocationChanged(Location loc) {
        if (location != null) {
            LastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            button3.setText("" + latLng.latitude + "," + latLng.longitude);
            lat = latLng.latitude;
            lon = latLng.longitude;
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0], params[1], params[2]);
            return null;
        }

        protected void onPostExecute(Double result) {

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String id, String t_lat, String t_lon) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/Strateg/update_geo.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("id", id));
                nameValuePairs.add(new BasicNameValuePair("lat", t_lat));
                nameValuePairs.add(new BasicNameValuePair("lon", t_lon));
                Log.d("UpdateBase", "id" + id + " Lat: " + t_lat + " Lon:  " + t_lon);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {

            } catch (IOException e) {
            }

        }
    }

    private class SendAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0], params[1], params[2], params[3], params[4], params[5]);
            return null;
        }

        protected void onPostExecute(Double result) {

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String keyapp, String date, String time, String latitude, String longitude, String radius) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/pubg/update_pubg.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("keyapp", keyapp));
                nameValuePairs.add(new BasicNameValuePair("date", date));
                nameValuePairs.add(new BasicNameValuePair("time", time));
                nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
                nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
                nameValuePairs.add(new BasicNameValuePair("radius", radius));
                //Log.d ("UpdateBase", "Login "+t_login+ " Lat: "+ t_lat + " Lon:  "+t_lon);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {

            } catch (IOException e) {
            }

        }
    }


    private class StrategAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0], params[1], params[2], params[3], params[4], params[5]);
            return null;
        }

        protected void onPostExecute(Double result) {

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String nickname, String team, String side, String latitude, String longitude, String event) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/Strateg/add_update_strateg.php");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("nickname", nickname));
                nameValuePairs.add(new BasicNameValuePair("team", team));
                nameValuePairs.add(new BasicNameValuePair("side", side));
                nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
                nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
                nameValuePairs.add(new BasicNameValuePair("event", event));
                //Log.d ("UpdateBase", "Login "+t_login+ " Lat: "+ t_lat + " Lon:  "+t_lon);
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
                            loadplayers(s);
                         }

                        if (urlWebService== getinfo)
                        {
                            loadinfo(s);
                            Log.d("Player","loadinfo");

                        }
                        if (urlWebService== getTeamInfo)
                        {
                            loadteam(s);
                            Log.d("Teammates",getTeamInfo);

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

    private void loadplayers(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        //info_s = "Координаты: ";
        Log.d ("GetJSON", "Taking data...");


        String[] id_s = new String[jsonArray.length()];
        String[] nics_s = new String[jsonArray.length()];
        String[] team_s = new String[jsonArray.length()];
        String[] side_s = new String[jsonArray.length()];
        String[] lat_s = new String[jsonArray.length()];
        String[] lon_s = new String[jsonArray.length()];
        String[] event_s = new String[jsonArray.length()];



        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                id_s[i] = obj.getString("id");
                nics_s[i] = obj.getString("nickname");
                team_s[i] = obj.getString("team");
                side_s[i] = obj.getString("side");
                lat_s[i] = obj.getString("latitude");
                lon_s[i] = obj.getString("longitude");
                event_s[i] = obj.getString("event_id");
                Log.d("GetJSON", "" + id_s[i] + ":" + nics_s[i] + ":" + team_s[i] + ":" + side_s[i] + ":" + lat_s[i] + ":" + lon_s[i] + event_s[i]  );

                if (team_s[i].length()>0) {
                teammate=Integer.parseInt(team_s[i]);}
                else teammate=0;
                Log.d("GetJSON", "Taking teammate: "+teammate);
                if (teammate==1)
                {
                    Log.d("GetJSON", "Taking teammate");
                    double lats=Double.parseDouble(lat_s[i]);
                    double lons=Double.parseDouble(lon_s[i]);
                    LatLng player = new LatLng (lats,lons);
                    mMap.addMarker(new MarkerOptions()
                            .position(player)
                            .title(nics_s[i])
                            .icon(getBitmapHighDescriptor(R.drawable.stalker)));
                }
            }

        }
    }

    private void loadinfo(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        //info_s = "Координаты: ";
        Log.d ("GetJSON", "Taking data...");


        String[] id_s = new String[jsonArray.length()];
        String[] nics_s = new String[jsonArray.length()];
        String[] team_s = new String[jsonArray.length()];
        String[] side_s = new String[jsonArray.length()];
        String[] lat_s = new String[jsonArray.length()];
        String[] lon_s = new String[jsonArray.length()];
        String[] event_s = new String[jsonArray.length()];
        String [] team_id_s = new String[jsonArray.length()];
        String [] side_id_s = new String[jsonArray.length()];
        Log.d("Player", "Taking"+jsonArray.length() );


        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                id_s[i] = obj.getString("id");
                nics_s[i] = obj.getString("nickname");
                lat_s[i] = obj.getString("lat");
                lon_s[i] = obj.getString("lon");
                team_id_s[i] = obj.getString("team_id");
                team_s[i] = obj.getString("json_team");
                side_id_s[i] = obj.getString("side_id");
                side_s[i] = obj.getString("json_side");
                Log.d("Player", "" + id_s[i] + ":" + nics_s[i] + ":" + team_s[i] + ":" + side_s[i]);
                SharedPreferences.Editor editor=mSettings.edit();
                editor.putString(APP_PREFERENCES_ID,id_s[i]);
                editor.apply();
                name=nics_s[i];
                teamname=team_s[i];
                sidename=side_s[i];


            }

        }
    }


    private void loadteam(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        //info_s = "Координаты: ";
        Log.d ("GetJSON", "Taking data...");
        String[] id_s = new String[jsonArray.length()];
        String[] nics_s = new String[jsonArray.length()];
        String[] team_s = new String[jsonArray.length()];
        String[] side_s = new String[jsonArray.length()];
        String[] lat_s = new String[jsonArray.length()];
        String[] lon_s = new String[jsonArray.length()];
        String[] event_s = new String[jsonArray.length()];
        String [] team_id_s = new String[jsonArray.length()];
        String [] side_id_s = new String[jsonArray.length()];
        LatLng [] teammates = new LatLng[jsonArray.length()];
        Log.d("Player", "Taking"+jsonArray.length() );


        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                id_s[i] = obj.getString("id");
                nics_s[i] = obj.getString("nickname");
                lat_s[i] = obj.getString("lat");
                lon_s[i] = obj.getString("lon");
                team_id_s[i] = obj.getString("team_id");
                team_s[i] = obj.getString("json_team");
                side_id_s[i] = obj.getString("side_id");
                side_s[i] = obj.getString("json_side");
                Log.d("Teammates", "Taken");
                Log.d("Teammates", "" + id_s[i] + ":" + nics_s[i] + ":" + lat_s[i] + ":" + lon_s[i]);
                double cLat=Double.parseDouble(lat_s[i]);
                double cLon=Double.parseDouble(lon_s[i]);
                teammates[i]=new LatLng(cLat,cLon);
                mMap.addMarker(new MarkerOptions()
                        .position(teammates[i])
                        .title(nics_s[i])
                        .icon(getBitmapHighDescriptor(R.drawable.stalker)));
                counter=0;

            }

        }
    }


}