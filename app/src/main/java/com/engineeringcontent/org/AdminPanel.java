package com.engineeringcontent.org;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class AdminPanel extends AppCompatActivity {

    EditText signalForNotification, DevelopedByEdttxt;
    Button idBtnSaveData, idBtnRetriveData, idBtnSaveString;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_panel);

        signalForNotification = findViewById(R.id.signalForNotification);
        DevelopedByEdttxt = findViewById(R.id.DevelopedByEdttxt);
        idBtnSaveData = findViewById(R.id.idBtnSaveData);
        idBtnRetriveData = findViewById(R.id.idBtnRetriveData);
        idBtnSaveString = findViewById(R.id.idBtnSaveString);


        firebaseDatabase = FirebaseDatabase.getInstance();


        idBtnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateDatatoFirebase(signalForNotification.getText().toString(), "SignalNotification");
            }
        });

        idBtnSaveString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateDatatoFirebase(DevelopedByEdttxt.getText().toString(), "AboutDeveloper");
            }
        });


        idBtnRetriveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retriveDataFromFireBase("SignalNotification");
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
                        Toast.makeText(AdminPanel.this, "Value updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminPanel.this, "Failed to update value", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void retriveDataFromFireBase(String node){
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
                        Toast.makeText(AdminPanel.this, "SignalNotification value: " + signalValue, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the data doesn't exist
                    Toast.makeText(AdminPanel.this, "SignalNotification data doesn't exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the retrieval process
                Toast.makeText(AdminPanel.this, "Failed to retrieve SignalNotification value", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateDatatoFirebaseTest(String[] value, String node) {
        databaseReference = firebaseDatabase.getReference(node);
        DatabaseReference signalNotificationRef = databaseReference;

        signalNotificationRef.setValue(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminPanel.this, "Value updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminPanel.this, "Failed to update value", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
