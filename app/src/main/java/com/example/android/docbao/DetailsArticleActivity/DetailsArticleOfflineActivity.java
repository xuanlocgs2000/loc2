package com.example.android.docbao.DetailsArticleActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.android.docbao.R;

public class DetailsArticleOfflineActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_read_article);
        webView=(WebView) findViewById(R.id.wv_docOffline);

        webView.getSettings().setJavaScriptEnabled( true );
        webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ONLY ); // load online by default
        Intent intent=getIntent();
        String mlink=intent.getStringExtra("link");
        webView.loadUrl(mlink);
        webView.setWebViewClient(new WebViewClient());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}
