package due2do.mobile.com.duetodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class create1 extends AppCompatActivity {

    Button addtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create1);

        addtask = (Button) findViewById(R.id.button2);

        addtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(create1.this, to_do.class));
            }
        });
    }
}
