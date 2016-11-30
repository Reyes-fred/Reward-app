package jivamukti.techdev.com.jivamukti;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class home extends Fragment {
ImageView codigo;
    TextView text1,nombre,porcentaje;

     DonutProgress puntos;
    String qrInputText;
    int Totalpuntos=0;View v;
    ImageView banner;
    String link;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    public home(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v =inflater.inflate(R.layout.home, container, false);
        nombre = (TextView) v.findViewById(R.id.nombre);
        porcentaje = (TextView) v.findViewById(R.id.porcentaje);
        StrictMode.setThreadPolicy(policy);
        banner = (ImageView) v.findViewById(R.id.banner);

        getinfo();

        codigo = (ImageView) v.findViewById(R.id.codigo);
        puntos = (DonutProgress) v.findViewById(R.id.puntosprogress);
        Log.e("porcentaje ", "" + Math.round((100 * Totalpuntos) / 1000));
        if(Math.round((100 * Totalpuntos) / 1000)>100){
            porcentaje.setText("100 %");
            puntos.setProgress(100);
        }

        else{
            porcentaje.setText("" + Math.round((100 * Totalpuntos) / 1000) + "%");
            puntos.setProgress(Math.round((100 * Totalpuntos) / 1000));
        }




        //Find screen size
        WindowManager manager = (WindowManager) v.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3/4;
        Log.e("Smalldimension",""+smallerDimension);
        smallerDimension =(smallerDimension * 2/4)+26;
        Log.e("Smalldimension",""+smallerDimension);
        //Encode with a QR Code image

        QRCodeEncoder barCodeEncoder = new QRCodeEncoder(qrInputText,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = barCodeEncoder.encodeAsBitmap();
            ImageView myImage = (ImageView) v.findViewById(R.id.codigo);
            myImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
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
        text1 = (TextView) v.findViewById(R.id.puntos);
       text1.setText(Html.fromHtml("<big><big><b>" + Totalpuntos + "</b></big></big>" + " / 1000 pts"));



        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
       mSwipeRefreshLayout.setColorSchemeResources(R.color.morado, R.color.morado, R.color.morado);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new DownloadFilesTask().execute("https://jivamuktiyogapuebla.com.mx/rewards/api/points/");
                    }
                }
        );


        return v;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public void getinfo() {
        SQLiteDatabase db2;
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT user_first_name,user_last_name,user_points," +
                "user_id FROM infouser", null);
        c.moveToFirst();

        nombre.setText(Html.fromHtml("<u><b>"+c.getString(0)+" "+c.getString(1)+"</b></u>"));
        //nombre.setText(c.getString(0) + " " + c.getString(1));
        qrInputText = c.getString(3);
        Totalpuntos = Integer.parseInt(c.getString(2));
        db2.close();
    }



   private class DownloadFilesTask extends AsyncTask<String, Void, String> {


       static final int DURACION = 3 * 1000; // 3 segundos de carga

        @Override
        protected void onPostExecute(String result) {
           if(status()) {
               if (!result.equals("")) {

                   actualizar2(result);
                   getinfo();
                   text1.setText(Html.fromHtml("<big><big><b>" + result + "</b></big></big>" + " / 1000 pts"));
                   puntos.setProgress(100);
                   puntos.setProgress(0);
                   puntos.setProgress(Math.round((100 * Integer.parseInt(result)) / 1000));
                   Log.e("porcentaje ", "" + Math.round((100 * Totalpuntos) / 1000));
                   if(Math.round((100 * Totalpuntos) / 1000)>100){
                       porcentaje.setText("100 %");
                       puntos.setProgress(100);
                   }

                   else{
                       porcentaje.setText("" + Math.round((100 * Totalpuntos) / 1000) + "%");
                       puntos.setProgress(Math.round((100 * Totalpuntos) / 1000));
                   }

                   if (Integer.parseInt(result) >= 1000) {
                       Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Puede cambiar por un premio", Toast.LENGTH_LONG);
                       toast.setGravity(Gravity.CENTER, 0, 0);
                       toast.show();

                   }

               }
           }
            else{
               Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No tienes conexión a internet", Toast.LENGTH_LONG);
               toast.setGravity(Gravity.CENTER, 0, 0);
               toast.show();

           }
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... params) {

           try {
                Thread.sleep(DURACION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // getting JSON string from URL

            return getPoints(params[0]);
        }
    }

    public String getPoints(String url) {
        HttpURLConnection urlConnection = null;
        SQLiteDatabase db2;
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        String nuevospuntos="";
        Cursor c = db2.rawQuery("SELECT user,password FROM user", null);
        c.moveToFirst();
        String email=c.getString(0);
        String pass=c.getString(1);
        String password = email+":"+pass;
        String result = "";
        Log.d("url", password);
        if(status()) {
            try {
                urlConnection = (HttpURLConnection) new URL(url).openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true); // automatically use POST method
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");

                String basic = "Basic " + Base64.encodeToString((password).getBytes(), Base64.NO_WRAP);
                Log.d("info", basic);
                urlConnection.setRequestProperty("Authorization", basic);


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
                        JSONObject json1 = jsonO.getJSONObject("user");
                        Log.e("Puntos ", "" + jsonO.getJSONObject("user"));
                        nuevospuntos = json1.getString("user_points");
                        Log.e("Puntos ", nuevospuntos);
                        db2.close();
                        return nuevospuntos;


                    } catch (JSONException e) {
                        Log.e("log_tag", "Error parsing data " + e.toString());
                    }

                    return nuevospuntos;
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
        }
        return nuevospuntos;
    }


    public void actualizar2(String puntos){

        SQLiteDatabase db2;
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        db2.execSQL("UPDATE infouser set "+
                "user_points =" + puntos +
                " ;" );
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
