package eu.ash64.duolingounmasked;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NotificationIntentService extends JobIntentService {

    static final int JOB_ID = 666;
    static int nextNotification;

    static Icon largeIcon;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationIntentService.class, JOB_ID, work);
    }

    public static Intent createNotificationIntent(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        return intent;
    }

    private static Bitmap iconToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private String chooseTitle() {
        String[] titles = getResources().getStringArray(R.array.titles);
        int i = ThreadLocalRandom.current().nextInt(titles.length);
        long time = new Date().getTime();
        Date date = new Date(time+600000+ThreadLocalRandom.current().nextInt(300000));
        String timestr = new SimpleDateFormat("HH:mm").format(date);
        return titles[i].replace("TIME", timestr);
    }

    private String chooseText() {
        Random random = new Random(System.currentTimeMillis());
        String lang = getSharedPreferences("dsm", 0).getString("lang", null);
        String[] texts = getResources().getStringArray(R.array.messages);
        if (lang == null) {
            int i = 0;
            while (texts[i].contains("LANG")) {
                i = random.nextInt(texts.length);
            }
            return texts[i];
        }

        int langTextsId = 0;
        switch (lang) {
            case "Arabic":
                langTextsId = R.array.messages_arabic;
                break;
            case "Chinese":
                langTextsId = R.array.messages_chinese;
                break;
            case "French":
                langTextsId = R.array.messages_french;
                break;
            case "German":
                langTextsId = R.array.messages_german;
                break;
            case "Hindi":
                langTextsId = R.array.messages_hindi;
                break;
            case "Italian":
                langTextsId = R.array.messages_italian;
                break;
            case "Japanese":
                langTextsId = R.array.messages_japanese;
                break;
            case "Russian":
                langTextsId = R.array.messages_russian;
                break;
        }

        if (langTextsId != 0) {
            String[] langTexts = getResources().getStringArray(langTextsId);
            int i = random.nextInt(texts.length + langTexts.length);
            if (i >= texts.length) {
                return langTexts[i - texts.length];
            } else {
                return texts[i].replace("LANG", lang);
            }
        } else {
            int i = random.nextInt(texts.length);
            return texts[i].replace("LANG", lang);
        }
    }


    protected void onHandleWork(Intent intent) {
        Log.i("dsm", "creating new notification");

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.duolingo");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(launchIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = chooseTitle();
        String text = chooseText();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "warnings")
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(iconToBitmap(getDrawable(R.mipmap.ic_notification_large_foreground)))
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        if (largeIcon != null) {
            //builder.setLargeIcon(iconToBitmap(largeIcon.loadDrawable(this)));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (nextNotification != 0) {
            notificationManager.cancel(nextNotification - 1);
        }

        notificationManager.notify(nextNotification, builder.build());
        nextNotification++;
    }

}
