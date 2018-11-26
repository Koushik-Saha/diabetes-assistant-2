package com.jets.medicalreminder.pending;

import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.beans.Medicine;
import com.jets.medicalreminder.database.MedicineAdapter;
import com.jets.medicalreminder.util.Constants;

public class PendingMedicinesFragment extends Fragment implements OnClickListener, OnItemClickListener {

    private Button button_take;
    private Button button_cancel;

    private ListView listview;
    private PendingMedicinesListAdapter listAdapter;
    private List<Medicine> medicines;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending_medicines, container, false);

        initViews(rootView);

        MedicineAdapter medicineAdapter = MedicineAdapter.getInstance(getActivity());
        if (medicines == null || medicineAdapter.isPendingMedicinesChanged()) {
            refreshList();
        } else {
            listview.setAdapter(listAdapter);
        }

        return rootView;
    }


    // Initialize Views
    private void initViews(View view) {
        button_take = (Button) view.findViewById(R.id.button_take);
        button_cancel = (Button) view.findViewById(R.id.button_cancel);

        listview = (ListView) view.findViewById(R.id.listView_pendingMedicines);
        listview.setOnItemClickListener(this);

        button_take.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
    }


    // Refresh List
    private void refreshList() {
        MedicineAdapter medicineAdapter = MedicineAdapter.getInstance(getActivity());
        medicines = medicineAdapter.getAllPendingMedicines();

        if (medicines != null && !medicines.isEmpty()) {
            Collections.sort(medicines);
            listAdapter = new PendingMedicinesListAdapter(getActivity(), medicines.toArray(new Medicine[medicines.size()]));
            listview.setAdapter(listAdapter);
        } else {
            cancelNotification();
            getActivity().finish();
        }

        medicineAdapter.setPendingMedicinesChanged(false);
    }


    // Cancel Notification
    private void cancelNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICATION_ID);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        listAdapter.itemPressed(position);
    }


    // Take Medicines
    private boolean takeMedicines() {
        boolean allChecked = true;
        boolean noneChecked = true;
        boolean[] checkedItems = listAdapter.getCheckedItems();

        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                MedicineAdapter medicineAdapter = MedicineAdapter.getInstance(getActivity());
                medicineAdapter.removePendingMedicine(medicines.get(i).getId());
                noneChecked = false;
            } else {
                allChecked = false;
            }
        }

        if (allChecked) {
            cancelNotification();
        }

        return !noneChecked;
    }


    @Override
    public void onClick(View view) {
        if (view == button_take) {
            if (takeMedicines()) {
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "No medicines selected.", Toast.LENGTH_SHORT).show();
            }
        } else if (view == button_cancel) {
            getActivity().finish();
        }
    }

}
