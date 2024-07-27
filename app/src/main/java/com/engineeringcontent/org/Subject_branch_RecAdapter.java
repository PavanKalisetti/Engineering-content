package com.engineeringcontent.org;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;


public class Subject_branch_RecAdapter extends RecyclerView.Adapter<Subject_branch_RecAdapter.ViewHolder>{

    private final RecyclerViewInterFace recyclerViewInterFace;
    private String[] Branch_SubjectNames;
    private int[] Branch_images;

    private Context context;
    private int GridOrLinear;
    private ArrayList<String> Absenties_names;

    public Subject_branch_RecAdapter(Context context, RecyclerViewInterFace recyclerViewInterFace, int GridOrLinear) {
        this.context = context;
        this.recyclerViewInterFace = recyclerViewInterFace;
        this.GridOrLinear = GridOrLinear;
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(GridOrLinear == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.branch_cardview, parent, false);
        }else if(GridOrLinear == 2){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_cardview, parent, false);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapters_cardview, parent, false);
        }
        return new ViewHolder(view, recyclerViewInterFace);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(GridOrLinear != 4){
            holder._name.setText(Branch_SubjectNames[position]);
            // loading images

            if(GridOrLinear != 3){
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), Branch_images[position]);
                holder._Image.setImageBitmap(bitmap);
            }
        }

        if(GridOrLinear == 4){
            holder._Image.setVisibility(View.GONE);
            holder._name.setText(Absenties_names.get(position));
        }




    }


    @Override
    public int getItemCount() {

        if(Branch_SubjectNames != null){
            return Branch_SubjectNames.length;
        }
        if(Absenties_names != null){
            return Absenties_names.size();
        }
        return 0;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView parent;
        ImageView _Image;
        TextView _name;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterFace recyclerViewInterFace) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            _Image = itemView.findViewById(R.id._image);
            _name = itemView.findViewById(R.id._name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterFace != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterFace.OnItemListener(pos);
                        }
                    }
                }
            });
        }
    }

    public void setBranch_SubjectNames(String[] branch_SubjectNames) {
        Branch_SubjectNames = branch_SubjectNames;
    }

    public void setBranch_images(int[] branch_images) {
        Branch_images = branch_images;
    }

    public void setAbsenties_names(ArrayList<String> absenties_names) {
        Absenties_names = absenties_names;
    }
}
