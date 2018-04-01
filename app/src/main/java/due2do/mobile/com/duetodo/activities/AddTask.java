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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import due2do.mobile.com.duetodo.model.CameraReminder;
import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.model.Task;
import pub.devrel.easypermissions.EasyPermissions;

public class AddTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private CameraReminder cameraReminder = new CameraReminder();
    TextView time, date;
    EditText taskName;
    ImageButton image, location, contact, createTask;
    ImageView displayimage;
    Task task = new Task();
    String storageImage;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String mCurrentPhotoPath;
    boolean flag = false;
    Query lastQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        database = FirebaseDatabase.getInstance();
        time = findViewById(R.id.storetime);
        date = findViewById(R.id.storedate);
        //image = findViewById(R.id.image);
        createTask = findViewById(R.id.createtask);
        taskName = findViewById(R.id.taskname);
        displayimage = findViewById(R.id.displayimage);

        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        final int today = c.get(Calendar.DAY_OF_MONTH);

        //storageImage = "cam" + currentYear + currentMonth + today + hour + minute;

        Task passedIntent = (Task) getIntent().getSerializableExtra("clickedData");

        if (passedIntent != null) {
            taskName.setText(passedIntent.getTask());
            date.setText(passedIntent.getDay()+"/"+passedIntent.getMonth()+"/"+passedIntent.getYear());
            time.setText(passedIntent.getHour()+":"+passedIntent.getMinute());

            String photoPath = Environment.getExternalStorageDirectory()+"/abc.jpg";

            Bitmap myBitmap = BitmapFactory.decodeFile(passedIntent.getImageUri());
            mCurrentPhotoPath = passedIntent.getImageUri();
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 500, 500, false);
            displayimage.setImageBitmap(myBitmap);
            //time.setText();

        }


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
                this, due2do.mobile.com.duetodo.activities.AddTask.this, currentYear, currentMonth, today);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, due2do.mobile.com.duetodo.activities.AddTask.this, hour, minute, DateFormat.is24HourFormat(this));

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });


        displayimage.setOnClickListener(new View.OnClickListener() {
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
                task.setTask(String.valueOf(taskName.getText()));
                lastQuery = mDatabaseReference.child(mUser.getUid()).child("CameraTask").orderByKey().limitToLast(1);

                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    Task reminder = new Task();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            reminder = ds.getValue(Task.class);
                        }

                        if (reminder.getId() != null && !(reminder.getId().isEmpty())) {
                            int val = Integer.valueOf(reminder.getId().substring(1));
                            val = val + 1;
                            task.setId("C" + val);

                        } else {
                            task.setId("C1");
                        }
                        mDatabaseReference.child(mUser.getUid()).child("CameraTask").push().setValue(task);
                        Toast.makeText(due2do.mobile.com.duetodo.activities.AddTask.this, "Task Created", Toast.LENGTH_SHORT).show();

                        Intent displayTask = new Intent(due2do.mobile.com.duetodo.activities.AddTask.this, to_do.class);
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
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        task.setYear(String.valueOf(datePicker.getYear()));
        task.setMonth(String.valueOf(datePicker.getMonth()));
        task.setDay(String.valueOf(datePicker.getDayOfMonth()));

        date.setText(dayOfMonth+"/"+month+"/"+year);

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        task.setHour(String.valueOf(hourOfDay));
        task.setMinute(String.valueOf(minute));

        time.setText(hourOfDay+":"+minute);
    }

    private void showPictureDialog() {

        if (flag) {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + mCurrentPhotoPath), "image/*");
            startActivity(intent);
        } else {
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
            pictureDialog.setTitle("Select Action");
            String[] pictureDialogItems = {
                    "Select photo from gallery",
                    "Capture photo from camera"};
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
            flag = true;
            pictureDialog.show();
        }

    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 1);
    }

    private void takePhotoFromCamera() {
        Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "due2do.mobile.com.duetodo",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            task.setImageUri(mCurrentPhotoPath);
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveImage(imageBitmap,"saved");*/
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 500, 500, false);
            displayimage.setImageBitmap(myBitmap);
        }

    }

    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File file = new File(myDir, imageFileName);
        if (file.exists()) file.delete();
        task.setImageUri(file.toString());
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
