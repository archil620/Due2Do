package due2do.mobile.com.duetodo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import due2do.mobile.com.duetodo.utils.AutoLoginReference;

/**
 * Created by Ankit Varshney on 28/03/2018.
 */

//Activity to show the login page or the home page
public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AutoLoginReference.getUsername(StartActivity.this).length() == 0){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
        }else{
            startActivity(new Intent(StartActivity.this, to_do.class));
        }
    }
}
