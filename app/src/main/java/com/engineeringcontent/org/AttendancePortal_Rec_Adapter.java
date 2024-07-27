package com.engineeringcontent.org;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AttendancePortal_Rec_Adapter extends RecyclerView.Adapter<AttendancePortal_Rec_Adapter.ViewHolder>{

    private static String[] StudentIds;
    private static int Present_or_absent_toggle;


    public AttendancePortal_Rec_Adapter(){
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_cardview_portal, parent, false);
        return new AttendancePortal_Rec_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.StudentId.setText((position + 1) + ". " + StudentIds[position]);
        holder.StudentId.setSelected(true);
        if(Present_or_absent_toggle == 1){
            holder.Present_absent_CheckBox.setText("Present");
        }else{
            holder.Present_absent_CheckBox.setText("Absent");
        }

        if (AttendanceCRsPortal.checkOrNotAttendence.get(position) == 1) {
            holder.Present_absent_CheckBox.setChecked(true);
        } else {
            holder.Present_absent_CheckBox.setChecked(false);
        }

        holder.Present_absent_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    AttendanceCRsPortal.checkOrNotAttendence.set(position, 1);
                }else{
                    AttendanceCRsPortal.checkOrNotAttendence.set(position, 0);
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return StudentIds.length;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        // Remove the listener when the view is recycled
        holder.Present_absent_CheckBox.setOnCheckedChangeListener(null);
        super.onViewRecycled(holder);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView StudentId;
        CheckBox Present_absent_CheckBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            StudentId = itemView.findViewById(R.id.StudentId);
            Present_absent_CheckBox = itemView.findViewById(R.id.AbsentOrPresent);

        }
    }

    public static void setStudentIds(String[] studentIds) {
        StudentIds = studentIds;
    }

    public static void setPresent_or_absent_toggle(int present_or_absent_toggle) {
        Present_or_absent_toggle = present_or_absent_toggle;
    }


}
