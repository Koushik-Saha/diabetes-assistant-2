package com.jets.medicalreminder.util;

public class Constants {

    private Constants() {
    }


    // General
    public static final int NOTIFICATION_ID = 1;


    // Date Time
    public static final String DATE_FORMAT = "MMM dd, yyyy";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;


    // Internal Storage Path
    public static final String PATH_IMAGES = "images/";
    public static final String PATH_IMAGES_MEDICINES = PATH_IMAGES + "medicines/";


    // Extras
    public static final String EXTRA_MEDICINE_ID = "Medicine ID";
    public static final String EXTRA_CHANGED = "changed";

}
