package jivamukti.techdev.com.jivamukti;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class horarios extends Fragment implements CalendarPickerController {
    View v;
    Toolbar mToolbar;
    AgendaCalendarView mAgendaCalendarView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.horarios, container, false);

        mToolbar = (android.support.v7.widget.Toolbar) v.findViewById(R.id.activity_toolbar);


        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        mAgendaCalendarView = (AgendaCalendarView) v.findViewById(R.id.agenda_calendar_view);

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -1);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.MONTH, 1);

        List<CalendarEvent> eventList = new ArrayList<>();
        getCalendar(eventList);
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
        mAgendaCalendarView.addEventRenderer(new DrawableEventRenderer());


        return v;
    }


    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

    }

    @Override
    public void onScrollToDate(Calendar calendar) {
        if ( ((AppCompatActivity) getActivity() ).getSupportActionBar() != null) {
            ( (AppCompatActivity) getActivity() ).getSupportActionBar().setTitle(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        }
    }

    // endregion

    public void getCalendar(List<CalendarEvent> eventList ) {
        HttpURLConnection urlConnection = null;
        SQLiteDatabase db2;
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT user,password FROM user", null);
        c.moveToFirst();
        String email=c.getString(0);
        String pass=c.getString(1);
        String password = email+":"+pass;
        String result = "";
        if(status()) {
            try {
                urlConnection = (HttpURLConnection) new URL("https://jivamuktiyogapuebla.com.mx/rewards/api/schedule/").openConnection();
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
                        JSONObject schedual = jsonO.getJSONObject("schedule");

                        schedual.getJSONArray("days");
                        JSONArray jArr = schedual.getJSONArray("times");

                        Log.e("Dias", ""+schedual.getJSONArray("days").toString());

                        if (!schedual.getJSONArray("times").equals(null)) {

                            for (int i = 0; i < jArr.length(); i++) {
                                JSONObject obj = jArr.getJSONObject(i);

                                Log.e("Hora",obj.getString("time_start")+"-"+obj.getString("time_end"));

                                JSONArray classes = obj.getJSONArray("classes");
                                if (!obj.getJSONArray("classes").equals(null)) {

                                    for (int j = 0; j < classes.length(); j++) {
                                        JSONObject obj2 = classes.getJSONObject(j);
                                        if(obj2.getString("is_empty").equals("false")){


                                            if(obj2.getString("has_teacher").equals("true")) {
                                                Log.e("Clase","Dia: "+j+" "+obj2.getString("class_name"));
                                                agregarEvento(eventList,j,obj.getString("time_start"),obj.getString("time_end"),obj2.getString("class_name"),obj2.getString("teacher_name"));
                                            }
                                            else{
                                                Log.e("Clase","Dia: "+j+" "+obj2.getString("class_name"));
                                                agregarEvento(eventList,j,obj.getString("time_start"),obj.getString("time_end"),obj2.getString("class_name"),"");
                                            }
                                        }


                                    }
                                }


                            }

                        }

                        db2.close();


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
            db2.close();
        }

    }


    public void agregarEvento(List<CalendarEvent> eventList ,int dia,String inicio,String fin,String clase,String profe){
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        resetCalender(startTime1);
        resetCalender(endTime1);
        int diaasoc=0;
        int ban=0;
        switch (dia){
            case 0:
                diaasoc=2;
                break;
            case 1:
                diaasoc=3;
                break;
            case 2:
                diaasoc=4;
                break;
            case 3:
                diaasoc=5;
                break;
            case 4:
                diaasoc=6;
                break;
            case 5:
                diaasoc=7;
                break;
            case 6:
                diaasoc=1;
               ban=1;
                break;
        }
        if(ban==1){
            startTime1.add(Calendar.WEEK_OF_MONTH, 1);

        }
        startTime1.set(Calendar.DAY_OF_WEEK, diaasoc);
        startTime1.set(Calendar.DAY_OF_WEEK, diaasoc);

        int color = R.color.morado;

        BaseCalendarEvent event1 = new BaseCalendarEvent(clase+"\n"+profe, "", inicio + " - " +fin,
                ContextCompat.getColor(getActivity(), color), startTime1, endTime1, true);

        eventList.add(event1);
    }

    public boolean status() {
        ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

            return true;
        }
        return false;
    }

    public static Calendar resetCalender(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
