package due2do.mobile.com.duetodo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private CameraReminder cameraReminder = new CameraReminder();
    EditText editTask,indate,intime;
    Button btn,addTask;
    Task task = new Task();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        database = FirebaseDatabase.getInstance();
        btn = findViewById(R.id.btn_date);
        addTask = findViewById(R.id.add);
        indate = findViewById(R.id.in_date);
        intime = findViewById(R.id.in_time);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();


        // database
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();



        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, AddTask.this, 2018, 01, 01);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();

            }
        });

    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        task.setYear(String.valueOf(year));
        task.setMonths(String.valueOf(month));
        task.setDay(String.valueOf(dayOfMonth));

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, AddTask.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();

        indate.setText("Date:" + String.valueOf(year) + "/" + String .valueOf(month) + "/" + String.valueOf(dayOfMonth));
        cameraReminder.setYear(String.valueOf(year));
        cameraReminder.setMonth(String.valueOf(month));
        cameraReminder.setDay(String.valueOf(dayOfMonth));

    }



    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    task.setHours(String.valueOf(hourOfDay));
    task.setMinutes(String.valueOf(minute));

        intime.setText("Time:" + String.valueOf(hourOfDay) + ":" + String .valueOf(minute));
    cameraReminder.setHour(String.valueOf(hourOfDay));
    cameraReminder.setMinute(String.valueOf(minute));

    }

    public  void  addButtonClicked(View view) {
        editTask= findViewById(R.id.editTask);
        String name= editTask.getText().toString();
        String time = indate.getText().toString() + "  "  + intime.getText().toString();
        cameraReminder.setTask(name);
        /*DatabaseReference newTask = mDatabaseReference.child(mUser.getUid()).child("SimpleTask").child(name);
        newTask.setValue(time);
*/
        mDatabaseReference.child(mUser.getUid()).child("SimpleTask").push().setValue(cameraReminder);
        Intent displayTask = new Intent(AddTask.this,to_do.class);
        startActivity(displayTask);

    }
}
