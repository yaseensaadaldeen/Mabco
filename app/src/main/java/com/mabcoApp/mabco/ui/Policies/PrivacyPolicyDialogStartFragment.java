package com.mabcoApp.mabco.ui.Policies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;

import com.mabcoApp.mabco.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PrivacyPolicyDialogStartFragment extends BottomSheetDialogFragment {

    private static final String ARG_ITEM_COUNT = "item_count";/*
    private Listener mListener;*/
    CheckBox checkbox;
    Button txt;
    Fragment me;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy_policy_dialog_start, container, false);
        checkbox = view.findViewById(R.id.checkbox);
        txt = view.findViewById(R.id.btn);
        me = this;
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox.isChecked()) {
                    txt.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}