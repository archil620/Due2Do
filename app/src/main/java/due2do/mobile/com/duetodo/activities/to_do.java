package due2do.mobile.com.duetodo.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

import due2do.mobile.com.duetodo.model.CameraReminder;
import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.adapter.ReminderAdapter;
import due2do.mobile.com.duetodo.model.Task;

public class to_do extends AppCompatActivity {

    private TextView username;
    TextView td;
    // FloatingActionButton  fab_photo, fab_map, fab_simple,fab_event;
    ImageButton add_task, camera_task, location, add_people ;
    Animation fabopen, fabclose, fabrotate, fabantirotate;
    TextView taskName,taskDate;
    boolean isopen = false;
    Task reminder = new Task();
    RecyclerView recyclerView;
    ReminderAdapter adapter;
    ImageButton next, previous;

    List<Task> reminderList = new ArrayList<>();

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        username = (TextView) findViewById(R.id.username);
        //fab_main = (FloatingActionButton) findViewById(R.id.add_task);
        add_task = (ImageButton) findViewById(R.id.add_task);
        camera_task = (ImageButton) findViewById(R.id.camera_btn);
        location = (ImageButton) findViewById(R.id.location_btn);
        add_people = (ImageButton) findViewById(R.id.add_people_btn);
       // fab_photo = (FloatingActionButton) findViewById(R.id.floatingActionButton4);
       // fab_map = (FloatingActionButton) findViewById(R.id.floatingActionButton5);
       // fab_simple = (FloatingActionButton) findViewById(R.id.floatingActionButton6);
       // fab_event = (FloatingActionButton) findViewById(R.id.floatingActionButton7);
        fabopen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);
        fabclose= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_close);
        fabrotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        fabantirotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        taskName = findViewById(R.id.TaskTitle);
        taskDate = findViewById(R.id.date);
        td = findViewById(R.id.today);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        //to display today's day
        final java.util.Calendar c = java.util.Calendar.getInstance();

        final SimpleDateFormat d = new SimpleDateFormat("dd");
        final SimpleDateFormat m = new SimpleDateFormat("MMMM");
        String cmonth = m.format(c.getTime());
        String cday = d.format(c.getTime());
        td.setText(String.valueOf(cmonth + ", " + cday));

        //to go to next date
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add(Calendar.DATE, 1);
                String cmonth = m.format(c.getTime());
                String cday = d.format(c.getTime());
                td.setText(String.valueOf(cmonth + ", " + cday));

            }
        });

        //to go to previous date
        //to go to next date
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add(Calendar.DATE, -1);
                String cmonth = m.format(c.getTime());
                String cday = d.format(c.getTime());
                td.setText(String.valueOf(cmonth + ", " + cday));

            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String name = user.getDisplayName(); // https://stackoverflow.com/questions/42056333/getting-user-name-lastname-and-id-in-firebase
        username.setText(name);

        // database
        final FirebaseUser mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference readRef = mDatabaseReference.child(mUser.getUid());
        //final DatabaseReference simpleReadRef = mDatabaseReference.child(mUser.getUid()).child("SimpleTask");


        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isopen){

                    location.startAnimation(fabclose);
                    camera_task.startAnimation(fabclose);
                    add_people.startAnimation(fabclose);
                   // fab_main.startAnimation(fabantirotate);
                    location.setClickable(false);
                    camera_task.setClickable(false);
                    add_people.setClickable(false);
                    isopen=false;

                }else {

                    location.startAnimation(fabopen);
                    camera_task.startAnimation(fabopen);
                    add_people.startAnimation(fabopen);
                    //fab_main.startAnimation(fabrotate);

                    location.setClickable(true);
                    camera_task.setClickable(true);
                    add_people.setClickable(true);
                    isopen=true;
                }
            }
        });


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(to_do.this, create2.class));
            }
        });

        camera_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(to_do.this,AddTask.class));
            }
        });

        add_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(to_do.this,Event.class));
            }
        });



        recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Read From Firebase Database
        readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reminderList.clear();
                for(DataSnapshot ds : dataSnapshot.child("CameraTask").getChildren()){

                    reminder = ds.getValue(Task.class);
                    reminderList.add(reminder);
                }

                for(DataSnapshot ds : dataSnapshot.child("SimpleTask").getChildren()){
                    reminder = ds.getValue(Task.class);
                    reminderList.add(reminder);
                }

                for(DataSnapshot ds : dataSnapshot.child("EventTask").getChildren()){
                    reminder = ds.getValue(Task.class);
                    reminderList.add(reminder);
                }

                adapter = new ReminderAdapter(to_do.this, reminderList);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_signout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(to_do.this, MainActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
