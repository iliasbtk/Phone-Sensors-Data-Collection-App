package com.example.datacollectionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DatabaseManager extends SQLiteOpenHelper {

    public final static String TABLE_NAME = "Sensor_Data_Table";
    public static final String X_COLUMN = "X";
    public static final String Y_COLUMN = "Y";
    public static final String Z_COLUMN = "Z";
    public static final String ID_COLUMN = "ID";
    public static final String LAT_COLUMN = "Latitude";
    public static final String LON_COLUMN = "Longitude";
    public static final String ALT_COLUMN = "Altitude";
    public static final String ACCURACY_COLUMN = "Accuracy";
    public static final String SPEED_COLUMN = "Speed";
    public  static final String ROAD_ANOMALY_COLUMN = "Road_Anomaly_type";
    public static final String DATE_TIME_COLUMN = "Date";

    public DatabaseManager(@Nullable Context context) {
        super(context, "sensors.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" + ID_COLUMN + " INTEGER PRIMAR" + Y_COLUMN +
                " KEY AUTOINCREMENT, "+ X_COLUMN + " REAL, " + Y_COLUMN + " REAL, " + Z_COLUMN +
                " REAL, " + LAT_COLUMN + " REAL, "+ LON_COLUMN + " REAL, "+ ALT_COLUMN + " REAL, " +
                ACCURACY_COLUMN + " REAL, "+ SPEED_COLUMN + " REAL, " + ROAD_ANOMALY_COLUMN +
                " TEXT(25), " + DATE_TIME_COLUMN + " TEXT(25))";

        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addOne(SensorsData sensorsData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        cv.put(X_COLUMN, sensorsData.getX());
        cv.put(Y_COLUMN, sensorsData.getY());
        cv.put(Z_COLUMN, sensorsData.getZ());
        cv.put(LAT_COLUMN, sensorsData.getLat());
        cv.put(LON_COLUMN, sensorsData.getLon());
        cv.put(ALT_COLUMN, sensorsData.getAlt());
        cv.put(ACCURACY_COLUMN, sensorsData.getAccuracy());
        cv.put(SPEED_COLUMN, sensorsData.getSpeed());
        cv.put(ROAD_ANOMALY_COLUMN, "NONE");
        cv.put(DATE_TIME_COLUMN, formatter.format(date));

        db.insert(TABLE_NAME, null, cv);

    }

    public void defineAnomalyType(SensorsData sensorsData, String anomalyType){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ROAD_ANOMALY_COLUMN, anomalyType);

        db.update(TABLE_NAME, cv, LAT_COLUMN + " = " +sensorsData.getLat(),null);

    }

}
