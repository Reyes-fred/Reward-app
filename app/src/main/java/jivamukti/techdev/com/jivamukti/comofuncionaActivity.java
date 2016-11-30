package jivamukti.techdev.com.jivamukti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class comofuncionaActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;

    private PageIndicator mIndicator;
    Button omitir;
    Intent i;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comofunciona);

        omitir = (Button) findViewById(R.id.omitir);
        omitir.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.graphs);
        mViewPager.setAdapter(new comofuncionaAdapter(getSupportFragmentManager()));

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
      switch (v.getId()) {
            case R.id.omitir:
                super.onBackPressed();
                break;
        }
    }

}
