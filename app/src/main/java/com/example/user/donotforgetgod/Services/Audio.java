package com.example.user.donotforgetgod.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.user.donotforgetgod.R;

import java.util.Random;

/**
 * Created by User on 3/18/2018.
 */

public class Audio {

    static MediaPlayer mediaPlayer = null;

    public static  void  playAudio(Context context){

        int zakerNumber;
        int audioResourceId = 0 ;

        TelephonyManager telManager  = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        TypedArray audios = context.getResources().obtainTypedArray(R.array.audios);
        SharedPreferences sharedPreferences = context.getSharedPreferences("Setting"
                , Context.MODE_PRIVATE);
        zakerNumber = sharedPreferences.getInt("ZakerNumber", 0);
        Toast.makeText(context,"ZakerNumber:"+zakerNumber,Toast.LENGTH_SHORT).show();
        /**
         * which Audio is play
         * */
        if(zakerNumber == 0) {
            int rnd = (int)( Math.random() * audios.length() );
            audioResourceId = audios.getResourceId(rnd,R.raw.sound0);
        }else{
            audioResourceId = audios.getResourceId(zakerNumber - 1, R.raw.sound0);
        }

        /**
         * Play audio
         * */
        mediaPlayer = MediaPlayer.create(context, audioResourceId);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        final PhoneStateListener phoneListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:
                case TelephonyManager.CALL_STATE_RINGING:
                    try {
                        mediaPlayer.stop();
                    }catch (Exception e){}
                    break;
                case TelephonyManager.CALL_STATE_IDLE: {
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    });
                    break;
                }
            }
            }
        };
        
        telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public static Boolean isRunningAudio(){
        if (mediaPlayer != null){
            try {
                boolean asd = mediaPlayer.isPlaying();
                return asd;
            }catch (Exception e){
                return false;
            }
        }
        return false;
    }

    public static void releaseAudio(){
        if (mediaPlayer != null){
            mediaPlayer.reset();
            mediaPlayer.release();
        }

    }

}
