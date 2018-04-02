package due2do.mobile.com.duetodo.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import due2do.mobile.com.duetodo.model.Task;
import due2do.mobile.com.duetodo.utils.UtilityClass;

/**
 * Created by Bhargav Dalal on 01/04/2018.
 */

public class NotificationService extends Service {

    private static final String TAG = "due2do.mobile.com.duetodo";
    Task camrem = new Task();
    String date;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.CANADA);

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mUser;

    Date storedDate;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("MyService", "onCreate callback called");
    }

    long diff;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference readRef = mDatabaseReference.child(mUser.getUid());

        final Date currentTime = Calendar.getInstance().getTime();

        Log.i(TAG, "Service is doing something");
        final Calendar cal = Calendar.getInstance();
        readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.child("CameraTask").getChildren()) {
                    camrem = ds.getValue(Task.class);
                    date = camrem.getDay() + "/" + camrem.getMonth() + "/" + camrem.getYear() + " " + camrem.getHour() + ":" + camrem.getMinute();
                    try {
                        storedDate = dateFormat.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    diff = storedDate.getTime() - currentTime.getTime();

                    if ((diff < 900000) && (diff > 0)) {
                        UtilityClass utilityClass = new UtilityClass();
                        utilityClass.NotificationManager(NotificationService.this, camrem);
                    }

                    Log.i(TAG, date);
                    Log.i(TAG, String.valueOf(storedDate));
                    Log.i(TAG, String.valueOf(currentTime));
                    Log.i(TAG, String.valueOf(storedDate.getTime()));
                    Log.i(TAG, String.valueOf(currentTime.getTime()));
                    Log.i(TAG, String.valueOf(diff));
                }

                for (DataSnapshot ds : dataSnapshot.child("SimpleTask").getChildren()) {
                    camrem = ds.getValue(Task.class);
                    Log.i(TAG, "Inside SimpleTask");
                    date = camrem.getDay() + "/" + camrem.getMonth() + "/" + camrem.getYear() + " " + camrem.getHour() + ":" + camrem.getMinute();
                    try {
                        storedDate = dateFormat.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    diff = storedDate.getTime() - currentTime.getTime();

                    if ((diff < 900000) && (diff > 0)) {
                        UtilityClass utilityClass = new UtilityClass();
                        utilityClass.NotificationManager(NotificationService.this, camrem);
                    }

                    Log.i(TAG, date);
                    Log.i(TAG, String.valueOf(storedDate));
                    Log.i(TAG, String.valueOf(currentTime));
                    Log.i(TAG, String.valueOf(storedDate.getTime()));
                    Log.i(TAG, String.valueOf(currentTime.getTime()));
                    Log.i(TAG, String.valueOf(diff));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        return Service.START_STICKY;
    }

}
