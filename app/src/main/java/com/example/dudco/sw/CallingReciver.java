package com.example.dudco.sw;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telecom.Call;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dudco on 2016. 9. 3..
 */
public class CallingReciver extends BroadcastReceiver {

    private static String mLastState;
    public static boolean isRunning = false;
    Context context;
    Intent i;
    CallThread callThread = new CallThread();
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        this.context = context;
        if (state.equals(mLastState)) {
            return;
        } else {
            mLastState = state;
        }

        if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            Log.d("dudco", "통화");

//            Intent i = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
//            i.putExtra("Call", "Start");
//            context.sendBroadcast(i);

//            mThread = new CallThread();
            callThread.start();
        }else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)){
//            Log.d("dudco", "종료");

            Intent i = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_EXAMPLE");
            i.putExtra("Call", "Quick");
            context.sendBroadcast(i);
//            if(callThread != null)
            callThread.stopThread();
        }
    }
    public class CallThread extends Thread{

//        public CallThread(Boolean isRunning) {
//            this.isRunning = isRunning;
//        }

        public void stopThread(){
            isRunning = !isRunning;
            Log.d("dudco" , "종료"+ isRunning);
        }

        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isRunning = true;
                    runAny();
                }
            }, 3000);
        }

        private void runAny() {
            if(isRunning) {
                Log.d("dudco", "시발"+isRunning);
                i = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
                i.putExtra("command", "list");
                context.sendBroadcast(i);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isRunning)
                            runAny();
                    }
                }, 3000);
            }
        }
    }

}
