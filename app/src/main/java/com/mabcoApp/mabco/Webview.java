package com.mabcoApp.mabco;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class Webview extends Fragment {

    WebView w5;
    String url;
    private Context context;
    private String from;
    ProgressBar progressBar;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 123;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static Webview init(Context context, String link) {
        Webview fragment = new Webview();
        fragment.context = context;
        fragment.url = link;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_webview, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        boolean isDetect = false;
        //
        if (context == null) context = getContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        isDetect = true;
                        break;
                    }
                }
            }
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            WebviewArgs args = WebviewArgs.fromBundle(bundle);
            url = args.getUrl();
        }
        else
            url = "https://hr1.mabcoonline.com";
        Bundle b = getActivity().getIntent().getExtras();

        w5 = (WebView) view.findViewById(R.id.w);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            w5.setNestedScrollingEnabled(true);
        
        w5.getSettings().setJavaScriptEnabled(true);
        w5.getSettings().setLoadWithOverviewMode(true);
        w5.getSettings().setAllowFileAccess(true);
        w5.getSettings().setPluginState(WebSettings.PluginState.ON);
        WebSettings mWebSettings = w5.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(false);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);

        w5.setWebChromeClient(new WebChromeClient() {
            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), 1);
            }


            // For Lollipop 5.0+ Devices
            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(context, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), 1);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), 1);
            }
        });
        w5.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);

                w5.loadUrl("javascript:(function() { " + "var head = document.getElementsByTagName('header')[0];" + "head.parentNode.removeChild(head);" + "})()");
                w5.loadUrl("javascript:(function() { " + "var head = document.getElementsByClassName('header')[0];" + "head.parentNode.removeChild(head);" + "})()");
                w5.loadUrl("javascript:(function() { " + "var head = document.getElementsByClassName('footer')[0];" + "head.parentNode.removeChild(head);" + "})()");
                w5.loadUrl("javascript:(function() { " + "var head = document.getElementsByClassName('pre-footer')[0];" + "head.parentNode.removeChild(head);" + "})()");
                w5.loadUrl("javascript:(function() { " + "var head = document.getElementsByClassName('media-icons')[0];" + "head.parentNode.removeChild(head);" + "})()");
                w5.loadUrl("javascript:(function() { " + "document.getElementsByClassName('clearfix')[0].style.display='none'; })()");
            }
        });
        w5.loadUrl(url);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == 1) {
                if (uploadMessage == null) return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == 1) {
            if (null == mUploadMessage) return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else Toast.makeText(context, "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }
}