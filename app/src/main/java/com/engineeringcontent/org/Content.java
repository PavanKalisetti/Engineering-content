package com.engineeringcontent.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

public class Content extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);

        Toast.makeText(this, "this is content act", Toast.LENGTH_SHORT).show();
        RecyclerView contentRecView = findViewById(R.id.contentRecView);

    }
}