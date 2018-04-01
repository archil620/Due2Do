package due2do.mobile.com.duetodo.model;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Ankit Varshney on 27/02/2018.
 */

public class CameraReminder implements Serializable {

    private String id, task, hour, minute, year, month, day;
    private String imageUri;


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public String toString() {
        return "CameraReminder{" +
                "id='" + id + '\'' +
                ", task='" + task + '\'' +
                ", hour='" + hour + '\'' +
                ", minute='" + minute + '\'' +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                '}';
    }

    /*public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }*/
}
