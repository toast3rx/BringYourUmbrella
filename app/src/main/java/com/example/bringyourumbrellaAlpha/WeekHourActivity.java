package com.example.bringyourumbrellaAlpha;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import receivers.AlarmReceiver;
import services.LocationService;

import java.util.Calendar;


/**
 *  WeekHourActivity
 *
 */
public class WeekHourActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private PendingIntent pendingIntent;
    // Variable for storing the language selected
    String language;
    TimePicker timePicker;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        this.getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_hour);

        timePicker = findViewById(R.id.weekTimePicker);
        timePicker.setIs24HourView(true);
        timePicker.setHour(7);
        timePicker.setMinute(00);

        // TextView for the greeting message
        TextView textView = findViewById(R.id.tfGreetingWeek);

        //Button for open next activity and save the data entered
        Button btNext = findViewById(R.id.btNextWeek);

        // Open a SharedPreferences instance for reading language and save  time
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();


         language = sharedPreferences.getString(getString(R.string.language), "");


        if(language.equals(getString(R.string.english))) {
            textView.setText("What time, on average, do you start your day during the week?");
            btNext.setText("Next");
        }
        else {
            textView.setText("La ce oră, în medie, îți începi ziua în timpul săptămânii?");
            btNext.setText("Următorul");
        }


        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    WeekHourActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION} ,
                    REQUEST_CODE_LOCATION_PERMISSION
            );

        }
        else {
            startLocationService();

        }
    }

    /**
     * Get hour and minute from time picker and save them in SharedPreference
     * @param view
     */
    public void nextActivity(View view) {

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Log.d("Ora", "In saptamana " + hour + ":" + minute);

        editor.putString(getString(R.string.weekHour), hour + "");
        editor.commit();
        editor.putString(getString(R.string.weekMinute), minute + "");
        editor.commit();

        Intent alarmIntent = new Intent(WeekHourActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(WeekHourActivity.this, 0 , alarmIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 00);
       // calendar.add(Calendar.DATE, 1);
        Log.d("Time", calendar.getTime()+"");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        if(language.equals("romana"))
            Toast.makeText(this, "Notificarea a fost setata", Toast.LENGTH_SHORT).show();
        else if (language.equals("english"))
            Toast.makeText(this, "Notification set", Toast.LENGTH_SHORT).show();



        //Move to WeekendsHourActivity
        Intent intent = new Intent(this, LoadingScreen.class);
        startActivity(intent);
        }

        /**
     * Check for permissions and start the service
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            startLocationService();
        }
        else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }


    ///////////////////////////////////////////////////////
    /////////////////////Service methods///////////////////
    ///////////////////////////////////////////////////////

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
            //this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                WeekHourActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private boolean isLocationServiceRunning() {

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            Log.d("Servicii", service.service.getClassName());
            if("services.LocationService".equals(service.service.getClassName())) {
                Log.d("LocationService", "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d("LocationService", "isLocationServiceRunning: location service is not running.");
        return false;
    }














    }

