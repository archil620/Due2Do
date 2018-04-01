package due2do.mobile.com.duetodo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import due2do.mobile.com.duetodo.model.EventReminder;
import due2do.mobile.com.duetodo.R;

public class Event extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView time, date;
    ImageButton btn, createtask;
    private final int REQUEST_CODE = 99;
    EditText eventName, location;
    Button btPick, btnSend;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    EventReminder eventReminder = new EventReminder();
    ArrayList<String> contactList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        time = findViewById(R.id.storetime);
        date = findViewById(R.id.storedate);
        eventName = findViewById(R.id.eventName);
        btn = findViewById(R.id.cal);
        btPick = findViewById(R.id.btnpick_contact);
        btnSend = findViewById(R.id.btnSend);
        location = (EditText) findViewById(R.id.meeting);
        createtask = findViewById(R.id.createtask);

        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        final int today = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this, due2do.mobile.com.duetodo.activities.Event.this, currentYear, currentMonth, today);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, due2do.mobile.com.duetodo.activities.Event.this, hour, minute, DateFormat.is24HourFormat(this));

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        eventReminder.setYear(String.valueOf(datePicker.getYear()));
        eventReminder.setMonth(String.valueOf(datePicker.getMonth()));
        eventReminder.setDay(String.valueOf(datePicker.getDayOfMonth()));

        date.setText(dayOfMonth + "/" + month + "/" + year);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        eventReminder.setHour(String.valueOf(hourOfDay));
        eventReminder.setMinute(String.valueOf(minute));

        time.setText(hourOfDay + ":" + minute);
    }
}
