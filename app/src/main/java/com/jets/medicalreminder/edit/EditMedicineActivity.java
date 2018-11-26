package com.jets.medicalreminder.edit;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.util.Constants;

public class EditMedicineActivity extends ActionBarActivity implements OnClickListener {

    private EditMedicineFragment fragment_EditMedicine;

    private Button button_save;
    private Button button_cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicine);

        initViews();
    }


    // Initialize Views
    private void initViews() {
        FragmentManager fragmentManager = getFragmentManager();
        fragment_EditMedicine = (EditMedicineFragment) fragmentManager.findFragmentById(R.id.fragment_editMedicine);

        button_save = (Button) findViewById(R.id.button_save);
        button_cancel = (Button) findViewById(R.id.button_cancel);

        button_save.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == button_save) {
            if (fragment_EditMedicine.saveMedicine()) {
                /*
                 * If the medicine has changed, inform the calling activity to
				 * update its view
				 */
                Intent intent = getIntent();
                intent.putExtra(Constants.EXTRA_CHANGED, true);
                setResult(Activity.RESULT_OK, intent);

                finish();
            }
        } else if (view == button_cancel) {
            finish();
        }
    }

}
