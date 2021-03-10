package com.example.bringyourumbrellaAlpha;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import receivers.AlarmReceiver;
import services.LocationService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Home extends AppCompatActivity {

    String urlString;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private String longitude;
    private String latitude;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



         // Get language preferred
         sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
         editor = sharedPreferences.edit();
         language = sharedPreferences.getString(getString(R.string.language), "");


         //Check if the permission is already granted and run service
        // Else, request permission
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    Home.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION} ,
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        }
        else {
            startLocationService();
        }


        /**
         * Get weather info based on location
         */
        try {
            longitude = sharedPreferences.getString(getString(R.string.longitude), "0");
            latitude = sharedPreferences.getString(getString(R.string.latitude), "0");

            //api.openweathermap.org/data/2.5/weather?lat=44.350410&lon=24.102470&units=metric&appid=ApiKey
             urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + MyApp.API_KEY;
            new CheckWeather().execute(new URL(urlString));
        } catch (Exception exception){
            Log.d("eroare", exception.toString());
        }
    }


    /////////////////////////////////////////////////////////////////
    ////////////////////////Menu options/////////////////////////////
    /////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    /**
     * Change titles based on language
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        MenuItem itemReset = menu.findItem(R.id.reset);
        MenuItem itemAbout = menu.findItem(R.id.about);

        if(language.equals(getString(R.string.romana))) {
            itemReset.setTitle("Resetare");
            itemAbout.setTitle("Despre aplicație");
        }
        else if(language.equals("english")){
            itemReset.setTitle("Reset");
            itemAbout.setTitle("About");
        }

        return  super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                Intent intent1 = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent =  PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                Intent intent = new Intent(this, LanguageSelect.class);
                startActivity(intent);
                return true;
            case R.id.about:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //////////////////////
    //////Check Weather///
    //////////////////////
    /**
     * AsyncTask Class for checking weather
     */
    class CheckWeather extends AsyncTask<URL, Void, Map<String, Object>> {

        /**
         *
         * @param urls Urls for making connection.
         *             Will use only one, the url for user's location
         * @return  Map with all weather information
         */
        protected Map<String, Object> doInBackground(URL... urls) {

            try {
                URL url = urls[0];
                StringBuilder result = new StringBuilder();

                URLConnection conn = url.openConnection();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                rd.close();

                Map<String, Object> resMap = jsonToMap(result.toString());



                return resMap;


            } catch (Exception exception) {
                Log.d("Eroare", exception.toString());
                return new Map<String, Object>() {
                    @Override
                    public int size() {
                        return 0;
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public boolean containsKey(@Nullable Object key) {
                        return false;
                    }

                    @Override
                    public boolean containsValue(@Nullable Object value) {
                        return false;
                    }

                    @Nullable
                    @Override
                    public Object get(@Nullable Object key) {
                        return null;
                    }

                    @Nullable
                    @Override
                    public Object put(String key, Object value) {
                        return null;
                    }

                    @Nullable
                    @Override
                    public Object remove(@Nullable Object key) {
                        return null;
                    }

                    @Override
                    public void putAll(@NonNull Map<? extends String, ?> m) {

                    }

                    @Override
                    public void clear() {

                    }

                    @NonNull
                    @Override
                    public Set<String> keySet() {
                        return null;
                    }

                    @NonNull
                    @Override
                    public Collection<Object> values() {
                        return null;
                    }

                    @NonNull
                    @Override
                    public Set<Entry<String, Object>> entrySet() {
                        return null;
                    }
                };

            }


        }

        @Override
        protected  void onPostExecute(Map<String, Object> map) {
            try {


            TextView textView = findViewById(R.id.tfHome);
            Map<String, Object> mainMap = jsonToMap(map.get("main").toString());

            textView.setText("Today's temperature in " + map.get("name").toString() + " is " +
                    mainMap.get("temp").toString());

            }catch (NullPointerException exception){
                TextView textView = findViewById(R.id.tfHome);
                if(language.equals("romana"))
                    textView.setText("Verifică conexiunea la internet și redeschideti aplicația " +
                            "sau apăsați pe \u22EE -> Resetare");
                else if (language.equals("english"))
                    textView.setText("Check your internet connection and reopen the app or" +
                            " go to \u22EE -> Reset");
            }


            catch (Exception e){
                TextView textView = findViewById(R.id.tfHome);
                textView.setText(e.toString());
            }
        }
        }



    /////////////////////////////////////////////////////////////////
    /////////////////////////////Json to Map/////////////////////////
    /////////////////////////////////////////////////////////////////
    public static  Map<String, Object> jsonToMap(String string ){
        Map<String, Object> map = new Gson().fromJson(string, new TypeToken<HashMap<String, Object>>() {}.getType());

        return map;
    }



    ////////////////////////////////////////////////////////
    //////////////////Disable back button //////////////////
    ////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Your are home already", Toast.LENGTH_SHORT).show();
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

                Home.this.startForegroundService(serviceIntent);
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




