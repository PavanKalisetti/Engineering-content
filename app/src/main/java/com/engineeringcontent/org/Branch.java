package com.engineeringcontent.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import com.example.engineeringcontent.R;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Branch extends AppCompatActivity implements RecyclerViewInterFace{
    private RecyclerView BranchRecView;
    private String[] BranchNames ;
    private int bannerAdSignal = 1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
//    private int[] BranchImages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Branches");
        setContentView(R.layout.branch);


        statusBarIconsColor();

        firebaseDatabase = FirebaseDatabase.getInstance();



        BranchRecView = findViewById(R.id.BranchRecView);
        BranchNames = getResources().getStringArray(R.array.branches_array);
        int[] BranchImages = {R.drawable.cse, R.drawable.ece, R.drawable.civil, R.drawable.eee, R.drawable.chem, R.drawable.mech, R.drawable.mme};

        Subject_branch_RecAdapter adapter = new Subject_branch_RecAdapter(Branch.this, this, 1);
        adapter.setBranch_SubjectNames(BranchNames);
        adapter.setBranch_images(BranchImages);
        BranchRecView.setAdapter(adapter);
//        BranchRecView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        BranchRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // ad loading
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // getting error in the below function why?
        retriveDataFromFireBase("bannerAdSignal");


    }

    @Override
    public void OnItemListener(int position) {
        Intent intent;
        switch (position){

            case 0:
                Notes_db.setBranch_db("cse");
                Intent intent0 = new Intent(Branch.this, ComputerScience.class);
                startActivity(intent0);
                break;
            case 1:
                Notes_db.setBranch_db("ece");
                Intent intent1 = new Intent(Branch.this, Electronics_and_Communications.class);
                startActivity(intent1);
                break;
            case 2:
                Notes_db.setBranch_db("eee");
                intent = new Intent(Branch.this, Electrical_and_Electronics.class);
                startActivity(intent);
                break;
            case 3:
                Notes_db.setBranch_db("ce");
                intent = new Intent(Branch.this, Civil.class);
                startActivity(intent);
                break;

            case 4:
                Notes_db.setBranch_db("chemical");
                intent = new Intent(Branch.this, Chemical.class);
                startActivity(intent);
                break;
            case 5:
                Notes_db.setBranch_db("mech");
                intent = new Intent(Branch.this, Mechanical.class);
                startActivity(intent);
                break;
            case 6:
                Notes_db.setBranch_db("mme");
                intent = new Intent(Branch.this, Metallurgy.class);
                startActivity(intent);
                break;
            default:
                intent = new Intent(Branch.this, ComingSoon.class);
                startActivity(intent);
                break;

        }
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
                getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Branch</font>"));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_black);
            }

        }
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