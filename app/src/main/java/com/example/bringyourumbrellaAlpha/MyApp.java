package com.example.bringyourumbrellaAlpha;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;


public class MyApp extends Application {

    final public static String API_KEY = BuildConfig.ApiKey;
    public static final String CHANNEL_ID_SERVICE = "mainChannel";
    public static final String CHANNEL_ID_WEATHER = "weatherChannel";

    @Override
    public void onCreate() {
        super.onCreate();


        createNotificationChannels();
    }

    private void createNotificationChannels() {
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           // NotificationChannel mainChannel = new NotificationChannel(
            //        CHANNEL_ID_SERVICE, "Main Channel",
           //         NotificationManager.IMPORTANCE_DEFAULT
        //    );

           // mainChannel.setDescription("This is the main channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
          //  manager.createNotificationChannel(mainChannel);


            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID_WEATHER, "Weather Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            manager.createNotificationChannel(notificationChannel);

        }
    }

//}
