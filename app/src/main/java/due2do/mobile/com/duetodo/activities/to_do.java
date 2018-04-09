package due2do.mobile.com.duetodo.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.adapter.ReminderAdapter;
import due2do.mobile.com.duetodo.model.Task;
import due2do.mobile.com.duetodo.services.NotificationService;
import due2do.mobile.com.duetodo.services.TrackLocationService;
import due2do.mobile.com.duetodo.utils.AutoLoginReference;

public class to_do extends AppCompatActivity {

    private TextView username;
    TextView td;
    ImageButton add_task, camera_task, location, add_people, done_task ;
    Animation fabopen, fabclose, fabrotate, fabantirotate;
    TextView taskName,taskDate;
    boolean isopen = false;
    Task reminder = new Task();
    RecyclerView recyclerView;
    ReminderAdapter adapter;
    ImageButton next, previous;

    List<Task> reminderList = new ArrayList<>();
    List<Task> displayList = new ArrayList<>();

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser;
    SimpleDateFormat d;
    SimpleDateFormat m;
    java.util.Calendar c;
    SimpleDateFormat month_index;

    private DatePickerDialog tdDatePicker;
    EditText tod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        username = findViewById(R.id.username);
        add_task = findViewById(R.id.add_task);
        camera_task = findViewById(R.id.camera_btn);
        location = findViewById(R.id.location_btn);
        add_people = findViewById(R.id.add_people_btn);
        done_task = findViewById(R.id.done_btn);
        fabopen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);
        fabclose= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_close);
        fabrotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        fabantirotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        taskName = findViewById(R.id.TaskTitle);
        taskDate = findViewById(R.id.date);
        tod = findViewById(R.id.today);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        // database
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        //to display today's day
        c = java.util.Calendar.getInstance();

        d = new SimpleDateFormat("dd");
        m = new SimpleDateFormat("MMMM");
        month_index = new SimpleDateFormat("M");
        final String month = month_index.format(c.getTime());
        String cmonth = m.format(c.getTime());
        String cday = d.format(c.getTime());
        //tod.setText(String.valueOf(cmonth + ", " + cday));

        tod.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(to_do.this, date, c
                        .get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
                displayData(c);
            }
        });

        //  to display today's task
        displayData(c);


        // database
        final FirebaseUser mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference readRef = mDatabaseReference.child(mUser.getUid());
        final DatabaseReference simpleReadRef = mDatabaseReference.child(mUser.getUid()).child("SimpleTask");

        //Code Changes for notification Functionality
        PowerManager mgr = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();

        //Intent for Notification service
        Intent intent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        //Intent for track location service
        Intent locationintent = new Intent(this, TrackLocationService.class);
        PendingIntent pendinglocIntent = PendingIntent.getService(this, 0, locationintent, 0);

        //Alarm Manager to start notification and track location services
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 0); // first time
        long frequency = 60 * 1000; // in ms
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendinglocIntent);

        //to go to next date
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {               //https://stackoverflow.com/questions/20582632/how-to-get-the-next-date-when-click-on-button-in-android
                c.add(Calendar.DATE, 1);
                displayData(c);
            }
        });

        //to go to previous date
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //https://stackoverflow.com/questions/20582632/how-to-get-the-next-date-when-click-on-button-in-android
                c.add(Calendar.DATE, -1);
                displayData(c);
            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String name = user.getDisplayName(); // https://stackoverflow.com/questions/42056333/getting-user-name-lastname-and-id-in-firebase
        username.setText(name);

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isopen){

                    location.startAnimation(fabclose);
                    camera_task.startAnimation(fabclose);
                    add_people.startAnimation(fabclose);
                    done_task.startAnimation(fabclose);
                    location.setClickable(false);
                    camera_task.setClickable(false);
                    add_people.setClickable(false);
                    done_task.setClickable(false);
                    isopen=false;

                }else {

                    location.startAnimation(fabopen);
                    camera_task.startAnimation(fabopen);
                    add_people.startAnimation(fabopen);
                    done_task.startAnimation(fabopen);

                    location.setClickable(true);
                    camera_task.setClickable(true);
                    add_people.setClickable(true);
                    done_task.setClickable(true);
                    isopen=true;
                }
            }
        });


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(to_do.this, LocationActivity.class));
            }
        });

        camera_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(to_do.this,BasicTaskActivity.class));
            }
        });

        add_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(to_do.this,EventActivity.class));
            }
        });
    } //end of oncreate

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

        tod.setText(sdf.format(myCalendar.getTime()));
        displayData(myCalendar);
    }


    //Show data display
    private void displayData(Calendar c) {
        recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String cmonth = m.format(c.getTime());
        final String cday = d.format(c.getTime());
        final String month = month_index.format(c.getTime());
        tod.setText(String.valueOf(cmonth + ", " + cday));

        DatabaseReference readRef = mDatabaseReference.child(mUser.getUid());

        readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayList.clear();
                //To show camera based task
                for(DataSnapshot ds : dataSnapshot.child("CameraTask").getChildren()){
                    Task details = ds.getValue(Task.class);
                    String m = details.getMonth();
                    String d = details.getDay();
                    if(m != null && d != null){


                        if (month.contains(m))
                        {
                            if (cday.contains(d))
                            {
                                reminder = ds.getValue(Task.class);
                                reminder.setKey(ds.getKey());
                                displayList.add(reminder);
                            }
                        }
                    }
                }

                //To show event based task
                for(DataSnapshot ds : dataSnapshot.child("EventTask").getChildren()){
                    Task details = ds.getValue(Task.class);
                    String m = details.getMonth();
                    String d = details.getDay();
                    if(m != null && d != null){
                        if (month.contains(m))
                        {
                            if (cday.contains(d))
                            {
                                reminder = ds.getValue(Task.class);
                                reminder.setKey(ds.getKey());
                                displayList.add(reminder);
                            }
                        }

                    }

                }
                //To show location based task
                for(DataSnapshot ds : dataSnapshot.child("LocationBased").getChildren()){
                    Task details = ds.getValue(Task.class);
                    String m = details.getMonth();
                    String d = details.getDay();
                    if( m != null && d !=null){
                        if (month.contains(m))
                        {
                            if (cday.contains(d))
                            {
                                reminder = ds.getValue(Task.class);
                                reminder.setKey(ds.getKey());
                                displayList.add(reminder);
                            }
                        }
                    }
                }
                //Adapter for showing the task
                adapter = new ReminderAdapter(to_do.this, displayList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action, menu);
        return true;
    }

    //Sign out button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_signout){
            FirebaseAuth.getInstance().signOut();
            AutoLoginReference.clearUsername(to_do.this);
            startActivity(new Intent(to_do.this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
