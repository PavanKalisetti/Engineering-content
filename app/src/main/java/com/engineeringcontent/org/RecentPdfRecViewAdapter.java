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

public class RecentPdfRecViewAdapter extends RecyclerView.Adapter<RecentPdfRecViewAdapter.ViewHolder>{

    private final pdfOpenInterface recyclerViewInterFace;
    private ArrayList<String> Branch_SubjectNames;
    private int[] Branch_images;
    private Context context;

    public RecentPdfRecViewAdapter( Context context, pdfOpenInterface recyclerViewInterFace) {
        this.recyclerViewInterFace = recyclerViewInterFace;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapters_cardview, parent, false);
        return new ViewHolder(view,recyclerViewInterFace);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder._name.setText(Branch_SubjectNames.get(position));

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), Branch_images[position]);
        holder._Image.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return MainActivity.SizeOfRecentPdfsOpen();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView _Image;
        TextView _name;
        MaterialCardView parent;
        public ViewHolder(@NonNull View itemView, pdfOpenInterface recyclerViewInterFace) {
            super(itemView);



            _Image = itemView.findViewById(R.id._image);
            _name = itemView.findViewById(R.id._name);
            parent = itemView.findViewById(R.id.parent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterFace != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterFace.openPdf(pos);
                        }
                    }
                }
            });
        }
    }

    public void setBranch_SubjectNames(ArrayList<String> branch_SubjectNames) {
        Branch_SubjectNames = branch_SubjectNames;
        notifyDataSetChanged();
    }

    public void setBranch_images(int[] branch_images) {
        Branch_images = branch_images;
    }
}
