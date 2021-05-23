package com.example.datacollectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btn_start, btn_stop, btn_sb_low, btn_sb_medium, btn_sb_high, btn_pothole, btn_pothole_deep, btn_turn;
    TextView txt_accel_x, txt_accel_y, txt_accel_z, txt_lat, txt_lon, txt_alt, txt_accuracy, txt_speed;

    //Define sensor manager and accelerometer
    SensorManager mSensorManager;
    Sensor mAccelerometer;

    //Define an instance of accelerometer data class
    AccelerometerData accelData = new AccelerometerData();


    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accelData.setX(x);
            accelData.setY(y);
            accelData.setZ(z);

            txt_accel_x.setText(String.format("X: %s", accelData.getX()));
            txt_accel_y.setText(String.format("Y: %s", accelData.getY()));
            txt_accel_z.setText(String.format("Z: %s", accelData.getZ()));

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

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

}