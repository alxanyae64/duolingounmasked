package eu.ash64.duolingounmasked;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("warnings", "Warnings.", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, NotificationListener.class);
        startService(intent);

        Switch enable_switch = findViewById(R.id.enable_switch);
        enable_switch.setChecked(NotificationListener.isEnabled(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        Switch enable_switch = (Switch) findViewById(R.id.enable_switch);
        enable_switch.setChecked(NotificationListener.isEnabled(this));
    }

    public boolean isDuolingoInstalled() {
        try {
            getPackageManager().getPackageInfo("com.duolingo", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void showDialog() {
        Log.i("dsm", "opening dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setTitle(R.string.dialogTitle);
        builder.setMessage(R.string.dialogMessage);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.enable_switch) {
            if (!NotificationListener.isEnabled(this)) {
                if (!isDuolingoInstalled()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setPositiveButton(R.string.ok, null);
                    builder.setMessage("Duolingo needs to be installed for this app to work. Do so at your own risk.");
                    builder.create().show();
                } else {
                    showDialog();
                }
            } else {
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            }
            ((Switch) v).setChecked(NotificationListener.isEnabled(this));
        }
    }
}