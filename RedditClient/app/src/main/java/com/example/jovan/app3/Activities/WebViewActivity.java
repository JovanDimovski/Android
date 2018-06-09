package com.example.jovan.app3.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jovan.app3.R;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        String url = getIntent().getExtras().getString("imageurl");

        final WebView wbView = (WebView) findViewById(R.id.web);
        wbView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        wbView.setWebChromeClient(new WebChromeClient());

        wbView.getSettings().setJavaScriptEnabled(true);
        wbView.loadUrl(url);
        wbView.clearView();
        wbView.measure(100, 100);
        wbView.getSettings().setUseWideViewPort(true);
        wbView.getSettings().setLoadWithOverviewMode(true);

    }
}
