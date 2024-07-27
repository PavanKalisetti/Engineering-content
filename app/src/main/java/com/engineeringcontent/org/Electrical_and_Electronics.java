package com.engineeringcontent.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Electrical_and_Electronics extends AppCompatActivity implements RecyclerViewInterFace{

    private AutoCompleteTextView SemesterDropDown;
    private ArrayAdapter<String> SemesterAdapterItems;
    private String[] SemesterName;
    private String[] SubjectNames;

    private int[] SubjectsImages;
    private String[] SixChapters;

    private int Sem_pos = 0;

    private int bannerAdSignal = 1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private RecyclerView EEERecView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Electrical And Electronics");
        setContentView(R.layout.activity_electrical_and_electronics);

        statusBarIconsColor();
        ActionBarColor();

        firebaseDatabase = FirebaseDatabase.getInstance();

        SemesterDropDown = findViewById(R.id.DropDownMenuEEE);
        EEERecView = findViewById(R.id.EEERecyclerView);

        int semesterSelected = getIntegerFromSharedPreferences();
        Notes_db.setYear_Semester_db("E1S1");

        SemesterName = getResources().getStringArray(R.array.Semester_Array);
        dropdownSwitchStatement(semesterSelected);

        SemesterDropDown.setAdapter(SemesterAdapterItems);
        SemesterAdapterItems = new ArrayAdapter<>(this, R.layout.dropdown_list_item, SemesterName);


        Subject_branch_RecAdapter adapter = new Subject_branch_RecAdapter( this,this, 2);
        adapter.setBranch_SubjectNames(SubjectNames);
        adapter.setBranch_images(SubjectsImages);
        
        EEERecView.setAdapter(adapter);
        EEERecView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));

        SemesterDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dropdownSwitchStatement(i);

                saveIntegerToSharedPreferences(i);

                SemesterAdapterItems = new ArrayAdapter<>(Electrical_and_Electronics.this, R.layout.dropdown_list_item, SemesterName);
                SemesterDropDown.setAdapter(SemesterAdapterItems);

                adapter.setBranch_SubjectNames(SubjectNames);
                adapter.setBranch_images(SubjectsImages);
                EEERecView.setAdapter(adapter);


            }
        });

        // ad loading
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // getting error in the below function why?
        retriveDataFromFireBase("bannerAdSignal");

    }

    @Override
    protected void onResume() {
        super.onResume();
        SemesterDropDown.setAdapter(SemesterAdapterItems);
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
                getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Electrical and Electronics</font>"));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_black);
            }

        }
    }


    public void ActionBarColor(){
        ActionBar actionBar = getSupportActionBar();

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Dark mode
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000"))); // Replace "#000000" with your desired color code
        } else {
            // Light mode
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ebf3fc"))); // Replace "#FFFFFF" with your desired color code
        }

    }


    @Override
    public void OnItemListener(int position) {
        SixChapters = getResources().getStringArray(R.array.SixChapters);

        Notes_db.TitleTxtName = SubjectNames[position];
        Notes_db.setSubject_db(SubjectNames[position]);
        Chapters.ChapterNames = SixChapters;
        if(Sem_pos == 0){
            Notes_db.setYear_Semester_db("E1S1");

        }else if(Sem_pos == 1){
            Notes_db.setYear_Semester_db("E1S2");
            if(position == 2){
                Chapters.ChapterNames = new String[]{"Unit 1", "Unit 2", "Unit 3", "Unit 4 & 5", "Unit 6"};
            }
        }else if(Sem_pos == 2){
            Notes_db.setYear_Semester_db("E2S1");
        }else if(Sem_pos == 3){
            Notes_db.setYear_Semester_db("E2S2");
        }
        Intent intent = new Intent(Electrical_and_Electronics.this, Chapters.class);
        startActivity(intent);
    }

    void dropdownSwitchStatement(int i){
        switch (i){
            case 0:
                Sem_pos = 0;
                SubjectNames = getResources().getStringArray(R.array.EEE_E1_S1);
                SubjectsImages = new int[]{R.drawable.maths, R.drawable.physics, R.drawable.drawing, R.drawable.lightning_bolt, R.drawable.c};
                SemesterDropDown.setText(getResources().getStringArray(R.array.Semester_Array)[0]);
                Notes_db.setYear_Semester_db("E1S1");
                break;
            case 1:
                Sem_pos = 1;
                SubjectNames = getResources().getStringArray(R.array.EEE_E1_S2);
                SubjectsImages = new int[]{R.drawable.maths, R.drawable.ds, R.drawable.lightning_bolt, R.drawable.networking,R.drawable.computer_men, R.drawable.english};
                SemesterDropDown.setText(getResources().getStringArray(R.array.Semester_Array)[1]);
                Notes_db.setYear_Semester_db("E1S2");
                break;
            case 2:
                Sem_pos = 2;
                SubjectNames = getResources().getStringArray(R.array.EEE_E2_S1);
                SubjectsImages = new int[]{R.drawable.maths, R.drawable.beee, R.drawable.c, R.drawable.drawing, R.drawable.ic,R.drawable.maths, R.drawable.beee, R.drawable.c, R.drawable.drawing, R.drawable.ic};
                SemesterDropDown.setText(getResources().getStringArray(R.array.Semester_Array)[2]);
                Notes_db.setYear_Semester_db("E2S1");
                break;
            case 3:
                Sem_pos = 3;
                SubjectNames = getResources().getStringArray(R.array.EEE_E2_S2);
                SubjectsImages = new int[]{R.drawable.maths, R.drawable.beee, R.drawable.c, R.drawable.drawing, R.drawable.ic};
                SemesterDropDown.setText(getResources().getStringArray(R.array.Semester_Array)[3]);
                Notes_db.setYear_Semester_db("E2S2");
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

    private void saveIntegerToSharedPreferences(int value) {
        SharedPreferences sharedPreferences = getSharedPreferences("saveIntegerToSharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SemesterSelected", value);
        editor.apply();
    }

    private int getIntegerFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("saveIntegerToSharedPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("SemesterSelected",0);
    }
}