package com.engineeringcontent.org;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;

public class DownloadPdfAdapter extends RecyclerView.Adapter<DownloadPdfAdapter.myViewHolder> {
    private final DownloadRecyclerViewInterface recyclerViewInterFace;
    ArrayList<Model> list;
    Context context;
    private boolean[] isDownloaded;

    public DownloadPdfAdapter(DownloadRecyclerViewInterface recyclerViewInterFace, ArrayList<Model> list, Context context) {
        this.recyclerViewInterFace = recyclerViewInterFace;
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
        holder._name.setSelected(true);

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
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        MaterialCardView parent;
        ImageView _Image;
        TextView _name;
        ImageView downloadButton;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            _Image = itemView.findViewById(R.id._image);
            // the below is the text view to apply the marquee
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



    public void setIsDownloaded(boolean[] isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

}
