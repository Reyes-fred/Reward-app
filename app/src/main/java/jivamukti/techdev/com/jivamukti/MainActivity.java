package jivamukti.techdev.com.jivamukti;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;

import org.joda.time.DateTime;
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
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton home;
    SQLiteDatabase db2;
    Intent salir;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    android.support.v7.widget.Toolbar mToolbar;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    private String[] tagTitles;
    Intent i;
    String Mensajeserver;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(policy);

        tagTitles = getResources().getStringArray(R.array.Tags);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        home = (ImageButton) findViewById(R.id.home);
        home.bringToFront();
        home.setOnClickListener(this);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);



        drawerToggle= new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);

        //Crear elementos de la lista
        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        items.add(new DrawerItem(tagTitles[0], R.drawable.icono_premios));
        items.add(new DrawerItem(tagTitles[1], R.drawable.icono_cuenta));
        items.add(new DrawerItem(tagTitles[6], R.drawable.icono_calendario));
        items.add(new DrawerItem(tagTitles[2], R.drawable.icono_funcion));
        items.add(new DrawerItem(tagTitles[3], R.drawable.icono_feed));
        items.add(new DrawerItem(tagTitles[4], R.drawable.icono_terminos));
        items.add(new DrawerItem(tagTitles[5], R.drawable.icono_cerrar));

        // Relacionar el adaptador y la escucha de la lista del drawer
        drawerList.setAdapter(new DrawerListAdapter(this, items));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());


        if(status()){
            db2 = openOrCreateDatabase("MyDB",Context.MODE_PRIVATE,null);
            Cursor c = db2.rawQuery("SELECT * FROM puntosbeacons where status=0",null);
            if(c.moveToFirst()){
                do{
                    int puntos = conectar();
                    if(puntos!=-1){
                        showNotification("Felicidades","Usted gano"+puntos);
                    }
                    db2.execSQL("UPDATE puntosbeacons SET status=1 where id="+c.getColumnIndex("id"));
                }while (c.moveToNext());
                db2.close();
            }

            //Pase de lista
            paseLista();
        }

         i = new Intent(this,comofuncionaActivity.class);
        selectItem(7);

        }




    @Override
    protected void onPause() {

        super.onPause();
    }



    /* La escucha del ListView en el Drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
       // FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.zoom_forward_in, R.anim.zoom_forward_out, R.anim.zoom_forward_in, R.anim.zoom_forward_out);


       switch (position){
            case 0:
                //fragmentManager.beginTransaction().replace(R.id.containerView, new premios()).commit();
                ft.replace(R.id.containerView, new premios());
                ft.addToBackStack(null);
                ft.commit();
                break;
           case 1:
               ft.replace(R.id.containerView, new micuenta());
               ft.addToBackStack(null);
               ft.commit();
               break;
           case 2:
               ft.replace(R.id.containerView, new horarios());
               ft.addToBackStack(null);
               ft.commit();
               break;
           case 3:
               startActivity(i);
               overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
               break;
           case 4:
               ft.replace(R.id.containerView, new feedback());
               ft.addToBackStack(null);
               ft.commit();
               break;
           case 5:
               ft.replace(R.id.containerView, new terminos());
               ft.addToBackStack(null);
               ft.commit();
               break;
           case 6:
               db2 = openOrCreateDatabase("MyDB",android.content.Context.MODE_PRIVATE,null);
               db2.execSQL("DELETE FROM user;");
               db2.execSQL("VACUUM;");
               db2.execSQL("DELETE FROM infouser;");
               db2.execSQL("VACUUM;");
               db2.execSQL("DELETE FROM puntosbeacons;");
               db2.execSQL("VACUUM;");
               db2.close();
               Toast toast1 =
                       Toast.makeText(getApplicationContext(),
                               "Usuario desconectado", Toast.LENGTH_SHORT);

               toast1.show();
               salir = new Intent(this,TutorialActivity.class);
               startActivity(salir);
               finish();

               break;
           case 7:
               ft.replace(R.id.containerView, new home());
               ft.addToBackStack(null);
               ft.commit();
               break;

        }

        // Se actualiza el item seleccionado y el título, después de cerrar el drawer
        drawerList.setItemChecked(position, true);
       // setTitle(tagTitles[position]);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        switch (v.getId()) {
            case R.id.home:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.zoom_forward_in, R.anim.zoom_forward_out, R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                ft.replace(R.id.containerView, new home());
                ft.addToBackStack(null);
                ft.commit();
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);


    }


    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/check_in").openConnection();
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

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    public boolean paseLista() {
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
                JSONArray ja = new JSONArray();
                db2 = openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
                c = db2.rawQuery("SELECT * FROM tiempoPaseLista where status=0", null);

                if (c.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros
                    do {

                        final DateTime in = DateTime.parse(c.getString(0));
                        final DateTime out = DateTime.parse(c.getString(1));
                        long entrounix = in.getMillis()/1000;
                        long saliounix = out.getMillis()/1000;

                        jo1.put("type", "arrival");
                        jo1.put("time", entrounix);
                        jo2.put("type", "departure");
                        jo2.put("time", saliounix);
                        db2.execSQL("UPDATE tiempoPaseLista SET status=1 where id=" + c.getColumnIndex("id"));
                        ja.put(jo1);
                        ja.put(jo2);
                    } while(c.moveToNext());
                }





                mainObj.put("times", ja);}
            catch(Exception e){}


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
                    if (json1.getInt("code") == 201)
                        return true;
                    else
                        Mensajeserver = json1.getString("message");
                    return false;

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



}
