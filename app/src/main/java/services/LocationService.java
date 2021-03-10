package services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import com.example.bringyourumbrellaAlpha.R;
import com.google.android.gms.location.*;



public class LocationService extends Service {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String TAG = "LocationService";


    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_LOW);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Location Service")
                    .setPriority(-2)
                    .setContentText("I'm a little helper to get your location").build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        return START_STICKY;
    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResult: got location result.");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                           // SharedPreferences.Editor editor = sharedPreferences.edit();

                            Log.d("Coords", location.getLatitude() + "/" + location.getLongitude());
                            editor.putString(getString(R.string.latitude), location.getLatitude() + "" );
                            editor.commit();
                            editor.putString(getString(R.string.longitude), location.getLongitude() + "");
                            editor.commit();

                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    /** private void saveUserLocation(final UserLocation userLocation){

       try{
            DocumentReference locationRef = FirebaseFirestore.getInstance()
                    .collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getUid());

            locationRef.set(userLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: \ninserted user location into database." +
                                "\n latitude: " + userLocation.getGeo_point().getLatitude() +
                                "\n longitude: " + userLocation.getGeo_point().getLongitude());
                    }
                }
            });
        }catch (NullPointerException e){
            Log.e(TAG, "saveUserLocation: User instance is null, stopping location service.");
            Log.e(TAG, "saveUserLocation: NullPointerException: "  + e.getMessage() );
            stopSelf();
        }

    } */




}
