package com.engineeringcontent.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class Attendence extends AppCompatActivity {

    private String[] columnNames = {"Subject Names", "Attended", "Conducted"};
//    private String[] subjects = {"Subject-1", "Subject-2", "Subject-3", "Subject-4", "Subject-5", "Subject-6", "Subject-7", "Subject-8", "Subject-9"};
    private String[] subjects;
    private int[] attendedClasses;
    private int[] conductedClasses;
    double totalAttendancePercentage = 0;
    TextView TotalAttendance;
    protected static String AttendencePathUntillYear, AttendeceUserBranch, AttendenceUserClass;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    int[][] AttendenceOfStudent = null;

    TableRow headerRow;
    TableLayout tableLayout;
    boolean isDarkModeEnabled;
    public static String userGmail;
    private String[] ClassStudentIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);

        statusBarIconsColor();
        ActionBarColor();

        firebaseDatabase = FirebaseDatabase.getInstance();


        if(AttendeceUserBranch != null){
                    retriveDataFromFireBaseForSubjects(AttendencePathUntillYear+AttendeceUserBranch+"/Subjects", 0);
//            Toast.makeText(this, AttendencePathUntillYear+AttendeceUserBranch+"/Subjects", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Login with college mail or this was not started for your class", Toast.LENGTH_SHORT).show();
        }


        tableLayout = findViewById(R.id.tableLayout); // Replace with your TableLayout ID

        // Check if dark mode is enabled
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDarkModeEnabled = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        // Create Header Row
        headerRow = new TableRow(this);






    }






        private void retriveDataFromFireBaseForAll(String path, int studentsLength){
            DatabaseReference parentRef = firebaseDatabase.getReference(path);
            parentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
//                        int rowCount = (int) dataSnapshot.getChildrenCount(); // Number of rows
//                        AttendenceOfStudent = new int[rowCount][studentsLength];
                        int row = 0;
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            // Iterate through the child nodes and get their keys and values
                            String childKey = childSnapshot.getKey();
                            String childValue = childSnapshot.getValue(String.class);

                            String[] subjectAttendenceStringArray = childValue.split(",");
                            for (int i = 0; i < studentsLength; i++) {
                                AttendenceOfStudent[row][i] = Integer.parseInt(subjectAttendenceStringArray[i]);
                            }
                            row++;

                        }
                    } else {
                        // Handle the case where the parent node does not exist
                    }
                    System.out.println(Arrays.deepToString(AttendenceOfStudent));

                    CreateTable();
//                    TestingFunction();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that occur during the retrieval process
                }
            });
    }
    private void retriveDataFromFireBaseForSubjects(String node, int signalForSomething){
        databaseReference = firebaseDatabase.getReference(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String signalValue = dataSnapshot.getValue(String.class);
                    if (signalValue != null) {
                        if(signalForSomething == 0){
                            subjects = signalValue.split(",");
                        }else if(signalForSomething == 1){
                            String[] conductedStringArray = signalValue.split(",");

                            conductedClasses = new int[conductedStringArray.length];

                            for (int i = 0; i < conductedStringArray.length; i++) {
                                conductedClasses[i] = Integer.parseInt(conductedStringArray[i]);
                            }

                        }else if(signalForSomething == 2){
                            ClassStudentIds = signalValue.split(",");
                        }

                    }
                } else {

                }

                if(signalForSomething == 0){
                    retriveDataFromFireBaseForSubjects(AttendencePathUntillYear+AttendeceUserBranch+"/"+AttendenceUserClass+"/"+"TotalConducted", 1);
                }
                else if(signalForSomething == 1){
                    retriveDataFromFireBaseForSubjects(AttendencePathUntillYear+AttendeceUserBranch+"/"+AttendenceUserClass+"/ids", 2);
                }else if (signalForSomething == 2){
                    System.out.println(Arrays.toString(ClassStudentIds));
                    AttendenceOfStudent = new int[conductedClasses.length][ClassStudentIds.length];
                    retriveDataFromFireBaseForAll(AttendencePathUntillYear+AttendeceUserBranch+"/"+AttendenceUserClass+"/SubjectsAttendence", ClassStudentIds.length);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process

            }
        });
    }

    private TextView createTextView(String text, boolean isHeader, boolean isDarkModeEnabled) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        if (isHeader) {
            textView.setTextColor(isDarkModeEnabled ? getResources().getColor(android.R.color.white) : getResources().getColor(android.R.color.black));
            textView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            textView.setTextSize(16);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setPadding(16, 16, 16, 16);
        } else {
            textView.setTextColor(isDarkModeEnabled ? getResources().getColor(android.R.color.white) : getResources().getColor(android.R.color.black));
            textView.setTextSize(14);
            textView.setPadding(16, 12, 16, 12);
        }
        return textView;
    }

    private void CalculatePercantage() {
        int totalAttended = 0;
        int totalConducted = 0;

        for (int i = 0; i < attendedClasses.length; i++) {
            totalAttended += attendedClasses[i];
            totalConducted += conductedClasses[i];
        }

        // Calculate Total Attendance Percentage
        totalAttendancePercentage = ((double) totalAttended / totalConducted) * 100;
        TotalAttendance = findViewById(R.id.totalAttendanceTextView);

        TotalAttendance.setText("Total Attendance : " + totalAttendancePercentage + "%");
    }

    private void CreateTable(){
        tableLayout.removeAllViews();
        if(subjects != null && conductedClasses != null){
            tableLayout.addView(headerRow);
            int studentIndex = -1;
            for (int i = 0; i < ClassStudentIds.length; i++) {
                if(ClassStudentIds[i].substring(1,7).equals(userGmail.substring(1,7))){
                    studentIndex = i;
                    break;
                }
            }
            if(studentIndex != -1){

                attendedClasses = new int[conductedClasses.length];
                for (int i = 0; i < attendedClasses.length; i++) {
                    attendedClasses[i] = AttendenceOfStudent[i][studentIndex];
                }

                
                for (String columnName : columnNames) {
                    TextView headerTextView = createTextView(columnName, true, isDarkModeEnabled);
                    headerRow.addView(headerTextView);
                }
//         Create Data Rows
                for (int i = 0; i < subjects.length; i++) {
                    TableRow dataRow = new TableRow(this);
                    TextView subjectTextView = createTextView(subjects[i], false, isDarkModeEnabled);
                    TextView attendedTextView = createTextView(String.valueOf(attendedClasses[i]), false, isDarkModeEnabled);
                    TextView conductedTextView = createTextView(String.valueOf(conductedClasses[i]), false, isDarkModeEnabled);

                    dataRow.addView(subjectTextView);
                    dataRow.addView(attendedTextView);
                    dataRow.addView(conductedTextView);

                    tableLayout.addView(dataRow);
                }

                CalculatePercantage();
            }
        }
        else {
            Toast.makeText(this, "Login with college mail", Toast.LENGTH_SHORT).show();
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
                getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Attendance</font>"));
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



}