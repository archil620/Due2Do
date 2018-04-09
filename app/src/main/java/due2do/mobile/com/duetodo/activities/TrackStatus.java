package due2do.mobile.com.duetodo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import due2do.mobile.com.duetodo.R;

public class TrackStatus extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // get uid
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String uid = firebaseAuth.getCurrentUser().getUid();

    private String taskName;
    private String taskId;
    // Database
    private DatabaseReference mDatabaseReference;
    private Double taskLatitude;
    private Double taskLongitude;

    private Button completeTaskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_status);

        completeTaskBtn = (Button)findViewById(R.id.completeTaskBtn);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        taskName = intent.getStringExtra("TaskName");
        taskId = intent.getStringExtra("TaskId");


        completeTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                final FirebaseUser mUser = firebaseAuth.getCurrentUser();

                mDatabaseReference.child(mUser.getUid()).child("LocationBased").child(taskId).child("taskStatus").setValue("Completed");

                Toast.makeText(TrackStatus.this, "Task Marked as Complete",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TrackStatus.this, to_do.class));


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

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference taskRef = mDatabaseReference.child(uid).child("LocationBased").child(taskId);
        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskLatitude = dataSnapshot.child("latitude").getValue(Double.class); // retrieved task latitude
                taskLongitude = dataSnapshot.child("longitude").getValue(Double.class); // retrieved task longitude


                Location location = getLocation();
                LatLng currentLoc = new LatLng(location.getLatitude(),location.getLongitude());
                LatLng taskLoc = new LatLng(taskLatitude,taskLongitude);
                mMap.addMarker(new MarkerOptions().position(taskLoc).title("Task "+taskName +" Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,15));
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);

                mMap.addMarker(new MarkerOptions().position(taskLoc).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                Polyline polyline = mMap.addPolyline(new PolylineOptions()
                        .add(taskLoc,currentLoc)
                        .width(5)
                        .color(Color.RED)
                );
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
