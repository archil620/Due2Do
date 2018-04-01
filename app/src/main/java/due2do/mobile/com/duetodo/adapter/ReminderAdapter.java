package due2do.mobile.com.duetodo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.activities.AddTask;
import due2do.mobile.com.duetodo.activities.MainActivity1;
import due2do.mobile.com.duetodo.activities.SingleTask;
import due2do.mobile.com.duetodo.model.CameraReminder;
import due2do.mobile.com.duetodo.model.Task;

/**
 * Created by Ankit Varshney on 17/03/2018.
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private Context mCtx;
    private List<Task> reminderList;
    private static final String TAG = "due2do.mobile.com.duetodo";
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
        holder.textViewPriority.setText("High");
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = reminder.getId();
            }
        });
    }



    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewTitle, textViewDate, textViewPriority;
        ImageButton delete;


        public ReminderViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            textViewTitle = itemView.findViewById(R.id.TaskTitle);
            textViewDate = itemView.findViewById(R.id.date);
            textViewPriority = itemView.findViewById(R.id.priority);
            delete = itemView.findViewById(R.id.delevent);


        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick " + reminderList.get(getAdapterPosition()));
            Context context = view.getContext();
            Intent singleTaskAvtivity = new Intent(context,AddTask.class);
            singleTaskAvtivity.putExtra("clickedData",reminderList.get(getAdapterPosition()));
            context.startActivity(singleTaskAvtivity);
        }
    }
}
