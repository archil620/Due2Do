package due2do.mobile.com.duetodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class to_do extends AppCompatActivity {

    FloatingActionButton fab_main, fab_photo, fab_map, fab_simple;
    Animation fabopen, fabclose, fabrotate, fabantirotate;
    boolean isopen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        fab_main = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab_photo = (FloatingActionButton) findViewById(R.id.floatingActionButton4);
        fab_map = (FloatingActionButton) findViewById(R.id.floatingActionButton5);
        fab_simple = (FloatingActionButton) findViewById(R.id.floatingActionButton6);
        fabopen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);
        fabclose= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_close);
        fabrotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        fabantirotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isopen){
                    fab_photo.startAnimation(fabclose);
                    fab_map.startAnimation(fabclose);
                    fab_simple.startAnimation(fabclose);
                    fab_main.startAnimation(fabantirotate);
                    fab_photo.setClickable(false);
                    fab_map.setClickable(false);
                    fab_simple.setClickable(false);
                    isopen=false;

                }else {
                    fab_photo.startAnimation(fabopen);
                    fab_map.startAnimation(fabopen);
                    fab_simple.startAnimation(fabopen);
                    fab_main.startAnimation(fabrotate);
                    fab_photo.setClickable(true);
                    fab_map.setClickable(true);
                    fab_simple.setClickable(true);
                    isopen=true;
                }
            }
        });

    }


}
