package jivamukti.techdev.com.jivamukti;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class TerminosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos);
        ProgressBar progreso = (ProgressBar) findViewById(R.id.progressBar);
        progreso.bringToFront();
        Bundle extras = getIntent().getExtras();
        String url="http://jivamuktiyogapuebla.com.mx";
        if (extras != null) {
            String datas = extras.getString("url");
            if (!datas.equals("")) {
                url=datas;
            }
        }

        WebView wv=(WebView) findViewById(R.id.webView);
        wv.getSettings().setJavaScriptEnabled(true);

        wv.setWebViewClient(new Callback(progreso));
        if(status()) {
            wv.loadUrl(url);
        }


    }

    public boolean status() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {

            return true;
        }
        return false;
    }

    private class Callback extends WebViewClient {
        private ProgressBar progressBar;

        public Callback(ProgressBar progressBar) {
            this.progressBar=progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

}
