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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class create2 extends FragmentActivity implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private GoogleMap mMap;
    private EditText taskName;
    private Button clearTaskBtn;
    private ImageButton setTaskBtn;
    private LatLng ltl = new LatLng(0, 0);
    private int taskFlag = 0;
    LocationModel model = new LocationModel();
    Query createQuery;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    TextView time, date;
    // get uid
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getCurrentUser().getUid();


    // Database
    private DatabaseReference mDatabaseReference;

    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create2);


        setTitle("Create Task");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        taskName = (EditText) findViewById(R.id.taskName);
        clearTaskBtn = (Button) findViewById(R.id.clearTaskBtn);
        setTaskBtn = (ImageButton) findViewById(R.id.setTaskBtn);

        time = findViewById(R.id.storetime);
        date = findViewById(R.id.storedate);
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        final int today = c.get(Calendar.DAY_OF_MONTH);


        //https://stackoverflow.com/questions/6451837/how-do-i-set-the-current-date-in-a-datepicker
        datePickerDialog = new DatePickerDialog(
                this, due2do.mobile.com.duetodo.activities.create2.this, currentYear, currentMonth, today);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, due2do.mobile.com.duetodo.activities.create2.this, hour, minute, DateFormat.is24HourFormat(this));

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
        Location location = getLocation();
        LatLng currentLoc = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);



        // https://stackoverflow.com/questions/24302112/how-to-get-the-latitude-and-longitude-of-location-where-user-taps-on-the-map-in
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // remove marker when new one added
                if(taskFlag == 0) {
                    taskFlag = 1;
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    CircleOptions circleOptions = new CircleOptions()
                            .center(latLng)
                            .radius(100)
                            .fillColor(0x40ff0000)  //semi-transparent
                            .strokeColor(Color.RED)
                            .strokeWidth(5);
                    mMap.addCircle(circleOptions);
                    ltl = latLng;
                }
            }
        });

        // setup the database
        final FirebaseUser mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //taskId = mDatabaseReference.getKey();

        setTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.setTaskName(String.valueOf(taskName.getText()));
                model.setTaskStatus("Active");
                model.setLatitude(ltl.latitude);
                model.setLongitude(ltl.longitude);

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
                            model.setId("C" + val);

                        } else {
                            model.setId("C1");

                        }
                        mDatabaseReference.child(mUser.getUid()).child("LocationBased").push().setValue(model);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Toast.makeText(create2.this, "Task Added",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(create2.this, TrackLocationService.class);
                intent.putExtra("TaskName",taskName.getText().toString());
                intent.putExtra("TaskId",taskId);
                startService(intent);

            }
        });

        clearTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskName.setText("");
                mMap.clear();
                onMapReady(mMap);

            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        model.setYear(String.valueOf(datePicker.getYear()));
        model.setMonth(String.valueOf(datePicker.getMonth()));
        model.setDay(String.valueOf(datePicker.getDayOfMonth()));
        date.setText(i + "/" + i1 + "/" + i2);

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        model.setHour(String.valueOf(i));
        model.setMinute(String.valueOf(i1));

        time.setText(i + ":" + i1);
    }
}
