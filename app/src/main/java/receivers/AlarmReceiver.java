package receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.example.bringyourumbrellaAlpha.Home;
import com.example.bringyourumbrellaAlpha.MyApp;
import com.example.bringyourumbrellaAlpha.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AlarmReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    String language;

    @Override
    public void onReceive(final Context context, Intent intent) {


        // Get weather id
        class CheckWeather extends AsyncTask<URL, Void, StringBuilder> {

            /**
             *
             * @param urls Urls for making connection.
             *             Will use only one, the url for user's location
             * @return  Map with all weather information
             */
            protected StringBuilder doInBackground(URL... urls) {
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
                    Log.d("Vremea", result.substring(result.indexOf("\"id\"") + 5, result.indexOf("\"id\"") + 8));
                    return result;
                } catch (Exception exception) {
                    Log.d("Eroare", exception.toString());
                    return new StringBuilder("");
                }
            }

            @Override
            protected  void onPostExecute(StringBuilder result) {
                try {
                    Log.d("alarma", result + "");
                    int weatherId = Integer.parseInt(result.substring(result.indexOf("\"id\"") + 5, result.indexOf("\"id\"") + 8));
                    Log.d("Vremea", weatherId + "");
                    /**
                     * 200-232 Thunderstorm
                     * 300-321 Drizzle
                     * 500-531 Rain
                     * 600-622 Snow
                     * 701-721 Atmosphere
                     *  701 Mist
                     *  711 Smoke
                     *  721 Haze
                     *  731 Dust
                     *  741 Fog
                     *  751 Sand
                     *  761 Dust
                     *  762 Ash
                     *  771 Squall
                     *  781 Tornado
                     * 800     Clear
                     * 801-804 Clouds
                     */
                    Log.d("Vremea", weatherId + "");
                    if (isBetween(weatherId, 200, 232))
                        createThunderstormNotification(context);
                    else if (isBetween(weatherId, 300, 321))
                        createDrizzleNotification(context);
                    else if (isBetween(weatherId, 500, 531))
                        createRainNotification(context);
                    else if (isBetween(weatherId, 600, 622))
                        createSnowNotification(context);
                    else if (isBetween(weatherId, 701, 781)) {
                        switch (weatherId) {
                            case 701:
                                createMistNotification(context);
                                break;
                            case 711:
                                createSmokeNotification(context);
                                break;
                            case 721:
                                createHazeNotification(context);
                                break;
                            case 731:
                                createDustNotification(context);
                                break;
                            case 741:
                                createFogNotification(context);
                                break;
                            case 751:
                                createSandNotification(context);
                                break;
                            case 762:
                                createAshNotification(context);
                                break;
                            case 771:
                                createSquallNotification(context);
                                break;
                            case 781:
                                createTornadoNotification(context);
                                break;
                        }
                    } else if (weatherId == 800)
                        createClearNotification(context);
                    else if (isBetween(weatherId, 801, 804))
                        createCloudsNotification(context);
                    else
                        createErrorNotification(context, "Unexpected Error");

                } catch (Exception exception){
                    Log.d("Eroare", exception.toString());
                    if(language.equals("romana"))
                        createErrorNotification(context, "Verifică conexiunea la internet");
                    else if(language.equals("english"))
                    createErrorNotification(context, "Check Internet Conenction");

                }
            }
        }

        try {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String longitude = sharedPreferences.getString(context.getString(R.string.longitude), "0");
            String latitude = sharedPreferences.getString(context.getString(R.string.latitude), "0");
            language = sharedPreferences.getString(context.getString(R.string.language), "english");

            String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + MyApp.API_KEY;
            new CheckWeather().execute(new URL(urlString));
        } catch (Exception exception) {
            Log.d("eroare", exception.toString());
            if(language.equals("romana"))
                createErrorNotification(context, "Verifică conexiunea la internet");
            else if(language.equals("english"))
                createErrorNotification(context, "Check Internet Conenction");
        }
    }


    private void createTornadoNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.mist)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);
        if(language.equals("romana")){
            notification
                    .setContentTitle("Tornada. Ai nevoie de umbrela.")
                    .setContentText("Desi nu o sa fie de mare ajutor.");
        }
        else if (language.equals("english")){
            notification
                    .setContentTitle("Tornado. You need an umbrella.")
                    .setContentText("Although it won't be very helpful.");
        }

        notificationMan.notify(2, notification.build());
    }

    private void createSquallNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.mist)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);
        if(language.equals("romana")){
            notification
                    .setContentTitle("Vijelie. Nu e nevoie de umbrela.")
                    .setContentText("De preferat sa ramai acasa");
        }
        else if(language.equals("english")){
            notification
                    .setContentTitle("Squall. You don'n need an umbrella.")
                    .setContentText("You should stay home.");
        }


        notificationMan.notify(2, notification.build());
    }

    private void createAshNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.mist)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);
        if (language.equals("romana")){
            notification
                    .setContentTitle("Cenușă.")
                    .setContentText("O poți lua la tine în caz de orice.");
        }
        else if (language.equals("english")){
            notification
                    .setContentTitle("Ash")
                    .setContentText("You don't need an umbrella at all");
            }


        notificationMan.notify(2, notification.build());
    }

    private void createSandNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.mist)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);
        if(language.equals("romana")){
            notification
                    .setContentTitle("Nisip")
                    .setContentText("O poți lua la tine în caz de orice.");
        }
        else if (language.equals("english")){
            notification
                    .setContentTitle("Sand")
                    .setContentText("You don't need an umbrella at all.");
        }

        notificationMan.notify(2, notification.build());
    }

    private void createDustNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.mist)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);
        if(language.equals("romana")){
            notification
                    .setContentTitle("Praf")
                    .setContentText("O poți lua la tine în caz de orice.");;
        }
        else if(language.equals("english")){
            notification
                    .setContentTitle("Dust")
                    .setContentText("You don't need an umbrella at all.");
        }

        notificationMan.notify(2, notification.build());
    }

    private void createFogNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.mist)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);
        if(language.equals("romana")){
            notification
                    .setContentTitle("Ceață")
                    .setContentText("O poți lua la tine în caz de orice.");
        }
        else if(language.equals("english")){
            notification
                    .setContentTitle("Fog")
                    .setContentText("You don't need an umbrella at all");
        }

        notificationMan.notify(2, notification.build());
    }

    private void createHazeNotification(Context context) {
        createFogNotification(context);
    }

    private void createSmokeNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.mist)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);
        if(language.equals("romana")){
            notification
                    .setContentTitle("Fum")
                    .setContentText("O poți lua la tine în caz de orice.");
        }
        else if(language.equals("english")){
            notification
                    .setContentTitle("Smoke")
                    .setContentText("You don't need an umbrella at all.");
        }

        notificationMan.notify(2, notification.build());
    }

    private void createMistNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.mist)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);

        if(language.equals("romana")){
            notification
                    .setContentTitle("Aburi")
                    .setContentText("O poți lua la tine în caz de orice.");
        }
        else if(language.equals("english")){
            notification
                    .setContentTitle("Mist")
                    .setContentText("You don't need an umbrella at all.");
        }


        notificationMan.notify(2, notification.build());
    }

    private void createCloudsNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.clouds)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);

        if(language.equals("romana")){
            notification
                    .setContentTitle("Înnorat. Nu e nevoie de umbrelă")
                    .setContentText("O poți lua la tine în caz de orice.");
        }
        else if(language.equals("english")){
            notification
                    .setContentTitle("Clouds. You don't need an umbrella.")
                    .setContentText("You can take it with you in any case");
        }
        notificationMan.notify(2, notification.build());
    }

    private void createClearNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.clear)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND)
              ;
        if (language.equals("romana")){
            notification
                .setContentTitle("Astăzi o să fie însorit. Nu ai nevoie de umbrelă.");
        }
        if ((language.equals("english"))){
            notification
                .setContentTitle("Today will be sunny. You don't need an umbrella.");
        }
        notificationMan.notify(2, notification.build());
    }

    private void createSnowNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.snow)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);

        if(language.equals("romana")){
            notification
                    .setContentTitle("Ninsoare. Poți lăsa umbrela acasă")
                    .setContentText("Astăzi se anunță ninsoare. Nu ai neapărat nevoie de umbrela.");
        }
        else if (language.equals("english")) {
            notification
                    .setContentTitle("Snow. You don;t need an umbrella.")
                    .setContentText("It will snow today. You don't really need an umbrella.");
        }

        notificationMan.notify(2, notification.build());
    }

    private void createErrorNotification(Context context, String message) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.ic_error)
                .setContentTitle("Error")
                .setContentText(message)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();


        notificationMan.notify(2, notification);
    }

    private void createRainNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.rain)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);

        if(language.equals("romana")){
            notification
                    .setContentTitle("Ploaie. Trebuie să îți iei umbrela!")
                    .setContentText("Astăzi va ploua. Nu uita umbrela acasă.");
        }
        else if(language.equals("english")){
            notification
                    .setContentTitle("Rain. You need tot take an umbrella with you!")
                    .setContentText("Today will rain. Don't forget your umbrella.");
        }

        notificationMan.notify(2, notification.build());
    }

    private void createDrizzleNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER)
                .setSmallIcon(R.drawable.mist)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);

        if(language.equals("romana")){
            notification
                    .setContentTitle("Burniță. Ai nevoie de umbrelă")
                    .setContentText("O să ai nevoie de umbrelă daca ieși afară");
        }
        else if (language.equals("english")) {
            notification
                    .setContentTitle("Drizzle. You will need an umbrella.")
                    .setContentText("You will need to bring your umbrella");
        }

        notificationMan.notify(2, notification.build());
    }

    private void createThunderstormNotification(Context context) {
        NotificationManagerCompat notificationMan = NotificationManagerCompat.from(context);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, MyApp.CHANNEL_ID_WEATHER);
        notification
                .setSmallIcon(R.drawable.thunderstorm)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND);

        if(language.equals("romana")) {
            notification
                    .setContentTitle("Furtună. Trebuie să îți iei umbrela!")
                    .setContentText("Astăzi se anunță furtună. Nu uita umbrela acasă.");
        }
        else if(language.equals("english")){
            notification
                    .setContentTitle("Thunderstorm! You clearly need an umbrella!")
                    .setContentText("Today will be a thunderstorm. Don't forget your umbrella! ");
        }


        notificationMan.notify(2, notification.build());
    }

    /////////////////////////////
    ///////////Json to Map///////
    /////////////////////////////
    public static Map<String, Object> jsonToMap(String string ){
        Map<String, Object> map = new Gson().fromJson(string, new TypeToken<HashMap<String, Object>>() {}.getType());

        return map;
    }

    
    //Return true if x is between lower and higher
    public boolean isBetween(int x, int lower, int upper){
        return lower <= x && x <= upper;
    }
}
