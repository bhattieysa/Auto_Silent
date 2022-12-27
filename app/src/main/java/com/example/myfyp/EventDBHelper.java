package com.example.myfyp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
// Database Helper class whose only function is to provide for the creation,
// modification, and deletion of tables in the database.
public class EventDBHelper extends SQLiteOpenHelper {

    //Constant TAG for logging purpose.
    private static final String TAG = "LocationDBHelper";

    //Database file name
    private static final String DATABASE_NAME = "events.db";
    //Database version
    private static final int DATABASE_VERSION = 1;

    //Table constants
    //Table name
    private static final String TABLE_NAME = "events";
    //Unique id -> auto increment.
    private static final String COLUMN_NAME_ID = "id";
    //Stores latitude of the geofence
    private static final String COLUMN_NAME_START = "start";
    //Column stores longitude of the geofence
    private static final String COLUMN_NAME_END = "end";
    //User given or the auto-generated name for the geofence
    private static final String COLUMN_NAME_NAME = "eventname";



    public EventDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Query for the table create.
        //All fields are created as TEXT to preserve the precision of the latitude, longitude and the radius.
        String createQuery = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_NAME + " TEXT,"
                + COLUMN_NAME_START + " TEXT,"
                + COLUMN_NAME_END + " TEXT);";
        Log.d(TAG, "onCreate: Executing "+ createQuery);
        //Executing the above query
        sqLiteDatabase.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void insert(EventModel model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_NAME, model.getName());
        contentValues.put(COLUMN_NAME_START, model.getStart());
        contentValues.put(COLUMN_NAME_END, model.getEnd());
        database.insert(TABLE_NAME, null, contentValues);

        database.close();

    }

    boolean remove(final int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean isRemoved = sqLiteDatabase.delete(TABLE_NAME, COLUMN_NAME_ID + "=" + id, null) >= 0;
        sqLiteDatabase.close();
        return isRemoved;
    }

    //Function fetches all data from the database and prepares, returns the ArrayList of Locations.
    public ArrayList<AutoSilenceEvent> getAllData() {
        ArrayList<AutoSilenceEvent> locations = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        int columnIndexId = cursor.getColumnIndex(COLUMN_NAME_ID);
        int columnIndexName = cursor.getColumnIndex(COLUMN_NAME_NAME);
        int columnIndexStart = cursor.getColumnIndex(COLUMN_NAME_START);
        int columnIndexEnd = cursor.getColumnIndex(COLUMN_NAME_END);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            AutoSilenceEvent location = new AutoSilenceEvent(
                    cursor.getInt(columnIndexId),
                    cursor.getString(columnIndexName),
                    cursor.getString(columnIndexStart),
                    cursor.getString(columnIndexEnd));
            locations.add(location);
        }
        cursor.close();
        database.close();
        return locations;
    }


}
