package com.mohyaghoub.calculator;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class SmartRoom extends AppCompatActivity   {


    public static InformationCollector informationCollector;

    private FloatingActionButton listen;
    private ProgressBar progressBar,LoadingInformation;
    private TextView displayText,History,ResultsTextView;
    public static ArrayList<Key> keys;
    private ScrollView History_ScrollView,DisplayText_ScrollView;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    public static ArrayList<ListOfKeys> KeysCombined;

    private ListOfKeys GetterKeys,ShowingKeys,ChangerKeys,OpenerKeys,AlternatingKeys,FunctioningKeys;



    //graph View
    private GraphView smart_GraphView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_room);
        initObjects();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void initObjects()
    {

        KeysCombined = new ArrayList<>();
        this.ResultsTextView = findViewById(R.id.ResultsTextView);
        this.LoadingInformation = findViewById(R.id.LoadingInformation);
        this.listen = findViewById(R.id.Listen_SmartRoom);
        this.progressBar = findViewById(R.id.progressBar_SmartRoom);
        this.displayText = findViewById(R.id.DisplayText_SmartRoom);
        this.History = findViewById(R.id.History_SmartRoom);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.History_ScrollView = findViewById(R.id.History_ScrollView_Smart);
        this.DisplayText_ScrollView = findViewById(R.id.DisplyText_ScrollView_Smart);
        this.mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        this.mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        this.mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        this.mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
//        this.mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,"1000");
        keys = new ArrayList<>();
        this.smart_GraphView = findViewById(R.id.Smart_GraphView);
        if(SmartRoom.informationCollector==null)
        {
            SmartRoom.informationCollector = new InformationCollector(this,this.smart_GraphView,this.History_ScrollView,this.DisplayText_ScrollView);
//            Toast.makeText(this,"Created new one",Toast.LENGTH_SHORT).show();
        }
        createListOfKeysArray();
        addKeysToArray();
        setOnClickListenerForSmart();
        setOnClickListenerForSpeechListener();
        setOnClickListenerForProgressBar();
    }

    private void createListOfKeysArray()
    {
        this.GetterKeys = new ListOfKeys("GETTER", KeysAndSubKeys.GetterKeys);
        this.ShowingKeys= new ListOfKeys("SHOWING", KeysAndSubKeys.ShowingKeys);
        this.ChangerKeys = new ListOfKeys("CHANGER", KeysAndSubKeys.ChangerKeys);
        this.OpenerKeys = new ListOfKeys("OPENER", KeysAndSubKeys.OpenerKeys);
        this.AlternatingKeys = new ListOfKeys("ALTERNATOR", KeysAndSubKeys.AlternatingKeys);
        this.FunctioningKeys = new ListOfKeys("FUNCTIONING", KeysAndSubKeys.FunctioningKeys);
    }


    private void addKeysToArray()
    {
        KeysCombined.add(this.GetterKeys);
        KeysCombined.add(this.ShowingKeys);
        KeysCombined.add(this.ChangerKeys);
        KeysCombined.add(this.OpenerKeys);
        KeysCombined.add(this.AlternatingKeys);
        KeysCombined.add(this.FunctioningKeys);
    }


    private void setOnClickListenerForSmart()
    {
        this.listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideHistory();
                requestAudioPermissions();
            }
        });
    }

    private void hideHistory()
    {
        if(History_ScrollView.getVisibility()==View.VISIBLE)
        {
            SmartRoom.informationCollector.hideHistory();
        }
    }





    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                       1);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now

//            AudioManager audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            listenLayout();
//            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
    }
    private void setOnClickListenerForSpeechListener()
    {
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onBeginningOfSpeech() {
                // TODO Auto-generated method stub


            }

            @Override
            public void onBufferReceived(byte[] arg0) {
                // TODO Auto-generated method stub


            }

            @Override
            public void onEndOfSpeech() {
                // TODO Auto-generated method stub


            }

            @Override
            public void onError(int arg0) {
                // TODO Auto-generated method stub
                    stopListeningLayout();
            }

            @Override
            public void onEvent(int arg0, Bundle arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // TODO Auto-generated method stub
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                displayText.setText(matches.get(0));
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                // TODO Auto-generated method stub


            }

            @Override
            public void onResults(Bundle results) {
                // TODO Auto-generated method stub
                hideHistory();
                stopListeningLayout();
                LoadingInformation.setVisibility(View.VISIBLE);
                LoadingInformation.setIndeterminate(true);
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String textRecognized = matches.get(0);
                SmartRoom.informationCollector.addHistory(SmartRoom.informationCollector.getResult());
                History.setText(SmartRoom.informationCollector.getHistory());
                SmartRoom.informationCollector.clearResults();
                separateKeys(textRecognized);
                displayText.setText(textRecognized);
                ResultsTextView.setText(SmartRoom.informationCollector.getResult());
                requestAudioPermissions();
                LoadingInformation.setIndeterminate(false);
                LoadingInformation.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // TODO Auto-generated method stub

            }

        });
    }
    private void setOnClickListenerForProgressBar()
    {
        this.progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopListeningLayout();
                mSpeechRecognizer.stopListening();
            }
        });
    }




    private void separateKeys(String text)
    {
        String sepEverything[] = text.split(" ");
        int CurrentPosition = 0;
        String nameOfKeyList = "";
        SmartRoom.informationCollector.setContinue(false);

        for(int i =0; i<sepEverything.length;i++)
        {
            if(!SmartRoom.informationCollector.getContinue()) {
                String word = sepEverything[i].toLowerCase();
                int length = word.length();
                if (i != 0 && length >= 3) {
                    for (ListOfKeys keyList : KeysCombined) {
                        String list[] = keyList.getKeys();
                        for (int p = 0; p < list.length; p++) {
                            if (word.equals(list[p])) {
                                String parseThis = "";
                                for (int s = CurrentPosition; s < i; s++) {
                                    parseThis += sepEverything[s] + " ";
                                }
                                CurrentPosition = i;
                                Key newKey = new Key(parseThis, nameOfKeyList, keys.size());
                                keys.add(newKey);
                                nameOfKeyList = keyList.getName();
                                break;
                            }
                        }
                    }
                } else if (i == 0 && length >= 3) {
                    boolean stop = false;
                    for (ListOfKeys keyList : KeysCombined) {
                        if (stop == false) {
                            String list[] = keyList.getKeys();
                            for (int p = 0; p < list.length; p++) {
                                if (word.equals(list[p])) {
                                    nameOfKeyList = keyList.getName();
                                    stop = true;
                                    break;
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if(!SmartRoom.informationCollector.getContinue())
        {
            String parseThis = "";
            for(int j = CurrentPosition;j<sepEverything.length;j++)
            {
                parseThis+=sepEverything[j]+" ";
            }
            Key newKey = new Key(parseThis,nameOfKeyList,keys.size());
            keys.add(newKey);
        }

    }



    private void listenLayout()
    {
        if(progressBar.getVisibility()!=View.VISIBLE)
        {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            listen.setVisibility(View.INVISIBLE);
        }
    }
    private void stopListeningLayout()
    {
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setIndeterminate(false);
        listen.setVisibility(View.VISIBLE);
    }


    private void stopListening()
    {
        this.mSpeechRecognizer.stopListening();
        this.mSpeechRecognizer.destroy();
    }


    @Override
    public void onDestroy()
    {
        stopListening();
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        RefreshPage();
    }

    private void RefreshPage()
    {
        SmartRoom.informationCollector.update(this,this.smart_GraphView,this.History_ScrollView,this.DisplayText_ScrollView);
        if(SmartRoom.informationCollector.wasInCalculatorMode())
        {
            ResultsTextView.setText(SmartRoom.informationCollector.getResult());
        }
    }










}
