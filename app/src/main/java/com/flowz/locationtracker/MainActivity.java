package com.flowz.locationtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity  {

    TextView  show, walker;
    Button start, stop, openMap;
    ImageView image;

    //values to initialize postions and enable map setup, which are updated as we save to the RoomDatebase
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

            }
        });


        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openMapView = new Intent(MainActivity.this, MapActivity.class);
                startActivity(openMapView);
            }
        });



    }

    private void requestLocation() {
        LocationRequest locationRequest = new LocationRequest();
        //locationRequest.setInterval(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }


        private void getDistanceBetweenLocations() {

        List<MyPlace> locations = MainActivity.myAppDataBase.myDAO().getPlaces();

//        Double startLA  = locations.get(locati  ons.size()-1).getLatitude();
//        Double startLO  = locations.get(locations.size()-1).getLongitude();

        Double startLA  = locations.get(0).getStartLatitude();
        Double startLO  = locations.get(0).getStartLongitude();

        Double stopLA  = locations.get(0).getStopLatitude();
        Double stopLO  = locations.get(0).getStopLatitude();

//        String a = startLA + "  " + startLO;
//        String b = stopLA + "  " + stopLO;

        LatLng StartLocation = new LatLng(startLA, startLO);
        LatLng StopLocation =  new LatLng(stopLA, stopLO);


        //Toast.makeText(MainActivity.this, "Stop location ;" + a , Toast.LENGTH_LONG).show();
            Double distance =  SphericalUtil.computeDistanceBetween(StartLocation, StopLocation);

            String showDistance = "Distance between both locations is :" + distance;

            Toast.makeText(this, "Stop location ;" + showDistance , Toast.LENGTH_LONG).show();


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

                   startLatitude = location.getLatitude();
                   startLongitude = location.getLongitude();


                     MyPlace myPlace = new MyPlace();

                     myPlace.setId(2);
                     myPlace.setStartLatitude(startLatitude);
                     myPlace.setStartLongitude(stopLongitude);
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
