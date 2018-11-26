package com.jets.medicalreminder.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.beans.Medicine;

public class TypesSpinnerAdapter extends ArrayAdapter<Medicine.Type> {

    private Context context;


    // Constructor
    public TypesSpinnerAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1, android.R.id.text1, Medicine.Type.values());
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    // Get Custom View
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cell_type, parent, false);

        ImageView imageView_type = (ImageView) view.findViewById(R.id.imageView_type);
        TextView textView_label = (TextView) view.findViewById(R.id.textView_label);

        Medicine.Type type = Medicine.Type.values()[position];
        imageView_type.setImageResource(type.getImageResource());
        textView_label.setText(type.toString());

        return view;
    }

}
