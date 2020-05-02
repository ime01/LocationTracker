package com.flowz.locationtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView start, stop, show;
    ImageView image;
    MapView mapView;
    public Double startLatitude;
    public Double startLongitude;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start_location);
        stop = findViewById(R.id.stop);
        show = findViewById(R.id.show_location);
        mapView = findViewById(R.id.mymap);

        client = LocationServices.getFusedLocationProviderClient(this);

        requestPermission();


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);


        if(ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location!=null){
                    //Toast.makeText(MainActivity.this, "you are hare"+location.toString(), Toast.LENGTH_LONG).show();

                    startLatitude  = location.getLatitude();
                    startLongitude = location.getLongitude();

                    String startLocation = String.valueOf("LAT :" + startLatitude + " LONG :" + startLongitude);

                    show.setText(startLocation);

                }

            }
        });



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

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



    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION}, 10);
    }

    public void getLocation() {

        if(ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location!=null){
                    //Toast.makeText(MainActivity.this, "you are hare"+location.toString(), Toast.LENGTH_LONG).show();

                   startLatitude  = location.getLatitude();
                   startLongitude = location.getLongitude();

                    String startLocation = String.valueOf("LAT :" + startLatitude + " LONG :" + startLongitude);

                    show.setText(startLocation);

                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

       // LatLng FRAairport = new LatLng(50.0379, 8.5622);
        LatLng FRAairport = new LatLng( startLatitude, startLongitude);
        LatLng JFKairport = new LatLng(40.6435529, -73.78211390000001);



        googleMap.addMarker(new MarkerOptions().position((FRAairport)).title("FRAairport"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(FRAairport));
        googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(FRAairport)
                .add(JFKairport)
                .width(8f)
                .color(getResources().getColor(R.color.colorAccent))
        );

        googleMap.addCircle(new CircleOptions()
                .center(JFKairport)
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
