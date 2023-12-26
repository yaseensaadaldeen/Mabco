package com.example.mabco;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {
    Context context;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        private static final int DIALOG_REQUEST = 9001;

        public GoogleMap mMap;

        @Override
        public void onMapReady(GoogleMap map) {

            mMap = map;

            if (servicesOK() /*&& initMap()*/) {

                //Geocoder gc = new Geocoder(this);
                //List<Address> list;
                try {
                    //list = gc.getFromLocationName(hotel.getAddress(), 1);
                    //Address address = list.get(0);

                    double lat = 0.6856979;
                    double lng = 16.8795434;
                    LatLng latLong = new LatLng(lat, lng);
//                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLong, 5);
                    Log.i("BEGINNING", "Check this");
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
                    Log.i("Finishing", "Check this");

                /*MarkerOptions options = new MarkerOptions()
                        .title(getString(R.string.landon_hotel) + ", " + city)
                        .position(new LatLng(lat, lng));
                mMap.addMarker(options);*/
                    //onMapReady(mMap);

                } /*catch (IOException e) {
                Toast.makeText(this, getString(R.string.error_finding_hotel), Toast.LENGTH_SHORT).show();
            }*/ catch (Exception e) {
                    // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Check this->", e.getMessage());
                }


            }
        }
    };

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_maps, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(callback);
            }
            context = getContext();
        }

        public boolean servicesOK() {
            int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

            if (result == ConnectionResult.SUCCESS) {
                return true;
            } else if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
//                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(result, getActivity(), DIALOG_REQUEST);
//                dialog.show();
            } else {
                //Toast.makeText(this, getString(R.string.error_connect_to_services), Toast.LENGTH_LONG).show();
            }
            return false;
        }

    }
