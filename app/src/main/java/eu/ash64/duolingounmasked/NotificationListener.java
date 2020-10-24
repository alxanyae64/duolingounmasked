package eu.ash64.duolingounmasked;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import androidx.core.app.NotificationManagerCompat;

public class NotificationListener extends NotificationListenerService {

    private int nextNotification;

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.i("dsm", "listener connected");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.i("dsm", "listener disconnected");
    }

    public static boolean isEnabled(Context context) {
        return NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.getPackageName());
    }

    private void writeLanguage(String title, String text) {
        String[] languages = getResources().getStringArray(R.array.languages);
        String chosenLang = null;
        for (String language : languages) {
            if (title.contains(language)) {
                chosenLang = language;
                break;
            } else if (text.contains(language)) {
                chosenLang = language;
                break;
            }
        }
        if (chosenLang != null) {
            SharedPreferences.Editor editor = getSharedPreferences("dsm", 0).edit();
            Log.i("dsm", "Detected language: "+chosenLang);
            editor.putString("lang", chosenLang);
            editor.apply();
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i("dsm", sbn.getPackageName());

        // since duolingo notifications only come twice a day, another application can be added to the list for testing purposes
        if (sbn.getPackageName().equals("com.duolingo") || ((sbn.getPackageName().equals("org.telegram.messenger")) && false)) {
            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            Log.i("dsm", title);

            String text = extras.getString(Notification.EXTRA_TEXT);
            writeLanguage(title, text);

            Log.i("dsm", "cancelling notification");
            this.cancelNotification(sbn.getKey());
            NotificationIntentService.largeIcon = notification.getLargeIcon();
            NotificationIntentService.enqueueWork(this, NotificationIntentService.createNotificationIntent(this));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}
