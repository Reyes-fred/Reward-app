package jivamukti.techdev.com.jivamukti;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
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


public class Tab2 extends Fragment  implements View.OnClickListener {
    Button registrarse,terminos;
    EditText name,lastname,phone,email,email2,password,password2,birthday;
    CheckBox acepta;
    Intent i;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    String Mensajeserver;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
View v;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.tab_2,container,false);
        StrictMode.setThreadPolicy(policy);
        registrarse = (Button) v.findViewById(R.id.registrarse);
        registrarse.setOnClickListener(this);
        terminos = (Button) v.findViewById(R.id.terminos);
        terminos.setOnClickListener(this);
        name = (EditText) v.findViewById(R.id.nombre);
        lastname = (EditText) v.findViewById(R.id.apellido);
        phone = (EditText) v.findViewById(R.id.numtelf);
        email = (EditText) v.findViewById(R.id.email);
        email2 = (EditText) v.findViewById(R.id.verificaemail);
        password = (EditText) v.findViewById(R.id.contrasena);
        password2 = (EditText) v.findViewById(R.id.verificacontrasena);
        birthday = (EditText) v.findViewById(R.id.cumpleanos);
        birthday.addTextChangedListener(tw);
        acepta = (CheckBox) v.findViewById(R.id.acepta);
        acepta.setOnClickListener(this);


        return v;
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
                birthday.setText(current);
                birthday.setSelection(sel < current.length() ? sel : current.length());
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        switch (v.getId()) {
            case R.id.registrarse:
                if(status()) {

                    if (camposObligatorios()) {
                        if (validaremail()) {
                            if (validarformatoemail()){
                                if (validarpass()) {
                                    if (validartamanopass()) {
                                        if (validarformatotel()){
                                            if (acepta.isChecked() == true) {

                                                new crearcuenta().execute(name.getText().toString(), lastname.getText().toString(), email.getText().toString(),
                                                phone.getText().toString(), birthday.getText().toString(), password.getText().toString());

                                            } else {
                                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Acepta los terminos y condiciones", Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();


                                            }
                                    }
                                    else{
                                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Escribe un teléfono correcto", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();

                                        }
                                    } else {
                                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Contraseña debe ser mayor a 6 digitos", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();

                                    }
                                } else {
                                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Contraseña no coincide", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                }
                        }
                        else{
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Escribe un correo valido", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            }
                        } else {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Email no coincide", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            }

                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Algunos campos son obligatorios", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();


                    }
                }
                else{
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No tienes conexión a internet", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();


                }
                break;
            case R.id.terminos:
                Intent i = new Intent(v.getContext(),TerminosActivity.class);
                String pdfURL = "http://jivamuktiyogapuebla.com.mx/rewards-app-terms.pdf";
                i.putExtra("url", "http://docs.google.com/gview?embedded=true&url=" + pdfURL);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                break;
        }
    }

    public boolean camposObligatorios(){
        if(!(name.getText().toString().equals(""))&&!(email.getText().toString().equals(""))&&!(email2.getText().toString().equals(""))
                &&!(password.getText().toString().equals(""))&&!(password2.getText().toString().equals(""))){
            return true;
        }
        return false;
    }

    public boolean validaremail() {
        if ((email.getText().toString()).equals(email2.getText().toString())) {
            return true;
        }
        return false;
    }

    public boolean validarformatoemail(){
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
            return true;
        return false;
    }

    public boolean validarformatotel(){
        if(android.util.Patterns.PHONE.matcher(phone.getText().toString()).matches())
            return true;
        return false;
    }

    public boolean validarpass() {
        if ((password.getText().toString()).equals(password2.getText().toString())) {

            return true;
        }
        return false;
    }

    public boolean validartamanopass(){
        if(password.getText().toString().length()>=6)
            return true;
        return false;
    }


    public boolean newuser(String name,String lastname,String email,String phone,String birthday,String pass){
        HttpURLConnection urlConnection = null;
        String result = null;
        try {
            urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/user/").openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true); // automatically use POST method
            urlConnection.setRequestMethod("POST");

            HashMap postDataParams = new HashMap();
            postDataParams.put("user_first_name", name);
            postDataParams.put("user_last_name", lastname);
            postDataParams.put("user_email", email);
            postDataParams.put("user_password", pass);
            postDataParams.put("user_phone", phone);
            postDataParams.put("user_birthday", birthday);



            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            Log.e("Fecha Nac", birthday);
            Log.e("Cuerpo", getPostDataString(postDataParams));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();


            Log.d("cone", "connect..");
            urlConnection.connect();

            int code = urlConnection.getResponseCode(); // status line!
            Log.e("code", ""+urlConnection.getResponseCode());
            Log.e("mensaje", ""+urlConnection.getResponseMessage());
            Log.e("codigo", ""+code);
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
                      if(json1.getInt("code")==201)
                          return true;
                      else
                          Mensajeserver =  json1.getString("message");
                          return false;

                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }

            }
            else{


                return false;
            }


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


    private class crearcuenta extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pdLoading = new ProgressDialog(v.getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("Creando usuario...");
            pdLoading.show();
        }
        @Override
        protected Boolean doInBackground(String ... params) {

            return newuser(params[0],params[1],params[2],params[3],params[4],params[5]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);



            if (result) {
                i = new Intent(v.getContext(), RegistroCompletoActivity.class);
                getActivity().finish();

                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Usuario creado con exito", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);

            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), Mensajeserver, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
            pdLoading.dismiss();
        }

    }
}
