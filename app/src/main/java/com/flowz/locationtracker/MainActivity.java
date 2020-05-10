package com.flowz.locationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flowz.locationtracker.map.MapActivity;
import com.flowz.locationtracker.room.MyAppDataBase;
import com.flowz.locationtracker.room.MyPlace;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;

public class MainActivity extends AppCompatActivity  {

    TextView  show, walker;
    Button start, stop, openMap;
    ImageView image;


    Boolean connected = false;
    Boolean stclicked = false;
    Boolean spclicked = false;
    public Double startLatitude;
    public Double startLongitude;
    public Double stopLatitude;
    public Double stopLongitude;
    public static MyAppDataBase myAppDataBase;

    private FusedLocationProviderClient client;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start_location);
        stop = findViewById(R.id.stop);
        openMap = findViewById(R.id.open_map);
        show = findViewById(R.id.show_location);
        image = findViewById(R.id.walker);
        walker = findViewById(R.id.keep_walking);

        image.setVisibility(View.GONE);
        walker.setVisibility(View.GONE);


        //Glide.with(MainActivity.this).load(R.drawable.johnywalker).into(new GlideDrawableImageViewTarget(image));
        Glide.with(this).load(R.raw.johnywalker).into(image);


        myAppDataBase = Room.databaseBuilder(MainActivity.this,MyAppDataBase.class,"locationdb").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        client = LocationServices.getFusedLocationProviderClient(this);

        requestPermission();
        checkConnectionState();

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                stopLatitude = locationResult.getLastLocation().getLatitude();
                stopLongitude = locationResult.getLastLocation().getLongitude();

                MyPlace myPlace = new MyPlace();

                myPlace.setId(1);
                myPlace.setStartLatitude(0.0);
                myPlace.setStartLongitude(0.0);
                myPlace.setStopLatitude(stopLatitude);
                myPlace.setStopLongitude(stopLongitude);

                MainActivity.myAppDataBase.myDAO().addPlace(myPlace);

                String stopLocation = String.valueOf("Stop Location :" + "LAT :" + stopLatitude + " LONG :" + stopLongitude);

                show.setText(stopLocation);

            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                getLocation();
                stclicked = true;

                image.setVisibility(View.VISIBLE);
                walker.setVisibility(View.VISIBLE);
                walker.setSingleLine();
                walker.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                walker.setMarqueeRepeatLimit(-1);
                walker.setSelected(true);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                walker.setVisibility(View.GONE);

                requestLocation();
                //getStopLocation();
                spclicked = true;

                getDistanceBetweenLocations();

            }
        });

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (stclicked.equals(true) && spclicked.equals(true)) {

                    Intent openMapView = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(openMapView);

                } else{
                    Toast.makeText(MainActivity.this, "Ensure you have pressed the START and STOP buttons to set your locations before clicking OPEN IN MAP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkConnectionState() {
        ConnectivityManager connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI).getState()== NetworkInfo.State.CONNECTED){
            Toast.makeText(this, "Internet access granted", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "PLEASE ENSURE INTERNET CONNECTION, FOR THE FIRST TIME USING THIS APP" , Toast.LENGTH_LONG).show();

            Intent onMobileData = new Intent();
            onMobileData.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DataUsageSummaryActivity"));
            startActivity(onMobileData);

        }
    }

    private void requestLocation() {
        LocationRequest locationRequest = new LocationRequest();

        //locationRequest.setSmallestDisplacement(1);
        //locationRequest.setInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }


        private void getDistanceBetweenLocations() {

        List<MyPlace> locations = MainActivity.myAppDataBase.myDAO().getPlaces();


        Double startLA  = locations.get(locations.size()-1).getStartLatitude();
        Double startLO  = locations.get(locations.size()-1).getStartLongitude();

        Double stopLA  = locations.get(locations.size()-1).getStartLatitude();
        Double stopLO  = locations.get(locations.size()-1).getStartLongitude();

//        Double startLA  = locations.get(0).getStartLatitude();
//        Double startLO  = locations.get(0).getStartLongitude();
//
//        Double stopLA  = locations.get(0).getStopLatitude();
//        Double stopLO  = locations.get(0).getStopLatitude();

        LatLng StartLocation = new LatLng(startLA, startLO);
        LatLng StopLocation =  new LatLng(stopLA, stopLO);

            Double distance =  SphericalUtil.computeDistanceBetween(StartLocation, StopLocation);

            String startAndStop = "Start" + StartLocation + "Stop" + StopLocation;

            //String showDistance = "Distance between both locations is :" + distance;
            //show.setText(startAndStop + distance);

            Toast.makeText(this,  startAndStop  + distance, Toast.LENGTH_LONG).show();

    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23){
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(INTERNET) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET},1);
            }
        }
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION}, 10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[2]==PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Permissions needed please GRANT permssions", Toast.LENGTH_SHORT).show();
                }
        }
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

                   startLatitude = location.getLatitude();
                   startLongitude = location.getLongitude();


                     MyPlace myPlace = new MyPlace();

                     myPlace.setId(2);
                     myPlace.setStartLatitude(startLatitude);
                     myPlace.setStartLongitude(startLongitude);
                     myPlace.setStopLatitude(0.0);
                     myPlace.setStopLongitude(0.0);
                     MainActivity.myAppDataBase.myDAO().addPlace(myPlace);

//                    Toast.makeText(MainActivity.this, "Start location saved to Room database successfully", Toast.LENGTH_LONG).show();

                    String startLocation = String.valueOf("Start Location " + "LAT :" + startLatitude + " LONG :" + startLongitude);

                    show.setText(startLocation);

//                     startLatitude = null;
//                     startLongitude = null;


                }
            }
        });
    }

    public void getStopLocation() {

        if(ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location!=null){
                    //Toast.makeText(MainActivity.this, "you are hare"+location.toString(), Toast.LENGTH_LONG).show();

                    stopLatitude = location.getLatitude();
                    stopLongitude = location.getLongitude();

                    MyPlace myPlace = new MyPlace();

//                     int idstart = 0;
//                     int id = idstart+1;
//
//                     myPlace.setId(id);
                    myPlace.setStopLatitude(stopLatitude);
                    myPlace.setStopLongitude(stopLongitude);


                    MainActivity.myAppDataBase.myDAO().addPlace(myPlace);

//                    Toast.makeText(MainActivity.this, "Start location saved to Room database successfully", Toast.LENGTH_LONG).show();

                    String startLocation = String.valueOf("LAT :" + stopLatitude + " LONG :" + stopLongitude);

                    show.setText(startLocation);


                }

            }
        });
    }


}
