/*
package due2do.mobile.com.duetodo.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.model.CameraReminder;
import pub.devrel.easypermissions.EasyPermissions;

*
 * Created by Ankit Varshney on 30/03/2018.



public class Task extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView time;
    EditText taskName;
    ImageButton image,location,contact,createTask;
    File file;
    private ViewGroup mLinearLayout;
    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    CameraReminder reminder = new CameraReminder();
    String storageImage = null;
    DatabaseReference mDatabaseReference;
    DatePickerDialog datePickerDialog;
    FirebaseUser mUser;
    Query lastQuery;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        time = findViewById(R.id.time);
        image = findViewById(R.id.image);
        location = findViewById(R.id.location);
        contact = findViewById(R.id.contact);
        createTask = findViewById(R.id.createtask);
        mLinearLayout = findViewById(R.id.displaytasks);
        taskName = findViewById(R.id.taskname);

        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int today = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        storageImage = "cam"+ currentYear + currentMonth + today + hour + minute;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Spinner spinner = (Spinner) findViewById(R.id.priority);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        //https://stackoverflow.com/questions/6451837/how-do-i-set-the-current-date-in-a-datepicker


        datePickerDialog = new DatePickerDialog(
                this, Task.this, currentYear, currentMonth, today);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //https://demonuts.com/pick-image-gallery-camera-android/
                showPictureDialog();
            }
        });

        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myRef.child("task").setValue()
                reminder.setTask(String.valueOf(taskName.getText()));

                lastQuery = mDatabaseReference.child(mUser.getUid()).child("CameraTask").orderByKey().limitToLast(1);

                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    CameraReminder databaseCameraReminder = new CameraReminder();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            databaseCameraReminder = ds.getValue(CameraReminder.class);
                        }

                        if(databaseCameraReminder.getId() != null && !(databaseCameraReminder.getId().isEmpty())){
                            int val = Integer.valueOf(databaseCameraReminder.getId().substring(1));
                            val = val + 1;
                            reminder.setId("C"+val);

                        }else{
                            reminder.setId("C1");
                        }
                        databaseCameraReminder = null;
                        mDatabaseReference.child(mUser.getUid()).child("CameraTask").push().setValue(reminder);
                        Toast.makeText(Task.this,"Task Created",Toast.LENGTH_SHORT).show();

                        Intent displayTask = new Intent(Task.this,to_do.class);
                        startActivity(displayTask);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        reminder.setYear(String.valueOf(datePicker.getYear()));
        reminder.setMonth(String.valueOf(datePicker.getMonth()));
        reminder.setDay(String.valueOf(datePicker.getDayOfMonth()));

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, Task.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        reminder.setHour(String.valueOf(i));
        reminder.setMinute(String.valueOf(i1));
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 1);
    }

    private void takePhotoFromCamera() {
        Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //https://stackoverflow.com/questions/17794974/create-folder-in-android
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Due2Do");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }

        file = new File(folder, storageImage);
        Uri uri = FileProvider.getUriForFile(Task.this, getApplicationContext().getPackageName() + ".provider", file);
        m_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(m_intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        //https://stackoverflow.com/questions/31939741/how-to-assign-a-value-to-a-text-view-in-another-layout-on-android
        Bitmap adjustedBitmap = null;
        Bitmap rotatedBitmap = null;

        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){


                    if (EasyPermissions.hasPermissions(this, galleryPermissions)) {
                        File imageFile = new File(Environment.getExternalStorageDirectory() + "/Due2Do", storageImage);
                        Uri uri = Uri.fromFile(imageFile);
                        reminder.setImageUri(uri);

                        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


                        try {
                            ExifInterface ei = new ExifInterface(uri.getPath());
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            myBitmap = Bitmap.createScaledBitmap(myBitmap,500,500,false);

                            switch(orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(myBitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(myBitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(myBitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = myBitmap;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        EasyPermissions.requestPermissions(this, "Access for storage",
                                101, galleryPermissions);
                    }

                    //https://stackoverflow.com/questions/27128425/add-multiple-custom-views-to-layout-programmatically
                    View layout2 = LayoutInflater.from(this).inflate(R.layout.image_layout, mLinearLayout, false);
                    ImageView view = (ImageView) layout2.findViewById(R.id.showimage);
                    view.setImageBitmap(rotatedBitmap);
                    layout2.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));

                    mLinearLayout.addView(layout2);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){

                }
                break;
        }


    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


}
*/
