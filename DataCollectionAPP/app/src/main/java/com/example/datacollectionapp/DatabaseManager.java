package com.example.datacollectionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseManager extends SQLiteOpenHelper {

    public final static String TABLE_NAME = "Sensor_Data_Table";
    public static final String X_COLUMN = "X";
    public static final String Y_COLUMN = "Y";
    public static final String Z_COLUMN = "Z";
    public static final String ID_COLUMN = "ID";
/*    public static final String LAT_COLUMN = "Latitude";
    public static final String LON_COLUMN = "Longitude";
    public static final String ALT_COLUMN = "Altitude";
    public static final String ACCURACY_COLUMN = "Accuracy";
    public static final String SPEED_COLUMN = "Speed";*/

    public DatabaseManager(@Nullable Context context) {
        super(context, "sensors.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" + ID_COLUMN + " INTEGER PRIMAR" + Y_COLUMN +
                " KEY AUTOINCREMENT, " + X_COLUMN + " REAL, " + Y_COLUMN + " REAL, " + Z_COLUMN +
                " REAL)";
//        , " + LAT_COLUMN + " REAL, "+ LON_COLUMN + " REAL, "+ ALT_COLUMN + " REAL, " + ACCURACY_COLUMN + " REAL, "+ SPEED_COLUMN + " REAL)
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Boolean addOne(AccelerometerData accelData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(X_COLUMN, accelData.getX());
        cv.put(Y_COLUMN, accelData.getY());
        cv.put(Z_COLUMN, accelData.getZ());
/*        cv.put(LAT_COLUMN, sensorsData.getLat());
        cv.put(LON_COLUMN, sensorsData.getLon());
        cv.put(ALT_COLUMN, sensorsData.getAlt());
        cv.put(ACCURACY_COLUMN, sensorsData.getAccuracy());
        cv.put(SPEED_COLUMN, sensorsData.getSpeed());*/


        long insert = db.insert(TABLE_NAME, null, cv);

        return insert != -1;
    }
}
