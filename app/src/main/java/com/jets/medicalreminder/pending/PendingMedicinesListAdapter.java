package com.jets.medicalreminder.pending;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.beans.Medicine;

public class PendingMedicinesListAdapter extends ArrayAdapter<Medicine> {

    private Context context;
    private Medicine[] medicines;
    private boolean[] checkedItems;


    // Constructor
    public PendingMedicinesListAdapter(Context context, Medicine[] medicines) {
        super(context, android.R.layout.simple_list_item_1, medicines);

        this.context = context;
        this.medicines = medicines;
        checkedItems = new boolean[medicines.length];
    }


    // Item Pressed
    public void itemPressed(int position) {
        checkedItems[position] = !checkedItems[position];
        notifyDataSetChanged();
    }


    // Get Checked Items
    public boolean[] getCheckedItems() {
        return checkedItems;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_pending_medicine, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.imageView_medicine = (ImageView) convertView.findViewById(R.id.imageView_medicine);
            viewHolder.textView_name = (TextView) convertView.findViewById(R.id.textView_name);
            viewHolder.checkBox_taken = (CheckBox) convertView.findViewById(R.id.checkBox_taken);

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

        viewHolder.textView_name.setText(medicine.getName());
        viewHolder.checkBox_taken.setChecked(checkedItems[position]);

        return convertView;
    }


    // View Holder
    private static class ViewHolder {
        private ImageView imageView_medicine;
        private TextView textView_name;
        private CheckBox checkBox_taken;
    }

}
