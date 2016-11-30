package jivamukti.techdev.com.jivamukti;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class terminos extends Fragment {
View v;
    ProgressBar progreso;

    public terminos(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         v=inflater.inflate(R.layout.terminos, container, false);
        ProgressBar progreso = (ProgressBar) v.findViewById(R.id.progressBar);
        progreso.bringToFront();
        WebView webView = (WebView) v.findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setPluginState(PluginState.ON);

        //---you need this to prevent the webview from
        // launching another browser when a url
        // redirection occurs---
        webView.setWebViewClient(new Callback(progreso));

        String pdfURL = "http://jivamuktiyogapuebla.com.mx/rewards-app-terms.pdf";
        if(status()) {
            webView.loadUrl(
                    "http://docs.google.com/gview?embedded=true&url=" + pdfURL);
        }
        // Inflate the layout for this fragment
        return v;
    }


    public boolean status() {
        ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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


    @Override
    public void onResume() {
        super.onResume();

    }
}
