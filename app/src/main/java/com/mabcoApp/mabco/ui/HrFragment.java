package com.mabcoApp.mabco.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;

import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.Webview;

public class HrFragment extends Fragment {
    NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hr, container, false);

        // Nest the WebViewFragment inside HrFragment
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
       String url = "https://hr1.mabcoonline.com";
        Webview webViewFragment = new Webview(url);
        transaction.replace(R.id.fragment_container, webViewFragment).commit();

        return view;

    }
}