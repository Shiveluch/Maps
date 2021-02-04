package com.example.maps;
import android.Manifest;
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


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.maps.classes.DataPack;
import com.example.maps.classes.PlayerChracteristics;

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
public class MapService extends Service
{

public PlayerChracteristics playerChracteristics;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void NotifyActivity(String Event){
        if(playerChracteristics!=null) {
            DataPack dp=DataPack.GetDataPack(this);
            SendDatapackBroadcast(dp, Event);
            NotifySystem(dp);
            createNotificationChannel();
        }
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(),"Служба создана",Toast.LENGTH_SHORT);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean reset;
        Toast.makeText(getApplicationContext(),""+"Сервис запущен",Toast.LENGTH_LONG);
        boolean resets=intent.getBooleanExtra("ResetPlayer", true);
        Toast.makeText(getApplicationContext(),""+resets,Toast.LENGTH_LONG);
if (intent.hasExtra("ResetPlayer"))
        {
            reset=intent.getBooleanExtra("ResetPlayer",true);
            Toast.makeText(getApplicationContext(),""+reset,Toast.LENGTH_LONG);
        }
else {reset=false;}
        if(!reset){

            LoadState();

        }
        return START_STICKY;
    }

    private void SendDatapackBroadcast(DataPack value, String Event){
        Intent intent = new Intent("MapService.DatapackBroadcast");
        intent.putExtra("Datapack", value);
        intent.putExtra("Event",Event);
        sendBroadcast(intent);
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

    SharedPreferences servicePreferences;
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


}
