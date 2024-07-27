package com.engineeringcontent.org;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class ClassContent extends AppCompatActivity {
    private int[] SubjectsImages;
    private String[] SubjectNames;
    private RecyclerView ClassContentRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_content);
    }
}