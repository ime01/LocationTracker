package com.flowz.locationtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flowz.locationtracker.room.MyAppDataBase;
import com.flowz.locationtracker.room.MyPlace;
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

import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView  show;
    Button start, stop;
    ImageView image;
    MapView mapView;
    public Double startLatitude;
    public Double startLongitude;
    public static MyAppDataBase myAppDataBase;
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

        myAppDataBase = Room.databaseBuilder(this,MyAppDataBase.class,"locationdb").allowMainThreadQueries().build();

        client = LocationServices.getFusedLocationProviderClient(this);

        requestPermission();


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
        

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                getLocation();
                
                MyPlace myPlace = new MyPlace();
                myPlace.setId(1);
                myPlace.setStartLatitude(startLatitude);
                myPlace.setStartLongitude(startLongitude);
                MainActivity.myAppDataBase.myDAO().addPlace(myPlace);


            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
                getLocation();
                
                MyPlace myPlace = new MyPlace();
                myPlace.setId(2);
                myPlace.setStopLatitude(startLatitude);
                myPlace.setStopLongitude(startLongitude);
                MainActivity.myAppDataBase.myDAO().addPlace(myPlace);

                mapView.setVisibility(View.VISIBLE);
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
                   

                    Toast.makeText(MainActivity.this, "Start location saved to Room database successfully", Toast.LENGTH_LONG).show();

                    String startLocation = String.valueOf("LAT :" + startLatitude + " LONG :" + startLongitude);

                    show.setText(startLocation);

                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        List<MyPlace> locations = MainActivity.myAppDataBase.myDAO().getPlaces();

        Double startLA  = locations.get(0).getStartLatitude();
        Double startLO  = locations.get(0).getStartLongitude();

        Double stopLA  = locations.get(0).getStopLatitude();
        Double stopLO  = locations.get(0).getStopLongitude();


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

        //LatLng FRAairport1 = new LatLng(50.0379, 8.5622);
        //LatLng JFKairport1 = new LatLng(40.6435529, -73.78211390000001);
        
        LatLng StartLocation = new LatLng( startLA, startLO);
        LatLng StopLocation = new LatLng(stopLA, stopLO);
       



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
