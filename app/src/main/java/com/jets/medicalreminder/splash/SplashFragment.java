package com.jets.medicalreminder.splash;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jets.medicalreminder.R;
import com.jets.medicalreminder.medicines.AllMedicinesActivity;

public class SplashFragment extends Fragment {

    private static final int SLEEP_TIME = 1000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Thread initializerThread = new Thread(initializer);
        initializerThread.start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }


    // Initializer
    private Runnable initializer = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getActivity(), AllMedicinesActivity.class));
                    getActivity().finish();
                }
            });
        }
    };

}
