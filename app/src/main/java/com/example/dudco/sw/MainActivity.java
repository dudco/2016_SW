package com.example.dudco.sw;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements BeaconConsumer {
    private TextView txtView;
    private NotificationReceiver nReceiver;
    private BeaconManager beaconManager = null;
    private ArrayList<Beacon> beaconList = new ArrayList<>();

    Boolean beaconRunning = false;
    Boolean isPop = false;

    BeaconTransmitter beaconTransmitter;
    BeaconParser beaconParser;

    public static Boolean isRunning = false;

    SpeechRecognizer mRecognizer;

    Beacon beacon_fire = new Beacon.Builder()
            .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
            .setId2("10231")
            .setId3("100")
            .setManufacturer(0x0118)
            .setTxPower(-59)
            .setDataFields(Arrays.asList(new Long[]{0l}))
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtView = (TextView) findViewById(R.id.text);
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.dudco.sw.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver, filter);

        beaconManager = BeaconManager.getInstanceForApplication(MainActivity.this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(MainActivity.this);

        beaconParser = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");
        beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);

        findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 123);
                    }
                }
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_noti).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtView.setText("");
                NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder ncomp = new NotificationCompat.Builder(MainActivity.this);
                ncomp.setContentTitle("My Notification");
                ncomp.setContentText("Notification Listener Service Example");
                ncomp.setTicker("Notification Listener Service Example");
                ncomp.setSmallIcon(R.mipmap.ic_launcher);
                ncomp.setAutoCancel(true);
                nManager.notify((int) System.currentTimeMillis(), ncomp.build());
            }
        });
        findViewById(R.id.btn_getall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtView.setText("");
                Intent i = new Intent("com.example.dudco.sw.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
                i.putExtra("command", "list");
                sendBroadcast(i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    List<Beacon> c = Collections.synchronizedList((List<Beacon>) beacons);
                    for (Beacon beacon : c) {
                        Log.d("Dudco", beacon.getId2().toString());
                        if (beacon.getId2().toString().equals("10231")) {
                            switch (beacon.getId3().toString()) {
                                case "100":
                                    handler.sendEmptyMessage(100);
                                    break;
                            }
                        }
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
            if (msg.what == 100) {
                if (!isPop) {
//                    Toast.makeText(MainActivity.this, "화재", Toast.LENGTH_SHORT).show();
                    startService(new Intent(MainActivity.this, AlertService.class));
//                    isPop = true;
                }
            }
        }
    };

    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event");
            String call = intent.getStringExtra("Call");

            if (temp != null) {
                if (temp.equals("선린영채")) {
                    if (isRunning == false) {
                        isRunning = true;
                        Log.d("dudco", temp + "와아아아아");

                        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

                        mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
                        mRecognizer.setRecognitionListener(listner);
                        Log.d("dudco", "start listening");
                        mRecognizer.startListening(i);
                    }
                }
                Log.d("dudco", temp);
            }
            if (call != null) {
                if (call.equals("Quick")) {
                    Log.d("dudco", "beacon stop");
                    if (beaconRunning) {
                        Log.d("dudco", "stop");
                        beaconTransmitter.stopAdvertising();
                        beaconRunning = false;
//                        isPop = false;
                    }
                    isRunning = false;
                    mRecognizer.stopListening();
                }
            }
            txtView.setText(txtView.getText() + "\n" + temp);
        }
    }

    public RecognitionListener listner = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(MainActivity.this, "준비 끝", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> strings = (ArrayList<String>) results.get(SpeechRecognizer.RESULTS_RECOGNITION);
            for (String str : strings) {
                if (str.contains("화재")) {
                    beaconManager.unbind(MainActivity.this);
                    if (!beaconRunning) {
                        beaconTransmitter.startAdvertising(beacon_fire);
                        beaconRunning = true;
                    }
                }
            }
            Toast.makeText(MainActivity.this, strings.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onError(int error) {
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };
}


