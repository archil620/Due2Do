package due2do.mobile.com.duetodo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.activities.AddTask;

import due2do.mobile.com.duetodo.activities.Event;
import due2do.mobile.com.duetodo.activities.SingleTask;
import due2do.mobile.com.duetodo.activities.create2;
import due2do.mobile.com.duetodo.model.CameraReminder;
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
    //private final View.OnClickListener mOnClickListener = new TaskOnClickListener();

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
        holder.textViewDate.setText(reminder.getDay()+"/"+reminder.getMonth()+"/"+reminder.getYear());
        holder.textViewPriority.setText(reminder.getPriority());
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewDate, textViewPriority;
        ImageButton delete,editEvent;
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

                    mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    final FirebaseUser mUser = firebaseAuth.getCurrentUser();
                    Task deleteTask = new Task();
                    //Delete task from list and database
                    deleteTask = reminderList.get(getAdapterPosition());
                    reminderList.remove(getAdapterPosition());

                    if(deleteTask.getId().contains("C")) {
                        DatabaseReference db1 = mDatabaseReference.child(mUser.getUid()).child("CameraTask").child(deleteTask.getKey());
                        db1.setValue(null);
                    }else if(deleteTask.getId().contains("L")) {
                        DatabaseReference db1 = mDatabaseReference.child(mUser.getUid()).child("LocationBased").child(deleteTask.getKey());
                        db1.setValue(null);
                    }else{
                        DatabaseReference db1 = mDatabaseReference.child(mUser.getUid()).child("EventTask").child(deleteTask.getKey());
                        db1.setValue(null);
                    }

                    Toast.makeText(mCtx,"Deleted", Toast.LENGTH_SHORT).show();
                }
            });

            //Edit task based on the functionality i.e. Camera, Event or Location
            editEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Task task = reminderList.get(getAdapterPosition());
                    if(task.getId().contains("C")){
                        Context context = view.getContext();
                        Intent singleTaskAvtivity = new Intent(context,AddTask.class);
                        singleTaskAvtivity.putExtra("clickedData",reminderList.get(getAdapterPosition()));
                        context.startActivity(singleTaskAvtivity);
                    }else if(task.getId().contains("L")){
                        Context context = view.getContext();
                        Intent singleTaskAvtivity = new Intent(context,create2.class);
                        singleTaskAvtivity.putExtra("clickedData",reminderList.get(getAdapterPosition()));
                        context.startActivity(singleTaskAvtivity);
                    }else{
                        Context context = view.getContext();
                        Intent singleTaskAvtivity = new Intent(context,Event.class);
                        singleTaskAvtivity.putExtra("clickedData",reminderList.get(getAdapterPosition()));
                        context.startActivity(singleTaskAvtivity);
                    }

                }
            });



        }
    }
}
