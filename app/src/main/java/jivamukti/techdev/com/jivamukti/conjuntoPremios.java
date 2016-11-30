package jivamukti.techdev.com.jivamukti;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
/**
 * Created by reyes on 12/7/15.
 */
public class conjuntoPremios {

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    conjuntoPremios(){
        StrictMode.setThreadPolicy(policy);
    }
    /**
     * Este método retorna en una lista aleatoria basada en el
     * atributo LISTAS.
     *
     * El parámetro entero count es el tamaño deseado de la lista
     * resultante
     */
    public static ArrayList<premio> randomList(String email,String pass,View v) {
        Random random = new Random();
        HashSet<premio> items = new HashSet<premio>();


        List<premio> LISTA = conectar(email, pass,v);
        for(int x=0;x<LISTA.size();x++) {
            items.add(LISTA.get(x));
        }

        return new ArrayList<premio>(items);
    }


    public static List<premio> conectar(String email, String pass,View v){

        HttpURLConnection urlConnection = null;
        String password = email + ":" + pass;
        String result = "";

        List<premio> LISTA = new ArrayList<premio>();
        try {
            urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/awards/300").openConnection();
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
                    JSONArray jArr = jsonO.getJSONArray("awards");

                    if (jsonO.getJSONArray("awards").equals(null)) {
                        //  return false;
                    } else {
                        premio nuevo=null;
                        Bitmap imagen=null;
                        for (int i = 0; i < jArr.length(); i++) {
                            JSONObject obj = jArr.getJSONObject(i);
                            obj.getString("title");
                            obj.getString("description");
                            obj.getString("available");
                            obj.getString("deadline");
                            obj.getString("price");
                            obj.getString("image_width");
                            obj.getString("image_height");
                            obj.getString("image_url");


                            Bitmap iconotemp = BitmapFactory.decodeResource(v.getContext().getResources(), R.drawable.logo_jivamukti);
                            imagen= iconotemp;
                            String url=obj.getString("image_url");
                            if(!url.equals("")){
                                if(loadImage(url)!=null){
                                    imagen=loadImage(url);
                                }
                            }

                            nuevo = new premio(obj.getString("title"), obj.getString("description"), "Valido hasta " + obj.getString("deadline"), imagen,obj.getString("link"));
                            LISTA.add(i,nuevo);

                            Log.e("log_tag", "Resultado dentro " + obj.toString());
                        }
                    }

                    return LISTA;




                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }


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

        return LISTA;
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



}