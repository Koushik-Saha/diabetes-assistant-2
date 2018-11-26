package com.jets.medicalreminder.medicines;

import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.beans.Medicine;
import com.jets.medicalreminder.database.MedicineAdapter;
import com.jets.medicalreminder.edit.EditMedicineActivity;
import com.jets.medicalreminder.details.MedicineDetailsActivity;
import com.jets.medicalreminder.util.Constants;
import com.jets.medicalreminder.util.Util;

public class AllMedicinesFragment extends Fragment implements OnClickListener, OnItemClickListener {

    private Button button_addMedicine;

    private ViewGroup layout_empty;

    private ListView listView_medicines;
    private MedicinesListAdapter listAdapter;
    private List<Medicine> medicines;
    private Medicine selectedMedicine;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_medicines, container, false);
        initComponents(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        MedicineAdapter medicinesAdapter = MedicineAdapter.getInstance(getActivity());

        if (medicines == null || medicinesAdapter.isMedicinesChanged()) {
            medicines = medicinesAdapter.getAllMedicines();
            refreshMedicines();
        } else {
            listView_medicines.setAdapter(listAdapter);
        }

        checkEmpty();
    }


    // Initialize Components
    private void initComponents(View view) {
        layout_empty = (ViewGroup) view.findViewById(R.id.linearLayout_empty);
        listView_medicines = (ListView) view.findViewById(R.id.listView_medicines);
        button_addMedicine = (Button) view.findViewById(R.id.button_addMedicine);

        listView_medicines.setOnItemClickListener(this);
        registerForContextMenu(listView_medicines);

        button_addMedicine.setOnClickListener(this);
    }


    // Refresh Medicines
    public void refreshMedicines() {
        Collections.sort(medicines);
        Medicine[] medicinesArray = medicines.toArray(new Medicine[medicines.size()]);
        listAdapter = new MedicinesListAdapter(getActivity(), medicinesArray);
        listView_medicines.setAdapter(listAdapter);

        MedicineAdapter medicinesAdapter = MedicineAdapter.getInstance(getActivity());
        medicinesAdapter.setMedicinesChanged(false);
    }


    // Check Empty
    private void checkEmpty() {
        boolean noMedicines = medicines == null || medicines.isEmpty();

        listView_medicines.setVisibility(noMedicines ? View.GONE : View.VISIBLE);
        layout_empty.setVisibility(noMedicines ? View.VISIBLE : View.GONE);
    }


    // Show Delete Dialog
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure you want to delete medicine \"" + selectedMedicine.getName() + "\" ?");

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSelectedMedicine();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        builder.show();
    }


    // Delete Selected Medicine
    private void deleteSelectedMedicine() {
        medicines.remove(selectedMedicine);
        MedicineAdapter medicinesAdapter = MedicineAdapter.getInstance(getActivity());
        medicinesAdapter.removeMedicine(selectedMedicine.getId());
        medicinesAdapter.removePendingMedicine(selectedMedicine.getId());
        refreshMedicines();
        checkEmpty();

        Util.cancelAlarm(getActivity(), selectedMedicine);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), MedicineDetailsActivity.class);
        intent.putExtra(Constants.EXTRA_MEDICINE_ID, medicines.get(position).getId());
        startActivity(intent);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        if (view == listView_medicines) {
            getActivity().getMenuInflater().inflate(R.menu.context_menu_medicine, menu);

            AdapterContextMenuInfo contextMenuInfo = (AdapterContextMenuInfo) menuInfo;
            selectedMedicine = (Medicine) listView_medicines.getItemAtPosition(contextMenuInfo.position);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.edit))) {
            Intent intent = new Intent(getActivity(), EditMedicineActivity.class);
            intent.putExtra(Constants.EXTRA_MEDICINE_ID, selectedMedicine.getId());
            startActivity(intent);
        } else if (item.getTitle().equals(getString(R.string.delete))) {
            showDeleteDialog();
        }

        return super.onContextItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        if (view == button_addMedicine) {
            Intent intent = new Intent(getActivity(), EditMedicineActivity.class);
            startActivity(intent);
        }
    }

}