package com.example.killerpad.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.killerpad.R;

public class SoundManager {
    private final int SHOOT_SOUND;
    private final int BOOST_SOUND;
    private final int DASH_SOUND;

    private Context context;
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;

    private static SoundManager soundManager;

    private SoundManager(){
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        SHOOT_SOUND = soundPool.load(context, R.raw.shot, 1);
        BOOST_SOUND = soundPool.load(context, R.raw.boost, 1);
        DASH_SOUND = soundPool.load(context, R.raw.jump, 1);

        if(Math.random() < 0.5){
            mediaPlayer = MediaPlayer.create(context, R.raw.soul_die );
        }else {
            mediaPlayer = MediaPlayer.create(context, R.raw.wasted_die);
        }

    }

    public static SoundManager getInstance(Context context){
        if (soundManager == null){ //if there is no instance available... create new one
            soundManager = new SoundManager();
        }
        return soundManager;
    }

    public void playShootSound(){
        soundPool.play(SHOOT_SOUND, 1,1,0,0, 1);
    }

    public void playDashSound(){
        soundPool.play(DASH_SOUND, 1,1,0,0, 1);
    }

    public void startTurboSound(){
        soundPool.play(BOOST_SOUND, 1, 1, 0, 0,1);
    }

    public void stopTurboSound(){
        soundPool.stop(BOOST_SOUND);
    }

    public void playDeathSound(){
        mediaPlayer.start();
    }

    public void dispose(){
        soundPool.release();

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();

        soundManager = null;
    }
}
