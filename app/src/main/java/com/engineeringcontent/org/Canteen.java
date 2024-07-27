package com.engineeringcontent.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
// current time

import java.util.Objects;

public class Canteen extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private int i2 = 0, k2 = 0, k3 = 0;
    private ImageView Close_Open_img_I2, Close_Open_img_K2, Close_Open_img_K3;
    private int lastUpdateTimestampI2 = 0,lastUpdateTimestampK2 = 0, lastUpdateTimestampK3 = 0;
    TextView updateTxt_I2;
    TextView updateTxt_K2;
    TextView updateTxt_K3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Canteen");
        setContentView(R.layout.activity_canteen);
        statusBarIconsColor();
        ActionBarColor();
        firebaseDatabase = FirebaseDatabase.getInstance();




        updateTxt_I2 = findViewById(R.id.UpdateTxt_I2);
        updateTxt_K2 = findViewById(R.id.UpdateTxt_K2);
        updateTxt_K3 = findViewById(R.id.UpdateTxt_K3);

        updateTxt_I2.setSelected(true);
        updateTxt_K2.setSelected(true);
        updateTxt_K3.setSelected(true);

        // i2 block retirve
        retrieveDataFromFirebase("I2Canteen", 0);
        retrieveDataFromFirebase("K2Canteen", 1);
        retrieveDataFromFirebase("K3Canteen", 2);

        // faced the erro in the below three lines of the code
        retrieveDataFromFirebaseTime("I2CanteenLastTimeUpdate", 0);
        retrieveDataFromFirebaseTime("K2CanteenLastTimeUpdate", 1);
        retrieveDataFromFirebaseTime("K3CanteenLastTimeUpdate", 2);




        Close_Open_img_I2 = findViewById(R.id.Close_Open_img_I2);
        Close_Open_img_K2 = findViewById(R.id.Close_Open_img_K2);
        Close_Open_img_K3 = findViewById(R.id.Close_Open_img_K3);




        updateUI();






    }

    private void updateLastUpdateTimeI2() {
        int currentTime = (int) System.currentTimeMillis();
        int timeDiff = currentTime - lastUpdateTimestampI2;

        int secondsPassed = timeDiff / 1000;
        int minutesPassed = secondsPassed / 60;
        int hoursPassed = minutesPassed / 60;
        int daysPassed = hoursPassed / 24;

        String updateTimeText;

        if (daysPassed > 0) {
            updateTimeText = "Updated " + daysPassed + " days ago";
        } else if (hoursPassed > 0) {
            updateTimeText = "Updated " + hoursPassed + " hours ago";
        } else if (minutesPassed > 0) {
            updateTimeText = "Updated " + minutesPassed + " minutes ago";
        } else {
            updateTimeText = "Updated " + secondsPassed + " seconds ago";
        }

        updateTxt_I2.setText(updateTimeText);
    }

    private void updateLastUpdateTimeK2() {
        int currentTime = (int) System.currentTimeMillis();
        int timeDiff = currentTime - lastUpdateTimestampK2;

        int secondsPassed = timeDiff / 1000;
        int minutesPassed = secondsPassed / 60;
        int hoursPassed = minutesPassed / 60;
        int daysPassed = hoursPassed / 24;

        String updateTimeText;

        if (daysPassed > 0) {
            updateTimeText = "Updated " + daysPassed + " days ago";
        } else if (hoursPassed > 0) {
            updateTimeText = "Updated " + hoursPassed + " hours ago";
        } else if (minutesPassed > 0) {
            updateTimeText = "Updated " + minutesPassed + " minutes ago";
        } else {
            updateTimeText = "Updated " + secondsPassed + " seconds ago";
        }

        updateTxt_K2.setText(updateTimeText);
    }

    private void updateLastUpdateTimeK3() {
        int currentTime = (int) System.currentTimeMillis();
        int timeDiff = currentTime - lastUpdateTimestampK3;

        int secondsPassed = timeDiff / 1000;
        int minutesPassed = secondsPassed / 60;
        int hoursPassed = minutesPassed / 60;
        int daysPassed = hoursPassed / 24;

        String updateTimeText;

        if (daysPassed > 0) {
            updateTimeText = "Updated " + daysPassed + " days ago";
        } else if (hoursPassed > 0) {
            updateTimeText = "Updated " + hoursPassed + " hours ago";
        } else if (minutesPassed > 0) {
            updateTimeText = "Updated " + minutesPassed + " minutes ago";
        } else {
            updateTimeText = "Updated " + secondsPassed + " seconds ago";
        }

        updateTxt_K3.setText(updateTimeText);
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
                getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Canteen</font>"));
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


    public void UpdateTheTime(View view) {
        int viewId = view.getId();

        if (viewId == R.id.UpdateBtn_I2) {
            showUpdateDialog("I2Canteen");
        } else if (viewId == R.id.UpdateBtn_K2) {
            showUpdateDialog("K2Canteen");
        }
        else if(viewId == R.id.UpdateBtn_K3){
            showUpdateDialog("K3Canteen");
        }
    }

    private void showUpdateDialog(String CanteenBlock) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.canteen_dialog_box, null);
        builder.setView(dialogView);
        Button YesBtn = dialogView.findViewById(R.id.Yes_dialog);
        Button NoBtn = dialogView.findViewById(R.id.No_dialog);
        int currentTimeStamp = 0;
        currentTimeStamp = (int) System.currentTimeMillis();

        int finalCurrentTimeStamp = currentTimeStamp;
        YesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateDataToFirebase(1,CanteenBlock);
                UpdateDataToFirebaseTime(finalCurrentTimeStamp, CanteenBlock+"LastTimeUpdate");


            }
        });

        NoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateDataToFirebase(0,CanteenBlock);
                UpdateDataToFirebaseTime(finalCurrentTimeStamp, CanteenBlock+"LastTimeUpdate");

            }
        });


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void UpdateDataToFirebase(int value, String node) {
        databaseReference = firebaseDatabase.getReference(node);
        DatabaseReference signalNotificationRef = databaseReference;

        signalNotificationRef.setValue(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Canteen.this, "updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Canteen.this, "Failed to update value", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void UpdateDataToFirebaseTime(int value, String node) {
        databaseReference = firebaseDatabase.getReference(node);
        DatabaseReference signalNotificationRef = databaseReference;

        signalNotificationRef.setValue(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Canteen.this, "updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Canteen.this, "Failed to update value", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void retrieveDataFromFirebase(String node, int block) {
        databaseReference = firebaseDatabase.getReference(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer signalValue = dataSnapshot.getValue(Integer.class);
                    if (signalValue != null) {
                        // Handle the retrieved signal value here
                        // For example, display it in a TextView
                        // textView.setText(String.valueOf(signalValue));
                        if(block == 0){  // I2 block
                            i2 = signalValue.intValue();
                            Log.d("DataDebug", "onDataChange: at i2"+signalValue);
                        }
                        else if(block == 1){ // k2 block
                            k2 = signalValue.intValue();
                            Log.d("DataDebug", "onDataChange: at k2"+signalValue);
                        }
                        else if(block == 2){ // k3 block
                            k3 = signalValue.intValue();
                            Log.d("DataDebug", "onDataChange: at k3"+signalValue);
                        }

                    }
                } else {
                    // Handle the case where the data doesn't exist

                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process
                Toast.makeText(Canteen.this, "Failed to retrive data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveDataFromFirebaseTime(String node, int block) {
        databaseReference = firebaseDatabase.getReference(node);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer signalValue = dataSnapshot.getValue(Integer.class);
                    if (signalValue != null) {
                        // Handle the retrieved signal value here
                        // For example, display it in a TextView
                        // textView.setText(String.valueOf(signalValue));
                        if(block == 0){  // I2 block
                            lastUpdateTimestampI2 = signalValue;
                            System.out.println("i2 block"+signalValue);

                        }
                        else if(block == 1){ // k2 block
                            lastUpdateTimestampK2 = signalValue;
                            System.out.println("k2 block"+signalValue);


                        }
                        else if(block == 2){ // k3 block
                            lastUpdateTimestampK3 = signalValue;
                            System.out.println("K3 block"+signalValue);


                        }

                    }
                } else {
                    // Handle the case where the data doesn't exist

                }
                updateUI();

                if(block == 0){
                    updateLastUpdateTimeI2();
                }
                else if(block == 1){
                    updateLastUpdateTimeK2();
                }
                else if(block == 2){
                    updateLastUpdateTimeK3();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process
                Toast.makeText(Canteen.this, "Failed to retrive data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(){
        if(i2 == 1){
            Close_Open_img_I2.setImageResource(R.drawable.canteen_open);
        }else{
            Close_Open_img_I2.setImageResource(R.drawable.canteen_closed);
        }
        if(k2 == 1){
            Close_Open_img_K2.setImageResource(R.drawable.canteen_open);
        }else{
            Close_Open_img_K2.setImageResource(R.drawable.canteen_closed);
        }
        if(k3 == 1){
            Close_Open_img_K3.setImageResource(R.drawable.canteen_open);
        }else{
            Close_Open_img_K3 .setImageResource(R.drawable.canteen_closed);
        }
    }


}