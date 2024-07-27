package com.engineeringcontent.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
// ad loading
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Chapters extends AppCompatActivity implements RecyclerViewInterFace {

    static String[] ChapterNames;
    private String[] PdfLinks;
    private static String YearSem;
    private int bannerAdSignal = 1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       Objects.requireNonNull(getSupportActionBar()).setTitle("Subject");

        setContentView(R.layout.chapters);
        statusBarIconsColor();

        firebaseDatabase = FirebaseDatabase.getInstance();


        RecyclerView chapters = findViewById(R.id.chapters);
        Subject_branch_RecAdapter adapter = new Subject_branch_RecAdapter(this, this, 3);
        adapter.setBranch_SubjectNames(ChapterNames);
        chapters.setAdapter(adapter);
        chapters.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        // ad loading
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // getting error in the below function why?
        retriveDataFromFireBase("bannerAdSignal");


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
                getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Subject</font>"));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_black);
            }

        }
    }


    @Override
    public void OnItemListener(int position) {

        switch (position){
            case 0:
                Notes_db.setUnit_db("unit_1");
                break;
            case 1:
                Notes_db.setUnit_db("unit_2");
                break;
            case 2:
                Notes_db.setUnit_db("unit_3");
                break;
            case 3:
                Notes_db.setUnit_db("unit_4");
                break;
            case 4:
                Notes_db.setUnit_db("unit_5");
                break;
            case 5:
                Notes_db.setUnit_db("unit_6");
                break;
            case 6:
                Notes_db.setUnit_db("unit_7");
                break;
            case 7:
                Notes_db.setUnit_db("unit_8");
                break;


        }
        Intent intent = new Intent(this, Notes_db.class);
        startActivity(intent);
    }

    public static void setYearSem(String yearSem) {
        YearSem = yearSem;
    }

    private void retriveDataFromFireBase(String node){
        databaseReference = firebaseDatabase.getReference(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer signalValue = dataSnapshot.getValue(Integer.class);
                    if (signalValue != null) {
                        bannerAdSignal = signalValue;

                    }
                } else {

                }
                AdView mAdview = findViewById(R.id.adView);

                if(bannerAdSignal == 0){
                    // hiding the ad
                    mAdview.setVisibility(View.GONE);
                }else{
                    // showing the banner
                    mAdview.setVisibility(View.VISIBLE);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process

            }
        });
    }
}