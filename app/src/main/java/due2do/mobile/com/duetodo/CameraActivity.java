package due2do.mobile.com.duetodo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Ankit Varshney on 28/02/2018.
 */

public class CameraActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {


    Button taskbtn;
    ImageButton btn,capture;
    /*TextView taskView;
    TextView dateView;
    TextView timeView;*/
    String pictureImagePath = null;
    ImageView imageView;
    String encodedImage = null;
    TextView textView;
    CameraReminder cameraReminder = new CameraReminder();

    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create1);

        btn = findViewById(R.id.cal);
        taskbtn = findViewById(R.id.task);
        capture = findViewById(R.id.capture);
        textView = findViewById(R.id.et);
        imageView = findViewById(R.id.image);
        /*taskView = findViewById(R.id.showtask);
        dateView = findViewById(R.id.showdate);
        timeView = findViewById(R.id.showtime);*/
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        final DatabaseReference myRef = database.getReference();
        final DatabaseReference readRef = database.getReference().child("task");
        readRef.keepSynced(true);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,CameraActivity.this, 2018, 01, 01);

        //myRef.child("CameraTask");

        /*readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    CameraReminder camrem = new CameraReminder();
                    //camrem.setTask(ds.child("message").getValue(CameraReminder.class).getTask());
                    camrem = ds.getValue(CameraReminder.class);
                    taskView.append(String.valueOf(camrem.getTask()) + " ");
                    dateView.append(String.valueOf(camrem.getYear() + "/" + camrem.getMonth() + "/" + camrem.getDay()) + " ");
                    timeView.append(String.valueOf(camrem.getHour()+ ":" + camrem.getMinute()) + " ");
                    //camrem.setYear(ds.child("message").getValue(CameraReminder.class).getYear());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();

            }
        });

        taskbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myRef.child("task").setValue()
                cameraReminder.setTask(String.valueOf(textView.getText()));
                myRef.child("CameraTask").push().setValue(cameraReminder);
                Toast.makeText(CameraActivity.this,"Task Created",Toast.LENGTH_SHORT).show();

            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageFileName = "image.jpg";
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
                File file = new File(pictureImagePath);
                Uri outputUri = Uri.fromFile(file);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                startActivityForResult(intent, 1);
            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        cameraReminder.setYear(String.valueOf(datePicker.getYear()));
        cameraReminder.setMonth(String.valueOf(datePicker.getMonth()));
        cameraReminder.setDay(String.valueOf(datePicker.getDayOfMonth()));

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, CameraActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        cameraReminder.setHour(String.valueOf(i));
        cameraReminder.setMinute(String.valueOf(i1));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (EasyPermissions.hasPermissions(this, galleryPermissions)) {
            File imageFile = new File(pictureImagePath);
            if (imageFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteFormat = stream.toByteArray();
                encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
                cameraReminder.setEncodedImage(encodedImage);
                Bitmap bitmap = Bitmap.createScaledBitmap(myBitmap, 1000, 1000, true);
                imageView.setImageBitmap(bitmap);
            }
        } else {
            EasyPermissions.requestPermissions(this, "Access for storage",
                    101, galleryPermissions);
        }
    }
}

