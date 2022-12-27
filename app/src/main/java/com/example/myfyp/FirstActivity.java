package com.example.myfyp;

import static com.example.myfyp.MainActivity.ACTIVITY_FROM;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class FirstActivity extends AppCompatActivity implements OnCompleteListener {

    //Variables
    CardView addlocation, savedlocations, addtime, savedtime;
    //Geofencing client
    //combines informations of the user's current location
    GeofencingClient geofencingClient;
   // LocationLoadTask loadTask;
   Snackbar snackbar = null;

    public static final String ACTIVITY_FROM = "activityFrom";

    //Constant key for Preference to store the request id count
    public static final String REQUEST_ID_EXTRA = "geofenceRequestId";
    private LocationDBHelper locationDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        getSupportActionBar().setTitle("Auto Silent");

        //initializing
        locationDBHelper = new LocationDBHelper(this);
        addlocation = findViewById(R.id.adloccard);
        savedlocations = findViewById(R.id.savedcard);
        addtime = findViewById(R.id.adtimecard);
        savedtime = findViewById(R.id.bookedtime);
        createNotificationChannel();


        //Requesting the location permissions
        if ((Build.VERSION.SDK_INT >= 23)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, permissions, 777);
            }
        }

        //Requesting permission to change Audio Settings
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }

        //Initialising GeofencingClient
        geofencingClient = LocationServices.getGeofencingClient(this);




        //click on cardview to navigate to Maps ACtivity to wselect location
        addlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstActivity.this, MapsActivity.class);
                startActivityForResult(intent, 777);
            }
        });

        //click on cardview to navigate to  MainACtivity for saved locations
        savedlocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstActivity.this,MainActivity.class));
            }
        });

        //click on cardview to navigate to ScheduleTime Activity to register events
        addtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(FirstActivity.this,ScheduleTime.class));
            }
        });

        //click on cardview to navigate to MyEvents Activity for saved events
        savedtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(FirstActivity.this,MyEvents.class));
            }
        });

    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            //Started activity is completed with result code RESULT_OK. If not just ignore.
            if (resultCode == RESULT_OK) {
                String fromActivity = data.getStringExtra(ACTIVITY_FROM);
                //Toast.makeText(this, "aa"+fromActivity, Toast.LENGTH_SHORT).show();
                Log.d("TAGestr", "onActivityResult: "+fromActivity);

                //Check if the resulted activity is Maps activity or the ConfirmDialogActivity
                if (fromActivity != null && fromActivity.equals(MapsActivity.FROM_MAPS_ACTIVITY)) {
                    //Activity resulted is the MapsActivity.
                    //Retrieve selected latitude and longitude.
                    double lat = data.getDoubleExtra(ConfirmDialogActivity.GEO_LATITUDE, 0.0);
                    double lng = data.getDoubleExtra(ConfirmDialogActivity.GEO_LONGITUDE, 0.0);
                    float rad = 100.0f;
                    Intent intent = new Intent(this, ConfirmDialogActivity.class)
                            .putExtra(ConfirmDialogActivity.GEO_LATITUDE, lat)
                            .putExtra(ConfirmDialogActivity.GEO_LONGITUDE, lng)
                            .putExtra(ConfirmDialogActivity.GEO_RADIUS, rad);
                    startActivityForResult(intent, 777);

                } else if (fromActivity.equals(ConfirmDialogActivity.FROM_CONFIRM_ACTIVITY)){
                    //Activity resulted is the ConfirmDialogActivity.
                    Double lat = data.getDoubleExtra(ConfirmDialogActivity.GEO_LATITUDE, 0.0);
                    Double lng = data.getDoubleExtra(ConfirmDialogActivity.GEO_LONGITUDE, 0.0);
                    Float rad = data.getFloatExtra(ConfirmDialogActivity.GEO_RADIUS, 0.0f);
                    String name = data.getStringExtra(ConfirmDialogActivity.GEO_NAME);
                    String address = data.getStringExtra(ConfirmDialogActivity.GEO_ADDRESS);
                    int idCount = getPreferences(MODE_PRIVATE).getInt(REQUEST_ID_EXTRA, 0);
                    SharedPreferences.Editor prefEdit = getPreferences(MODE_PRIVATE).edit();
                    prefEdit.putInt(REQUEST_ID_EXTRA, idCount + 1).apply();
                    String requestId = REQUEST_ID_EXTRA + idCount;
                    //Adding selected location to the geofencing client
                    ArrayList<Geofence> geofenceList = new ArrayList<>();
                    geofenceList.add(new Geofence.Builder()
                            .setCircularRegion(lat, lng, rad)
                            .setRequestId(requestId)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .build());
                    GeofencingRequest request = new GeofencingRequest.Builder()
                            .addGeofences(geofenceList)
                            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).build();
                    try {
                        geofencingClient.addGeofences(request, getPendingIntent()).addOnCompleteListener((OnCompleteListener<Void>) FirstActivity.this);
                        locationDBHelper.insert(requestId, name, lat, lng, rad, address);
                    } catch (SecurityException e) {
                        Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
        }
    }

    //called when location is selected and saved to DB
    @Override
    public void onComplete(@NonNull Task task) {
        if (snackbar != null) snackbar.dismiss();
        snackbar = Snackbar.make(findViewById(R.id.snacklayout), "Adding geofence done", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
    private PendingIntent getPendingIntent() {
        try {
            //INTENT TO THE BROADCAST RECEIVER CLASS FOR GEOFENCING
            Intent intent = new Intent(this, GeofenceReceiver.class);
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        } catch (Exception e) {
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    //CREATING CHANNEL FOR THE NOTIFICATOINS
    private void createNotificationChannel () {

        Log.d("TAGchannel", "createNotificationChannel: "+"start channel");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myFyp";
            String description = "Channel For Silent Mode";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("myFypp", name, importance);
            channel.setDescription(description);


            //creating channel through notification manager
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }





    //INFLATING MENU TO THE ACTION BAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    //WHATEVER TO PERFORM WHEN CLICK ON MENU ITEM
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            //LOGOUT FROM THE APP
            if (item.getItemId() == R.id.logout) {
                UserSession sessionManager = new UserSession(FirstActivity.this);
                sessionManager.setLoggedin(false);
                startActivity(new Intent(FirstActivity.this, Login.class));
                finish();
            }

        } catch (Exception e) {
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


}