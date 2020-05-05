package com.flowz.locationtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.flowz.locationtracker.map.MapActivity;
import com.flowz.locationtracker.room.MyAppDataBase;
import com.flowz.locationtracker.room.MyPlace;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity  {

    TextView  show;
    Button start, stop, openMap;
    VideoView image;
    public Double startLatitude = 0.0;
    public Double startLongitude = 0.0;
    public Double stopLatitude = 0.0;
    public Double stopLongitude = 0.0;
    public static MyAppDataBase myAppDataBase;

    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start_location);
        stop = findViewById(R.id.stop);
        openMap = findViewById(R.id.open_map);
        show = findViewById(R.id.show_location);
        image = findViewById(R.id.walker);

        image.setVisibility(View.GONE);

        //Glide.with(MainActivity.this).load(R.drawable.johnywalker).into(new GlideDrawableImageViewTarget(image));


        myAppDataBase = Room.databaseBuilder(MainActivity.this,MyAppDataBase.class,"locationdb").fallbackToDestructiveMigration().allowMainThreadQueries().build();

        client = LocationServices.getFusedLocationProviderClient(this);

        requestPermission();


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                getLocation();


                image.setVideoPath("raw/johnywalker");
                image.start();
                image.setVisibility(View.VISIBLE);



                Toast.makeText(MainActivity.this, "Start location saved to Room database successfully", Toast.LENGTH_LONG).show();


            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                getStopLocation();

                Toast.makeText(MainActivity.this, "Stop location saved to Room database successfully", Toast.LENGTH_LONG).show();

                //getloacationfromRoom();

               // mapView.setVisibility(View.VISIBLE);
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


//    private void getloacationfromRoom() {
//
//        List<MyPlace> locations = MainActivity.myAppDataBase.myDAO().getPlaces();
//
////        Double startLA  = locations.get(locations.size()-1).getLatitude();
////        Double startLO  = locations.get(locations.size()-1).getLongitude();
//
//        Double startLA  = locations.get(0).getLatitude();
//        Double startLO  = locations.get(0).getLongitude();
//
//        Double stopLA  = locations.get(0).getStopLatitude();
//        Double stopLO  = locations.get(0).getStopLatitude();
//
//        String a = startLA + "  " + startLO;
//        String b = stopLA + "  " + stopLO;
//
//        //Toast.makeText(MainActivity.this, "Stop location ;" + a , Toast.LENGTH_LONG).show();
//
//        show.setText("start location :" + a + " stop location :" + b);
//
//
//    }
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

                     myPlace.setLatitude(startLatitude);
                     myPlace.setLongitude(startLongitude);


                     MainActivity.myAppDataBase.myDAO().addPlace(myPlace);

//                    Toast.makeText(MainActivity.this, "Start location saved to Room database successfully", Toast.LENGTH_LONG).show();

                    String startLocation = String.valueOf("LAT :" + startLatitude + " LONG :" + startLongitude);

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
