package com.example.myfyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnCompleteListener, LocationListAdapter.RecyclerViewClickCallbacks {

    //Constant to determine the activity from which the result is returned.
    public static final String ACTIVITY_FROM = "activityFrom";
    //Constant key for Preference to store the request id count
    public static final String REQUEST_ID_EXTRA = "geofenceRequestId";
    //Constant tag for logging purpose.
    private static final String TAG = MainActivity.class.getSimpleName();
    Snackbar snackbar = null;
    RecyclerView locationList;
    AudioManager audioManager;
    //Geofencing client
    GeofencingClient geofencingClient;
    LocationLoadTask loadTask;
    //The recyclerview adapter for the locationList.
    private LocationListAdapter listAdapter;
    //Database instance to store the added geofences.
    private LocationDBHelper locationDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //Setting main content
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

            //initializing DB Helper class to save and get locations
            locationDBHelper = new LocationDBHelper(this);

            //recyclerview
             locationList = findViewById(R.id.rv_list_locations);
            locationList.setLayoutManager(new LinearLayoutManager(this));
            locationList.setAdapter((listAdapter = new LocationListAdapter(null)));
            //Register for RecyclerView Click Callback
            listAdapter.setRecyclerViewClickCallbacks(this);
            new ItemTouchHelper(listAdapter.getSwipeHelper()).attachToRecyclerView(locationList);
        } catch (Exception e) {
            //Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
            Log.e(TAG, "onCreate: An Exception occurred!", e);
        }
    }


    //called when the activity ius resumed
    @Override
    protected void onResume() {
        super.onResume();
        loadDataIntoAdapter();
    }

    //method to load data from DB
    void loadDataIntoAdapter() {
        loadTask = new LocationLoadTask();
        loadTask.execute();
    }


    @Override
    public void onComplete(@NonNull Task task) {

    }

    @Override
    public void onItemClick(View v, int position) {
        //Triggers when recyclerview item clicked.
        if (snackbar != null) snackbar.dismiss();
        snackbar = Snackbar.make(findViewById(R.id.mainActivityLayout), "Item clicked!", Snackbar.LENGTH_SHORT);
        snackbar.show();

    }

    @Override
    public boolean onItemLongClick(View v, int position) {
        //Triggers when recyclerview item long clicked.
        if (snackbar != null) snackbar.dismiss();
        snackbar = Snackbar.make(findViewById(R.id.mainActivityLayout), "Long clicked on the item", Snackbar.LENGTH_SHORT);
        snackbar.show();

        return false;
    }

    @Override
    public void onItemSwipe(final int position) {
        if (geofencingClient == null) {
            geofencingClient = LocationServices.getGeofencingClient(this);
        }

        //getting the item position from adapter class
        AutoSilenceLocation location = listAdapter.getItem(position);
        ArrayList<String> requestIdList = new ArrayList<>();
        //gettting the unique id of the location fro the DB
        requestIdList.add(location.getRequestId());
        try {
            //removing marker of the location from the maps also
            geofencingClient.removeGeofences(requestIdList).addOnCompleteListener(MainActivity.this);
            if (locationDBHelper.remove(location.getId())) {
                if (snackbar != null) snackbar.dismiss();
                snackbar = Snackbar.make(findViewById(R.id.mainActivityLayout), "Item deleted!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                listAdapter.removeLocation(position);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
            if (snackbar != null) snackbar.dismiss();
            snackbar = Snackbar.make(findViewById(R.id.mainActivityLayout), "Can't delete!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            listAdapter.notifyDataSetChanged();
        }
    }

    //method to get all data from DB in the background task
    @SuppressWarnings("StaticFieldLeak")
    class LocationLoadTask extends AsyncTask<Void, Void, ArrayList<AutoSilenceLocation>> {
        @Override
        protected ArrayList<AutoSilenceLocation> doInBackground(Void... voids) {
            return locationDBHelper.getAllData();
        }

        @Override
        protected void onPostExecute(ArrayList<AutoSilenceLocation> autoSilenceLocations) {
            super.onPostExecute(autoSilenceLocations);
            listAdapter.setLocations(autoSilenceLocations);
        }
    }

}