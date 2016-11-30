package jivamukti.techdev.com.jivamukti;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.JsonObject;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MyApplication extends Application {
    private BeaconManager beaconManager;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    String Mensajeserver;
    SQLiteDatabase db2;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
    Date date1,date2;
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        StrictMode.setThreadPolicy(policy);
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {

                DateTime today = new DateTime();


                String tiempo = "" + today.toString();
                db2 = openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
                db2.execSQL("Insert into tiempo (tiempobeacon) values('" + tiempo + "')");
                db2.close();
                showNotification("Bienvenido a Jivamukti", "");

            }

            @Override
            public void onExitedRegion(Region region) {
                //showNotification("Exited region.", "You have exited the region.");
                Log.e("Salio", "");


                final DateTime out = DateTime.now();
                final DateTime in = DateTime.parse(getinfo());
                Log.e("Entro", in.toString());
                Log.e("Salio", out.toString());
                Log.e("Diferencia", "" + Minutes.minutesBetween(in, out).getMinutes());

                String entro = in.toString("yyyy-M-dd hh:mm:ss");
                String salio= out.toString("yyyy-M-dd hh:mm:ss");
                Log.e("AsistenciaEntro",entro);
                Log.e("AsistenciaSalio",salio);

                long entrounix = in.getMillis()/1000;
                long saliounix = out.getMillis()/1000;
                if (status()) {

                    paseListaconInternet(entrounix, saliounix);
                } else {//sin conexion
                    db2 = openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
                    db2.execSQL("Insert into tiempoPaseLista (entrada,salida,status) values('" + entro + "','" + salio+ "',0)");
                    db2.close();
                }

                if (Minutes.minutesBetween(in, out).getMinutes() >= 40) {
                    Log.e("Conecto", "");
                    if (status()) {
                        int puntos = conectar();
                        if (puntos != -1) {
                            showNotification("Felicidades", "Usted gano " + puntos);
                        } else {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    conectar();
                                }
                            }, 5000);

                        }

                    } else {
                        db2 = openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
                        db2.execSQL("Insert into puntosbeacons (status) values(0)");
                        db2.close();
                    }
                }


            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        null, null
                ));

            }
        });
    }

    public String getinfo() {
        String tiempo="";
        SQLiteDatabase db2;
        db2 = openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT tiempobeacon FROM tiempo", null);
        if(c.moveToFirst()){
            tiempo=c.getString(0);
        }


        db2.execSQL("DELETE FROM tiempo;");
        db2.execSQL("VACUUM;");
        db2.close();
        return tiempo;
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.iconoappnotif)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    public int conectar(){
        int puntos=-1;
        HttpURLConnection urlConnection = null;
        SQLiteDatabase db2;
        db2 = openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);

        Cursor c = db2.rawQuery("SELECT user,password FROM user", null);
        if(c.moveToFirst()){
            String email = c.getString(0);
            String pass = c.getString(1);
            String password = email + ":" + pass;
            String result = "";
            try {
                urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/check_in/").openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true); // automatically use POST method
                urlConnection.setRequestMethod("GET");

                String basic = "Basic " + Base64.encodeToString((password).getBytes(), Base64.NO_WRAP);
                Log.d("info", basic);
                urlConnection.setRequestProperty("Authorization", basic);

                Log.d("cone", "connect..");
                urlConnection.connect();



                int code = urlConnection.getResponseCode(); // status line!

                if(code == 200){



                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        result = sb.toString();
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }
                    //parse json data
                    try {
                        JSONObject jsonO = new JSONObject(result);
                        JSONObject json1 = jsonO.getJSONObject("status");
                        if(json1.getInt("code")==200) {

                            //parse json data
                            try {
                                jsonO = new JSONObject(result);
                                String total = jsonO.getString("awarded_points");
                                puntos = Integer.parseInt(total);
                                return puntos;


                            } catch (JSONException e) {
                                Log.e("log_tag", "Error parsing data " + e.toString());
                            }


                        } else{
                            Mensajeserver =  json1.getString("message");
                            Log.e("log_tag", "Error " + Mensajeserver);
                            return puntos;
                        }
                        return puntos;

                    } catch (JSONException e) {
                        Log.e("log_tag", "Error parsing data " + e.toString());
                    }





                    Log.d("code", "code=" + code);



                }
                else{
                    return puntos;
                }


            } catch (MalformedURLException e) {
                Log.e("men", e.getMessage(), e);
            } catch (IOException e) {
                Log.e("men2", e.getMessage(), e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }}
        return puntos;
    }

    public boolean status() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

            return true;
        }
        return false;
    }


    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }



    public boolean paseListaconInternet(long in,long out) {
        HttpURLConnection urlConnection = null;
        String result = "";
        SQLiteDatabase db2;
        db2 = openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);

        Cursor c = db2.rawQuery("SELECT user,password FROM user", null);
        c.moveToFirst();
        String email = c.getString(0);
        String pass = c.getString(1);
        String password = email + ":" + pass;
        JSONObject mainObj = new JSONObject();

        try {
            urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/record_times/").openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true); // automatically use POST method
            urlConnection.setRequestMethod("POST");

            String basic = "Basic " + Base64.encodeToString((password).getBytes(), Base64.NO_WRAP);
            Log.d("info", basic);
            urlConnection.setRequestProperty("Authorization", basic);


            /*Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("message", cuerpo);
            String query = builder.build().getEncodedQuery();
            */

            HashMap postDataParams = new HashMap();

            try{
                JSONObject jo1 = new JSONObject();
                JSONObject jo2 = new JSONObject();
                        jo1.put("type", "arrival");
                        jo1.put("time", in);
                        jo2.put("type", "departure");
                        jo2.put("time", out);


                JSONArray ja = new JSONArray();
                ja.put(jo1);
                ja.put(jo2);


                mainObj.put("times", ja);}
            catch(Exception e){}

            Log.e("tiempo",mainObj.toString());
            // populate hash map
            postDataParams.put("times", mainObj.toString());

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            //Log.e("Cuerpo", getPostDataString(postDataParams));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();


            Log.d("cone", "connect..");
            urlConnection.connect();

            int code = urlConnection.getResponseCode(); // status line!
            if (code == 200) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }
                //parse json data
                try {
                    JSONObject jsonO = new JSONObject(result);
                    JSONObject json1 = jsonO.getJSONObject("status");
                    if (json1.getInt("code") == 201){
                        Mensajeserver = json1.getString("message");
                        Log.e("Mensaje Server si entro",Mensajeserver);
                        return true;
                    }
                    else{
                        Mensajeserver = json1.getString("message");
                    Log.e("Mensaje Server",Mensajeserver);
                    return false;}

                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }

            } else {


                return false;
            }
            Log.d("code", "code=" + code);

        } catch (MalformedURLException e) {
            Log.e("men", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("men2", e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        db2.close();

        return false;
    }


}
