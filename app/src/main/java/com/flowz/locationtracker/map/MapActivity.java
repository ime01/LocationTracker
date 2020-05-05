package com.flowz.locationtracker.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.flowz.locationtracker.MainActivity;
import com.flowz.locationtracker.R;
import com.flowz.locationtracker.room.MyPlace;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.List;

//import static com.flowz.locationtracker.MainActivity.MAPVIEW_BUNDLE_KEY;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mymap);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        List<MyPlace> locations = MainActivity.myAppDataBase.myDAO().getPlaces();

        Double startLA  = locations.get(0).getStartLatitude();
        Double startLO  = locations.get(0).getStartLongitude();

        Double stopLA  = locations.get(1).getStopLatitude();
        Double stopLO  = locations.get(1).getStopLongitude();

        String userLocations = String.valueOf("Start Location :" + startLA + " " + startLO  + " StopLocation " + stopLA + " " + startLO);



        Toast.makeText(MapActivity.this, userLocations, Toast.LENGTH_LONG).show();


//        for (MyPlace place: locations){
//
//            int id = 1;
//
//            Double startLat = place.getStartLatitude();
//            Double startLong = place.getStartLongitude();
//
//            startLA = startLat;
//            startLO = startLong;
//        }
//

        //LatLng StartLocation = new LatLng(50.0379, 8.5622);
        //LatLng JFKairport1 = new LatLng(40.6435529, -73.78211390000001);

        LatLng StartLocation = new LatLng(startLA, startLO);
        LatLng StopLocation =  new LatLng(stopLA, stopLO);

//        StartLocation.distanceTo(StopLocation);

        Double distance =  SphericalUtil.computeDistanceBetween(StartLocation, StopLocation);

        String showDistance = "Distance between both locations is :" + distance;

        Toast.makeText(MapActivity.this, "Stop location ;" + showDistance , Toast.LENGTH_LONG).show();





        googleMap.addMarker(new MarkerOptions().position((StartLocation)).title("StartLocation"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(StartLocation));
        googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(StartLocation)
                .add(StopLocation)
                .width(8f)
                .color(getResources().getColor(R.color.colorAccent))
        );

        googleMap.addCircle(new CircleOptions()
                .center(StopLocation)
                .radius(50000.0)
                .strokeWidth(3f)
                .strokeColor(getResources().getColor(R.color.colorAccent))
                .fillColor(getResources().getColor(R.color.colorAccent)));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }


    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
