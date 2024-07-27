package com.engineeringcontent.org;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;


public class DownloadRecViewAdapter extends RecyclerView.Adapter<DownloadRecViewAdapter.ViewHolder>{

    private final DownloadRecyclerViewInterface recyclerViewInterFace;
    private String[] Branch_SubjectNames;
    private int[] Branch_images;
    private boolean[] isDownloaded;

    private Context context;
    private int GridOrLinear;

    public DownloadRecViewAdapter(DownloadRecyclerViewInterface recyclerViewInterFace, Context context, int gridOrLinear) {
        this.recyclerViewInterFace = recyclerViewInterFace;
        this.context = context;
        GridOrLinear = gridOrLinear;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_cardview, parent, false);
        return new ViewHolder(view, recyclerViewInterFace);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder._name.setText(Branch_SubjectNames[position]);
        holder._name.setSelected(true);
        // loading images
        if(GridOrLinear != 3){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), Branch_images[position]);
            holder._Image.setImageBitmap(bitmap);
        }

        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        if(!isDownloaded[position]){
            int downloadButtonResource = isDarkMode ? R.drawable.download_white : R.drawable.download_black;
            holder.downloadButton.setImageResource(downloadButtonResource);
        }else{
            int downloadButtonResource = isDarkMode ? R.drawable.delete_white : R.drawable.delete_black;
            holder.downloadButton.setImageResource(downloadButtonResource);

        }

        // set click listener for card view
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerViewInterFace != null){
                    int pos = holder.getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        recyclerViewInterFace.OnItemListener(pos);
                    }
                }
            }
        });

        // set click listener for image
        holder._Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerViewInterFace != null){
                    int pos = holder.getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(!isDownloaded[pos]){
                            recyclerViewInterFace.OnDownloadListener(pos);
                        }else{
                            // we gonna change the layout to pdfviewer
                            recyclerViewInterFace.OnOpenPdfListener(pos);
                        }

                    }
                }
            }
        });

    }



    @Override
    public int getItemCount() {
        return Branch_SubjectNames.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView parent;
        ImageView _Image;
        TextView _name;
        ImageView downloadButton;
        public ViewHolder(@NonNull View itemView, DownloadRecyclerViewInterface recyclerViewInterFace) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            _Image = itemView.findViewById(R.id._image);
            _name = itemView.findViewById(R.id._name);
            downloadButton = itemView.findViewById(R.id.downloadImage);

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
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterFace != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterFace.OnDownloadListener(pos);
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

    public void setIsDownloaded(boolean[] isDownloaded) {
        this.isDownloaded = isDownloaded;
    }
}
