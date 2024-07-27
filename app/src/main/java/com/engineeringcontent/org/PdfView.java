package com.engineeringcontent.org;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;


public class PdfView extends AppCompatActivity {


    public static File pdfFile;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(flags);




        setContentView(R.layout.activity_pdf_view);


        // Find the WebView by its unique ID
        WebView webView = findViewById(R.id.web);



        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);

        // loading https://www.geeksforgeeks.org url in the WebView.

        String filePath = pdfFile.getAbsolutePath();
        webView.loadUrl(filePath);

        // this will enable the javascript.
        webView.getSettings().setJavaScriptEnabled(true);


    }

}