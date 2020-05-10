package com.example.whatsapptospeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.SynthesisRequest;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import android.speech.tts.TextToSpeech;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "myChannel";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private AlertDialog enableNotificationListenerAlertDialog;

    // Variables
    public TextView dispMsg;
    public TextView dispSender;
    public TextView dispPackage;
    public Switch StartListening;
    public Button SendNotification;
    private ReceiveBroadcastReceiver ReceiveBroadcast;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitializeVariables();
        SubscribeToEvents();
    }

    public void InitializeVariables(){
        StartListening = findViewById(R.id.wts_ListenToNotifications);
        dispSender = findViewById(R.id.wts_dispSender);
        dispPackage = findViewById(R.id.wts_PackageName);
        dispMsg = findViewById(R.id.wts_MsgDisplay);
        SendNotification = (Button) findViewById(R.id.wts_CreateNotification);
        ReceiveBroadcast = new ReceiveBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.whatsapptospeech");
        registerReceiver(ReceiveBroadcast, intentFilter);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.GERMAN);
                    Log.d("ME", "Language set to GERMAN");

                }
                else {
                    TextView errorField = findViewById(R.id.wts_MsgDisplay);
                    errorField.setText("Error setting language");
                }
            }
        });
    }

    // Implements button handling
    private void SubscribeToEvents(){
        // Switch
        StartListening.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String msg = String.valueOf(isChecked);
                GenerateSpeech("This is speech test");
                dispMsg.setText(msg);
            }
        });

        // Button for triggering notification
        SendNotification.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("ME", "Attempting to send a notification");
                GenerateSpeech("Bing bing This is not german");
                CreateNotification();
            }
        });
    }

    // Create a notification
    private void CreateNotification(){
        if (!isNotificationServiceEnabled()){
            Log.d("ME", "Notifications not enabled");
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
        else{
            Log.d("ME", "Notifications enabled?");
        }
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Bing BING BING BING NOTIFICATION TITLE")
                .setContentText("THIS IS A TEXT FROM NOTIFICATION")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        int notId = 0;
        NotificationManagerCompat notifMngr = NotificationManagerCompat.from(this);
        notifMngr.notify(notId, builder.build());
    }

    /**
     * Build Notification Listener Alert Dialog.
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void GenerateSpeech(final String textToSpeak){
        Log.d("ME", "Trying to generate speech");

        tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, null);
    }

    @Override
    protected void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            super.onPause();
        }
    }


    /**
     * Receive Broadcast Receiver.
     * */
    public class ReceiveBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("ME", "Broadcast received");
            int receivedNotificationCode = intent.getIntExtra("Notification Code", -1);
            String packages = intent.getStringExtra("package");
            String title = intent.getStringExtra("Title ");
            String text = intent.getStringExtra("Text ");

            dispMsg.setText(text);
            dispPackage.setText(packages);
            dispSender.setText(title);

        }
    }
}
