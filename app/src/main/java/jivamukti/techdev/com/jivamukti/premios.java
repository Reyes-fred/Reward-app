package jivamukti.techdev.com.jivamukti;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class premios extends Fragment {
    View v;
    private RecyclerView recycler;
    private premiosAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private SwipeRefreshLayout refreshLayout;

    public premios(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       v = inflater.inflate(R.layout.premios, container, false);

        // Obtener el Recycler
        recycler = (RecyclerView) v.findViewById(R.id.reciclador);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);


        // Crear un nuevo adaptador
        if(status()) {
            new inicialiar().execute();

        }
        else{
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No tienes conexión a internet", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }


        // Obtener el refreshLayout
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);



        // Iniciar la tarea asíncrona al revelar el indicador
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new HackingBackgroundTask().execute();
                    }
                }
        );
        // That's all!



        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private class inicialiar extends AsyncTask<Void, Void, List<premio>> {
        private final ProgressDialog dialog = new ProgressDialog(v.getContext());

        protected void onPreExecute() {
            this.dialog.setMessage("Cargando...");
            this.dialog.show();
        }
        @Override
        protected List<premio> doInBackground(Void... params) {
            // Simulación de la carga de items

            SQLiteDatabase db2;
            db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
            Cursor c = db2.rawQuery("SELECT user,password FROM user", null);
            c.moveToFirst();
            String email = c.getString(0);
            String pass = c.getString(1);
            db2.close();
            // Retornar en nuevos elementos para el adaptador

            return conjuntoPremios.randomList(email,pass,v);
        }

        @Override
        protected void onPostExecute(List<premio> result) {
            super.onPostExecute(result);
            if (this.dialog.isShowing()) { // if dialog box showing = true
                this.dialog.dismiss(); // dismiss it
            }

            if(status()){
                if(result.size()<=0) {

                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Error en el servidor", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            else{
                    adapter = new premiosAdapter(result);
                    recycler.setAdapter(adapter);
                    recycler.setHasFixedSize(true);
                }
            }
            else{
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No tienes conexión a internet", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();


            }

        }

    }



    private class HackingBackgroundTask extends AsyncTask<Void, Void, List<premio>> {


        @Override
        protected List<premio> doInBackground(Void... params) {
            // Simulación de la carga de items

            SQLiteDatabase db2;
            db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
            Cursor c = db2.rawQuery("SELECT user,password FROM user", null);
            c.moveToFirst();
            String email = c.getString(0);
            String pass = c.getString(1);
            db2.close();
            // Retornar en nuevos elementos para el adaptador

            return conjuntoPremios.randomList(email,pass,v);
        }

        @Override
        protected void onPostExecute(List<premio> result) {
            super.onPostExecute(result);

            // Limpiar elementos antiguos
            adapter.clear();

            // Añadir elementos nuevos
            adapter.addAll(result);
            if(status()){
            if(result.size()<=0) {

                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Error en el servidor", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }}
            else{
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "No tienes conexión a internet", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();


            }
            // Parar la animación del indicador
            refreshLayout.setRefreshing(false);
        }

    }

    public boolean status() {
        ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

            return true;
        }
        return false;
    }



}


