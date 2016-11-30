package jivamukti.techdev.com.jivamukti;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class feedback extends Fragment implements View.OnClickListener {
    TextView text1;
    EditText cuerpo;
    Button enviar;
    String Mensajeserver;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    View v;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);

    public feedback() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.feedback, container, false);
        StrictMode.setThreadPolicy(policy);

        text1 = (TextView) v.findViewById(R.id.lobueno);
        text1.setText(Html.fromHtml("<b>Lo bueno y lo malo</b>" + ",estamos aquí para escucharte"));
        cuerpo = (EditText) v.findViewById(R.id.editText3);
        enviar = (Button) v.findViewById(R.id.enviar);
        enviar.setOnClickListener(this);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        switch (v.getId()) {
            case R.id.enviar:

                if (status()) {
                    if (!cuerpo.getText().toString().equals("")) {
                        new enviarfeed().execute(cuerpo.getText().toString());
                    } else {


                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Porfavor ingresa un texto", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No tienes conexión a internet", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public boolean sendfeed(String cuerpo) {
        HttpURLConnection urlConnection = null;
        String result = "";
        SQLiteDatabase db2;
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);

        Cursor c = db2.rawQuery("SELECT user,password FROM user", null);
        c.moveToFirst();
        String email = c.getString(0);
        String pass = c.getString(1);
        String password = email + ":" + pass;
        try {
            urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/feedback/").openConnection();
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

            // populate hash map
            postDataParams.put("message", cuerpo);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            Log.e("Cuerpo", getPostDataString(postDataParams));
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

    public boolean status() {
        ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

            return true;
        }
        return false;
    }


    private class enviarfeed extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pdLoading = new ProgressDialog(v.getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("Enviando...");
            pdLoading.show();
        }
        @Override
        protected Boolean doInBackground(String ... params) {

            return  sendfeed(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            pdLoading.dismiss();

            if (result) {
                cuerpo.setText("");

                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Gracias por tu opinión", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {

                Toast toast = Toast.makeText(getActivity().getApplicationContext(), Mensajeserver, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        }

    }


}