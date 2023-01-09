package com.example.android.docbao.DetailsArticleActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.docbao.MainActivity.MainActivity;
import com.example.android.docbao.R;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DetailsArticleActivity extends AppCompatActivity {
    WebView webView;
    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;
    String duongLink;
    String image, date,title;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().requestFeature(Window.FEATURE_PROGRESS);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_details_article);
        webView=(WebView) findViewById(R.id.wv);
        shareDialog= new ShareDialog(DetailsArticleActivity.this);



        Intent intent=getIntent();
        duongLink=intent.getStringExtra("link");
        image=intent.getStringExtra("image");
        date=intent.getStringExtra("date");
        title=intent.getStringExtra("title");

        //Dùng webview để gọi trình duyệt của điện thoại để khởi chạy link, điển hình là chrome
        webView.getSettings().setAllowFileAccess( true );
       // webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 30);
        webView.getSettings().setAppCacheMaxSize(Long.MAX_VALUE);
        //Log.d("max","....."+Long.MAX_VALUE);
        webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setJavaScriptEnabled( true );
        webView.getSettings().setBuiltInZoomControls(true);
       // webView.getSettings().setTextSize(WebSettings.TextSize.SMALLEST);
        //webView.getSettings().setTextZoom(50);
        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default


        progressBar= (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setMax(100);


        webView.setWebViewClient(new HelpClient());

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                progressBar.setProgress(newProgress);
                if(newProgress==100){
                    progressBar.setVisibility(View.GONE);
                }

                super.onProgressChanged(view, newProgress);
            }


        });

        webView.loadUrl(duongLink);

        // API share  của Facebook, easy :V
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.android.docbao",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Show button



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_btn_share,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.mn_share){
//            if(shareDialog.canShow(ShareLinkContent.class)){
//                shareLinkContent= new ShareLinkContent.Builder()
//                        .setContentTitle("1")
//                        .setContentDescription("2")
//                        .setContentUrl(Uri.parse(duongLink))
//                        .build();
//            }
//            shareDialog.show(shareLinkContent);
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_SUBJECT, "Share bai bao");
            share.putExtra(Intent.EXTRA_TEXT, duongLink);
            startActivity(Intent.createChooser(share, "Share link!"));
        }else if(item.getItemId()==R.id.mn_save){
            MainActivity.databaseSavedArticle.QueryData("INSERT INTO contacts VALUES(null,'" +image+ "','"+title+"','"+duongLink+"','"+date+"')");
            Toast.makeText(DetailsArticleActivity.this,"Đã Lưu",Toast.LENGTH_SHORT).show();
        }else if( item.getItemId()== android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }




    class  HelpClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }

    }







}


