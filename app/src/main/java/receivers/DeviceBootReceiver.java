package receivers;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.example.bringyourumbrellaAlpha.Home;
import com.example.bringyourumbrellaAlpha.R;
import services.LocationService;

import java.util.Calendar;

public class DeviceBootReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            int hour = Integer.parseInt(sharedPreferences.getString(context.getString(R.string.weekHour), ""));
            int minute = Integer.parseInt(sharedPreferences.getString(context.getString(R.string.weekMinute), ""));

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 00);
            //calendar.add(Calendar.DATE, 1);

            Log.d("Time", calendar.toString());
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            Intent serviceIntent = new Intent(context, LocationService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}

