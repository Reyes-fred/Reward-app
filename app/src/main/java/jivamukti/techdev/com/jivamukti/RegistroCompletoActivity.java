package jivamukti.techdev.com.jivamukti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class RegistroCompletoActivity extends AppCompatActivity  implements View.OnClickListener {
Button next;
    ImageView img;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_completo);

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        img = (ImageView) findViewById(R.id.home);
        img.bringToFront();

    }



    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        switch (v.getId()) {
            case R.id.next:
            Intent i = new Intent(this,AccesoActivity.class);
                i.putExtra("id", 1);
                startActivity(i);
                finish();
                break;

        }
    }

}
