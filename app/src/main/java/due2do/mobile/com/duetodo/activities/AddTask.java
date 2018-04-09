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
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import due2do.mobile.com.duetodo.model.CameraReminder;
import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.model.Task;
import pub.devrel.easypermissions.EasyPermissions;

public class AddTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    TextView time, date;
    EditText taskName;
    ImageButton createTask;
    ImageView displayimage;
    Task task = new Task();
    Task passedIntent = new Task();
    String priority, mCurrentPhotoPath;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Bitmap cameraBitmap;
    Query createQuery;
    Map<String, String> flagValue = new HashMap<>();
    private StorageReference mStorageRef;
    Uri photoUri;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        time = findViewById(R.id.storetime);
        date = findViewById(R.id.storedate);
        createTask = findViewById(R.id.createtask);
        taskName = findViewById(R.id.taskname);
        displayimage = findViewById(R.id.displayimage);

        //Calendar Defaults
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        final int today = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Show data when task opened
        mStorageRef = FirebaseStorage.getInstance("gs://due2do-app.appspot.com").getReference();
        passedIntent = (Task) getIntent().getSerializableExtra("clickedData");
        if (passedIntent != null) {
            taskName.setText(passedIntent.getTask());
            date.setText(passedIntent.getDay() + "/" + passedIntent.getMonth() + "/" + passedIntent.getYear());
            time.setText(passedIntent.getHour() + ":" + passedIntent.getMinute());
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://due2do-app.appspot.com");
            if (passedIntent.getImageUri() != null) {
                StorageReference ref = storage.getReferenceFromUrl(passedIntent.getImageUri());

                try {
                    final File file = File.createTempFile("Images", "JPG");
                    ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            cameraBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            cameraBitmap = Bitmap.createScaledBitmap(cameraBitmap, 500, 500, false);
                            displayimage.setImageBitmap(cameraBitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

                } catch (IOException e) {
                    Toast.makeText(AddTask.this, "Some error occured! Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //Database Initializaiton
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Spinner Initialization
        spinner = (Spinner) findViewById(R.id.priority);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Date Picker Dialog
        //https://stackoverflow.com/questions/6451837/how-do-i-set-the-current-date-in-a-datepicker
        datePickerDialog = new DatePickerDialog(
                this, due2do.mobile.com.duetodo.activities.AddTask.this, currentYear, currentMonth, today);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        //Time picker dialog
        timePickerDialog = new TimePickerDialog(this, due2do.mobile.com.duetodo.activities.AddTask.this, hour, minute, DateFormat.is24HourFormat(this));
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });

        //Capture Image
        displayimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhotoFromCamera();
            }
        });

        //Create or update task operations
        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskName.getText().toString().length() != 0 && date.getText().toString().length() != 0) {
                    priority = spinner.getSelectedItem().toString();
                    task.setPriority(priority);
                    //Update
                    if (passedIntent != null) {
                        passedIntent.setTask(String.valueOf(taskName.getText()));
                        DatabaseReference db1 = mDatabaseReference.child(mUser.getUid()).child("CameraTask").child(passedIntent.getKey());
                        db1.setValue(passedIntent);
                        Toast.makeText(due2do.mobile.com.duetodo.activities.AddTask.this, "Task Updated", Toast.LENGTH_SHORT).show();

                        Intent displayTask = new Intent(due2do.mobile.com.duetodo.activities.AddTask.this, to_do.class);
                        startActivity(displayTask);

                    }
                    //Create
                    else {
                        createQuery = mDatabaseReference.child(mUser.getUid()).child("CameraTask").orderByKey().limitToLast(1);
                        createQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                task.setTask(String.valueOf(taskName.getText()));
                                if (flagValue.size() >= 1) {
                                    if ((flagValue.get("Done")).equals("Yes")) {
                                        mDatabaseReference.child(mUser.getUid()).child("CameraTask").push().setValue(task);
                                        Toast.makeText(due2do.mobile.com.duetodo.activities.AddTask.this, "Task Created", Toast.LENGTH_SHORT).show();

                                        Intent displayTask = new Intent(due2do.mobile.com.duetodo.activities.AddTask.this, to_do.class);
                                        startActivity(displayTask);
                                    } else {
                                        Toast.makeText(AddTask.this, "Image Still Uploading", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    mDatabaseReference.child(mUser.getUid()).child("CameraTask").push().setValue(task);
                                    Toast.makeText(due2do.mobile.com.duetodo.activities.AddTask.this, "Task Created", Toast.LENGTH_SHORT).show();

                                    Intent displayTask = new Intent(due2do.mobile.com.duetodo.activities.AddTask.this, to_do.class);
                                    startActivity(displayTask);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Task name and date required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Post Date picker operations
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        if (passedIntent != null) {
            passedIntent.setYear(String.valueOf(datePicker.getYear()));
            passedIntent.setMonth(String.valueOf(datePicker.getMonth() + 1));
            passedIntent.setDay(String.valueOf(datePicker.getDayOfMonth()));
        } else {
            task.setYear(String.valueOf(datePicker.getYear()));
            task.setMonth(String.valueOf(datePicker.getMonth() + 1));
            task.setDay(String.valueOf(datePicker.getDayOfMonth()));

        }
        date.setText(dayOfMonth + "/" + String.valueOf(month + 1) + "/" + year);

    }


    //Post time picker operations
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if (passedIntent != null) {
            passedIntent.setHour(String.valueOf(hourOfDay));
            passedIntent.setMinute(String.valueOf(minute));
        } else {
            task.setHour(String.valueOf(hourOfDay));
            task.setMinute(String.valueOf(minute));
        }

        time.setText(hourOfDay + ":" + minute);
    }

    //Capture image function
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
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "due2do.mobile.com.duetodo",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    //Post capture image functions
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            StorageReference filepath = mStorageRef.child(mUser.getUid()).child("CameraTask").child(photoUri.getLastPathSegment());
            flagValue.put("Done", "No");
            Toast.makeText(this, "Image Uploading", Toast.LENGTH_SHORT).show();
            filepath.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    flagValue.put("Done", "Yes");
                    Toast.makeText(AddTask.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    task.setImageUri(String.valueOf(taskSnapshot.getDownloadUrl()));
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(AddTask.this.getContentResolver(), photoUri);
                        displayimage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddTask.this, "Some error occured! Please try again", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    //Create and store image file
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
