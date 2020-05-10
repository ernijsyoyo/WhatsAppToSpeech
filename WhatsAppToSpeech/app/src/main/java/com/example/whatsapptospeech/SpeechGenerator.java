package com.example.whatsapptospeech;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SpeechGenerator extends AppCompatActivity {

    TextToSpeech tts;
    public SpeechGenerator(){
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.ENGLISH);
                    Log.d("ME", "Language set to GERMAN");

                }
                else {
                    TextView errorField = findViewById(R.id.wts_MsgDisplay);
                    errorField.setText("Error setting language");
                }
            }
        });
    }

}
