package com.example.bringyourumbrellaAlpha;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import services.LocationService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /** Create sharedPreferences object */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /** Check if this is the first run after install and if so, it will open the setup activity
         * else, it will run the home activity
         */


        String weekTime = sharedPreferences.getString(getString(R.string.weekHour), "");




        Intent intent;
        if( weekTime.isEmpty()){


            intent = new Intent(this, LanguageSelect.class);

        }
        else {
            intent = new Intent(this, Home.class);
        }
        startActivity(intent);

    }


}
