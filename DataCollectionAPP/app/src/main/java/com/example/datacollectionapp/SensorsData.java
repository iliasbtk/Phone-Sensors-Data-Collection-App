package com.example.datacollectionapp;

public class SensorsData {
    private float accel_x, accel_y, accel_z, accuracy, speed, bearing, gyro_x, gyro_y, gyro_z;
    double lat, lon, alt;
    private int id;

    public SensorsData() {
    }

    public void setAccel_x(float accel_x) {
        this.accel_x = accel_x;
    }

    public void setAccel_y(float accel_y) {
        this.accel_y = accel_y;
    }

    public void setAccel_z(float accel_z) {
        this.accel_z = accel_z;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public void setGyro_x(float gyro_x) {
        this.gyro_x = gyro_x;
    }

    public void setGyro_y(float gyro_y) {
        this.gyro_y = gyro_y;
    }

    public void setGyro_z(float gyro_z) {
        this.gyro_z = gyro_z;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAccel_x() {
        return accel_x;
    }

    public float getAccel_y() {
        return accel_y;
    }

    public float getAccel_z() {
        return accel_z;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public float getBearing() {
        return bearing;
    }

    public float getGyro_x() {
        return gyro_x;
    }

    public float getGyro_y() {
        return gyro_y;
    }

    public float getGyro_z() {
        return gyro_z;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getAlt() {
        return alt;
    }

    public int getId() {
        return id;
    }




}
