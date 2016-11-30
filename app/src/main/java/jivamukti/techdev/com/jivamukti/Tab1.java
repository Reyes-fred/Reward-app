package jivamukti.techdev.com.jivamukti;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Alfredo on 04/11/2015.
 */
public class Tab1 extends Fragment implements View.OnClickListener {
    View v;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    Intent i;
    Button login;
    EditText user,pass;
    SQLiteDatabase db2;
    ImageView banner;
    String link;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.tab_1,container,false);

        StrictMode.setThreadPolicy(policy);
        login = (Button) v.findViewById(R.id.login);
        login.setOnClickListener(this);
        user = (EditText) v.findViewById(R.id.user);
        pass = (EditText) v.findViewById(R.id.pass);
        banner = (ImageView) v.findViewById(R.id.banner);
        if(status()) {
            new cargarbanner().execute();
            banner.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (status()) {
                        Intent i = new Intent(v.getContext(), TerminosActivity.class);
                        if (!link.equals(""))
                            i.putExtra("url", link);
                        else
                            i.putExtra("url", "");
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);

                    }
                }
            });
        }

        return v;
    }


    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        switch (v.getId()) {
            case R.id.login:

                if (status()) {//checa wifi

                if (!user.getText().toString().equals("") || !pass.getText().toString().equals("")) {

                    if (conectar(user.getText().toString(), pass.getText().toString())) {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Bienvenido " + user.getText().toString(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        i = new Intent(v.getContext(), MainActivity.class);
                        getActivity().finish();
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Datos incorrectos", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        pass.setText("");
                    }

                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Ingresa tus datos porfavor", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
        }else{
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No tienes conexión a internet", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
                break;

        }
    }
/*
    public boolean getServerData(String user, String pass){
        String strURL = "https://mvp.oakandsand.com/projects/rewards/api/";
        final String username = user;
        final String password = pass;
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                PasswordAuthentication pa = new PasswordAuthentication (username, password.toCharArray());
//					System.out.println(pa.getUserName() + ":" + new String(pa.getPassword()));
                return pa;
            }
        });
        BufferedReader in = null;
        StringBuffer sb = new StringBuffer();

        try {
            URL url = new URL(strURL);
            URLConnection connection = url.openConnection();
            if(connection!=null){
            in = new BufferedReader(new InputStreamReader(connection
                    .getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                sb.append(line);
            }}
        } catch (java.net.ProtocolException e) {
            sb.append("User Or Password is wrong!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                System.out.println("Exception");
            }
        }

        Log.d("DATA", sb.toString());

    return false;
}*/

    public boolean conectar(String user, String pass){
        HttpURLConnection urlConnection = null;
        String password = user+":"+pass;
        String result = "";
        try {
            urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/").openConnection();
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
            BufferedReader reader  = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
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
            JSONObject json1 = jsonO.getJSONObject("user");


                actualizar(user,pass);
                actualizar2(json1.getString("user_id"),
                json1.getString("user_first_name"),
                json1.getString("user_last_name"),
                json1.getString("user_email"),
                json1.getString("user_creation_timestamp"),
                json1.getString("user_last_login_timestamp"),
                json1.getString("user_phone"),
                json1.getString("user_birthday"),
                json1.getString("user_points"),
                json1.getString("user_in_newsletter"),
                json1.getString("user_last_check_in"));



        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return true;
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
        return false;
    }

/*https://code.google.com/p/android/issues/detail?id=9579
 http://www.xyzws.com/javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
 http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post*/
public void actualizar(String user,String password){
    db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
    db2.execSQL("INSERT INTO user (user,password)" + " VALUES ('"+user+"','"+password+"');");
    db2.close();
    Log.d("inserto", "");
}
public void actualizar2(String user_id,String user_first,String user_last,String user_email,String user_creation,
                        String user_last_login,String user_phone,String user_birthday,String user_points,
                        String user_in,String user_last_check){
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        db2.execSQL("INSERT INTO infouser (user_id,user_first_name,user_last_name," +
                "user_email,user_creation_timestamp,user_last_login_timestamp," +
                "user_phone,user_birthday,user_points,user_in_newsletter,user_last_check_in" +
                ")" + " VALUES ('"+user_id+"','"+user_first+"','"+user_last+"','"+user_email+"','"+user_creation+"','"+user_last_login+"'," +
                "'"+user_phone+"','"+user_birthday+"','"+user_points+"','"+user_in+"','"+user_last_check+"');");
        db2.close();
        Log.d("inserto", "");
    }


    public boolean status() {
        ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

           return true;
        }
       return false;
    }



    public String geturlBanner() {
        HttpURLConnection urlConnection = null;
        String bannerurl="";


        String result = "";

        if(status()) {
            try {
                urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/banner/300").openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true); // automatically use POST method
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");




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
                        JSONObject json1 = jsonO.getJSONObject("banner");
                        bannerurl = json1.getString("image_url");
                        link=json1.getString("link");

                        return bannerurl;


                    } catch (JSONException e) {
                        Log.e("log_tag", "Error parsing data " + e.toString());
                    }

                    return bannerurl;
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

        }
        return bannerurl;
    }

    public static Bitmap loadImage(String image_location){
        Bitmap bitmap=null;
        URL imageURL = null;

        try {
            imageURL = new URL(image_location);
        }

        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection connection= (HttpURLConnection)imageURL.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            return bitmap = BitmapFactory.decodeStream(inputStream);//Convert to bitmap

        }
        catch (IOException e) {

            e.printStackTrace();
        }
        return bitmap;
    }


    private class cargarbanner extends AsyncTask<Void, Void, Bitmap>
    {
        ProgressDialog pdLoading = new ProgressDialog(v.getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("Cargando...");
            pdLoading.show();
        }
        @Override
        protected Bitmap doInBackground(Void ... params) {

            String url=geturlBanner();
            if(!url.equals("")){
                    return loadImage(url);
            }
            else return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            if (result!=null) {
                banner.setImageBitmap(result);
            } else {
                Bitmap iconotemp = BitmapFactory.decodeResource(v.getContext().getResources(), R.drawable.logo_jivamukti);
                Bitmap imagen= iconotemp;
                banner.setImageBitmap(imagen);
            }
            pdLoading.dismiss();
        }

    }


}
