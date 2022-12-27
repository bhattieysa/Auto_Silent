package com.example.myfyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleTime extends AppCompatActivity {

    //VARIABLES
    EditText eventName;
    TextView start, end, confirm;
    String first, second;
    public static final String FROM_SCHEDULE_ACTIVITY = "fromScheduleActivity";
    private EventDBHelper dbHelper;
    public static final String NAME = "NAME";
    public static final String START = "START";
    public static final String END = "END";
    private MaterialTimePicker picker, endPicker;
    private Calendar calendar, endCAlendar;
    private AlarmManager alarmManager, endManager;
    private PendingIntent pendingIntent, pendingIntentEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_time);


        //INITIALIZING VARIABLES
        eventName = findViewById(R.id.editTextName);
        start = findViewById(R.id.editTextLatitude);
        end = findViewById(R.id.editTextLongitude);
        confirm = findViewById(R.id.btnconfirm);
        dbHelper = new EventDBHelper(ScheduleTime.this);
        start.setFocusable(false);
        end.setFocusable(false);
        createNotificationChannel();
        createNotificationChannelForEnd();


        //SHOW THE TIME PICKER DIALOG ON START CLICK
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                picker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Time")
                        .build();

                picker.show(getSupportFragmentManager(), "myFyp");

                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (picker.getHour() > 12) {
                            start.setText((picker.getHour() - 12) + " : " + (picker.getMinute()) + " PM");
                        } else {
                            start.setText(picker.getHour() + " : " + picker.getMinute() + " AM");
                        }

                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                        calendar.set(Calendar.MINUTE, picker.getMinute());
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                    }
                });
            }
        });


        //SHOW THE TIME PICKER DIALOG ON END CLICK
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                endPicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Time")
                        .build();

                endPicker.show(getSupportFragmentManager(), "times");

                endPicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (endPicker.getHour() > 12) {
                            end.setText((endPicker.getHour() - 12) + " : " + (endPicker.getMinute()) + " PM");
                        } else {
                            end.setText(endPicker.getHour() + " : " + endPicker.getMinute() + " AM");
                        }

                        endCAlendar = Calendar.getInstance();
                        endCAlendar.set(Calendar.HOUR_OF_DAY, endPicker.getHour());
                        endCAlendar.set(Calendar.MINUTE, endPicker.getMinute());
                        endCAlendar.set(Calendar.SECOND, 0);
                        endCAlendar.set(Calendar.MILLISECOND, 0);
                    }
                });
            }
        });


        //SAVING THE EVENT IN DATABASE AFTER VALIDATION
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = eventName.getText().toString().trim();
                if (name.equals("")) {
                    eventName.setError("Please Enter Event Name");
                    Toast.makeText(getApplicationContext(), "Enter a name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String starttime = start.getText().toString().trim();
                if (starttime.equals("")) {
                    end.setError("Please Select Start Time");
                    Toast.makeText(getApplicationContext(), "Please Select Start Time", Toast.LENGTH_SHORT).show();
                    return;
                }

                String endtime = end.getText().toString().trim();
                if (endtime.equals("")) {
                    end.setError("Please Select End Time");
                    Toast.makeText(getApplicationContext(), "Please Select End Time", Toast.LENGTH_SHORT).show();
                }


                EventModel model = new EventModel(name, starttime, endtime);

                dbHelper.insert(model);

                //Intent to the Myevents containing all the data.
                Intent resultData = new Intent();
                resultData.putExtra(NAME, name)
                        .putExtra(START, starttime)
                        .putExtra(END, endtime)
                        .putExtra(MyEvents.ACTIVITY_FROM, FROM_SCHEDULE_ACTIVITY);
                //If all data are valid return with the RESULT_OK and the collected data.
                setResult(RESULT_OK, resultData);
                Log.d("TAGsdsds", "onClick: " + MyEvents.ACTIVITY_FROM);
                setTime();
                setEndTime();
                finish();
            }
        });
    }


    //SCHEDULING THE ENDTIME FOR THE AUTO SILENT MODE WITH THE ALARM MANAGER
    private void setEndTime() {

        endManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        //INTENT TO THE BROADCAST RECEIVER CLASS
        Intent intent = new Intent(this, EndTimeReceiver.class);

        pendingIntentEnd = PendingIntent.getBroadcast(this, 0, intent, 0);

        endManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, endCAlendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntentEnd);

        Log.d("TAGchannel", "setEndTime: ended");
    }


    //CREATE NOTIFICATION CHANNEL FOR THE ENDTIME NOTIFICATION
    private void createNotificationChannelForEnd() {

        Log.d("TAGchannel", "createNotificationChannelForEnd: "+"end channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence names = "myFypS";
            String descriptions = "Channel For Silent Modes";

            int importances = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("myFypS", names, importances);
            channel.setDescription(descriptions);


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //SCHEDULING THE STARTTIME FOR THE AUTO SILENT MODE WITH THE ALARM MANAGER
        private void setTime () {

            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, Receiver.class);

            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

            Log.d("TAGchannel", "setTime: started");


        }

    //CREATE NOTIFICATION CHANNEL FOR THE STARTTIME NOTIFICATION

        private void createNotificationChannel () {

            Log.d("TAGchannel", "createNotificationChannel: "+"start channel");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "myFyp";
                String description = "Channel For Silent Mode";

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("myFyp", name, importance);
                channel.setDescription(description);


                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

        }
    }
    

