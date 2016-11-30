package jivamukti.techdev.com.jivamukti;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private TutorialAdapter pagerAdapter;
    private PageIndicator mIndicator;
    Button login,registro;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);

    Intent i;
    SQLiteDatabase db2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        login = (Button) findViewById(R.id.inicio);
        registro = (Button) findViewById(R.id.registro);
        login.setOnClickListener(this);
        registro.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.graphs);
        mViewPager.setAdapter(new TutorialAdapter(getSupportFragmentManager()));

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);


        CreaBase();
        if (!verifica()) {
            i = new Intent(this,MainActivity.class);
            startActivity(i);
        }


    }

    private boolean CreaBase(){
        db2 = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
        db2.execSQL("CREATE  TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT,user VARCHAR NOT NULL," +
                "password VARCHAR NOT NULL)");

        db2.execSQL("CREATE  TABLE IF NOT EXISTS infouser (id INTEGER PRIMARY KEY AUTOINCREMENT,user_id VARCHAR NOT NULL," +
                "user_first_name VARCHAR NOT NULL, user_last_name VARCHAR," +
                "user_email VARCHAR NOT NULL,user_creation_timestamp VARCHAR," +
                "user_last_login_timestamp VARCHAR,user_phone VARCHAR," +
                "user_birthday VARCHAR,user_points VARCHAR," +
                "user_in_newsletter VARCHAR,user_last_check_in VARCHAR)");

        db2.execSQL("CREATE  TABLE IF NOT EXISTS puntosbeacons (id INTEGER PRIMARY KEY AUTOINCREMENT,status INTEGER NOT NULL )");

        db2.execSQL("CREATE  TABLE IF NOT EXISTS tiempo (id INTEGER PRIMARY KEY AUTOINCREMENT,tiempobeacon VARCHAR NOT NULL )");

        db2.execSQL("CREATE TABLE IF NOT EXISTS tiempoPaseLista (id INTEGER PRIMARY KEY AUTOINCREMENT,entrada VARCHAR NOT NULL,salida VARCHAR NOT NULL,status INTEGER NOT NULL)");

                db2.close();
        return true;
    }

    public boolean verifica(){

        db2 = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
        Cursor c = db2.rawQuery("SELECT * FROM user", null);
        c.moveToFirst();
        db2.close();
        if(c.getCount()==0)
            return true;
        return false;
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
      switch (v.getId()) {
            case R.id.inicio:
                i = new Intent(this,AccesoActivity.class);
                i.putExtra("id", 1);
                startActivity(i);
                overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                break;
          case R.id.registro:
              i = new Intent(this,AccesoActivity.class);
              i.putExtra("id", 2);
              startActivity(i);
              overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
              break;
        }
    }

}
