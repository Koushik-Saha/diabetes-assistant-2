package com.jets.medicalreminder.beans;

import java.util.Locale;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.jets.medicalreminder.R;

public class Medicine implements Comparable<Medicine> {

    public enum Type {
        ORAL(R.drawable.oral), TOPICAL(R.drawable.topical), INHALATION(R.drawable.inhalation),
        INJECTION(R.drawable.injection), OTHER(R.drawable.default_medicine);

        private int imageResource;

        Type(int imageResource) {
            this.imageResource = imageResource;
        }

        public int getImageResource() {
            return imageResource;
        }

        public String toString() {
            String origin = super.toString();
            return (origin.substring(0, 1) + origin.substring(1).toLowerCase(Locale.getDefault()));
        }
    }


    public static final int NAME_MAX_LENGTH = 30;
    public static final int DOSE_MAX_VALUE = 10;
    public static final int NOTES_MAX_LENGTH = 255;

    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_START_DATE = 1;

    private static int sortType;

    public static int getSortType() {
        return sortType;
    }

    public static void setSortType(int sortType) {
        if (sortType < SORT_BY_NAME || sortType > SORT_BY_START_DATE) {
            return;
        }
        Medicine.sortType = sortType;
    }


    private int id;
    private String name;
    private Type type;
    private int dose;
    private long startDateTime;
    private long endDateTime;
    private long interval;
    private String notes;
    transient private Bitmap image;


    // Constructors

    public Medicine() {
    }

    public Medicine(int id) {
        setId(id);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getDose() {
        return dose;
    }

    public void setDose(int dose) {
        this.dose = dose;
    }

    public long getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }

    public long getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(long endDateTime) {
        this.endDateTime = endDateTime;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


    @Override
    public String toString() {
        return name;
    }


    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof Medicine && id == ((Medicine) object).id;
    }


    @Override
    public int compareTo(@NonNull Medicine medicine) {
        int order = 0;

        if (sortType == SORT_BY_NAME) {
            order = name.compareToIgnoreCase(medicine.name);
            if (order == 0) {
                order = name.compareTo(medicine.name);
            }
        } else if (sortType == SORT_BY_START_DATE) {
            long dateDifference = startDateTime - medicine.getStartDateTime();
            order = dateDifference > 0 ? 1 : -1;
        }

        return order;
    }

}
