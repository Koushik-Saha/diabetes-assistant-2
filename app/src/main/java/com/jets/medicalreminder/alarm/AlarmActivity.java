package com.jets.medicalreminder.alarm;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.beans.Medicine;
import com.jets.medicalreminder.database.MedicineAdapter;
import com.jets.medicalreminder.util.Constants;
import com.jets.medicalreminder.util.Util;

public class AlarmActivity extends FragmentActivity {

    private static final long DAY_MILLISECONDS = 24 * 60 * 60 * 1000;


    private Medicine medicine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        setMedicine();
        showDialog();
        playSound();

        // If medicine end time is not infinite, and the end time has already passed, then cancel the alarm
        if (medicine.getEndDateTime() != 0 && System.currentTimeMillis() > medicine.getEndDateTime() + DAY_MILLISECONDS) {
            Util.cancelAlarm(this, medicine);
        }
    }


    // Set Medicine
    private void setMedicine() {
        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt(Constants.EXTRA_MEDICINE_ID);
        MedicineAdapter medicineAdapter = MedicineAdapter.getInstance(this);
        medicine = medicineAdapter.getMedicine(id);
    }


    // Show Dialog
    private void showDialog() {
        Bundle arguments = new Bundle();
        arguments.putInt(Constants.EXTRA_MEDICINE_ID, medicine.getId());
        ReminderDialog reminderDialog = new ReminderDialog();
        reminderDialog.setArguments(arguments);
        reminderDialog.setCancelable(false);
        reminderDialog.show(getFragmentManager(), "Reminder Dialog");
    }


    // Play Sound
    private void playSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.start();
    }

}
