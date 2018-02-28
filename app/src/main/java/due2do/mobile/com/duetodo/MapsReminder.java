package due2do.mobile.com.duetodo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsReminder extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText taskName;
    private Button setTaskBtn;
    private Button clearTaskBtn;
    private LatLng ltl = new LatLng(0, 0);
    private int taskFlag = 0;

    // get uid
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getCurrentUser().getUid();


    // Database
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_reminder);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        taskName = (EditText) findViewById(R.id.taskName);
        setTaskBtn = (Button) findViewById(R.id.setTaskBtn);
        clearTaskBtn = (Button) findViewById(R.id.clearTaskBtn);

    }

    public Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation != null)
                return currentLocation;
            else{
                currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                return currentLocation;
            }
        }
        else
            return null;
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
                            .strokeColor(Color.BLUE)
                            .strokeWidth(5);
                    mMap.addCircle(circleOptions);
                    ltl = latLng;
                }
            }
        });

        // setup the database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser mUser = firebaseAuth.getCurrentUser();

        setTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseReference.child(mUser.getUid()).child(taskName.getText().toString()).setValue(ltl);
                Toast.makeText(MapsReminder.this, "Task Added",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapsReminder.this, TrackLocationService.class);
                intent.putExtra("TaskName",taskName.getText().toString());
                //intent.putExtra("TaskLat",ltl.latitude);
                //intent.putExtra("TaskLong",ltl.longitude);
                startService(intent);

                //startActivity(new Intent(MapsReminder.this, TrackStatus.class));
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


}
