package com.mabcoApp.mabco.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.Webview;

public class SavingListFragment extends Fragment {
    NavController navController;
    String local;
    SharedPreferences PersonalPreference;
    Context context;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saving_list, container, false);
        navController = NavHostFragment.findNavController(this);
        context = this.getContext();
        // Nest the WebViewFragment inside HrFragment
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        local = PersonalPreference.getString("Language", "ar");

        String url = local.equals("ar") ? "https://www.mabcoonline.com/ar/SavingsLists.aspx" : "https://www.mabcoonline.com/SavingsLists.aspx";

        Webview webViewFragment = new Webview( url);
        transaction.replace(R.id.fragment_container, webViewFragment).commit();
        return view;
    }
}