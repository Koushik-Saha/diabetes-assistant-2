package com.jets.medicalreminder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jets.medicalreminder.beans.Medicine;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = " Users ";

    private static final String CREATE_TABLE = "CREATE TABLE ";

    private static final String INTEGER = " INTEGER ";
    private static final String VARCHAR = " VARCHAR";

    private static final String PRIMARY_KEY = " PRIMARY KEY ";
    private static final String NOT_NULL = " NOT NULL ";
    private static final String AUTO_INCREMENT = " AUTOINCREMENT ";
    private static final String CHECK = " CHECK ";

    static final String TABLE_MEDICINES = "medicines";
    static final String TABLE_PENDING_MEDICINES = "pendingMedicines";

    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_TYPE = "type";
    static final String COLUMN_DOSE = "dose";
    static final String COLUMN_START_DATE_TIME = "startDateTime";
    static final String COLUMN_END_DATE_TIME = "endDateTime";
    static final String COLUMN_INTERVAL = "interval";
    static final String COLUMN_NOTES = "notes";


    private static final String CREATE_MEDICINES_TABLE = CREATE_TABLE + TABLE_MEDICINES
            + "(" + COLUMN_ID + INTEGER + PRIMARY_KEY + AUTO_INCREMENT + ", "
            + COLUMN_NAME + VARCHAR + "(30) " + NOT_NULL + ", " +
            COLUMN_TYPE + INTEGER + CHECK
            + "(" + COLUMN_TYPE + " >= 0 AND " + COLUMN_TYPE + " < " + Medicine.Type.values().length + "), "
            + COLUMN_DOSE + INTEGER + CHECK
            + "(" + COLUMN_DOSE + " > 0 AND " + COLUMN_DOSE + " <= " + Medicine.DOSE_MAX_VALUE + "), "
            + COLUMN_START_DATE_TIME + INTEGER + NOT_NULL + ", "
            + COLUMN_END_DATE_TIME + INTEGER + ", "
            + COLUMN_INTERVAL + INTEGER + NOT_NULL + ", "
            + COLUMN_NOTES + VARCHAR + "(255)" + ");";


    private static final String CREATE_PENDING_MEDICINES_TABLE = CREATE_TABLE + TABLE_PENDING_MEDICINES + "("
            + COLUMN_ID + INTEGER + PRIMARY_KEY + ");";


    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_MEDICINES_TABLE);
        database.execSQL(CREATE_PENDING_MEDICINES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

}
