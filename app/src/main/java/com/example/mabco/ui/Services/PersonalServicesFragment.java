package com.example.mabco.ui.Services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mabco.R;
public class PersonalServicesFragment extends Fragment {
    public static PersonalServicesFragment newInstance(String param1, String param2) {
        PersonalServicesFragment fragment = new PersonalServicesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_services, container, false);
    }
}