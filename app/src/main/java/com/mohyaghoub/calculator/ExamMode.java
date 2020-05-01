package com.mohyaghoub.calculator;

import android.app.ActivityManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class ExamMode {

    private boolean FIRST_TIME_ALARM, ACTIVATED_EXAM_MODE, SCREEN_IS_OFF;
    private Context context;

    ExamMode(Context mainContext)
    {
        this.context = mainContext;
        this.FIRST_TIME_ALARM = true;
        this.ACTIVATED_EXAM_MODE = false;
        this.SCREEN_IS_OFF = false;




    }

    public void checkExamMode()
    {
        if(this.activateExamMode())
        {
            this.EnterExamMode();
            MainActivity.ExamModeConfirm.setVisibility(View.VISIBLE);
            MainActivity.ExamModeConfirm.startAnimation(MainActivity.downToUp);
        }
//        else if(this.isACTIVATED_EXAM_MODE())
//        {
//            this.EnterExamMode();
//        }
    }


    public void EnterExamMode()
    {
            setACTIVATED_EXAM_MODE(true);
            WifiManager wifiManager = (WifiManager) this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(false);
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(!isSCREEN_IS_OFF()&&!isAppInLockTaskMode()&&isACTIVATED_EXAM_MODE()&&isFIRST_TIME_ALARAM())
                    {
                        startAlarm();
                    }
                }
            },100,100);
    }

    public void startAlarm()
    {
//        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        context.sendBroadcast(closeDialog);

        AudioManager am =
            (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        am.setSpeakerphoneOn(true);

        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm_exam_mode);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        AudioManager.OnAudioFocusChangeListener focusChangeListener =
                new AudioManager.OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        switch (focusChange) {

                            case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                                // Lower the volume while ducking.
//                                mediaPlayer.setVolume(0.2f, 0.2f);
                                break;
                            case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                                break;

                            case (AudioManager.AUDIOFOCUS_LOSS):

                                break;

                            case (AudioManager.AUDIOFOCUS_GAIN):
                                // Return the volume to normal and resume if paused.
                                mediaPlayer.start();
                                break;
                            default:
                                break;
                        }
                    }
                };
       int res =  am.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

       if(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
       {
           mediaPlayer.start();
       }

        setFIRST_TIME_ALARM(false);
    }




    public boolean isSCREEN_IS_OFF() {
        return SCREEN_IS_OFF;
    }

    public boolean isFIRST_TIME_ALARAM() {
        return FIRST_TIME_ALARM;
    }

    public void setFIRST_TIME_ALARM(boolean FIRST_TIME_ALARM) {
        this.FIRST_TIME_ALARM = FIRST_TIME_ALARM;
    }

    public Context getContext() {
        return context;
    }

    public boolean isACTIVATED_EXAM_MODE() {
        return ACTIVATED_EXAM_MODE;
    }

    public void setACTIVATED_EXAM_MODE(boolean ACTIVATED_EXAM_MODE) {
        this.ACTIVATED_EXAM_MODE = ACTIVATED_EXAM_MODE;
    }

    public boolean isAppInLockTaskMode() {
        ActivityManager activityManager;

        activityManager = (ActivityManager)
                this.context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For SDK version 23 and above.
            return activityManager.getLockTaskModeState()
                    != ActivityManager.LOCK_TASK_MODE_NONE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // When SDK version >= 21. This API is deprecated in 23.
            return activityManager.isInLockTaskMode();
        }

        return false;
    }

    public boolean activateExamMode()
    {
        return !ACTIVATED_EXAM_MODE && isAppInLockTaskMode();
    }



}
