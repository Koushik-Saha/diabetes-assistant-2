package com.jets.medicalreminder.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jets.medicalreminder.R;

public class SpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] values;


    // Constructor
    public SpinnerAdapter(Context context, String[] values) {
        super(context, android.R.layout.simple_list_item_1, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_details_spinner, parent, false);

        TextView textView_text = (TextView) view.findViewById(R.id.textView_text);
        textView_text.setText(values[position].toString());

        return view;
    }

}
