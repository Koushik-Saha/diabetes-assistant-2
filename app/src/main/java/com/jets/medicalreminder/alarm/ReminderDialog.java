package com.jets.medicalreminder.alarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.beans.Medicine;
import com.jets.medicalreminder.database.MedicineAdapter;
import com.jets.medicalreminder.pending.PendingMedicinesActivity;
import com.jets.medicalreminder.util.Constants;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReminderDialog extends DialogFragment implements OnClickListener {

    private ImageView imageView_medicine;
    private ImageView imageView_type;
    private TextView textView_name;
    private TextView textView_dose;
    private TextView textView_startDate;
    private TextView textView_endDate;

    private Medicine medicine;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        int medicineID = arguments.getInt(Constants.EXTRA_MEDICINE_ID);
        MedicineAdapter medicineAdapter = MedicineAdapter.getInstance(getActivity());
        medicine = medicineAdapter.getMedicine(medicineID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.app_name));
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.dialog_reminder, null, false));

        builder.setPositiveButton("Take", this);
        builder.setNegativeButton("Decline", this);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        setRetainInstance(true);

        initComponents(alertDialog);
        setFields();

        return alertDialog;
    }


    // Initialize Components
    private void initComponents(AlertDialog alertDialog) {
        imageView_medicine = (ImageView) alertDialog.findViewById(R.id.imageView_medicine);
        imageView_type = (ImageView) alertDialog.findViewById(R.id.imageView_type);
        textView_name = (TextView) alertDialog.findViewById(R.id.textView_name);
        textView_dose = (TextView) alertDialog.findViewById(R.id.textView_dose);
        textView_startDate = (TextView) alertDialog.findViewById(R.id.textView_startDate);
        textView_endDate = (TextView) alertDialog.findViewById(R.id.textView_endDate);
    }


    // Set Fields
    private void setFields() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);

        Bitmap image = medicine.getImage();
        if (image != null) {
            imageView_medicine.setImageBitmap(image);
        } else {
            imageView_medicine.setImageResource(R.drawable.default_medicine);
        }

        imageView_type.setImageResource(medicine.getType().getImageResource());

        textView_name.setText(medicine.getName());
        textView_dose.setText(String.valueOf(medicine.getDose()));
        textView_startDate.setText(simpleDateFormat.format(medicine.getStartDateTime()));
        textView_endDate.setText(simpleDateFormat.format(medicine.getEndDateTime()));
    }


    // Display Notification
    private void displayNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setContentTitle("Medical Reminder");
        builder.setContentText("You've received new message.");
        builder.setTicker("Medical Reminder!");
        builder.setSmallIcon(R.drawable.default_medicine);
        // builder.setNumber(++messagesNumber);

        Intent intent = new Intent(getActivity(), PendingMedicinesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), medicine.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        MedicineAdapter medicineAdapter = MedicineAdapter.getInstance(getActivity());

        if (which == AlertDialog.BUTTON_POSITIVE) {
            medicineAdapter.removePendingMedicine(medicine.getId());
        } else if (which == AlertDialog.BUTTON_NEGATIVE) {
            medicineAdapter.addPendingMedicine(medicine.getId());
            displayNotification();
        }

        getActivity().finish();
    }

}
