package com.example.dudco.sw;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.List;

/**
 * Created by dudco on 2016. 9. 3..
 */
public class NLService extends NotificationListenerService {
    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver nlservicereciver;

    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.dudco.sw.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "**********  onNotificationPosted");
        Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());
        Intent i = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event", "onNotificationPosted :" + sbn.getNotification().tickerText);

        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "********** onNotificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());
        Intent i = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event", "onNotificationRemoved :" + sbn.getPackageName() + "n");
        sendBroadcast(i);
    }

    public class NLServiceReceiver extends BroadcastReceiver {
        Boolean isRunning = false;
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra("command") != null && intent.getStringExtra("command").equals("clearall")) {
                NLService.this.cancelAllNotifications();
            } else if (intent.getStringExtra("command") != null && intent.getStringExtra("command").equals("list")) {
//                if (isRunning) {
                    Intent i1 = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_EXAMPLE");
                    i1.putExtra("notification_event", "=====================");
                    sendBroadcast(i1);
                    for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                        Intent i2 = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_EXAMPLE");
                        Notification mNotification = sbn.getNotification();
                        Bundle extras = mNotification.extras;
                        String title = extras.getString(Notification.EXTRA_TITLE);
//                    Log.d("dudco", title);
                        if (title != null) {
                            i2.putExtra("notification_event", title);
                            sendBroadcast(i2);
                        }
                    }
                    Intent i3 = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_EXAMPLE");
                    i3.putExtra("notification_event", "===== Notification List ====");
                    sendBroadcast(i3);
//                }
            } else if (intent.getStringExtra("Call").equals("Quick")) {
                isRunning = false;
            } else if(intent.getStringExtra("Call").equals("Start")){
                isRunning = true;
            }

        }
    }
}
