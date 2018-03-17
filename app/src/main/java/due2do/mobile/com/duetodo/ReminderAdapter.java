package due2do.mobile.com.duetodo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ankit Varshney on 17/03/2018.
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private Context mCtx;
    private List<CameraReminder> reminderList;

    public ReminderAdapter(Context mCtx, List<CameraReminder> reminderList) {
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
        CameraReminder reminder = reminderList.get(position);
        holder.textViewTitle.setText(reminder.getTask());
        holder.textViewDate.setText(reminder.getDay()+"/"+reminder.getMonth()+"/"+reminder.getYear());
        holder.textViewPriority.setText("High");
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle, textViewDate, textViewPriority;

        public ReminderViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.TaskTitle);
            textViewDate = itemView.findViewById(R.id.date);
            textViewPriority = itemView.findViewById(R.id.priority);
        }
    }
}
