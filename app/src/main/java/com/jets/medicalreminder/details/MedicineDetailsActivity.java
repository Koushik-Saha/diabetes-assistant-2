package com.jets.medicalreminder.details;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.edit.EditMedicineActivity;
import com.jets.medicalreminder.util.Constants;

public class MedicineDetailsActivity extends ActionBarActivity implements OnClickListener {

    private static final int REQUEST_CODE_MEDICINE_EDIT = 0;


    private MedicineDetailsFragment fragment_MedicineDetails;

    private Button button_ok;
    private Button button_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);

        initViews();
    }


    // Initialize Views
    private void initViews() {
        FragmentManager fragmentManager = getFragmentManager();
        fragment_MedicineDetails = (MedicineDetailsFragment) fragmentManager.findFragmentById(R.id.fragment_medicineDetails);

        button_ok = (Button) findViewById(R.id.button_ok);
        button_edit = (Button) findViewById(R.id.button_edit);

        button_ok.setOnClickListener(this);
        button_edit.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == button_ok) {
            finish();
        } else if (view == button_edit) {
            Intent intent = new Intent(this, EditMedicineActivity.class);
            intent.putExtra(Constants.EXTRA_MEDICINE_ID, fragment_MedicineDetails.getMedicine().getId());
            startActivityForResult(intent, REQUEST_CODE_MEDICINE_EDIT);
        }
    }

}
