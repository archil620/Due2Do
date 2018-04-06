# Due 2 Do
Due 2 Do is powerful to-do list aimed to facilitate one’s life. It can organize your task based
on priority. Due 2 Do is a one-for-all option for the users where the task can be classified as a
routine, location-based task and a gathering event task. The user can be carefree as Due 2 Do
will take care of all their activity and will also provide valuable inputs corresponding to the
task. It will provide a priority-based reminder and facilitate the user with navigational
functionalities. People can store links, capture image, create gathering groups, create a to-do
list for grocery shopping and many more alternative usages.
Due 2 Do is design to boost your productivity and make your life easier.
## Libraries

**Firebase:** Firebase is a library and platform which provides us the facility to authenticate the user and to store/retrieve the data. 

**Permission Manager:** It is library which ask the user about the permission to use the camera, location services, etc.

**Google Maps API:** Google Maps API helps to fetch the location.

**google-gson:** Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object. Source [here](https://github.com/google/gson)

## Installation Notes
Minimum SDK version - 23 <br />
Minimum CompileSKDversion - 26 <br />
Device's GPS needs to be keep on. <br />
Application required will not run on emulator as it requires GPS, hence please intall on android phone.


## Code Examples
**Problem 1: Unable to store and retrieve the image if another device is used with same user.**

The application earlier was able to save the picture locally on the phone and hence when the device is change, the application was unable to retrieve the image.
This issue was resolved by storing the image in the firebase storage instead of storing it in local storage.
```
//Used firebase storage for image accessing and retrieving.
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
                Toast.makeText(AddTask.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddTask.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
```

**Problem 2: The application was directly accessing device services without asking user's permission.**

Every application ask for the user's permission for giving access to device services such as camera, contact, storage and location services, etc. The application was earlier directly accessing these device services without user's permission. 
Hence, here is a snapshot of the code which help us to solve this issue.
```
//Solution for the problem was obtained from the github/karanchuri/PermissionManager.
 permissionManager= new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);

public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
}
```

**Problem 3: Difficulties in retreiving the contact number from the contact list.**

It was difficult to fetch contact number from the contact list and also to display its respective name in a ListView.
```
public void onActivityResult(int reqCode, int resultCode, Intent data) {
        int flag = 0;
        //http://www.worldbestlearningcenter.com/tips/Android-get-phone-number-from-contacts-list.htm
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                {
                                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(num));

                                    String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

                                    String contactName = "";
                                    Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

                                    if (cursor != null) {
                                        if (cursor.moveToFirst()) {
                                            contactName = cursor.getString(0);
                                            Log.i("name", contactName);
                                            if (flag == 0) {
                                                String numName = contactName + "," + num;
                                                contactList.add(numName);
                                                adapter.notifyDataSetChanged();
                                                flag = 1;
                                            }
                                        }
                                        cursor.close();
                                    }
                                }
                            }
                        }
                    }
                    eventReminder.setContactList(contactList);
                    break;
                }
        }
    }
```

## Feature Section
* **Authentication functionality:** We have google authentication services for athenticating the user.
* **Simple Reminder:** The user can add edit and delete the task. Furthermore, image from camera can be added in the reminder.
* **Location based reminder:** The user can create a task based on the location and not the time. The location of the address is fetched and displayed it on the map. We have integrated the maps with the Google maps. The location-based reminder will work according to the distance set from the address,
i.e., if the person is in the radius of 1 mile, the user will be notified for the task.
* **Meeting based reminder:** If a user wants to create a reminder for a social gathering or a professional gathering,
the user can use the application to create an event, add people to it and the added people will receive the message about the reminder.
* **Priority based reminder:** Different set of vibrations for reminders based on priority.

 
## Final Project Status
The application is working smoothly along with the polish User Interface. All the functionality such as simple reminder, location-base reminder and meeting base reminder is working well. We have also given an option to add the image from camera in simple reminder.
We have implemented different set of vibration according to the priority of the reminder. However, the widget of the application is not working. So, we wish to complete it and then we may be think to release the application on Google play-store.


#### Minimum Functionality
- Authentication functionality (Completed)
- Simple Reminder (Completed)
- Location based reminder (Completed)

#### Expected Functionality
- Location based reminder - added notification if in 1 mile radius(Completed)
- Meeting based reminder (Completed)
- Priority based reminder (Completed)
- Capture image/Attach links (Completed)
- Navigation Feature (Completed)

#### Bonus Functionality
- Meeting based reminder - added message functionality(Completed)
- Widget (Partially Completed but not merged in master)

## Sources
[1] thenewboston, “Android App Development for Beginners - 1 - Introduction,” YouTube, 11-Dec-2014. [Online]. Available: https://www.youtube.com/watch?v=QAbQgLGKd3Y&list=PL6gx4Cwl9DGBsvRxJJOzG4r4k_zLKrnxl. [Accessed: 02-Apr-2018].

[2] “Authenticate Using Google Sign-In on Android  |  Firebase,” Google. [Online]. Available: https://firebase.google.com/docs/auth/android/google-signin?authuser=0. [Accessed: 28-Feb-2018].

[3] akshayejh, “Android Studio Tutorial - Google Sign In - Firebase Authentication - Part 16,” YouTube, 22-Aug-2016. [Online]. Available: https://www.youtube.com/watch?v=-ywVw2O1pP8&t=226s. [Accessed: 02-Apr-2018].

[4] "karanchuri/PermissionManager", GitHub, 2018. [Online]. Available: https://github.com/karanchuri/PermissionManager. [Accessed: 25- March- 2018]. 

[5] “How to Build To do list Application using Database ( Android Studio tutorial ),” YouTube, 29-Sep-2017. [Online]. Available: https://www.youtube.com/watch?v=xVZ0vY2P1S4. [Accessed: 02-Apr-2018].

[6] TICOONTECHNOLOGIES, “Android Floating Action Button with Animations,” YouTube, 24-Jul-2016. [Online]. Available: https://www.youtube.com/watch?v=orcpzMo7igQ&t=780s. [Accessed: 02-Apr-2018].

[7] “Generic icon generator,” Android Asset Studio - Generic icon generator. [Online]. Available: https://romannurik.github.io/AndroidAssetStudio/icons-generic.html. [Accessed: 02-Apr-2018].

[8] Android get phone number from contacts list. [Online]. Available: http://www.worldbestlearningcenter.com/tips/Android-get-phone-number-from-contacts-list.htm. [Accessed: 02-Apr-2018].

[9] “Android app widget with ListView,” Laaptu, 19-Jul-2013. [Online]. Available: https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/. [Accessed: 02-Apr-2018].

[10] 2/4 How to add buttons to action bar Android Studio,” YouTube, 27-Jul-2015. [Online]. Available: https://www.youtube.com/watch?v=5MSKuVO2hV4&t=155s. [Accessed: 02-Apr-2018].
