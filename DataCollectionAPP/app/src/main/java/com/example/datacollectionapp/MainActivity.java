package com.example.datacollectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    // How often the location check occur (Seconds)
    public static final int UPDATE_INTERVAL = 5;
    // How often the location check occur when maximum power is used (Seconds)
    public static final int UPDATE_FASTEST_INTERVAL = 1;

    private static final int PERMISSION_FINE_LOCATION = 99;

    Button btn_start, btn_stop, btn_sb_low, btn_sb_medium, btn_sb_high, btn_pothole, btn_pothole_deep, btn_turn;
    TextView txt_accel_x, txt_accel_y, txt_accel_z, txt_lat, txt_lon, txt_alt, txt_accuracy, txt_speed;

    //Define sensor manager and accelerometer
    SensorManager mSensorManager;
    Sensor mAccelerometer;

    //Define an instance of accelerometer data class
    AccelerometerData accelData = new AccelerometerData();

    //Config file for FusedLocationProviderClient
    LocationRequest locationRequest;

    //Google API for GPS location services
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallback;


    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            DatabaseManager databaseManager = new DatabaseManager(MainActivity.this);

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accelData.setX(x);
            accelData.setY(y);
            accelData.setZ(z);

            txt_accel_x.setText(String.format("X: %s", accelData.getX()));
            txt_accel_y.setText(String.format("Y: %s", accelData.getY()));
            txt_accel_z.setText(String.format("Z: %s", accelData.getZ()));

            databaseManager.addOne(accelData);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Map variables to layout items

        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);

        btn_sb_low = findViewById(R.id.btn_sb_low);
        btn_sb_medium = findViewById(R.id.btn_sb_medium);
        btn_sb_high = findViewById(R.id.btn_sb_high);
        btn_pothole = findViewById(R.id.btn_pothole);
        btn_pothole_deep = findViewById(R.id.btn_pothole_deep);
        btn_turn = findViewById(R.id.btn_turn);

        txt_accel_x = findViewById(R.id.txt_accel_x);
        txt_accel_y = findViewById(R.id.txt_accel_y);
        txt_accel_z = findViewById(R.id.txt_accel_z);

        txt_lat = findViewById(R.id.txt_lat);
        txt_lon = findViewById(R.id.txt_lon);
        txt_alt = findViewById(R.id.txt_alt);
        txt_accuracy = findViewById(R.id.txt_accuracy);
        txt_speed = findViewById(R.id.txt_speed);

        // Initialize sensor manager and accelerometer

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Set LocationRequest's properties
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * UPDATE_FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        updateGPS();

        // Event triggered when the update interval is met
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateLocationValuesUI(locationResult.getLastLocation());
            }
        };


    }


    protected void onResume() {
        super.onResume();
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationUpdate();
                mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorManager.unregisterListener(sensorEventListener);
                stopLocationUpdate();

            }
        });

    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            updateGPS();
            return;
        }

    }

    private void stopLocationUpdate() {
        txt_lat.setText("Location tracking is off");
        txt_lon.setText("Location tracking is off");
        txt_accuracy.setText("Location tracking is off");
        txt_alt.setText("Location tracking is off");
        txt_speed.setText("Location tracking is off");

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }else{
                    finish();
                }
        }
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateLocationValuesUI(location);

                }
            });

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);

            }

        }

    }
    public void updateLocationValuesUI(Location location){
        txt_lat.setText("Latitude: "+ String.valueOf(location.getLatitude()));
        txt_lon.setText("Longitude: "+String.valueOf(location.getLongitude()));
        txt_accuracy.setText("Accuracy: "+String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            txt_alt.setText("Altitude: "+String.valueOf(location.getAltitude()));
        }else{
            txt_alt.setText("Altitude not available");
        }
        if(location.hasSpeed()){
            txt_speed.setText("Speed: "+String.valueOf(location.getSpeed()));
        }else{
            txt_speed.setText("Speed not available");
        }


    }

}