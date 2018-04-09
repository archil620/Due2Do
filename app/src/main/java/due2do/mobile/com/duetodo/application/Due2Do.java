package due2do.mobile.com.duetodo.application;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ankit Varshney on 31/03/2018.
 */

public class Due2Do extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // See https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase.html#public-methods
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Log.d("TheApp", "application created");
    }
}