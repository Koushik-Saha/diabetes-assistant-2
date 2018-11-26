package com.jets.medicalreminder.medicines;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.beans.Medicine;
import com.jets.medicalreminder.util.Constants;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MedicinesListAdapter extends ArrayAdapter<Medicine> {

    private Context context;
    private Medicine[] medicines;


    // Constructor
    public MedicinesListAdapter(Context context, Medicine[] medicines) {
        super(context, android.R.layout.simple_list_item_1, medicines);

        this.context = context;
        this.medicines = medicines;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_medicine, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.imageView_medicine = (ImageView) convertView.findViewById(R.id.imageView_medicine);
            viewHolder.imageView_type = (ImageView) convertView.findViewById(R.id.imageView_type);
            viewHolder.textView_name = (TextView) convertView.findViewById(R.id.textView_name);
            viewHolder.textView_dose = (TextView) convertView.findViewById(R.id.textView_dose);
            viewHolder.textView_startDate = (TextView) convertView.findViewById(R.id.textView_startDate);
            viewHolder.textView_endDate = (TextView) convertView.findViewById(R.id.textView_endDate);

            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        Medicine medicine = medicines[position];

        Bitmap image = medicine.getImage();
        if (image != null) {
            viewHolder.imageView_medicine.setImageBitmap(image);
        } else {
            viewHolder.imageView_medicine.setImageResource(R.drawable.default_medicine);
        }

        viewHolder.imageView_type.setImageResource(medicine.getType().getImageResource());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);

        viewHolder.textView_name.setText(medicine.getName());
        viewHolder.textView_dose.setText(String.valueOf(medicine.getDose()));
        viewHolder.textView_startDate.setText(simpleDateFormat.format(medicine.getStartDateTime()));
        viewHolder.textView_endDate.setText(simpleDateFormat.format(medicine.getEndDateTime()));

        return convertView;
    }


    // View Holder
    private static class ViewHolder {
        private ImageView imageView_medicine;
        private ImageView imageView_type;
        private TextView textView_name;
        private TextView textView_dose;
        private TextView textView_startDate;
        private TextView textView_endDate;
    }

}
