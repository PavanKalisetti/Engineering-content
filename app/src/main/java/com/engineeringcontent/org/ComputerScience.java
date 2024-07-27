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

public class ComputerScience extends AppCompatActivity implements RecyclerViewInterFace{

    private AutoCompleteTextView SemesterDropDown;
    private ArrayAdapter<String> SemesterAdapterItems;
    private String[] SemesterName;
    private String[] SubjectNames;

    private int[] SubjectsImages;
    private RecyclerView CseRecView;

    private String[] SixChapters;
    private int Sem_pos = 0;

    private int bannerAdSignal = 1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        SemesterDropDown.setAdapter(SemesterAdapterItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Computer Science");
        setContentView(R.layout.computer_science);



        statusBarIconsColor();

        // action baq color was used only in branches classes to fix the problem of dropdown box color missing
        ActionBarColor();

        SemesterDropDown = findViewById(R.id.DropDownMenu);


        int semesterSelected = getIntegerFromSharedPreferences();

        firebaseDatabase = FirebaseDatabase.getInstance();


        // for real time database path
        Notes_db.setYear_Semester_db("E1S1");

        CseRecView = findViewById(R.id.CseRecView);

        SemesterName = getResources().getStringArray(R.array.Semester_Array);
        dropdownSwitchStatement(semesterSelected);



        SemesterDropDown = findViewById(R.id.DropDownMenu);
        SemesterAdapterItems = new ArrayAdapter<>(this, R.layout.dropdown_list_item, SemesterName);

        SemesterDropDown.setAdapter(SemesterAdapterItems);

        Subject_branch_RecAdapter adapter = new Subject_branch_RecAdapter(ComputerScience.this, this, 2);
        adapter.setBranch_SubjectNames(SubjectNames);
        adapter.setBranch_images(SubjectsImages);

        CseRecView.setAdapter(adapter);
        CseRecView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false ));



        SemesterDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // function to which semester chapters should be shown
                dropdownSwitchStatement(i);
                // saving the selected semester in dropdown
                saveIntegerToSharedPreferences(i);


                SemesterAdapterItems = new ArrayAdapter<>(ComputerScience.this, R.layout.dropdown_list_item, SemesterName);
                SemesterDropDown.setAdapter(SemesterAdapterItems);


                adapter.setBranch_SubjectNames(SubjectNames);
                adapter.setBranch_images(SubjectsImages);
                CseRecView.setAdapter(adapter);


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
                getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Computer Science</font>"));
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
        Chapters chapters = new Chapters();
        SixChapters = getResources().getStringArray(R.array.SixChapters);
//        String NotesTitleName;

        // get the name
//        if(position == 0){
//            NotesTitleName = SubjectNames[0];
//        }else if(position == 1){
//            NotesTitleName = SubjectNames[1];
//        }else if(position == 2){
//            NotesTitleName = SubjectNames[2];
//        }else if(position == 3){
//            NotesTitleName = SubjectNames[3];
//        }else if(position == 4){
//            NotesTitleName = SubjectNames[4];
//        }else if(position == 5){
//            NotesTitleName = SubjectNames[5];
//        }else if(position == 6){
//            NotesTitleName = SubjectNames[6];
//        }else if(position == 7){
//            NotesTitleName = SubjectNames[7];
//        }else{
//            NotesTitleName = "Lesson";
//        }

        Notes_db.TitleTxtName = SubjectNames[position];
        Notes_db.setSubject_db(SubjectNames[position]);

//        Notes.TitleTxtName = NotesTitleName;


        if(Sem_pos == 0){
            Notes_db.setYear_Semester_db("E1S1");
            if(position == 0){
                Chapters.setYearSem("00");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 1){
                Chapters.setYearSem("01");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 2){
                Chapters.setYearSem("02");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 3){
                // egcd
                Chapters.setYearSem("03");
                Chapters.ChapterNames = new String[]{"Assignment 1", "Assignment 2", "Assignment 3", "Assignment 4", "Assignment 5", "Assignment 6", "Teaching Plan"};
            }else if(position == 4){
                Chapters.setYearSem("04");
                Chapters.ChapterNames = new String[]{"Kirchoff Law", "Ohms Law", "PN Junction", "Zener Diode", "Rectifiers without Filters", "Rectifiers with Filters"};
            }else if(position == 5){
                Chapters.setYearSem("05");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 6){
                Chapters.setYearSem("06");
                Chapters.ChapterNames = SixChapters;
            }
        }else if(Sem_pos == 1){
            Notes_db.setYear_Semester_db("E1S2");
            if(position == 0){
                Chapters.setYearSem("10");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 1){
                Chapters.setYearSem("11");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 2){
                Chapters.setYearSem("12");
                Chapters.ChapterNames = new String[]{"Total Content", "Unit-1", "Unit-2", "Unit-3", "Unit-4", "Unit-5", "Unit-6"};
            }else if(position == 3){
                Chapters.setYearSem("13");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 4){
                Chapters.setYearSem("14");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 5){
                Chapters.setYearSem("15");
                Chapters.ChapterNames = SixChapters;
            }
        }else if(Sem_pos == 2){
            Notes_db.setYear_Semester_db("E2S1");
            if(position == 0){
                Chapters.setYearSem("20");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 1){
                Chapters.setYearSem("21");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 2){
                Chapters.setYearSem("22");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 3){
                Chapters.setYearSem("23");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 4){
                Chapters.setYearSem("24");
                Chapters.ChapterNames = SixChapters;
            }
        }else if(Sem_pos == 3){
            Notes_db.setYear_Semester_db("E2S2");
            if(position == 0){
                Chapters.setYearSem("30");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 1){
                Chapters.setYearSem("31");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 2){
                Chapters.setYearSem("32");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 3){
                Chapters.setYearSem("33");
                Chapters.ChapterNames = SixChapters;
            }else if(position == 4){
                Chapters.setYearSem("34");
                Chapters.ChapterNames = SixChapters;
            }
        }
        Intent intent00 = new Intent(ComputerScience.this, Chapters.class);
        startActivity(intent00);
    }


    void dropdownSwitchStatement(int i){
        switch (i){
            case 0:
                Sem_pos = 0;
                SubjectNames = getResources().getStringArray(R.array.Cse_E1_S1);
                SubjectsImages = new int[]{R.drawable.maths, R.drawable.beee, R.drawable.c, R.drawable.drawing, R.drawable.ic, R.drawable.drawing, R.drawable.english};
                SemesterDropDown.setText(getResources().getStringArray(R.array.Semester_Array)[0]);
                Notes_db.setYear_Semester_db("E1S1");
                break;
            case 1:
                Sem_pos = 1;
                SubjectNames = getResources().getStringArray(R.array.Cse_E1_S2);
                SubjectsImages = new int[]{R.drawable.maths, R.drawable.physics, R.drawable.java, R.drawable.ds, R.drawable.mefa, R.drawable.environment};
                SemesterDropDown.setText(getResources().getStringArray(R.array.Semester_Array)[1]);
                Notes_db.setYear_Semester_db("E1S2");
                break;
            case 2:
                Sem_pos = 2;
                SubjectNames = getResources().getStringArray(R.array.Cse_E2_S1);
                SubjectsImages = new int[]{R.drawable.maths, R.drawable.beee, R.drawable.ds, R.drawable.dbms, R.drawable.cse};
                SemesterDropDown.setText(getResources().getStringArray(R.array.Semester_Array)[2]);
                Notes_db.setYear_Semester_db("E2S1");
                break;
            case 3:
                Sem_pos = 3;
                SubjectNames = getResources().getStringArray(R.array.Cse_E2_S2);
                SubjectsImages = new int[]{R.drawable.operation_reserch, R.drawable.computer_men, R.drawable.ds, R.drawable.web, R.drawable.cse};
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