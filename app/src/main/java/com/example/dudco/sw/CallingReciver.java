package com.example.dudco.sw;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dudco on 2016. 9. 3..
 */
public class CallingReciver extends BroadcastReceiver {

    private static String mLastState;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        this.context = context;
        if (state.equals(mLastState)) {
            return;
        } else {
            mLastState = state;
        }
        CallThread thread = new CallThread();
        if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            Log.d("dudco", "통화");
            thread.start();
        }else{
            thread.interrupt();
        }
    }

    public class CallThread extends Thread{
        @Override
        public void run() {
            while(true){
                try {
                    Intent i = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
                    i.putExtra("command","list");
                    context.sendBroadcast(i);
                    Thread.sleep(3000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
