package due2do.mobile.com.duetodo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Calendar;

public class Event extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    ImageButton btn;
    private final int REQUEST_CODE=99;
    EditText txtPhoneNo, eventName;
    Button btPick,btnSend;
    EventReminder eventReminder = new EventReminder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventName=findViewById(R.id.eventName);
        btn = findViewById(R.id.cal);
        txtPhoneNo=(EditText)findViewById(R.id.txtPhoneNo);
        btPick=(Button)findViewById(R.id.btpick_contact);
        btnSend=(Button)findViewById(R.id.btnSend);

        btPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo =txtPhoneNo.getText().toString();
                String message = "You have been added to an event named "+eventName.getText().toString()+" on "+
                        eventReminder.getDay()+"/"+eventReminder.getMonth()+"/"+eventReminder.getYear();
                if(phoneNo.length()>0) {
                    sendMessage(phoneNo, message);
                }else{
                    Toast.makeText(getApplicationContext(), "Phone or message field is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, Event.this, 2018, 01, 01);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();

            }
        });
    }

    public void sendMessage( String phoneNo, String message) {
        //https://stackoverflow.com/questions/40861846/android-get-phone-number-from-contact-list
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "MSG Failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
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
                                txtPhoneNo.setText(num);
                                {
                                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(num));

                                    String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

                                    String contactName = "";
                                    Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

                                    if (cursor != null) {
                                        if (cursor.moveToFirst()) {
                                            contactName = cursor.getString(0);
                                            Log.i("name", contactName);
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
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        eventReminder.setYear(String.valueOf(datePicker.getYear()));
        eventReminder.setMonth(String.valueOf(datePicker.getMonth()));
        eventReminder.setDay(String.valueOf(datePicker.getDayOfMonth()));

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, Event.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        eventReminder.setHour(String.valueOf(i));
        eventReminder.setMinute(String.valueOf(i1));
    }
}
