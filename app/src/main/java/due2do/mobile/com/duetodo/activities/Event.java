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

        btPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        createtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location.getText().toString().length() != 0 && eventName.getText().toString().length() != 0) {
                    //String phoneNo =txtPhoneNo.getText().toString();

                    for (int i = 0; i < contactList.size(); i++) {
                        String phoneNo = contactList.get(i);
                        String message = "You have been added to an event named " + eventName.getText().toString() + " on " +
                                eventReminder.getDay() + "/" + eventReminder.getMonth() + "/" + eventReminder.getYear() + "at " + eventReminder.getHour() + ":" + eventReminder.getMinute() + ".The vemue is " +
                                location.getText().toString();
                        if (adapter != null) {
                            mDatabaseReference.child(mUser.getUid()).child("EventTask").push().setValue(eventReminder);
                            sendMessage(phoneNo, message);

                        } else {
                            Toast.makeText(getApplicationContext(), "Phone or message field is empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Enter all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendMessage(String phoneNo, String message) {
        //https://stackoverflow.com/questions/40861846/android-get-phone-number-from-contact-list
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "MSG Failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        int flag = 0;
        //http://www.worldbestlearningcenter.com/tips/Android-get-phone-number-from-contacts-list.htm
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                {
                                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(num));

                                    String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

                                    String contactName = "";
                                    Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

                                    if (cursor != null) {
                                        if (cursor.moveToFirst()) {
                                            contactName = cursor.getString(0);
                                            Log.i("name", contactName);
                                            if (flag == 0) {
                                                contactList.add(contactName);
                                                adapter.notifyDataSetChanged();
                                                flag = 1;
                                            }
                                        }
                                        cursor.close();
                                    }
                                }
                            }
                        }
                    }

                    break;
                }
        }
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
