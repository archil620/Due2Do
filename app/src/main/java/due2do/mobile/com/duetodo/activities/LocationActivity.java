package due2do.mobile.com.duetodo.activities;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.model.LocationModel;
import due2do.mobile.com.duetodo.model.Task;
import due2do.mobile.com.duetodo.services.TrackLocationService;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private GoogleMap mMap;
    private EditText taskName;
    private Button clearTaskBtn;
    private ImageButton setTaskBtn;
    private LatLng ltl = new LatLng(0, 0);
    private int taskFlag = 0;
    Task model = new Task();
    Query createQuery;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    TextView time, date;
    // get uid
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getCurrentUser().getUid();
    Task passedIntent = new Task();
    Marker oldMarker;
    Marker newMarkr;


    // Database
    private DatabaseReference mDatabaseReference;
    LatLng oldlatLng;
    private String taskId;
    Spinner spinner;
    String priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        setTitle("Create Location Task");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        taskName = (EditText) findViewById(R.id.taskname);
        clearTaskBtn = (Button) findViewById(R.id.clearTaskBtn);
        setTaskBtn = (ImageButton) findViewById(R.id.createtask);

        time = findViewById(R.id.storetime);
        date = findViewById(R.id.storedate);
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        final int today = c.get(Calendar.DAY_OF_MONTH);

        passedIntent = (Task) getIntent().getSerializableExtra("clickedData");
        spinner = (Spinner) findViewById(R.id.priority);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        if(passedIntent != null){
            taskName.setText(passedIntent.getTask());
            date.setText(passedIntent.getDay() + "/" + passedIntent.getMonth() + "/" + passedIntent.getYear());
            time.setText(passedIntent.getHour() + ":" + passedIntent.getMinute());
            oldlatLng = new LatLng(passedIntent.getLatitude(),passedIntent.getLongitude());

        }


        //https://stackoverflow.com/questions/6451837/how-do-i-set-the-current-date-in-a-datepicker
        datePickerDialog = new DatePickerDialog(
                this, due2do.mobile.com.duetodo.activities.LocationActivity.this, currentYear, currentMonth, today);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, due2do.mobile.com.duetodo.activities.LocationActivity.this, hour, minute, DateFormat.is24HourFormat(this));

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });



    }
    public Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation != null)
                return currentLocation;
            else{
                currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                return currentLocation;
            }
        }
        else{
            // ask the user to turn on the GPS
            return null;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        taskFlag = 0;
        // showing the current location
        final Location location = getLocation();
        LatLng currentLoc = new LatLng(location.getLatitude(),location.getLongitude());
        if(passedIntent != null){

            oldMarker = mMap.addMarker(new MarkerOptions().position(oldlatLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oldlatLng,15));
        }else{
            newMarkr = mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,15));

        }

        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);



        // https://stackoverflow.com/questions/24302112/how-to-get-the-latitude-and-longitude-of-location-where-user-taps-on-the-map-in
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // remove marker when new one added
                if(taskFlag == 0) {
                    taskFlag = 1;
                    if(passedIntent != null){
                        oldMarker.remove();
                    }
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    CircleOptions circleOptions = new CircleOptions()
                            .center(latLng)
                            .radius(100)
                            .fillColor(0x40ff0000)  //semi-transparent
                            .strokeColor(Color.RED)
                            .strokeWidth(5);
                    mMap.addCircle(circleOptions);
                    ltl = latLng;
                    if(passedIntent != null){
                        passedIntent.setLatitude(ltl.latitude);
                        passedIntent.setLongitude(ltl.longitude);
                    }else{
                        model.setLatitude(ltl.latitude);
                        model.setLongitude(ltl.longitude);
                    }
                }
            }
        });

        // setup the database
        final FirebaseUser mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();



        setTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setTask(String.valueOf(taskName.getText()));
                model.setTaskStatus("Active");
                model.setLatitude(ltl.latitude);
                model.setLongitude(ltl.longitude);


                if(passedIntent != null){
                    priority = spinner.getSelectedItem().toString();
                    model.setPriority(priority);
                    passedIntent.setTask(String.valueOf(taskName.getText()));
                    DatabaseReference db1 = mDatabaseReference.child(mUser.getUid()).child("LocationBased").child(passedIntent.getKey());
                    db1.setValue(passedIntent);
                    Toast.makeText(due2do.mobile.com.duetodo.activities.LocationActivity.this, "Task Updated", Toast.LENGTH_SHORT).show();


                    taskId = passedIntent.getKey();
                    Intent intent = new Intent(LocationActivity.this, TrackLocationService.class);
                    intent.putExtra("TaskName",taskName.getText().toString());
                    intent.putExtra("TaskId",taskId);
                    intent.putExtra("Day",model.getDay());
                    intent.putExtra("Month",model.getMonth());
                    intent.putExtra("Year",model.getYear());
                    intent.putExtra("Day",model.getMinute());
                    intent.putExtra("Day",model.getHour());
                    startService(intent);

                    Intent displayTask = new Intent(due2do.mobile.com.duetodo.activities.LocationActivity.this, to_do.class);
                    startActivity(displayTask);

                }else{
                    priority = spinner.getSelectedItem().toString();
                    model.setPriority(priority);
                    createQuery = mDatabaseReference.child(mUser.getUid()).child("LocationBased").orderByKey().limitToLast(1);

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
                                model.setId("L" + val);

                            } else {
                                model.setId("L1");

                            }
                            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mUser.getUid()).child("LocationBased").push();
                            taskId = mDatabaseReference.getKey();
                            mDatabaseReference.setValue(model);


                            Toast.makeText(LocationActivity.this, "Task Added",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LocationActivity.this, TrackLocationService.class);
                            intent.putExtra("TaskName",taskName.getText().toString());
                            intent.putExtra("TaskId",taskId);
                            intent.putExtra("Day",model.getDay());
                            intent.putExtra("Month",model.getMonth());
                            intent.putExtra("Year",model.getYear());
                            intent.putExtra("Minute",model.getMinute());
                            intent.putExtra("Hour",model.getHour());
                            startService(intent);

                            Intent displayTask = new Intent(due2do.mobile.com.duetodo.activities.LocationActivity.this, to_do.class);
                            startActivity(displayTask);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }
        });

        clearTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskName.setText("");
                date.setText("");
                time.setText("");
                mMap.clear();

                onMapReady(mMap);

            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        if(passedIntent != null){
            passedIntent.setYear(String.valueOf(datePicker.getYear()));
            passedIntent.setMonth(String.valueOf(datePicker.getMonth() + 1));
            passedIntent.setDay(String.valueOf(datePicker.getDayOfMonth()));
        }else{
            model.setYear(String.valueOf(datePicker.getYear()));
            model.setMonth(String.valueOf(datePicker.getMonth() + 1));
            model.setDay(String.valueOf(datePicker.getDayOfMonth()));

        }


        date.setText(i + "/" + (i1+1) + "/" + i2);

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if(passedIntent !=null){
            passedIntent.setHour(String.valueOf(i));
            passedIntent.setMinute(String.valueOf(i1));
        }else{
            model.setHour(String.valueOf(i));
            model.setMinute(String.valueOf(i1));
        }

        time.setText(i + ":" + i1);
    }
}
