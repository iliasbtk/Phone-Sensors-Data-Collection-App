package com.example.datacollectionapp;

import android.Manifest;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // How often the location check occur (Milliseconds)
    public static final int UPDATE_INTERVAL = 1;

    private static final int PERMISSION_FINE_LOCATION = 99;

    Button btn_start, btn_stop, btn_speed_bump;
    TextView txt_accel_x, txt_accel_y, txt_accel_z, txt_lat, txt_lon, txt_alt, txt_accuracy,
            txt_speed, txt_bearing, txt_gyro_x, txt_gyro_y, txt_gyro_z;

    //Define sensor manager and accelerometer
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mGyroScope;

    //Config file for FusedLocationProviderClient
    LocationRequest locationRequest;

    //Google API for GPS location services
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallback;

    //Variables to store collected GPS data
    double latitude, longitude, altitude;
    float speed, accuracy, bearing;

    //Variables to store accelerometer data
    float accel_x, accel_y, accel_z, gyro_x, gyro_y, gyro_z;

    DatabaseManager databaseManager =new DatabaseManager(MainActivity.this);

    //Accelerometer sensor changes listener
    private SensorEventListener sensorEventListenerAcc = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            accel_x = sensorEvent.values[0];
            accel_y = sensorEvent.values[1];
            accel_z = sensorEvent.values[2];

            txt_accel_x.setText(String.format("X: %s", accel_x));
            txt_accel_y.setText(String.format("Y: %s", accel_y));
            txt_accel_z.setText(String.format("Z: %s", accel_z));

            //Add an entry to the database
            databaseManager.addOne(storeSensorsData());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    //Gyroscope sensor changes listener
    private SensorEventListener sensorEventListenerGyro = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            gyro_x = sensorEvent.values[0];
            gyro_y = sensorEvent.values[1];
            gyro_z = sensorEvent.values[2];

            txt_gyro_x.setText(String.format("X: %s", gyro_x));
            txt_gyro_y.setText(String.format("Y: %s", gyro_y));
            txt_gyro_z.setText(String.format("Z: %s", gyro_z));
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

        btn_speed_bump = findViewById(R.id.btn_speed_bump);

        txt_accel_x = findViewById(R.id.txt_accel_x);
        txt_accel_y = findViewById(R.id.txt_accel_y);
        txt_accel_z = findViewById(R.id.txt_accel_z);

        txt_lat = findViewById(R.id.txt_lat);
        txt_lon = findViewById(R.id.txt_lon);
        txt_alt = findViewById(R.id.txt_alt);
        txt_accuracy = findViewById(R.id.txt_accuracy);
        txt_speed = findViewById(R.id.txt_speed);
        txt_bearing = findViewById(R.id.txt_bearing);

        txt_gyro_x = findViewById(R.id.txt_gyro_x);
        txt_gyro_y = findViewById(R.id.txt_gyro_y);
        txt_gyro_z = findViewById(R.id.txt_gyro_z);

        // Initialize sensor manager and accelerometer
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroScope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //Check if the phone has a gyroscope
        if(mGyroScope == null){
            txt_gyro_x.setText("Gyroscope not available");
            txt_gyro_y.setText("Gyroscope not available");
            txt_gyro_z.setText("Gyroscope not available");
        }

        // Set LocationRequest's properties
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10 * UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //Start tracking GPS Location
        updateGPS();

        // Event triggered when the update interval is met
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateLocationValuesUI(locationResult.getLastLocation());
            }
        };

        //Button to capture the presence of a speed bump
        btn_speed_bump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseManager.defineAnomalyType(storeSensorsData(), "Speed Bump");
            }
        });


    }

    protected void onResume() {
        super.onResume();
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationUpdate();
                mSensorManager.registerListener(sensorEventListenerAcc, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                mSensorManager.registerListener(sensorEventListenerGyro, mGyroScope, SensorManager.SENSOR_DELAY_NORMAL);

            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorManager.unregisterListener(sensorEventListenerAcc);
                mSensorManager.unregisterListener(sensorEventListenerGyro);
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

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListenerAcc);
        mSensorManager.unregisterListener(sensorEventListenerGyro);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
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

    public void updateLocationValuesUI(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        speed = location.getSpeed();
        accuracy = location.getAccuracy();
        bearing = location.getBearing();

        txt_lat.setText("Latitude: " + String.valueOf(location.getLatitude()));
        txt_lon.setText("Longitude: " + String.valueOf(location.getLongitude()));
        txt_accuracy.setText("Accuracy: " + String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()) {
            txt_alt.setText("Altitude: " + String.valueOf(location.getAltitude()));
        } else {
            txt_alt.setText("Altitude not available");
        }

        if (location.hasSpeed()) {
            txt_speed.setText("Speed: " + String.valueOf(location.getSpeed()));
        } else {
            txt_speed.setText("Speed not available");
        }
        if (location.hasBearing()){
            txt_bearing.setText("Bearing: "+ String.valueOf(location.getBearing()));
        }else{
            txt_bearing.setText("Bearing not available");
        }
    }

    public SensorsData storeSensorsData() {

        SensorsData sensorsData = new SensorsData();



        sensorsData.setAccel_x(accel_x);
        sensorsData.setAccel_y(accel_y);
        sensorsData.setAccel_z(accel_z);
        sensorsData.setGyro_x(gyro_x);
        sensorsData.setGyro_y(gyro_y);
        sensorsData.setGyro_z(gyro_z);
        sensorsData.setLat(latitude);
        sensorsData.setLon(longitude);
        sensorsData.setAlt(altitude);
        sensorsData.setAccuracy(accuracy);
        sensorsData.setSpeed(speed);
        sensorsData.setBearing(bearing);
        return sensorsData;
    }

}