package com.example.maps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.maps.classes.DataPack;
import com.example.maps.classes.KMLResParcer;
import com.example.maps.classes.PlayerChracteristics;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import static android.view.View.GONE;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocListenerInterface {

    private GoogleMap mMap;
    TextView org_passinfo, sidecom,divcom_title, teamcom;
    GroundOverlayOptions newMarkMap;
    public static final int RequestPermissionCode = 1;
    Button button, divorders, teamorders, addnewteam;
    Context context;
    Location location;
    private Location LastLocation;

    EditText nick, password, eventName,orgLogin,orgPassword, newteamfield;
    RelativeLayout org, s_com,d_com_layout,t_com;
    ImageView approve_org,cancel_org;
    int teamscounter=1;

    public String nickname,passcode,status_id,status,side_id,side_name,division_id,division,team_id,team_name,event_id,event_name;
    public LatLngBounds bounds;
    public LatLng center;
    ListView div_list,team_list, team_div_list,side_order_list;
    GroundOverlayOptions  mGroundOverlayOptions;
    public String Date, Time;
    String team, side, event;
    LatLng swMapCoord, neMapCoord;
    KmlLayer kmlLayer;
    PlayerChracteristics playerChracteristics;
    String sbname = "http://kamonline.r41.ru/Strateg/get_players_info.php";
    String strateg_getinfo = "http://kamonline.r41.ru/Strateg/get_team_strateg.php/get.php?nom=";
    String strateg_getplayer="http://kamonline.r41.ru/Strateg/get_strateg_player_byname.php/get.php?nom=";
    String get_sidecom="";
    String get_divcom="";
    String add_passcodes;
    String getinfo="";
    String divpass="";
    String teampass="";
    String teampass_div;
    String getTeamInfo="";
    String serviceinfo;
    public GroundOverlayOptions newarkMap;
    List<Marker> markers = new ArrayList<>();

    private final List<BitmapDescriptor> images = new ArrayList<BitmapDescriptor>();


    public int teammate;

    public double lat, lon, touch_lat, touch_lon;
    public int radiation = 0;

    Criteria criteria;
    String alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    public int counter = 0, dang_counter = 0;
    Calendar dateAndTime = Calendar.getInstance();
    String currentName,currentTeam,currentSide;
    double center_lat = 55.37581958597319;
    double center_lon = 36.7833898306835;
    int sam_radius = 2000;
    private GroundOverlay groundOverlay;
    boolean show_console=false;
    private GroundOverlay groundOverlayRotated;

    String name,teamname,sidename;
    String summary="";
    boolean showmap=true;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
   public static final String APP_PREFERENCES = "mysettings";//filename
    public static final String APP_PREFERENCES_NAME = "Nickname";//player's nickname
    public static final String APP_PREFERENCES_EVENTPASS="Eventpass";//player's event password

    public static final String APP_PREFERENCES_PASSWORD = "Password";//player's system password
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

    String DEBUG_TAG="GEST";
    int swipe=1;
    private static final int SWIPE_MIN_DISTANCE = 130;
    private static final int SWIPE_MAX_DISTANCE = 300;
    private static final int SWIPE_MIN_VELOCITY = 200;
    RelativeLayout RL1;
    private GestureDetectorCompat lSwipeDetector;

    @Override
    public void onStart() {
        super.onStart();
        if(NeedStartService()){
            Log.d("жопонька","инициируем запуск сервиса из активити ПДА");
            intent = new Intent(this, MapService.class);
            intent.setPackage("com.example.user.pdashiveluch");
            //intent.putExtra("ResetPlayer",resetPlayer);
            //intent.putExtra("Name",Name);
            //intent.putExtra("GroupID",group);
            startService(intent);
        } else{
            Log.d("жопонька","в активити ПДА обнаружен старый ранее запущенный сервис");
        }




    }

    @Override
    protected void onDestroy() {
        Log.d("жопонька","onDestroy активности pda");
        unregisterReceiver(ServiceReceiver);
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        unregisterReceiver(ServiceReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitBroadcastReceiver();
        SendRequestService();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EnableRuntimePermission();


        org_passinfo=findViewById(R.id.org_passinfo);
        lSwipeDetector = new GestureDetectorCompat(this, new MyGestureListener());
       RL1 = findViewById(R.id.RL1);
       org=findViewById(R.id.org);
       org.setVisibility(GONE);
       s_com=findViewById(R.id.side_commander);
       s_com.setVisibility(GONE);
       eventName=findViewById(R.id.eventName);
       orgLogin=findViewById(R.id.orgLogin);
       orgPassword=findViewById(R.id.orgPassword);
       sidecom=findViewById(R.id.Sidecom_title);
       div_list=findViewById(R.id.div_list);
       team_list=findViewById(R.id.team_list);
       divorders=findViewById(R.id.div_orders);
       teamorders=findViewById(R.id.team_orders);
       d_com_layout=findViewById(R.id.division_commander);
       d_com_layout.setVisibility(GONE);
       team_div_list=findViewById(R.id.team_div_list);
       side_order_list=findViewById(R.id.side_orders_list);
       newteamfield=findViewById(R.id.newteamfield);
       addnewteam=findViewById(R.id.addnewteam);
        KMLparsing();
        //playerChracteristics=new PlayerChracteristics()
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        nickname=mSettings.getString(APP_PREFERENCES_NAME,"");
        passcode=mSettings.getString(APP_PREFERENCES_PASSWORD,"");
        status_id=mSettings.getString(APP_PREFERENCES_STATUS_ID,"");
        status=mSettings.getString(APP_PREFERENCES_STATUS,"");
        side_id=mSettings.getString(APP_PREFERENCES_SIDE_ID,"");
        side_name=mSettings.getString(APP_PREFERENCES_SIDE,"");
        division_id=mSettings.getString(APP_PREFERENCES_DIVISION_ID,"");
        division=mSettings.getString(APP_PREFERENCES_DIVISION,"");
        team_id=mSettings.getString(APP_PREFERENCES_TEAM_ID,"");
        team_name=mSettings.getString(APP_PREFERENCES_TEAM,"");
        event_id=mSettings.getString(APP_PREFERENCES_EVENT_ID,"");
        event_name=mSettings.getString(APP_PREFERENCES_EVENT,"");
        approve_org=findViewById(R.id.approve_org);
        cancel_org=findViewById(R.id.cancel_org);

        nick = findViewById(R.id.nick);
        password = findViewById(R.id.password);
        sidecom.setText("командир стороны: "+"\n"+mSettings.getString(APP_PREFERENCES_SIDE,""));
        divcom_title=findViewById(R.id.Divcom_title);
        divcom_title.setText("Командир подразделения: "+"\n"+mSettings.getString(APP_PREFERENCES_SIDE,"")+", "+mSettings.getString(APP_PREFERENCES_DIVISION,""));
        Log.d("Info",mSettings.getString(APP_PREFERENCES_SIDE_ID,""));
        //ExpandableListView divlist=findViewById(R.id.ExpList);
        String[] anomalyGeo=Initializator.anomaly();

        for (int i=0;i<anomalyGeo.length;i++)
        {
            String[] currentAnomalyGeo=anomalyGeo[i].split(":");
            double initLat, initLon;
            initLat=Double.parseDouble(currentAnomalyGeo[0]);
            initLon=Double.parseDouble(currentAnomalyGeo[1]);
            Toast.makeText(MapsActivity.this,""+ initLat+", "+initLon, Toast.LENGTH_SHORT).show();
        }

        //criteria = new Criteria();

        context = getApplicationContext();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        InputStream kmlInputStream = getResources().openRawResource(R.raw.doc);

        try {
          KmlLayer layer = new KmlLayer(mMap, kmlInputStream,
                    getApplicationContext());
            Log.i("KML", ""+layer);


            layer.addLayerToMap();


            if (layer.getPlacemarks()!=null) Log.i("KML", ""+layer.getPlacemarks());

            if (layer.getContainers()!=null)
                Log.i("KML", ""+layer.getContainers());
            for (KmlContainer container : layer.getContainers()) {

                if (container.hasProperty("name")) {
                    Log.i("KML", container.getProperty("name"));
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        button = findViewById(R.id.button);
//        if (mSettings.contains(Login.APP_PREFERENCES_NAME))
//        summary+=mSettings.getString(Login.APP_PREFERENCES_NAME,"0");
//        if (mSettings.contains(Login.APP_PREFERENCES_TEAM))
//            summary+=mSettings.getString(Login.APP_PREFERENCES_TEAM,"0");
//        if (mSettings.contains(Login.APP_PREFERENCES_SIDE))
//            summary+=mSettings.getString(Login.APP_PREFERENCES_SIDE,"0");
//        if (mSettings.contains(Login.APP_PREFERENCES_ID))
//            summary+=mSettings.getString(APP_PREFERENCES_ID,"0");
//        String player=mSettings.getString(APP_PREFERENCES_NAME,"0");
//        Log.d("Player",player);
//        getinfo=strateg_getplayer+player;
//
//        getJSON(getinfo);

        //button.setText(summary);




//        setInitialDate();
//        setInitialTime();

//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LatLng anomaly1 = new LatLng(60.667923, 29.177713);
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(anomaly1));
//            }
//        });
approve_org.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String event=eventName.getText().toString();
        String orgLog=orgLogin.getText().toString();
        String orgPas=orgPassword.getText().toString();
        //new CreateEventAsyncTask().execute(event);
        add_passcodes= "http://kamonline.r41.ru/Strateg/add_strateg_passcodes.php/get.php?log="+orgLog+"&pass="
                +orgPas+"&select="+mSettings.getString(APP_PREFERENCES_EVENTPASS,"")+"&event="+event;
        getJSON(add_passcodes);

    }
});
        cancel_org.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                org.setVisibility(GONE);
            }
        });

        divorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_com.setVisibility(GONE);
            }
        });


        teamorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_com.setVisibility(GONE);
            }
        });

        addnewteam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newteam=newteamfield.getText().toString();
                Log.d("newteam",newteam+"Всего команд: "+teamscounter);

                if (newteam.length()>0 && teamscounter<15)
                {
                    new AddTeamAsyncTask().execute(newteam,mSettings.getString(APP_PREFERENCES_SIDE_ID,"")
                    ,mSettings.getString(APP_PREFERENCES_DIVISION_ID,"")
                    ,(""+(teamscounter+1))
                    ,mSettings.getString(APP_PREFERENCES_EVENT_ID,""));
                    Log.d("newteam", "Выполнено");


                }
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
//                    SharedPreferences.Editor editor=mSettings.edit();
//                    editor.putString(APP_PREFERENCES_NAME,currentName);
//                    editor.putString(APP_PREFERENCES_TEAM,currentTeam);
//                    editor.putString(APP_PREFERENCES_SIDE,currentSide);
//                    editor.apply();
//                    if (mSettings.contains(APP_PREFERENCES_NAME))
//                        summary+=mSettings.getString(APP_PREFERENCES_NAME,"0");
//
//                    if (mSettings.contains(APP_PREFERENCES_TEAM))
//                        summary+=mSettings.getString(APP_PREFERENCES_TEAM,"0");
//                    if (mSettings.contains(APP_PREFERENCES_SIDE))
//                        summary+=mSettings.getString(APP_PREFERENCES_SIDE,"0");
//                    Toast.makeText(MapsActivity.this, summary, Toast.LENGTH_LONG).show();
                    //button.setText(summary);

                    if (CheckFillField())
                    {
                        got(true);
                    }

                }}});



    }



//region blocked
    /*
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
     */
//endregion

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
//                    SharedPreferences.Editor editor=mSettings.edit();
//                    editor.putString(APP_PREFERENCES_NAME,currentName);
                 //   editor.putString(APP_PREFERENCES_TEAM,currentTeam);
                  //  editor.putString(APP_PREFERENCES_SIDE,currentSide);
                    //editor.apply();
//                    if (mSettings.contains(APP_PREFERENCES_NAME))
//                        summary+=mSettings.getString(APP_PREFERENCES_NAME,"0");
//
//                    if (mSettings.contains(APP_PREFERENCES_TEAM))
//                        summary+=mSettings.getString(APP_PREFERENCES_TEAM,"0");
//                    if (mSettings.contains(APP_PREFERENCES_SIDE))
//                        summary+=mSettings.getString(APP_PREFERENCES_SIDE,"0");
//                    Toast.makeText(MapsActivity.this, summary, Toast.LENGTH_LONG).show();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
        double b=distanceInKmBetweenEarthCoordinates(swMapCoord.latitude,swMapCoord.longitude,neMapCoord.latitude,neMapCoord.longitude);


        float zoomLevel = getZoomLevel(b);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoomLevel-1));


    }

    private int getZoomLevel (double radius) {
        double scale = radius / 2;
        return ((int) (16 - Math.log(scale) / Math.log(2)));
    }

    public void autologin (MenuItem item)
    {
        new StrategAsyncTask().execute("Лаврик", "3", "3", "0", "0", "3");

        currentName="Лаврик";
        getinfo=strateg_getplayer+currentName;
        getJSON(getinfo);
        currentTeam="3";
        currentSide="3";
//        SharedPreferences.Editor editor=mSettings.edit();
//        editor.putString(APP_PREFERENCES_NAME,currentName);
//        editor.putString(APP_PREFERENCES_TEAM,currentTeam);
//        editor.putString(APP_PREFERENCES_SIDE,currentSide);
//        editor.apply();
//        if (mSettings.contains(APP_PREFERENCES_NAME))
//            summary+=mSettings.getString(APP_PREFERENCES_NAME,"0");
//
//        if (mSettings.contains(APP_PREFERENCES_TEAM))
//            summary+=mSettings.getString(APP_PREFERENCES_TEAM,"0");
//        if (mSettings.contains(APP_PREFERENCES_SIDE))
//            summary+=mSettings.getString(APP_PREFERENCES_SIDE,"0");
//       //Toast.makeText(MapsActivity.this, summary, Toast.LENGTH_LONG).show();
//

    }

    public void teamInfo(MenuItem item)
    {
//        currentTeam=mSettings.getString(APP_PREFERENCES_TEAM,"0");
//        getTeamInfo=strateg_getinfo+currentTeam;
//        getJSON(getTeamInfo);

    }

    public void console(MenuItem item)
    {
        int roletype=Integer.parseInt(mSettings.getString(APP_PREFERENCES_STATUS_ID,""));
        Log.d("console",""+mSettings.getString(APP_PREFERENCES_STATUS_ID,""));

        switch(roletype) {

            case 1:
                get_sidecom = "http://kamonline.r41.ru/Strateg/get_sidecom_pass.php/get.php?select=" + mSettings.getString(APP_PREFERENCES_EVENTPASS, "");
                Log.d("com", get_sidecom);
                getJSON(get_sidecom);
                org.setVisibility(View.VISIBLE);
                break;

            case 2:
                Log.d("Sidecom","командир стороны "+mSettings.getString(APP_PREFERENCES_SIDE,""));
//                get_divcom="http://kamonline.r41.ru/Strateg/get_division_pass.php/get.ghp?select="+mSettings.getString(APP_PREFERENCES_EVENTPASS,"")
//                        +"&side="+mSettings.getString(APP_PREFERENCES_STATUS_ID,"");
//                getJSON(get_divcom);
        divpass="http://kamonline.r41.ru/Strateg/get_division_pass.php/get.ghp?select="
                +mSettings.getString(APP_PREFERENCES_EVENTPASS,"")+"&side="
                +mSettings.getString(APP_PREFERENCES_SIDE_ID,"");
        getJSON(divpass);

                s_com.setVisibility(View.VISIBLE);
                break;
            case 3:
                Log.d("Divcom","командир подразделения "+mSettings.getString(APP_PREFERENCES_DIVISION,""));
                d_com_layout.setVisibility(View.VISIBLE);
                teampass_div="http://kamonline.r41.ru/Strateg/get_team_pass_bydiv.php/get.ghp?select="
                        +mSettings.getString(APP_PREFERENCES_EVENTPASS,"")+"&side="
                        +mSettings.getString(APP_PREFERENCES_SIDE_ID,"")+"&div="
                +mSettings.getString(APP_PREFERENCES_DIVISION_ID,"");
                Log.d("Divcom",teampass_div);
                getJSON(teampass_div);

                break;
        }

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
        LatLng anomaly1 = new LatLng(0, 0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(anomaly1));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        AnomalyMarkersDraw();
        init();
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

    private BitmapDescriptor getBitmapBigDescriptor(int id) {
        Drawable vectorDrawable = ContextCompat.getDrawable(getApplicationContext(), id);
        vectorDrawable.setBounds(0, 0, 50, 90);
        Bitmap bm = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
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
         //   button3.setText("" + latLng.latitude + "," + latLng.longitude);
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

    private class AddTeamAsyncTask extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0], params[1], params[2], params[3], params[4]);
            return null;
        }

        protected void onPostExecute(Double result) {

        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public void postData(String team_name, String side, String division, String team, String event) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://kamonline.r41.ru/Strateg/add_team.php/get.php?select="+mSettings.getString(APP_PREFERENCES_EVENTPASS,""));

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("teamname", newteamfield.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("side_id", side));
                nameValuePairs.add(new BasicNameValuePair("division_id", division));
                nameValuePairs.add(new BasicNameValuePair("team_id", team));
                nameValuePairs.add(new BasicNameValuePair("event_id", event));
                //Log.d ("UpdateBase", "Login "+t_login+ " Lat: "+ t_lat + " Lon:  "+t_lon);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
                getJSON(teampass_div);
                newteamfield.setText("");
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

                        if (urlWebService == get_sidecom) {
                            getsidecom(s);
                        }

                        if (urlWebService == teampass) {
                            get_divcompass(s);
                        }
                        if (urlWebService == get_sidecom) {
                            get_divcom(s);
                        }

                        if (urlWebService== teampass_div)
                        {
                            get_divteampass(s);
                            Log.d("Player","loadinfo");

                        }

                        if (urlWebService== getinfo)
                        {
                            loadinfo(s);
                            Log.d("Player","loadinfo");

                        }
                        if (urlWebService== divpass)
                        {
                            getDivisionPasscodes(s);
                            Log.d("Division","loadinfo");

                        }


                        if (urlWebService== add_passcodes)
                        {
                            addpasscodes(s);
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


    private void getsidecom(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
       Log.d ("GetJSONSidecom", "Taking data..."+jsonArray.length());
       String temp="";
        String[] pass_s = new String[jsonArray.length()];
        String[] side_s = new String[jsonArray.length()];
        String[] event_s=new String[jsonArray.length()];
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d ("GetJSONSidecom", "Taking data... Current: "+i);
                JSONObject obj = jsonArray.getJSONObject(i);
                pass_s[i] = obj.getString("pass");
                side_s[i] = obj.getString("side");

                Log.d("GetJSONSidecom", "" + pass_s[i] + ":" + side_s[i]);
                temp+="Сторона: <strong>"+side_s[i]+"</strong>, пароль: <strong>"+pass_s[i]+"</strong><br> ";
            }
        org_passinfo.setText(Html.fromHtml(temp));
        }
        else {org_passinfo.setText("Пароли не найдены");}
    }

    private void get_divcom(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        Log.d ("GetJSONSidecom", "Taking data..."+jsonArray.length());
        String temp="";
        String[] pass_s = new String[jsonArray.length()];
        String[] div_s = new String[jsonArray.length()];
        String[] event_s=new String[jsonArray.length()];
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d ("GetJSONSidecom", "Taking data... Current: "+i);
                JSONObject obj = jsonArray.getJSONObject(i);
                pass_s[i] = obj.getString("pass");
                div_s[i] = obj.getString("side");
                event_s[i]=obj.getString("event");

                Log.d("GetJSONSidecom", "" + pass_s[i] + ":" + div_s[i]+":"+event_s[i]);
                //temp+="Сторона: <strong>"+side_s[i]+"</strong>, пароль: <strong>"+pass_s[i]+"</strong><br> ";
            }
            org_passinfo.setText(Html.fromHtml(temp));
        }
        else {org_passinfo.setText("Пароли не найдены");}
    }


    private void addpasscodes(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        Log.d ("GetJSONaddpass", "Taking data..."+jsonArray.length());
        String temp="";
        String[] pass_s = new String[jsonArray.length()];
        String[] side_s = new String[jsonArray.length()];
        String[] event_s = new String[jsonArray.length()];


        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d ("GetJSONSidecom", "Taking data... Current: "+i);
                JSONObject obj = jsonArray.getJSONObject(i);
                pass_s[i] = obj.getString("pass");
                side_s[i] = obj.getString("side");
                event_s[i] = obj.getString("event");


                Log.d("GetJSONaddpass", "" + pass_s[i] + ":" + side_s[i]);
                temp+="Сторона: <strong>"+side_s[i]+"</strong>, пароль: <strong>"+pass_s[i]+"</strong><br> ";
            }
            org_passinfo.setText(Html.fromHtml("Игра: "+event_s[0]+"<br><br>"+temp));
        }
        else {org_passinfo.setText("Пароли не найдены");}
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
//                SharedPreferences.Editor editor=mSettings.edit();
//                editor.putString(APP_PREFERENCES_ID,id_s[i]);
//                editor.apply();
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


//    void overlay()
//    LatLng sw = new LatLng(47.01377857060625, 8.305519705172628);
//    LatLng ne = new LatLng(47.01395211967171, 8.306270482717082);
//    LatLng nw = new LatLng(47.014014755501165, 8.305559697328135);
//    LatLng se = new LatLng(47.01370751919609, 8.306236284552142);
//    LatLngBounds latLngBounds = new LatLngBounds(sw, ne).including(nw).including(se);
//    GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
//    .BitmapDescriptorFactory.fromResource(R.drawable.map_bmp);
//groundOverlayOptions.
//groundOverlayOptions.positionFromBounds(latLngBounds);
//mMap.addGroundOverlay(groundOverlayOptions);


    public void addOverlay(LatLng place) {

        GroundOverlay groundOverlay = mMap.addGroundOverlay(new
                GroundOverlayOptions()
                .position(place, 5000000f)
                .transparency(0.5f)
                .zIndex(3)
                .image(BitmapDescriptorFactory.fromResource(R.drawable.map_bmp)));
groundOverlay.setDimensions(90000,1000000);
       // startOverlayAnimation(groundOverlay);
    }


    private void startOverlayAnimation(final GroundOverlay groundOverlay) {

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator vAnimator = ValueAnimator.ofInt(0, 60000000);
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        vAnimator.setInterpolator(new LinearInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final Integer val = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(val);

            }
        });

        ValueAnimator tAnimator = ValueAnimator.ofFloat(0, 1);
        tAnimator.setRepeatCount(ValueAnimator.INFINITE);
        tAnimator.setRepeatMode(ValueAnimator.RESTART);
        tAnimator.setInterpolator(new LinearInterpolator());
        tAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                groundOverlay.setTransparency(val);
            }
        });

        animatorSet.setDuration(2000);
        animatorSet.playTogether(vAnimator, tAnimator);
        animatorSet.start();
    }
//public void overlay()
//    {
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_background);
//
//        LatLng sw=new LatLng(1,1);
//        LatLng ne = new LatLng(10,10);
//        LatLngBounds bounds = new LatLngBounds(sw,ne);
//        GroundOverlay groundOverlay = new GroundOverlay();
//        if (overlay == null) {
//            GroundOverlayOptions background = new GroundOverlayOptions()
//                    .image(BitmapDescriptorFactory.fromBitmap(bitmap))
//                    .position(cameraPosition.target, mapWidth)
//                    .bearing(cameraPosition.bearing)
//                    .zIndex(zIndex);
//            overlay = map.addGroundOverlay(background);
//
//
//         = mMap.addGroundOverlay(new GroundOverlayOptions()
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.map_bmp))
//                .positionFromBounds(bounds)
//                .transparency(0.5f));
//
//
//
//
//    }

    public void NotifySelect(String value){
        Log.d("EVENT","NotifySelect "+value);

        switch(value){
            case "NICKNAME":
                NotifyNickname();
                break;
            case "PARAMETERS":
                NotifyParameters();
                break;
            default:
                Log.d("жопонька","Неизвестный нотификатор "+value);
                break;
    }


}

    private void NotifyNickname() {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=mSettings.edit();
      //  editor.putString(APP_PREFERENCES_SERVICE_NAME, dataPack.nickname);
        editor.apply();
      //  nick.setText(mSettings.getString(APP_PREFERENCES_SERVICE_NAME+" from datapack",currentName+" activityName"));

    }

    private void NotifyParameters() {
       //получен свежий датапак, надо обновить то что на экране



    }

    private void NotifyLog(String event) {

        Log.d("LOG",event);
    }

    private void NotifyToast(String value) {
        Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
    }

    public void got(boolean Reset){
        boolean needStartService=NeedStartService();
        if(needStartService){
//dataPack.nickname=currentName;
//dataPack.team=currentTeam;
//dataPack.side=currentSide;

            Intent intent2 = new Intent(this, MapService.class);
            intent2.setPackage("com.example.user.maps");
            intent2.putExtra("ResetPlayer",Reset);
            intent2.putExtra("Name",currentName);
            intent2.putExtra("Team",currentTeam);
            intent2.putExtra("Side",currentSide);
            startService(intent2);
            Toast.makeText(getApplicationContext(), "Сервис запущен", Toast.LENGTH_LONG).show();
            //String takename = intent2.getStringExtra("Name");
            //Toast.makeText(getApplicationContext(), takename, Toast.LENGTH_LONG).show();



        }

    }

    private boolean CheckFillField(){
        if (nick.getText().toString().length()<1)
        {
            Toast.makeText(getApplicationContext(), "Не введено имя сталкера/идентификатор", Toast.LENGTH_LONG).show();
            return false;
        } else{
            return true;
        }
    }




    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_DISTANCE)
                return false;
            if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                swipe++;
               Log.d(DEBUG_TAG,""+swipe);
            }
            return false;
        }
    }
//<north>55.37581958597319</north>
//		<south>55.37174896110438</south>
//		<east>36.7833898306835</east>
//		<west>36.77321949707529</west>

    private void init() {
        //double s_coord=Double.parseDouble(south)

        LatLng swLatLng = new LatLng(55.3717489, 36.77321);
        LatLng neLatLng = new LatLng(55.3758195, 36.78338);


       bounds= new LatLngBounds(swMapCoord, neMapCoord);
       center = bounds.getCenter();

        new AddGroundOverlay().execute();

//        newarkMap = new GroundOverlayOptions()
//                .image(BitmapDescriptorFactory.fromPath("http://kamonline.r41.ru/Strateg/KMZ/event24/files/event24.bmp"))
//                .positionFromBounds(bounds);
//        Log.d ("MAP_NEW",""+mMap);
//        newarkMap.transparency( 0.5f);
//       if (newarkMap!=null)
//       {mMap.addGroundOverlay(newarkMap);
//              }
    }

    public class AddGroundOverlay extends AsyncTask<String, Integer, BitmapDescriptor> {

        BitmapDescriptor bitmapDescriptor;

        @Override
        protected BitmapDescriptor doInBackground(String... url) {
            String myUrl = "http://kamonline.r41.ru/Strateg/KMZ/event26/files/event26.png";
            try {
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Picasso.with(getApplicationContext()).load(myUrl).get());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmapDescriptor;
        }

        protected void onPostExecute(BitmapDescriptor icon) {

            try {

                GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
                        .image(bitmapDescriptor)
                       .positionFromBounds(bounds)
                        .transparency(0.5f);
                // Updated
                mGroundOverlayOptions = groundOverlay;
                mMap.addGroundOverlay(groundOverlay);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String [] array_divisions=new String[15];
    public String [] array_teams = new String [15];
//    private ArrayList<Division> getData()
//    {
//
//        divpass="http://kamonline.r41.ru/Strateg/get_division_pass.php/get.ghp?select="
//                +mSettings.getString(APP_PREFERENCES_EVENTPASS,"")+"&side="
//                +mSettings.getString(APP_PREFERENCES_SIDE_ID,"");
//        getJSON(divpass);
//
//return
//    }
final String D_NAME_PASS = "division"; // основной текст
    final String D_ID = "ID";  // скрытый текст
    final String D_ICON = "icon"; // основной текст

    private void getDivisionPasscodes(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        Log.d ("GetJSONaddpass", "Taking data..."+jsonArray.length());
        String temp="";


        String[] pass_s = new String[jsonArray.length()];
        String[] division_s = new String[jsonArray.length()];
        String[] event_s = new String[jsonArray.length()];
        String[] divlist=new String[jsonArray.length()];
        ArrayList<HashMap<String, Object>> divList = new ArrayList<>();
        HashMap<String, Object> hashMap;

        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d ("GetJSONDivcom", "Taking data... Current: "+i);
                JSONObject obj = jsonArray.getJSONObject(i);
                pass_s[i] = obj.getString("pass");
                division_s[i] = obj.getString("division");
                event_s[i] = obj.getString("event");

                divlist[i]=division_s[i]+","+"\n"+"пароль: "+pass_s[i];
                String current_id=""+(i+2);
                Log.d("GetJSONaddDivpass", "" + pass_s[i] + ":" + division_s[i]);
                hashMap = new HashMap<>();
                hashMap.put(D_NAME_PASS, divlist[i]); // Название
                hashMap.put(D_ID, current_id); // Описание
                divList.add(hashMap);


                temp+="Сторона: <strong>"+division_s[i]+"</strong>, пароль: <strong>"+pass_s[i]+"</strong><br> ";
            }
            SimpleAdapter array_adapter = new SimpleAdapter(this, divList,
                    R.layout.sidelist_item, new String[]{D_NAME_PASS, D_ID},
                    new int[]{R.id.div_main_info, R.id.service_info});
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.sidelist_item, divlist);

            div_list.setAdapter(array_adapter);
            div_list.setOnItemClickListener(itemClickListener);
            //org_passinfo.setText(Html.fromHtml("Игра: "+event_s[0]+"<br><br>"+temp));
        }
        //else {org_passinfo.setText("Пароли не найдены");}
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, Object> itemHashMap =
                    (HashMap<String, Object>) parent.getItemAtPosition(position);
            String main = itemHashMap.get(D_NAME_PASS).toString();

            serviceinfo = itemHashMap.get(D_ID).toString();

            Log.d("ARRAY","Подразделение: "+main+"Идентификатор: "+serviceinfo+", "
                    +mSettings.getString(APP_PREFERENCES_EVENTPASS,"")
                    +", "+mSettings.getString(APP_PREFERENCES_SIDE_ID,""));
            teampass="http://kamonline.r41.ru/Strateg/get_team_pass.php/get.php?select="
                    +mSettings.getString(APP_PREFERENCES_EVENTPASS,"")+"&side="
                    +mSettings.getString(APP_PREFERENCES_SIDE_ID,"")+"&div="
                    +serviceinfo;

            getJSON(teampass);

        }
    };

    private void get_divteampass(String json) throws JSONException {
        JSONArray jsonAr = new JSONArray(json);
        Log.d ("GetJSONteampass", "Taking data..."+jsonAr.length());
        int currentside=Integer.parseInt(mSettings.getString(APP_PREFERENCES_SIDE_ID,"0"));
        String icon_side="";
        teamscounter=1;

        String[] pass_s = new String[jsonAr.length()];
        String[] team_s = new String[jsonAr.length()];
        String[] event_s = new String[jsonAr.length()];
        String[] divlist=new String[jsonAr.length()];
        ArrayList<HashMap<String, Object>> divList = new ArrayList<>();
        HashMap<String, Object> hashMap;

        if (jsonAr.length() > 0) {
            for (int i = 0; i < jsonAr.length(); i++) {
                Log.d ("GetJSONteampass", "Taking data... Current: "+i);
                JSONObject obj = jsonAr.getJSONObject(i);
                pass_s[i] = obj.getString("pass");
                team_s[i] = obj.getString("teamname");
                event_s[i] = obj.getString("event");

                if (team_s[i]!="null")
                {Log.d("newteam",team_s[i]+", "+teamscounter);
                    teamscounter++;}
                    if (team_s[i]=="null") team_s[i]="Команда не назначена";
                divlist[i]=team_s[i]+","+"\n"+"пароль: "+pass_s[i];
                String current_id=""+(i+2);
                Log.d("GetJSONteampass", "" + pass_s[i] + ":" + team_s[i]);
                hashMap = new HashMap<>();
                hashMap.put(D_NAME_PASS, divlist[i]); // Название
                hashMap.put(D_ID, current_id); // Описание

                divList.add(hashMap);


                //temp+="Сторона: <strong>"+division_s[i]+"</strong>, пароль: <strong>"+pass_s[i]+"</strong><br> ";
            }
            SimpleAdapter array_adapter = new SimpleAdapter(this, divList,
                    R.layout.teamspass_items, new String[]{D_NAME_PASS, D_ID},
                    new int[]{R.id.div_main_info, R.id.service_info});
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.teamlist_side, divlist);

            team_div_list.setAdapter(array_adapter);
            team_div_list.setOnItemClickListener(itemTeamClickListener);
            //org_passinfo.setText(Html.fromHtml("Игра: "+event_s[0]+"<br><br>"+temp));
        }
        //else {org_passinfo.setText("Пароли не найдены");}
    }

    AdapterView.OnItemClickListener itemTeamClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, Object> itemHashMap =
                    (HashMap<String, Object>) parent.getItemAtPosition(position);
            String main = itemHashMap.get(D_NAME_PASS).toString();
            String serviceinfo = itemHashMap.get(D_ID).toString();
            Log.d("ARRAY","Команда: "+main+"Идентификатор: "+serviceinfo);
//team_list.setBackgroundColor(Color.TRANSPARENT);
  //          view.setBackgroundColor(Color.GREEN);


        }
    };


    private void get_divcompass(String json) throws JSONException {
        JSONArray jsonAr = new JSONArray(json);
        Log.d ("GetJSONaddpass", "Taking data..."+jsonAr.length());
        int currentside=Integer.parseInt(mSettings.getString(APP_PREFERENCES_SIDE_ID,"0"));
        String icon_side="";
        switch (currentside)
        {
            case 2:
                icon_side="R.drawable.yellow_chev";
                break;
            case 3:
                icon_side="R.drawable.green_chev";
                break;
            case 4:
                icon_side="R.drawable.red_chev";
                break;
            case 5:
                icon_side="R.drawable.blue_chev";
                break;
            case 6:
                icon_side="R.drawable.white_chev";
                break;
        }


        String[] pass_s = new String[jsonAr.length()];
        String[] team_s = new String[jsonAr.length()];
        String[] event_s = new String[jsonAr.length()];
        String[] divlist=new String[jsonAr.length()];
        ArrayList<HashMap<String, Object>> divList = new ArrayList<>();
        HashMap<String, Object> hashMap;

        if (jsonAr.length() > 0) {
            for (int i = 0; i < jsonAr.length(); i++) {
                Log.d ("GetJSONDivcom", "Taking data... Current: "+i);
                JSONObject obj = jsonAr.getJSONObject(i);
                pass_s[i] = obj.getString("pass");
                team_s[i] = obj.getString("teamname");
                event_s[i] = obj.getString("event");
                if (team_s[i]=="null") team_s[i]="Команда не назначена";

                divlist[i]=team_s[i]+","+"\n"+"пароль: "+pass_s[i];
                String current_id=""+(i+2);
                Log.d("GetJSONaddDivpass", "" + pass_s[i] + ":" + team_s[i]);
                hashMap = new HashMap<>();
                hashMap.put(D_NAME_PASS, divlist[i]); // Название
                hashMap.put(D_ID, current_id); // Описание
                hashMap.put(D_ICON,icon_side);
                divList.add(hashMap);


                //temp+="Сторона: <strong>"+division_s[i]+"</strong>, пароль: <strong>"+pass_s[i]+"</strong><br> ";
            }
            SimpleAdapter array_adapter = new SimpleAdapter(this, divList,
                    R.layout.teamlist_side, new String[]{D_NAME_PASS, D_ID,D_ICON},
                    new int[]{R.id.div_main_info, R.id.service_info,R.id.imageview_icon});
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.teamlist_side, divlist);

            team_list.setAdapter(array_adapter);
            team_list.setOnItemClickListener(itemTeamDivClickListener);
            //org_passinfo.setText(Html.fromHtml("Игра: "+event_s[0]+"<br><br>"+temp));
        }
        //else {org_passinfo.setText("Пароли не найдены");}
    }

    AdapterView.OnItemClickListener itemTeamDivClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, Object> itemHashMap =
                    (HashMap<String, Object>) parent.getItemAtPosition(position);
            String main = itemHashMap.get(D_NAME_PASS).toString();
            String serviceinfo = itemHashMap.get(D_ID).toString();
            Log.d("ARRAY","Команда: "+main+"Идентификатор: "+serviceinfo);
//team_list.setBackgroundColor(Color.TRANSPARENT);
            //          view.setBackgroundColor(Color.GREEN);


        }
    };

    private void KMLparsing() {
        String result;
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.doc);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            result = new String(b);
            Log.d("KML",result);

            //север
            int north_begin=result.indexOf("north>", 1);
            int north_end=result.indexOf("</nort", 1);
            String northcoord=result.substring(north_begin,north_end);
            northcoord = northcoord.replaceAll("[a-zA-Zа-яА-Я <>]*", "");

            //юг
                int south_begin=result.indexOf("south>", 1);
                int south_end=result.indexOf("</south", 1);
                String southcoord=result.substring(south_begin,south_end);
                southcoord=southcoord.replaceAll("[a-zA-Zа-яА-Я <>]*", "");


                //восток
            int east_begin=result.indexOf("east>", 1);
            int east_end=result.indexOf("</east", 1);
            String eastcoord=result.substring(east_begin,east_end);
            eastcoord=eastcoord.replaceAll("[a-zA-Zа-яА-Я <>]*", "");

            //запад
            int west_begin=result.indexOf("west>", 1);
            int west_end=result.indexOf("</west", 1);
            String westcoord=result.substring(west_begin,west_end);
            westcoord=westcoord.replaceAll("[a-zA-Zа-яА-Я <>]*", "");

                Log.d("KML",northcoord+", "+southcoord+", "+eastcoord+", "+westcoord);

             double s_coord=Double.parseDouble(southcoord);
             double w_coord = Double.parseDouble(westcoord);
             double n_coord = Double.parseDouble(northcoord);
             double e_coord=Double.parseDouble(eastcoord);

             swMapCoord=new LatLng(s_coord,w_coord);
             neMapCoord=new LatLng(n_coord,e_coord);



        } catch (Exception e) {
            // e.printStackTrace();
            result = "Error: can't show file.";
        }

    }

    private void AnomalyMarkersDraw(){
        LatLng[] anomaly = new LatLng[anomaly_latitudes.length];
        for (int i = 0; i < anomaly_latitudes.length; i++) {
            anomaly[i] = new LatLng(anomaly_latitudes[i], anomaly_longitudes[i]);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(anomaly[i])
                    .title(anomaly_titles[i])
                    .icon(getBitmapDescriptor(anomaly_draw_res[i])));
            marker.setTag(0);
            markers.add(marker);
        }

    }


    //region связь с сервисом
    Intent intent;
    DataPack dataPack;

    private void SendRequestService(){
        Log.d("LOG","отсылаем запрос в сервис на получение первичного датапака");
        Intent InnerIntent = new Intent("MapService.RequestService");
        sendBroadcast(InnerIntent);
    }

    private void InitBroadcastReceiver(){
        registerReceiver(ServiceReceiver, new IntentFilter("MapService.StringBroadcast"));
        registerReceiver(ServiceReceiver, new IntentFilter("MapService.ToastBroadcast"));
        registerReceiver(ServiceReceiver, new IntentFilter("MapService.DatapackBroadcast"));
    }




    private final BroadcastReceiver ServiceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent InnerIntent) {
            //Log.d("жопонька","принят бродкаст");
            String action = "";
            action = InnerIntent.getAction();

            // When discovery finds a new device
            switch (action) {
                case "MapService.StringBroadcast":
                    Log.d("LOG","в стартовое активити пришла строка:"+InnerIntent.getStringExtra("Message"));
                    NotifyLog(InnerIntent.getStringExtra("Message"));
                    break;
                case "MapService.ToastBroadcast":
                    NotifyToast(InnerIntent.getStringExtra("Message"));
                    break;
                case "MapService.DatapackBroadcast":
                    dataPack=(DataPack)InnerIntent.getSerializableExtra("Datapack");
                    NotifySelect(InnerIntent.getStringExtra("Event"));
                    break;
            }
        }
    };

    private Handler handle;
    public Handler GetHandler(){
        return handle;
    }



    private void SendActionBroadcast(String action){
        Intent InnerIntent = new Intent("ShiveluchActivity.Action");
        InnerIntent.putExtra("ExtraAction", action);
        sendBroadcast(InnerIntent);
    }



    public boolean NeedStartService(){

        boolean tStartService = true;
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(Integer.MAX_VALUE);
        String ServiceName=MapService.class.getName();
        for (int i=0; i<rs.size(); i++) {
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            if(ServiceName.equalsIgnoreCase(rsi.service.getClassName())){
                tStartService = false;
                break;
            }
        }
        return tStartService;

    }

    //endregion
}