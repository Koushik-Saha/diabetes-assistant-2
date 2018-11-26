package com.jets.medicalreminder.database;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jets.medicalreminder.beans.Medicine;

public class MedicineAdapter {

    private static final String[] MEDICINE_COLUMNS = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_TYPE, DatabaseHelper.COLUMN_DOSE, DatabaseHelper.COLUMN_START_DATE_TIME,
            DatabaseHelper.COLUMN_END_DATE_TIME, DatabaseHelper.COLUMN_INTERVAL, DatabaseHelper.COLUMN_NOTES};

    private static MedicineAdapter medicineAdapter;

    public static synchronized MedicineAdapter getInstance(Context context) {
        if (medicineAdapter == null) {
            medicineAdapter = new MedicineAdapter(context);
        }

        return medicineAdapter;
    }


    private Context context;
    private DatabaseHelper databaseHelper;

    private boolean medicinesChanged;
    private boolean pendingMedicinesChanged;


    // Constructor
    private MedicineAdapter(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }


    // Set Medicines Changed
    public void setMedicinesChanged(boolean medicinesChanged) {
        this.medicinesChanged = medicinesChanged;
    }

    // Is Medicines Changed
    public boolean isMedicinesChanged() {
        return medicinesChanged;
    }


    // Set Pending Medicines Changed
    public void setPendingMedicinesChanged(boolean pendingMedicinesChanged) {
        this.pendingMedicinesChanged = pendingMedicinesChanged;
    }

    // Is Pending Medicines Changed
    public boolean isPendingMedicinesChanged() {
        return pendingMedicinesChanged;
    }


    // Add Medicine
    public long addMedicine(Medicine medicine) {
        ContentValues contentValues = getContentValues(medicine);

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long rowID = database.insert(DatabaseHelper.TABLE_MEDICINES, null, contentValues);
        medicine.setId((int) rowID);
        database.close();

        saveMedicineImage(medicine.getImage(), (int) rowID);

        medicinesChanged = true;

        return rowID;
    }


    // Update Medicine
    public long updateMedicine(Medicine medicine) {
        ContentValues contentValues = getContentValues(medicine);

        String where = DatabaseHelper.COLUMN_ID + " = " + medicine.getId();

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long rowsAffected = database.update(DatabaseHelper.TABLE_MEDICINES, contentValues, where, null);
        database.close();

        saveMedicineImage(medicine.getImage(), medicine.getId());

        medicinesChanged = true;

        return rowsAffected;
    }


    // Get Medicine
    public Medicine getMedicine(int id) {
        String selection = DatabaseHelper.COLUMN_ID + " = " + id;

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_MEDICINES, MEDICINE_COLUMNS, selection, null, null, null, null);

        Medicine medicine = null;
        if (cursor.moveToFirst()) {
            medicine = getMedicine(cursor);
        }

        cursor.close();
        database.close();

        return medicine;
    }


    // Get All Medicines
    public List<Medicine> getAllMedicines() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_MEDICINES, MEDICINE_COLUMNS, null, null, null, null, null);

        List<Medicine> medicines = new ArrayList<>();
        while (cursor.moveToNext()) {
            medicines.add(getMedicine(cursor));
        }

        cursor.close();
        database.close();

        return medicines;
    }


    // Search Medicines
    public ArrayList<Medicine> searchMedicines(String name) {
        String selection = DatabaseHelper.COLUMN_NAME + " like '%" + name + "%'";

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_MEDICINES, MEDICINE_COLUMNS, selection, null, null, null, null);

        ArrayList<Medicine> medicines = new ArrayList<>();
        while (cursor.moveToNext()) {
            medicines.add(getMedicine(cursor));
        }

        cursor.close();
        database.close();

        return medicines;
    }


    // Remove Medicine
    public long removeMedicine(int id) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String where = DatabaseHelper.COLUMN_ID + " = " + id;
        long rowsAffected = database.delete(DatabaseHelper.TABLE_MEDICINES, where, null);
        database.close();

        removeMedicineImage(id);

        medicinesChanged = true;

        return rowsAffected;
    }


    // Remove Medicine Image
    private void removeMedicineImage(int id) {
        context.deleteFile(Integer.toString(id));
    }


    // Get Content Values
    private ContentValues getContentValues(Medicine medicine) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.COLUMN_NAME, medicine.getName());
        contentValues.put(DatabaseHelper.COLUMN_TYPE, medicine.getType().ordinal());
        contentValues.put(DatabaseHelper.COLUMN_DOSE, medicine.getDose());
        contentValues.put(DatabaseHelper.COLUMN_START_DATE_TIME, medicine.getStartDateTime());
        contentValues.put(DatabaseHelper.COLUMN_END_DATE_TIME, medicine.getEndDateTime());
        contentValues.put(DatabaseHelper.COLUMN_INTERVAL, medicine.getInterval());
        contentValues.put(DatabaseHelper.COLUMN_NOTES, medicine.getNotes());

        return contentValues;
    }


    // Get Medicine
    private Medicine getMedicine(Cursor cursor) {
        Medicine medicine = new Medicine();

        medicine.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        medicine.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
        medicine.setType(Medicine.Type.values()[cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE))]);
        medicine.setDose(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DOSE)));
        medicine.setStartDateTime(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_START_DATE_TIME)));
        medicine.setEndDateTime(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_END_DATE_TIME)));
        medicine.setInterval(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_INTERVAL)));
        medicine.setNotes(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTES)));
        try {
            FileInputStream fileInputStream = context.openFileInput(medicine.getId() + "");
            medicine.setImage(BitmapFactory.decodeStream(fileInputStream));
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return medicine;
    }


    // Save Medicine Image
    private void saveMedicineImage(Bitmap image, int id) {
        if (image != null) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(Integer.toString(id), Context.MODE_PRIVATE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                fileOutputStream.write(stream.toByteArray());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Is Medicine Pending
    public boolean isMedicinePending(int id) {
        String[] columns = {DatabaseHelper.COLUMN_ID};
        String selection = DatabaseHelper.COLUMN_ID + " = " + id;

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_PENDING_MEDICINES, columns, selection, null, null, null, null);

        boolean medicinePending = cursor.moveToFirst();

        cursor.close();
        database.close();

        return medicinePending;
    }


    // Add Pending Medicine
    public long addPendingMedicine(int id) {
        if (isMedicinePending(id)) {
            return -1;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_ID, id);

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long rowID = database.insert(DatabaseHelper.TABLE_PENDING_MEDICINES, null, contentValues);
        database.close();

        pendingMedicinesChanged = true;

        return rowID;
    }


    // Remove Pending Medicine
    public long removePendingMedicine(int id) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        String where = DatabaseHelper.COLUMN_ID + " = " + id;
        long rowsAffected = database.delete(DatabaseHelper.TABLE_PENDING_MEDICINES, where, null);
        database.close();

        pendingMedicinesChanged = true;

        return rowsAffected;
    }


    // Get All Pending Medicines
    public ArrayList<Medicine> getAllPendingMedicines() {
        String[] columns = {DatabaseHelper.COLUMN_ID};

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_PENDING_MEDICINES, columns, null, null, null, null, null);

        ArrayList<Medicine> medicines = new ArrayList<>();
        while (cursor.moveToNext()) {
            medicines.add(getMedicine(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))));
        }

        cursor.close();
        database.close();

        return medicines;
    }

}
