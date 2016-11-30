package jivamukti.techdev.com.jivamukti;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class micuenta extends Fragment implements View.OnClickListener  {
    EditText nombre,apellido,telefono,email,password,cumple;
    TextView titulo,nummiembro;
    int iduser;
    CheckBox check;
    SQLiteDatabase db2;
    String Mensajeserver;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
View v;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    public micuenta(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         v=inflater.inflate(R.layout.micuenta, container, false);
        nombre = (EditText) v.findViewById(R.id.nombre);
        StrictMode.setThreadPolicy(policy);
        apellido = (EditText) v.findViewById(R.id.apellido);
        telefono = (EditText) v.findViewById(R.id.numero);
        email = (EditText) v.findViewById(R.id.email);
        password = (EditText) v.findViewById(R.id.contrasena);
        cumple = (EditText) v.findViewById(R.id.cumpleanos);
        cumple.addTextChangedListener(tw);
        titulo = (TextView) v.findViewById(R.id.titulo);
        nummiembro = (TextView) v.findViewById(R.id.nummiembro);
        check = (CheckBox) v.findViewById(R.id.check);
        Button enviar =(Button) v.findViewById(R.id.enviar);
        enviar.setOnClickListener(this);
        // Inflate the layout for this fragment
        getinfo();
        return v;
    }

    public void getinfo() {
        SQLiteDatabase db2;
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT user_first_name,user_last_name,user_phone," +
                "user_email,user_birthday,user_id,user_in_newsletter FROM infouser", null);
        c.moveToFirst();

        nombre.setText(c.getString(0));
        titulo.setText(c.getString(0) + " " + c.getString(1));
        apellido.setText(c.getString(1));
        telefono.setText(c.getString(2));
        email.setText(c.getString(3));
        cumple.setText(c.getString(4));
        nummiembro.setText("Número de miembro # " + c.getString(5));
        iduser=Integer.parseInt(c.getString(5));
        if(Integer.parseInt(c.getString(6))==1){
            check.setChecked(true);
        }else{
            check.setChecked(false);
        }
        c = db2.rawQuery("SELECT password FROM user", null);
        c.moveToFirst();
        password.setText(c.getString(0));
        db2.close();
    }


    public boolean conectar(){
        HttpURLConnection urlConnection = null;
        SQLiteDatabase db2;
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);

        Cursor c = db2.rawQuery("SELECT user,password FROM user", null);
        c.moveToFirst();
        String email = c.getString(0);
        String pass = c.getString(1);
        String password = email + ":" + pass;
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
                db2.close();
                urlConnection.disconnect();
            }
        }
        return false;
    }




    public void actualizar2(String user_id,String user_first,String user_last,String user_email,String user_creation,
                            String user_last_login,String user_phone,String user_birthday,String user_points,
                            String user_in,String user_last_check){
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);

        db2.execSQL("UPDATE infouser set user_id=" +"'" +user_id+"'" +
                ",user_first_name =" +"'"+user_first +"'"+
                ",user_last_name =" +"'" +user_last+"'" +
                ",user_email =" + "'"+user_email +"'"+
                ",user_creation_timestamp =" + "'"+user_creation +"'"+
                ",user_last_login_timestamp =" + "'"+user_last_login +"'"+
                ",user_phone =" + "'"+user_phone +"'"+
                ",user_birthday =" +"'"+user_birthday +"'"+
                ",user_points =" + "'"+user_points +"'"+
                ",user_in_newsletter =" + "'"+user_in +"'"+
                ",user_last_check_in =" + "'"+user_last_check +"'"+
                " ;" );

        db2.close();
        Log.d("inserto", "");
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public boolean validarformatoemail(){
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
            return true;
        return false;
    }

    public boolean validarformatotel(){
        if(!telefono.getText().toString().equals("")){
        if(android.util.Patterns.PHONE.matcher(telefono.getText().toString()).matches())
            return true;
        else{
            return false;}
        }

        return true;

    }

    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        switch (v.getId()) {
            case R.id.enviar:
                if(status()){
                if(camposObligatorios()) {
                    if (validarformatoemail()) {
                        if (validarformatotel()){
                            new enviardatoscuenta().execute(nombre.getText().toString(), apellido.getText().toString(), email.getText().toString(),
                                    telefono.getText().toString(), cumple.getText().toString(), password.getText().toString());
                    }
                    else{
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Escribe un teléfono valido", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                    else{
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Escribe un email valido", Toast.LENGTH_LONG);
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
                break;
        }
    }

    public boolean camposObligatorios(){
        if(!(nombre.getText().toString().equals("")) &&!(email.getText().toString().equals(""))&&!(password.getText().toString().equals(""))){
            return true;
        }
        return false;
    }

    public boolean actualizar(String name,String lastname,String email,String phone,String birthday,String pass){
        HttpURLConnection urlConnection = null;
        SQLiteDatabase db2;
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
       String result="";
        Cursor c = db2.rawQuery("SELECT user,password FROM user", null);
        c.moveToFirst();
        String email2 = c.getString(0);
        String pass2 = c.getString(1);
        String password = email2 + ":" + pass2;
        try {
            urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/user/"+iduser).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true); // automatically use POST method
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");

            String basic = "Basic " + Base64.encodeToString((password).getBytes(), Base64.NO_WRAP);
            Log.d("info", basic);
            urlConnection.setRequestProperty("Authorization", basic);

            HashMap postDataParams = new HashMap();
            postDataParams.put("user_first_name", name);
            postDataParams.put("user_last_name", lastname);
            postDataParams.put("user_email", email);
            postDataParams.put("user_password", pass);
            postDataParams.put("user_phone", phone);
            postDataParams.put("user_birthday", birthday);
            if(check.isChecked()){
                postDataParams.put("user_in_newsletter", "1");}
            else{
                postDataParams.put("user_in_newsletter", "0");
            }

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();


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
                    if(json1.getInt("code")==200)
                        return true;
                    else
                        Mensajeserver =  json1.getString("message");
                    return false;

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

    TextWatcher tw = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "YYYYMMDD";
        private Calendar cal = Calendar.getInstance();
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]", "");
                String cleanC = current.replaceAll("[^\\d.]", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day  = Integer.parseInt(clean.substring(6,8));
                    int mon  = Integer.parseInt(clean.substring(4,6));
                    int year = Integer.parseInt(clean.substring(0,4));

                    if(mon > 12) mon = 12;
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900:(year>2100)?2100:year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",year, mon, day);
                }

                clean = String.format("%s-%s-%s", clean.substring(0, 4),
                        clean.substring(4, 6),
                        clean.substring(6, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                cumple.setText(current);
                cumple.setSelection(sel < current.length() ? sel : current.length());
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    };

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


    public boolean status() {
        ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

            return true;
        }
        return false;
    }


    private class enviardatoscuenta extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pdLoading = new ProgressDialog(v.getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("Modificando datos...");
            pdLoading.show();
        }
        @Override
        protected Boolean doInBackground(String ... params) {

            return actualizar(params[0],params[1],params[2],params[3],params[4],params[5]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            if (result) {
                conectar();
                getinfo();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Los datos fueron modificados", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.zoom_forward_in, R.anim.zoom_forward_out, R.anim.zoom_forward_in, R.anim.zoom_forward_out);


                ft.replace(R.id.containerView, new home());
                ft.addToBackStack(null);
                ft.commit();
            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), Mensajeserver, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            pdLoading.dismiss();
        }

    }


}
