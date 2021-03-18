package com.example.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;
//import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;
//
//import com.example.user.pdashiveluch.classes.DataPack;
//import com.example.user.pdashiveluch.classes.Debug;
//import com.example.user.pdashiveluch.classes.PDA_Event;
//import com.example.user.pdashiveluch.classes.PDA_MessageEvent;
//import com.example.user.pdashiveluch.classes.PlayerCharcteristics;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
//import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.maps.classes.DataPack;
import com.example.maps.classes.HideNotificationService;
import com.example.maps.classes.PlayerChracteristics;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MapService extends Service {

    public PlayerChracteristics playerChracteristics;
    private LocationManager locationManager;
    private Location LastLocation;
    private MyLocListener myLocListener;
    Context context;
    Location location;
    boolean GpsStatus = false;
    protected MapService service;
    private final IBinder binder = new MapServiceBinder();

    private Timer mTimer;
    private MainTimer mMainTimer;
    private Criteria criteria;
    private double lat, lon, touch_lat, touch_lon;
    private int counter = 0, dang_counter = 0;
    private int sam_radius = 2000;
    double center_lat = 55.37581958597319;
    double center_lon = 36.7833898306835;
    int[] anomaly_radius = {100, 20, 300, 50, 30, 100, 50, 50};
    int radiation = 0;

    double[] anomaly_latitudes =
            {60.667923,//1
                    60.674902,//2
                    60.681165,//3
                    55.527917,//4
                    55.5275223,//5
                    55.3133,//6
                    53.093370,//7
                    53.056837
            };
    double[] anomaly_longitudes =
            {29.177713,//1
                    29.173336,//2
                    29.161963,//3
                    28.6753678,//4
                    28.6707544,//5
                    28.4018,//6
                    158.851072,//7
                    158.663521//8
            };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MapServiceBinder extends Binder {
        MapService getService() {return MapService.this;}
    }

    private void SendStringBroadcast(String value){
        Intent intent = new Intent("MapService.StringBroadcast");
        intent.putExtra("Message", value);
        sendBroadcast(intent);
    }

    private void SendToastBroadcast(String value){
        Intent intent = new Intent("MapService.ToastBroadcast");
        intent.putExtra("Message", value);
        sendBroadcast(intent);
    }

    private void SendDatapackBroadcast(DataPack value, String Event){
        Intent intent = new Intent("MapService.DatapackBroadcast");
        intent.putExtra("Datapack", value);
        intent.putExtra("Event",Event);
        sendBroadcast(intent);
    }

    private final BroadcastReceiver ActivityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = "";
            action=intent.getAction();
            Log.d("жопонька","принят бродкаст "+ action);
            switch(action){
                case "MapsActivity.RequestService":
                    Log.d("жопонька","уведомление активити по запросу");
                    //HermesEventBus.getDefault().post();
                    SendDatapackBroadcast(DataPack.GetDataPack(service),"ALL");
                    break;
                case "MapsActivity.Action":
                    String ExtraAction=intent.getStringExtra("ExtraAction");
                    Log.d("жопонька","параметр: "+ExtraAction);
                    switch(ExtraAction) {
                        /*
                        case "VibrateCancel":
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.cancel();
                            break;

                         */
                        default:
                            Log.d("жопонька","Неизвестный нотификатор "+ExtraAction);
                            break;
                    }



                    break;
                default:
                    Log.d("жопонька","Неизвестный action "+action);
                    break;


            }


        }
    };

    public static final String CHANNEL_ID = "786445";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotification(Service context) {
        String channelId = createChannel(context);
        Notification notification =
                buildNotification(context, channelId);
        context.startForeground(
                786445, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification buildNotification(
            Service context, String channelId) {


        // Create a notification.
        return new Notification.Builder(context, channelId)
                .setContentTitle("Maps")
                .setContentText("Maps")
                .setSmallIcon(R.drawable.tactics)
                //.setContentIntent(piLaunchMainActivity)
                //.setActions(stopAction)
                .setStyle(new Notification.BigTextStyle())
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    private String createChannel(Service ctx) {
        // Create a channel.
        NotificationManager notificationManager =
                (NotificationManager)
                        ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence channelName = "Playback channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel =
                new NotificationChannel(
                        CHANNEL_ID, channelName, importance);

        notificationManager.createNotificationChannel(
                notificationChannel);
        return CHANNEL_ID;
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "Служба создана", Toast.LENGTH_SHORT);
        super.onCreate();
        myLocListener = new MyLocListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Permisns", "недостаточно разрешений");
            NotifyLog("Недостаточно разрешений");
            //Toast.makeText(context, "Недостаточно разрешений", Toast.LENGTH_LONG).show();
            return;
        }
        criteria = new Criteria();
        playerChracteristics=new PlayerChracteristics(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotification(this);
        } else {
            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.tactics); //заменить на нужное
            Notification notification;
            //builder.setChannelId("maps");
            notification = builder.build();
            startForeground(777, notification);
            //Intent hideIntent = new Intent(this, HideNotificationService.class);
            //startForeground(786445,notification);
        }


        Log.d("жопонька","onCreate завершен");
        super.onCreate();
    }

    public void CheckGpsStatus() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void InitCounter() {
        mTimer = new Timer();
        mMainTimer = new MainTimer();
        mTimer.schedule(mMainTimer, 1000, 1000);

    }

    class MainTimer extends TimerTask {
        private double distanceInKmBetweenEarthCoordinates(double lat1, double lon1, double lat2, double lon2) {
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

        private double degreesToRadians(double degrees) {
            return degrees * Math.PI / 180;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            CheckGpsStatus();
            Log.d("coord",""+lat+" , "+ lon);
            if (GpsStatus == true) {

                service.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, myLocListener);
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                //dataset.setText(Date);

                if (location != null) {


                    double cLat = location.getLatitude();
                    double cLon = location.getLongitude();
                    lat=cLat;
                    lon=cLon;

                    playerChracteristics.setLongitude(lon);
                    playerChracteristics.setLatitude(lat);
                    /*
                    LatLng position = new LatLng(cLat, cLon);

//                                        button3.setText("" + cLat + "," + cLon);
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
                            //   button3.setText(button3.getText().toString() + "\n" + "Опасность в " + dist + " метрах. " + "\n" + anomaly_titles[i]
                            //          + "\n" + "Накопленная радиация: " + radiation);


                        }

                    }


                    counter++;
                    if (counter > 30) {
                        counter=0;
                        if(markers!=null&&mMap!=null){
                            for(int i=0;i<markers.size();i++){
                                Log.d ("Markers", ""+markers.get(i));
                                markers.get(i).remove();
                            }
                        }

                        markers.clear();

                        mMap.addMarker(new MarkerOptions().position(position).icon(getBitmapHighDescriptor(R.drawable.stalker)).title("I'm here"));

                        //init();
                    }

                    mMap.setMyLocationEnabled(true);




                    LatLng touch = new LatLng(touch_lat, touch_lon);

                    mMap.addMarker(new MarkerOptions()
                            .position(touch)
                            .title("Точка")
                    );
                    */
                }
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean reset;
        //Toast.makeText(getApplicationContext(),""+"Сервис запущен",Toast.LENGTH_LONG);
        NotifyLog("Сервис запущен");
        //boolean resets=intent.getBooleanExtra("ResetPlayer", true);
        //Toast.makeText(getApplicationContext(),""+resets,Toast.LENGTH_LONG);
        //NotifyLog("Недостаточно разрешений");
        service=this;
        CheckGpsStatus();
        InitCounter();

        SendDatapackBroadcast(DataPack.GetDataPack(service),"ALL");
        Log.d("жопонька","Первичный датапак отправлен в бродкаст");
        return START_STICKY;
    }





    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Tactical Maps";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Initializator.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /*SharedPreferences servicePreferences;
    private void SaveState(){
        //Debug.Log("начало сохранения состояния сервиса");
        servicePreferences=getSharedPreferences("datapack",MODE_PRIVATE);
        Editor ed=servicePreferences.edit();
        ed.putBoolean("ResetPlayer",false);
        ed.putString("name",playerChracteristics.isNickname());
        ed.putString("team",playerChracteristics.isTeam());
        ed.putString("side",playerChracteristics.isSide());
        ed.apply();
        //Debug.Log("конец сохранения состояния сервиса");
    }


    private void LoadState(){
              servicePreferences=getSharedPreferences("datapack",MODE_PRIVATE);
        boolean ResetPlayer=servicePreferences.getBoolean("ResetPlayer",true);
        if(!ResetPlayer){
            playerChracteristics=new PlayerChracteristics(this);
            playerChracteristics.setNickname(servicePreferences.getString("name",""));
            playerChracteristics.setTeam(servicePreferences.getString("team",""));
            playerChracteristics.setSide(servicePreferences.getString("side",""));
            Toast.makeText(getApplicationContext(), "Данные загружены: "+playerChracteristics.isNickname()+", "+playerChracteristics.isTeam()
                    +", "+playerChracteristics.isSide(), Toast.LENGTH_LONG).show();

        } else


            Toast.makeText(getApplicationContext(), "Данные не загружены", Toast.LENGTH_LONG).show();;
    }
    */

    //region Notify
    public void NotifyLog(String addict){
        //HermesEventBus.getDefault().post(new LogEvent(addict));
        SendStringBroadcast(addict);
    }

    public void NotifyToast(String value){
        //HermesEventBus.getDefault().post(new ToastEvent(value));
        SendToastBroadcast(value);
    }

    public void NotifyActivity(String Event) {
        //if(playerChracteristics!=null) {
        DataPack dp = DataPack.GetDataPack(this);
        SendDatapackBroadcast(dp, Event);
        NotifySystem(dp);
        createNotificationChannel();
        //}
    }

    public void NotifySystem(DataPack dataPack){
        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        String status="";
        status="Позывной: "+(dataPack.nickname)+", Команда: "+(dataPack.team);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(MapService.this, Initializator.CHANNEL_ID)
                        .setSmallIcon(R.drawable.tactics)
                        .setContentTitle("Состояние")
                        .setContentText(status)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(contentIntent);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(MapService.this);
        notificationManager.notify(Initializator.NOTIFY_ID, builder.build());
    }

    //endregion
}
