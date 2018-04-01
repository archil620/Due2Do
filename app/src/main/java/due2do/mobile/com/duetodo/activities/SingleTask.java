package due2do.mobile.com.duetodo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.model.CameraReminder;

public class SingleTask extends AppCompatActivity {

    private String task_key = null;
    private TextView singleTask;
    private TextView singleTime;
    private DatabaseReference mDatabase;
    private static final String TAG = "due2do.mobile.com.duetodo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        /*CameraReminder reminder = (CameraReminder) getIntent().getSerializableExtra("clickedData");
        Log.i(TAG,"Value from intent - serializable "+reminder);

        singleTask= findViewById(R.id.singleTask);
        singleTime= findViewById(R.id.singleTime);

        singleTask.setText(reminder.getTask());
        singleTime.setText(reminder.getDay()+"/"+reminder.getMonth()+"/"+reminder.getYear()+" "+ reminder.getHour()+":"+reminder.getMinute());
*/


        /*task_key = getIntent().getExtras().getString("TaskId");
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Tasks");

        singleTask= findViewById(R.id.singleTask);
        singleTime= findViewById(R.id.singleTime);

        mDatabase.child(task_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String task_title = (String) dataSnapshot.child("name").getValue();
                String task_time = (String) dataSnapshot.child("time").getValue();

                singleTask.setText(task_title);
                singleTime.setText(task_time);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/

    }
}
