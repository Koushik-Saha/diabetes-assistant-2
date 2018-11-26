package com.jets.medicalreminder.edit;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.beans.Medicine;
import com.jets.medicalreminder.database.MedicineAdapter;
import com.jets.medicalreminder.util.Constants;
import com.jets.medicalreminder.util.ImagesUtils;
import com.jets.medicalreminder.util.Util;

public class EditMedicineFragment extends Fragment implements OnClickListener {

    private static final int REQUEST_CODE_IMAGE_CAPTURE = 0;

    private static final String IMAGE_FORMAT = "jpg";

    private static final int SCALE_DIVIDE_FACTOR = 4;

    private static final int DATE_TYPE_START = 0;
    private static final int DATE_TYPE_END = 1;

    private static final String TIME_MID_NIGHT = "00:00";


    private ViewGroup layout_captureImage;
    private ViewGroup layout_startDate;
    private ViewGroup layout_endDate;
    private ViewGroup layout_startTime;

    private ImageView imageView_medicinePhoto;
    private EditText editText_medicineName;
    private Spinner spinner_medicineType;
    private Spinner spinner_medicineDose;
    private TextView textView_startDate;
    private TextView textView_endDate;
    private TextView textView_startTime;
    private EditText editText_medicineInterval;
    private Spinner spinner_medicineInterval;
    private EditText editText_notes;

    private Medicine medicine;

    private Bitmap image;
    private String imagePath;

    private boolean editing;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Intent intent = getActivity().getIntent();
        int id = intent.getIntExtra(Constants.EXTRA_MEDICINE_ID, 0);
        if (id != 0) {
            MedicineAdapter medicineAdapter = MedicineAdapter.getInstance(getActivity());
            medicine = medicineAdapter.getMedicine(id);
            editing = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_medicine, container, false);

        initComponents(rootView);

        if (medicine == null) {
            medicine = new Medicine();
        } else {
            image = medicine.getImage();
            setFields();
        }

        if (imagePath == null) {
            setStorageFile();
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        saveMedicineState();

        super.onDestroyView();
    }


    // Initialize Components
    private void initComponents(View view) {
        layout_captureImage = (ViewGroup) view.findViewById(R.id.relativeLayout_captureImage);
        layout_startDate = (ViewGroup) view.findViewById(R.id.linearLayout_startDate);
        layout_endDate = (ViewGroup) view.findViewById(R.id.linearLayout_endDate);
        layout_startTime = (ViewGroup) view.findViewById(R.id.linearLayout_startTime);
        imageView_medicinePhoto = (ImageView) view.findViewById(R.id.imageView_medicinePhoto);
        editText_medicineName = (EditText) view.findViewById(R.id.editText_medicineName);
        spinner_medicineType = (Spinner) view.findViewById(R.id.spinner_medicineType);
        spinner_medicineDose = (Spinner) view.findViewById(R.id.spinner_medicineDose);
        textView_startDate = (TextView) view.findViewById(R.id.textView_startDate);
        textView_endDate = (TextView) view.findViewById(R.id.textView_endDate);
        textView_startTime = (TextView) view.findViewById(R.id.textView_startTime);
        editText_medicineInterval = (EditText) view.findViewById(R.id.editText_medicineInterval);
        spinner_medicineInterval = (Spinner) view.findViewById(R.id.spinner_medicineInterval);
        editText_notes = (EditText) view.findViewById(R.id.editText_medicineNotes);

        layout_captureImage.setOnClickListener(this);
        layout_startDate.setOnClickListener(this);
        layout_endDate.setOnClickListener(this);
        layout_startTime.setOnClickListener(this);

        editText_medicineName.setFilters(new InputFilter[]{new NameFilter()});

        // Image
        if (image == null) {
            imageView_medicinePhoto.setImageResource(R.drawable.default_medicine);
        } else {
            imageView_medicinePhoto.setImageBitmap(image);
        }

        // Types Spinner
        TypesSpinnerAdapter typesSpinnerAdapter = new TypesSpinnerAdapter(getActivity());
        spinner_medicineType.setAdapter(typesSpinnerAdapter);

        // Doses Spinner
        String[] doses = new String[Medicine.DOSE_MAX_VALUE];
        for (int i = 0; i < doses.length; i++) {
            doses[i] = (i + 1) + "";
        }
        SpinnerAdapter spinnerAdapter_doses = new SpinnerAdapter(getActivity(), doses);
        spinner_medicineDose.setAdapter(spinnerAdapter_doses);

        // Interval Spinner
        Util.Interval[] intervals = Util.Interval.values();
        String[] intervalsStrings = new String[intervals.length];
        for (int i = 0; i < intervals.length; i++) {
            intervalsStrings[i] = intervals[i].toString();
        }
        SpinnerAdapter spinnerAdapter_intervals = new SpinnerAdapter(getActivity(), intervalsStrings);
        spinner_medicineInterval.setAdapter(spinnerAdapter_intervals);
        spinner_medicineInterval.setSelection(Util.Interval.HOURS.ordinal());
    }


    // Set Fields
    private void setFields() {
        editText_medicineName.setText(medicine.getName());
        spinner_medicineType.setSelection(medicine.getType().ordinal());
        spinner_medicineDose.setSelection(medicine.getDose() - 1);
        editText_notes.setText(medicine.getNotes());

        // Set Interval
        long milliseconds = medicine.getInterval();
        for (Util.Interval interval : Util.Interval.values()) {
            boolean larger = milliseconds / interval.milliSeconds != 0;
            boolean divisible = milliseconds % interval.milliSeconds == 0;
            if (larger && divisible) {
                editText_medicineInterval.setText(String.valueOf(milliseconds / interval.milliSeconds));
                spinner_medicineInterval.setSelection(interval.ordinal());
                break;
            }
        }

        // Set Start Date & Time
        long startDateTime = medicine.getStartDateTime();
        if (startDateTime != 0) {
            textView_startDate.setText(Util.toDateString(startDateTime, Constants.DATE_FORMAT));
            textView_startTime.setText(Util.toDateString(startDateTime, Constants.TIME_FORMAT));
        }

        // Set End Date
        long endDateTime = medicine.getEndDateTime();
        if (endDateTime != 0) {
            textView_endDate.setText(Util.toDateString(endDateTime, Constants.DATE_FORMAT));
        }
    }


    // Check Fields
    private boolean checkFields() {
        boolean fieldsSet = true;
        String message = null;

        if (editText_medicineName.getText().toString().isEmpty()) {
            message = "Set medicine name";
            fieldsSet = false;
        } else if (textView_startDate.getText().toString().isEmpty()) {
            message = "Set start date";
            fieldsSet = false;
        } else if (textView_startTime.getText().toString().isEmpty()) {
            message = "Set start time";
            fieldsSet = false;
        } else if (editText_medicineInterval.getText().toString().isEmpty()) {
            message = "Set interval";
            fieldsSet = false;
        } else {
            try {
                int interval = Integer.parseInt(editText_medicineInterval.getText().toString());
                if (interval == 0) {
                    message = "Can't set interval to zero";
                    fieldsSet = false;
                }
            } catch (NumberFormatException ex) {
                message = "Invalid interval";
                fieldsSet = false;
            }
        }

        if (!fieldsSet) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        return fieldsSet;
    }


    // Set Storage File
    private void setStorageFile() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDirectory.mkdir();

        imagePath = storageDirectory + getString(R.string.app_name) + "." + IMAGE_FORMAT;
    }


    // Capture Photo
    private void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imagePath)));
            startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
        }
    }


    // Scale & Set Image
    private void scaleAndSetImage() {
        image = ImagesUtils.scaleImage(image, imageView_medicinePhoto);
        imageView_medicinePhoto.setImageBitmap(image);
    }


    // Set Image
    private void setImage(String path) {
        try {
            image = ImagesUtils.getAdjustedImage(path, SCALE_DIVIDE_FACTOR);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int imageViewWidth = imageView_medicinePhoto.getWidth();
        int imageViewHeight = imageView_medicinePhoto.getHeight();

        if (imageViewWidth == 0 || imageViewHeight == 0) {
            ViewTreeObserver observer = imageView_medicinePhoto.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    scaleAndSetImage();

                    imageView_medicinePhoto.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        } else {
            scaleAndSetImage();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {
                setImage(imagePath);
                new File(imagePath).delete();
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view == layout_captureImage) {
            if (imagePath != null) {
                capturePhoto();
            } else {
                Toast.makeText(getActivity(), "Can't access media directory.", Toast.LENGTH_SHORT).show();
            }
        } else if (view == layout_startDate) {
            showDateDialog(DATE_TYPE_START);
        } else if (view == layout_endDate) {
            showDateDialog(DATE_TYPE_END);
        } else if (view == layout_startTime) {
            showTimeDialog();
        }
    }


    // Show date dialog (start or end date)
    private void showDateDialog(int type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        TextView textView_date = type == DATE_TYPE_START ? textView_startDate : textView_endDate;

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(textView_date.getText().toString()));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (type == DATE_TYPE_START) {
            DatePickerDialog datePickerDialog_start = new DatePickerDialog(getActivity(),
                    new DateHandler(DATE_TYPE_START), year, month, day);
            datePickerDialog_start.show();
        } else if (type == DATE_TYPE_END) {
            DatePickerDialog datePickerDialog_end = new DatePickerDialog(getActivity(),
                    new DateHandler(DATE_TYPE_END), year, month, day);
            datePickerDialog_end.show();
        }
    }


    // Show time dialog (start time)
    private void showTimeDialog() {
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault());
            calendar.setTime(dateFormat.parse(textView_startDate.getText().toString()));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog_startTime = new TimePickerDialog(getActivity(),
                new TimeHandler(layout_startTime), hour, minute, true);
        timePickerDialog_startTime.show();
    }


    // Save Medicine State
    private void saveMedicineState() {
        medicine.setName(editText_medicineName.getText().toString());
        medicine.setType(Medicine.Type.values()[spinner_medicineType.getSelectedItemPosition()]);
        medicine.setDose(spinner_medicineDose.getSelectedItemPosition() + 1);
        medicine.setNotes(editText_notes.getText().toString());

        String intervalString = editText_medicineInterval.getText().toString();
        if (!intervalString.isEmpty()) {
            int interval = Integer.parseInt(intervalString);
            long interval_milliSeconds =
                    interval * Util.Interval.values()[spinner_medicineInterval.getSelectedItemPosition()].milliSeconds;
            medicine.setInterval(interval_milliSeconds);
        }

        // Set Start Date & Time
        try {
            String startDate = textView_startDate.getText().toString();
            String startTime = textView_startTime.getText().toString();
            if (startTime.isEmpty()) {
                startTime = TIME_MID_NIGHT;
            }
            medicine.setStartDateTime(Util.toMilliseconds(startDate + " " + startTime, Constants.DATE_TIME_FORMAT));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        // Set End Date
        try {
            String endDate = textView_endDate.getText().toString();
            medicine.setEndDateTime(Util.toMilliseconds(endDate, Constants.DATE_FORMAT));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        medicine.setImage(image);
    }


    // Save Medicine
    public boolean saveMedicine() {
        if (!checkFields()) {
            return false;
        }

        saveMedicineState();

        MedicineAdapter medicineAdapter = MedicineAdapter.getInstance(getActivity());
        if (medicine.getId() == 0) {
            medicineAdapter.addMedicine(medicine);
        } else {
            medicineAdapter.updateMedicine(medicine);
        }

        if (editing) {
            Util.cancelAlarm(getActivity(), medicine);
        }

        Util.setAlarm(getActivity(), medicine);

        return true;
    }


    // Date Handler (call back from date dialog)
    private class DateHandler implements OnDateSetListener {
        private int type;

        public DateHandler(int type) {
            this.type = type;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar_start = Calendar.getInstance();
            Calendar calendar_end = Calendar.getInstance();
            boolean start = false;
            boolean end = false;

            // Check for both start & end date
            try {
                // Start Date
                SimpleDateFormat dateFormat_start = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
                calendar_start.setTime(dateFormat_start.parse(textView_startDate.getText().toString()));
                start = true;

                // End Date
                SimpleDateFormat dateFormat_end = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
                calendar_end.setTime(dateFormat_end.parse(textView_endDate.getText().toString()));
                end = true;
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);

            if (type == DATE_TYPE_START) {
                // setStartDate(calendar);
                if (end && calendar.getTimeInMillis() > calendar_end.getTimeInMillis()) {
                    textView_startDate.setText(Util.toDateString(calendar_end.getTimeInMillis(), Constants.DATE_FORMAT));
                } else {
                    textView_startDate.setText(Util.toDateString(calendar.getTimeInMillis(), Constants.DATE_FORMAT));
                }
            } else if (type == DATE_TYPE_END) {
                // setEndDate(calendar);
                if (start && calendar.getTimeInMillis() < calendar_start.getTimeInMillis()
                        || calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    textView_endDate.setText(Util.toDateString(calendar_start.getTimeInMillis(), Constants.DATE_FORMAT));
                } else {
                    textView_endDate.setText(Util.toDateString(calendar.getTimeInMillis(), Constants.DATE_FORMAT));
                }
            }
        }
    }


    // Time Handler (call back from time dialog)
    private class TimeHandler implements OnTimeSetListener {
        private View view;

        public TimeHandler(View view) {
            this.view = view;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            if (this.view == layout_startTime) {
                textView_startTime.setText(Util.toDateString(calendar.getTimeInMillis(), Constants.TIME_FORMAT));
            }
        }
    }


    // Name Filter
    private class NameFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.toString(source.charAt(i)).equals(" ")) {
                    return "";
                }
            }

            return null;
        }
    }

}
