package com.engineeringcontent.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class AttendanceCRsPortal extends AppCompatActivity implements RecyclerViewInterFace{

    private AutoCompleteTextView SubjectDropDown;
    private ArrayAdapter<String> SubjectAdapterItems;
    private String[] SubjectNames = null;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Switch absent_or_present_Switch;

    String[] IdstringArray = new String[0];
    AttendancePortal_Rec_Adapter adapter;
    RecyclerView StudentAttendance;
    private int SubjectSelected = -1, AbsentPresentSignal = 0;
    static ArrayList<Integer> checkOrNotAttendence = new ArrayList<>();
    private Button AttendenceSubmitBtn;
    private String AttendenceOfSubject, TotalClassesConductedString;

    protected static String CrCampusAndYear ,CrBranch, CrClass;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_crs_portal);

        firebaseDatabase = FirebaseDatabase.getInstance();

        absent_or_present_Switch = findViewById(R.id.absent_or_present_Switch);

        statusBarIconsColor();
        ActionBarColor();

        SubjectDropDown = findViewById(R.id.SubjectDropDownMenu);

//      SubjectNames = getResources().getStringArray(R.array.Semester_Array);



//        SubjectAdapterItems = new ArrayAdapter<>(this, R.layout.dropdown_list_item, SubjectNames);
//
//        SubjectDropDown.setAdapter(SubjectAdapterItems);


        absent_or_present_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    // toggle is enabled
                    AttendancePortal_Rec_Adapter.setPresent_or_absent_toggle(1);
                    AbsentPresentSignal = 1;
                    StudentAttendance.setAdapter(adapter);

                    // present selection
                }else{
                    // toggle offed
                    AttendancePortal_Rec_Adapter.setPresent_or_absent_toggle(0);
                    AbsentPresentSignal = 0;
                    StudentAttendance.setAdapter(adapter);
                    // absent selection
                }
            }
        });




        retriveDataFromFireBase(CrCampusAndYear  + CrBranch + "/" + CrClass +"/TotalConducted", 3);
        StudentAttendance = findViewById(R.id.StudentsIdNo_RecView);
        retriveDataFromFireBase(CrCampusAndYear + CrBranch + "/" + CrClass +"/names", 0);
        retriveDataFromFireBase(CrCampusAndYear  + CrBranch + "/Subjects", 1);


        AttendancePortal_Rec_Adapter.setStudentIds(IdstringArray);
        for (int i = 0; i <100; i++) {
            checkOrNotAttendence.add(0);
        }
        AttendancePortal_Rec_Adapter.setPresent_or_absent_toggle(0);
        adapter = new AttendancePortal_Rec_Adapter();
        StudentAttendance.setAdapter(adapter);
        StudentAttendance.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        SubjectDropDown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SubjectSelected = i;
//                Toast.makeText(AttendanceCRsPortal.this, CrCampusAndYear + CrBranch + "/" + CrClass+ "/SubjectsAttendence/"+i , Toast.LENGTH_SHORT).show();
                retriveDataFromFireBase(CrCampusAndYear + CrBranch + "/" + CrClass+ "/SubjectsAttendence/"+i, 2);
                AttendancePortal_Rec_Adapter.setStudentIds(IdstringArray);

                for (int j = 0; j < checkOrNotAttendence.size(); j++) {
                    checkOrNotAttendence.set(j, 0);
                }
                adapter.notifyDataSetChanged();
            }
        });

        AttendenceSubmitBtn = findViewById(R.id.AttendenceSubmitBtn);
        AttendenceSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateDialog();
//                SubmitAttendence();
            }
        });



    }



    private void retriveDataFromFireBase(String node, int signal){
        // zero (0) -- ids
        // one (1) -- subjects

        databaseReference = firebaseDatabase.getReference(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String signalValue = dataSnapshot.getValue(String.class);
                    if (signalValue != null) {
                        // Handle the retrieved signal value here
                        // For example, display it in a TextView
                        // textView.setText(signalValue);
                        if(signal == 0){
                            IdstringArray = signalValue.split(",");
                        }
                        else if(signal == 1){
                            SubjectNames = signalValue.split(",");

                        }
                        else if(signal == 2){
                            // retriving the data of attendence of a subject
                            AttendenceOfSubject = signalValue;

                        }else if(signal == 3){
                            TotalClassesConductedString = signalValue;
                        }
//                        Toast.makeText(AttendanceCRsPortal.this, "SignalNotification value: " + Arrays.toString(SubjectNames), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    // Handle the case where the data doesn't exist
                    Toast.makeText(AttendanceCRsPortal.this, "SignalNotification data doesn't exist " + signal, Toast.LENGTH_SHORT).show();
                }
                if(signal == 0 && SubjectSelected != -1){
                    System.out.println("ids of student: "+IdstringArray);
                    AttendancePortal_Rec_Adapter.setStudentIds(IdstringArray);
                    adapter.notifyDataSetChanged();
                } else if(signal == 1){
                    SubjectAdapterItems = new ArrayAdapter<>(AttendanceCRsPortal.this, R.layout.dropdown_list_item, SubjectNames);

                    SubjectDropDown.setAdapter(SubjectAdapterItems);
                    SubjectAdapterItems.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process
                Toast.makeText(AttendanceCRsPortal.this, "Failed to retrieve SignalNotification value", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateDatatoFirebase(String value, String node) {
        databaseReference = firebaseDatabase.getReference(node);
        DatabaseReference signalNotificationRef = databaseReference;

        signalNotificationRef.setValue(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AttendanceCRsPortal.this, "Value updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AttendanceCRsPortal.this, "Failed to update value", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SubmitAttendence(){
        String path = CrCampusAndYear + CrBranch + "/"+ CrClass +"/";
        // AttendenceOfSubject
        String[] subjectAttendenceStringArray = AttendenceOfSubject.split(",");

        int[] SubjectAttendanceIntArray = new int[subjectAttendenceStringArray.length];
        for (int i = 0; i < subjectAttendenceStringArray.length; i++) {
            SubjectAttendanceIntArray[i] = Integer.parseInt(subjectAttendenceStringArray[i]);
        }


        if(AbsentPresentSignal == 0){
            // it is in absent mode
            for (int i = 0; i < subjectAttendenceStringArray.length; i++) {
                if(checkOrNotAttendence.get(i) == 1){
                    checkOrNotAttendence.set(i, 0);
                }else{
                    checkOrNotAttendence.set(i, 1);
                }
            }

        }


        for (int i = 0; i < subjectAttendenceStringArray.length; i++) {
            SubjectAttendanceIntArray[i] += checkOrNotAttendence.get(i);
        }


        String realAttendenceString = convertIntArrayToStringArray(SubjectAttendanceIntArray);

        UpdateDatatoFirebase(realAttendenceString, path+"SubjectsAttendence/"+SubjectSelected);

        // update the total classes conducted
//        TotalClassesConductedString
        String[] totalAttendenceStringArray = TotalClassesConductedString.split(",");
        int[] totalAttendenceIntArray = new int[totalAttendenceStringArray.length];
        for (int i = 0; i < totalAttendenceStringArray.length; i++) {
            totalAttendenceIntArray[i] = Integer.parseInt(totalAttendenceStringArray[i]);
        }
        totalAttendenceIntArray[SubjectSelected]++;
        String TotalConductedFinal = convertIntArrayToStringArray(totalAttendenceIntArray);
        UpdateDatatoFirebase(TotalConductedFinal, path + "TotalConducted");




    }

    public static String convertIntArrayToStringArray(int[] input) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < input.length; i++) {
            stringBuilder.append(input[i]);

            if (i < input.length - 1) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.attendence_preview, null);
        builder.setView(dialogView);

        Button SubmitFinal = dialogView.findViewById(R.id.submitBtnFinal);
        TextView AbsentOfsubjectTextView = dialogView.findViewById(R.id.AbsentOfsubjectTextView);
        RecyclerView AbsentsRecView = dialogView.findViewById(R.id.AbsentsRecView);

        if(AbsentPresentSignal == 0){
            AbsentOfsubjectTextView.setText(SubjectNames[SubjectSelected] + ": Absenties");
        }else{
            AbsentOfsubjectTextView.setText(SubjectNames[SubjectSelected] + ": Presenties");
        }





        Subject_branch_RecAdapter adapter_Absenties = new Subject_branch_RecAdapter(this, this, 4);
        ArrayList<String> absent_Students_ArrayList = new ArrayList<>();
        for (int i = 0; i < IdstringArray.length; i++) {
            if(checkOrNotAttendence.get(i) == 1){
                absent_Students_ArrayList.add(IdstringArray[i]);
            }
        }
        adapter_Absenties.setAbsenties_names(absent_Students_ArrayList);
        AbsentsRecView.setAdapter(adapter_Absenties);
        AbsentsRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        SubmitFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitAttendence();
//                Toast.makeText(AttendanceCRsPortal.this, "trying to submit", Toast.LENGTH_SHORT).show();
            }
        });


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

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
                getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Attendence</font>"));
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

    }
}