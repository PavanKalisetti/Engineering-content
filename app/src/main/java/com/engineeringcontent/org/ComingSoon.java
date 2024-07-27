package com.engineeringcontent.org;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ComingSoon extends AppCompatActivity {

    private Button sendContentToAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_coming_soon);



        statusBarIconsColor();

        sendContentToAdmin = findViewById(R.id.sendContentToAdmin);

        sendContentToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGmail();
            }
        });

    }



    public void statusBarIconsColor(){
        // Get the current theme mode (light or dark)
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Get the Window object for the activity
        Window window = getWindow();

        // Check the current theme mode and change the title text color accordingly
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE); // For light mode, set text color to black
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_white);
            }


        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // For dark mode, set text color to white
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>ComingSoon</font>"));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_black);
            }

        }
    }

    private void openGmail() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        String[] recipients={"engineeringcontent09@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT,"");
        intent.putExtra(Intent.EXTRA_TEXT,"");
        intent.putExtra(Intent.EXTRA_CC,"engineeringcontent09@gmail.com");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }



}