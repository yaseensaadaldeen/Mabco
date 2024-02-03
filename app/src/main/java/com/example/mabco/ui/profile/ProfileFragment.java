package com.example.mabco.ui.profile;

import static androidx.core.app.ActivityCompat.recreate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mabco.MainActivity;
import com.example.mabco.R;
import com.example.mabco.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    RelativeLayout Language_Area;
    Context context;
    private boolean isInitialization = true;
    SharedPreferences PersonalPreference;
    Spinner Language_spinner;
    private Boolean spinnerTouched = false;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        context = getContext();
        Language_Area = binding.LanguageArea;
        Language_spinner = binding.LanguageSpinner;
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.english_item));
        list.add(getString(R.string.arabic_item));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item,list);
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        Language_spinner.setAdapter(adapter);
        Locale currentLocale = getResources().getConfiguration().locale;
        String languageCode = currentLocale.getLanguage();
        Language_spinner.setSelection(languageCode.equals("ar") ?1 :0, false);
        Language_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("Real touch felt.");
                spinnerTouched = true;
                return false;
            }
        });
        Language_Area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Programmatically perform a click on the Spinner
                Language_spinner.performClick();
                spinnerTouched = true;

            }
        });

        Language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinnerTouched){
                    String selectedLanguage = parentView.getItemAtPosition(position).toString();
                    SharedPreferences.Editor editor = PersonalPreference.edit();

                    switch (selectedLanguage) {
                        case "English":
                        case "الانكليزية":
                            setLocale("en");
                            editor.putString("Language", "en");
                            editor.apply();
                            break;
                        case "العربية":
                        case "Arabic":
                            setLocale("ar");
                            editor.putString("Language", "ar");
                            editor.apply();
                            break;
                        // Add more languages if needed
                    }
                }
                spinnerTouched  = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        View root = binding.getRoot();

        return root;
    }

    private void setLocale(String languageCode) {
        Resources standardResources = getResources();
        AssetManager assets = standardResources.getAssets();
        DisplayMetrics metrics = standardResources.getDisplayMetrics();
        Configuration config = new Configuration(standardResources.getConfiguration());

        Locale newLocale = new Locale(languageCode);
        config.setLocale(newLocale);
        config.setLayoutDirection(newLocale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Restart the activity to apply changes
        recreate(getActivity());
        restartApp() ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private void restartApp() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }
}