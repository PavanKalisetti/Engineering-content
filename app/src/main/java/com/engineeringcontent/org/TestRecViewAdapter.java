package com.engineeringcontent.org;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;


public class TestRecViewAdapter extends RecyclerView.Adapter<TestRecViewAdapter.myViewHolder> {

    ArrayList<Model> list;
    Context context;

    public TestRecViewAdapter(ArrayList<Model> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_cardview, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Model model = list.get(position);
        holder._name.setText(model.getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        MaterialCardView parent;
        ImageView _Image;
        TextView _name;
        ImageView downloadButton;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            _Image = itemView.findViewById(R.id._image);
            _name = itemView.findViewById(R.id._name);
            downloadButton = itemView.findViewById(R.id.downloadImage);

        }
    }

}
