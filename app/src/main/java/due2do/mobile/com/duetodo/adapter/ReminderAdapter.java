package due2do.mobile.com.duetodo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.activities.BasicTaskActivity;

import due2do.mobile.com.duetodo.activities.EventActivity;
import due2do.mobile.com.duetodo.activities.LocationActivity;
import due2do.mobile.com.duetodo.model.Task;

/**
 * Created by Ankit Varshney on 17/03/2018.
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private Context mCtx;
    private List<Task> reminderList;
    private static final String TAG = "due2do.mobile.com.duetodo";
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public ReminderAdapter(Context mCtx, List<Task> reminderList) {
        this.mCtx = mCtx;
        this.reminderList = reminderList;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.reminder_list, null);
        ReminderViewHolder holder = new ReminderViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        final Task reminder = reminderList.get(position);
        holder.textViewTitle.setText(reminder.getTask());
        holder.textViewDate.setText(reminder.getDay() + "/" + reminder.getMonth() + "/" + reminder.getYear());
        holder.textViewPriority.setText(reminder.getPriority());
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewDate, textViewPriority;
        ImageButton delete, editEvent;
        ImageView image;


        public ReminderViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.TaskTitle);
            textViewDate = itemView.findViewById(R.id.date);
            textViewPriority = itemView.findViewById(R.id.priority);
            image = itemView.findViewById(R.id.img);
            delete = itemView.findViewById(R.id.delevent);
            editEvent = itemView.findViewById(R.id.editEvent);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog diaBox = deleteDialog();
                    diaBox.show();

                }
            });

            //Edit task based on the functionality i.e. Camera, EventActivity or Location
            editEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Task task = reminderList.get(getAdapterPosition());
                    if (task.getId().contains("C")) {
                        Context context = view.getContext();
                        Intent singleTaskAvtivity = new Intent(context, BasicTaskActivity.class);
                        singleTaskAvtivity.putExtra("clickedData", reminderList.get(getAdapterPosition()));
                        context.startActivity(singleTaskAvtivity);
                    } else if (task.getId().contains("L")) {
                        Context context = view.getContext();
                        Intent singleTaskAvtivity = new Intent(context, LocationActivity.class);
                        singleTaskAvtivity.putExtra("clickedData", reminderList.get(getAdapterPosition()));
                        context.startActivity(singleTaskAvtivity);
                    } else {
                        Context context = view.getContext();
                        Intent singleTaskAvtivity = new Intent(context, EventActivity.class);
                        singleTaskAvtivity.putExtra("clickedData", reminderList.get(getAdapterPosition()));
                        context.startActivity(singleTaskAvtivity);
                    }

                }
            });


        }

        //Delete Dialog Box
        private AlertDialog deleteDialog() {
            AlertDialog deleteDialog = new AlertDialog.Builder(mCtx)
                    //set message, title, and icon
                    .setTitle("Delete")
                    .setMessage("Do you want to Delete")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                            final FirebaseUser mUser = firebaseAuth.getCurrentUser();
                            Task deleteTask = new Task();
                            //Delete task from list and database
                            deleteTask = reminderList.get(getAdapterPosition());
                            reminderList.remove(getAdapterPosition());

                            if (deleteTask.getId().contains("C")) {
                                DatabaseReference db1 = mDatabaseReference.child(mUser.getUid()).child("CameraTask").child(deleteTask.getKey());
                                db1.setValue(null);
                            } else if (deleteTask.getId().contains("L")) {
                                DatabaseReference db1 = mDatabaseReference.child(mUser.getUid()).child("LocationBased").child(deleteTask.getKey());
                                db1.setValue(null);
                            } else {
                                DatabaseReference db1 = mDatabaseReference.child(mUser.getUid()).child("EventTask").child(deleteTask.getKey());
                                db1.setValue(null);
                            }

                            Toast.makeText(mCtx, "Deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    })

                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .create();
            return deleteDialog;
        }
    }
}
