package due2do.mobile.com.duetodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity {

    private Button signOutBtn;
    private TextView authName;
    private TextView authId;
    private Button mapsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        signOutBtn = (Button)findViewById(R.id.signOutBtn);
        authName = (TextView) findViewById(R.id.authName);
        authId = (TextView) findViewById(R.id.authId);
        mapsBtn = (Button) findViewById(R.id.mapsBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String name = user.getDisplayName(); // https://stackoverflow.com/questions/42056333/getting-user-name-lastname-and-id-in-firebase
        authName.setText("Welcome " + name);
        authId.setText("Your ID: " + uid);

        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( Dashboard.this, MapsReminder.class));
            }
        });


        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Dashboard.this, MainActivity.class));
            }
        });
    }
}
