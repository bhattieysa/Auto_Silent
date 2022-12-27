package com.example.myfyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MyEvents extends AppCompatActivity implements OnCompleteListener,EventListAdapter.RecyclerViewClickCallbacks {

    //variables
    public static final String ACTIVITY_FROM = "activityFrom";
    public static final String REQUEST_ID_EXTRA = "geofenceRequestId";
    private static final String TAG = MyEvents.class.getSimpleName();
    Snackbar snackbar = null;
    RecyclerView recyclerView;
    AudioManager audioManager;
    EventLoadTask loadTask;
    private EventListAdapter adapter;
    private EventDBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //Setting main content
            setContentView(R.layout.activity_my_events);
            //setting a custom toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

            //initialize db helper class to get data from database
            dbHelper = new EventDBHelper(MyEvents.this);

            //initialize the recyclerview and setting layout manager and adapter
            recyclerView = findViewById(R.id.rv_list_events);
            recyclerView.setLayoutManager(new LinearLayoutManager(MyEvents.this));
            recyclerView.setAdapter((adapter = new EventListAdapter(null,this)));

            //Register for RecyclerView Click Callback
            adapter.setRecyclerViewClickCallbacks((EventListAdapter.RecyclerViewClickCallbacks) this);
            new ItemTouchHelper(adapter.getSwipeHelper()).attachToRecyclerView(recyclerView);
        } catch (Exception e) {

            Log.e(TAG, "onCreate: An Exception occurred!", e);
        }
    }

    //Swipe to delete data from recyclerview
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myeventsa),"Item Deleted",Snackbar.LENGTH_LONG);
            snackbar.show();
            adapter.deleteItem(viewHolder.getAdapterPosition());

        }
    };


    //loading data again when the activity is resume
    @Override
    protected void onResume() {
        super.onResume();
        loadDataIntoAdapter();


    }


    //loading or getting data from database
    void loadDataIntoAdapter() {
        loadTask = new EventLoadTask();
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

        //getting item position from adapter
        AutoSilenceEvent location = adapter.getItem(position);

        //getting the unique id of items when swiped
        ArrayList<String> requestIdList = new ArrayList<>();
        requestIdList.add(String.valueOf(location.getId()));

        try {

            //when the item is deleted show snackbar
            if (dbHelper.remove(location.getId())) {
                if (snackbar != null) snackbar.dismiss();
                snackbar = Snackbar.make(findViewById(R.id.myeventsa), "Item deleted!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                adapter.removeLocation(position);
            }
        } catch (SecurityException e) {
            //when catch the exception
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
            if (snackbar != null) snackbar.dismiss();
            snackbar = Snackbar.make(findViewById(R.id.myeventsa), "Can't delete!", Snackbar.LENGTH_SHORT);
            snackbar.show();

            adapter.notifyDataSetChanged();
        }
    }


    //getting data from DB in the background task
    @SuppressWarnings("StaticFieldLeak")
    class EventLoadTask extends AsyncTask<Void, Void, ArrayList<AutoSilenceEvent>> {
        @Override
        protected ArrayList<AutoSilenceEvent> doInBackground(Void... voids) {
            return dbHelper.getAllData();
        }

        @Override
        protected void onPostExecute(ArrayList<AutoSilenceEvent> autoSilenceLocations) {
            super.onPostExecute(autoSilenceLocations);
            adapter.setEvents(autoSilenceLocations);
        }
    }
}