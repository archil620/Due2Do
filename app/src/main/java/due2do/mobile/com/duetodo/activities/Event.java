package due2do.mobile.com.duetodo.activities;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import due2do.mobile.com.duetodo.model.EventReminder;
import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.model.Task;

public class Event extends ListActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView time, date;
    ImageButton btn, createtask;
    private final int REQUEST_CODE = 99;
    EditText eventName, location;
    ImageButton btPick;
    Task passedIntent2 = new Task();
    String priority;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Task eventReminder = new Task();
    ArrayList<String> contactList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    Query createQuery;
    Spinner spinner;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        time = findViewById(R.id.storetime);
        date = findViewById(R.id.storedate);
        eventName = findViewById(R.id.taskname);
        btn = findViewById(R.id.cal);
        btPick = findViewById(R.id.btnpick_contact);
        location = (EditText) findViewById(R.id.meeting);
        createtask = findViewById(R.id.createtask);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                contactList);
        setListAdapter(adapter);

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

        //Database
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        spinner = (Spinner) findViewById(R.id.priority);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinneradapter = ArrayAdapter.createFromResource(this,
                R.array.priority, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinneradapter);

        passedIntent2 = (Task) getIntent().getSerializableExtra("clickedData");

        if (passedIntent2 != null) {

            eventName.setText(passedIntent2.getTask());
            date.setText(passedIntent2.getDay() + "/" + passedIntent2.getMonth() + "/" + passedIntent2.getYear());
            time.setText(passedIntent2.getHour() + ":" + passedIntent2.getMinute());
            location.setText(passedIntent2.getLocation());
            contactList = passedIntent2.getContactList();

            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    contactList);
            setListAdapter(adapter);
        }


        createtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location.getText().toString().length() != 0 && eventName.getText().toString().length() != 0) {
                    //String phoneNo =txtPhoneNo.getText().toString();

                    if (passedIntent2 != null) {
                        priority = spinner.getSelectedItem().toString();
                        passedIntent2.setPriority(priority);
                        passedIntent2.setTask(String.valueOf(eventName.getText()));
                        passedIntent2.setLocation(String.valueOf(location.getText()));
                        passedIntent2.setTask(String.valueOf(eventName.getText()));
                        DatabaseReference db1 = mDatabaseReference.child(mUser.getUid()).child("EventTask").child(passedIntent2.getKey());
                        db1.setValue(passedIntent2);
                        Toast.makeText(due2do.mobile.com.duetodo.activities.Event.this, "Task Updated", Toast.LENGTH_SHORT).show();

                        Intent displayTask = new Intent(due2do.mobile.com.duetodo.activities.Event.this, to_do.class);
                        startActivity(displayTask);


                    } else {
                        priority = spinner.getSelectedItem().toString();
                        eventReminder.setPriority(priority);
                        eventReminder.setTask(String.valueOf(eventName.getText()));
                        eventReminder.setLocation(String.valueOf(location.getText()));
                        eventReminder.setTaskStatus("Active");
                        for (int i = 0; i < contactList.size(); i++) {
                            String phoneNo = contactList.get(i);
                            String contactDetails[] = phoneNo.split(",");
                            String message = "You have been added to an event named " + eventName.getText().toString() + " on " +
                                    eventReminder.getDay() + "/" + eventReminder.getMonth() + "/" + eventReminder.getYear() + " at " + eventReminder.getHour() + ":" + eventReminder.getMinute() + ".The venue is " +
                                    location.getText().toString() + ".";
                            if (adapter != null) {
                                sendMessage(contactDetails[1], message);

                            } else {
                                Toast.makeText(getApplicationContext(), "Phone or message field is empty", Toast.LENGTH_SHORT).show();
                            }
                        }


                        createQuery = mDatabaseReference.child(mUser.getUid()).child("EventTask").orderByKey().limitToLast(1);
                        createQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            Task reminder = new Task();

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    reminder = ds.getValue(Task.class);
                                }
                                if (reminder.getId() != null && !(reminder.getId().isEmpty())) {
                                    int val = Integer.valueOf(reminder.getId().substring(1));
                                    val = val + 1;
                                    eventReminder.setId("E" + val);

                                } else {
                                    eventReminder.setId("E1");

                                }
                                mDatabaseReference.child(mUser.getUid()).child("EventTask").push().setValue(eventReminder);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Intent displayTask = new Intent(due2do.mobile.com.duetodo.activities.Event.this, to_do.class);
                        startActivity(displayTask);

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
                                                String numName = contactName + "," + num;
                                                contactList.add(numName);
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
                    if (passedIntent2 != null) {
                        passedIntent2.setContactList(contactList);
                    } else {
                        eventReminder.setContactList(contactList);
                    }

                    break;
                }
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        if (passedIntent2 != null) {
            passedIntent2.setYear(String.valueOf(datePicker.getYear()));
            passedIntent2.setMonth(String.valueOf(datePicker.getMonth() + 1));
            passedIntent2.setDay(String.valueOf(datePicker.getDayOfMonth()));
        } else {
            eventReminder.setYear(String.valueOf(datePicker.getYear()));
            eventReminder.setMonth(String.valueOf(datePicker.getMonth() + 1));
            eventReminder.setDay(String.valueOf(datePicker.getDayOfMonth()));

        }


        date.setText(dayOfMonth + "/" + String.valueOf(month + 1) + "/" + year);

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (passedIntent2 != null) {
            passedIntent2.setHour(String.valueOf(hourOfDay));
            passedIntent2.setMinute(String.valueOf(minute));
        } else {
            eventReminder.setHour(String.valueOf(hourOfDay));
            eventReminder.setMinute(String.valueOf(minute));
        }

        time.setText(hourOfDay + ":" + minute);
    }
}
