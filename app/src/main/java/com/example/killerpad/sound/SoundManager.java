package com.example.killerpad.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.killerpad.R;

/**
 * @author Alejandra
 * Singleton class charged with playing the sounds
 */
public class SoundManager {
    private final int SHOOT_SOUND;
    private final int TURBO_SOUND;
    private final int DASH_SOUND;
    private final int RELOAD_SOUND;
    private final int NO_BULLET_SOUND;
    private final int VICTORY_SOUND;
    private final int POWER_UP_SOUND;
    private final int HIT_SOUND;

    private int turboStream;
    private boolean mpIsReleased = false;

    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;

    private static SoundManager soundManager;

    /**
     * Creates the soundpool and the mediaplayer instances, and obtains the identifier of each stream of
     * the audio files added to the soundpool
     * @param context Context to obtain the applications resources
     */
    private SoundManager(Context context){
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        SHOOT_SOUND = soundPool.load(context, R.raw.shot, 1);
        TURBO_SOUND = soundPool.load(context, R.raw.boost, 1);
        DASH_SOUND = soundPool.load(context, R.raw.jump, 1);
        RELOAD_SOUND = soundPool.load(context, R.raw.reloaded, 1);
        NO_BULLET_SOUND = soundPool.load(context, R.raw.no_shots, 1);
        VICTORY_SOUND = soundPool.load(context, R.raw.victory, 1);
        POWER_UP_SOUND = soundPool.load(context, R.raw.power_up,1);
        HIT_SOUND = soundPool.load(context, R.raw.hit_marker, 1);

        //Choses death sound randomly
        if(Math.random() < 0.5){
            mediaPlayer = MediaPlayer.create(context, R.raw.soul_die );
        }else {
            mediaPlayer = MediaPlayer.create(context, R.raw.wasted_die);
        }

    }

    /**
     * Gets or creates the class's instance
     * @param context Context used to obtain the resources
     * @return Unique soundManager instance
     */
    public static SoundManager getInstance(Context context){
        if (soundManager == null){ //if there is no instance available... create new one
            soundManager = new SoundManager(context);
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
        turboStream = soundPool.play(TURBO_SOUND, 1, 1, 0, 0,1);
    }

    public void stopTurboSound(){
        soundPool.stop(turboStream);
    }

    public void playDeathSound(){
        mediaPlayer.start();
    }

    public void playHitSound() {soundPool.play(HIT_SOUND, 1, 1, 0, 0, 1);}

    public void playReloadSound() { soundPool.play(RELOAD_SOUND, 1, 1, 0, 0, 1);}

    public void playNoBulletSound() { soundPool.play(NO_BULLET_SOUND, 1, 1, 0, 0, 1); }

    public void playVictorySound() { soundPool.play(VICTORY_SOUND, 1, 1, 0, 0, 1);}

    public void playPowerUpSound() { soundPool.play(POWER_UP_SOUND, 1, 1, 0, 0, 1); }

    /**
     * Closes the soundpool and the mediaplayer, and sets the soundmanager to null
     */
    public void dispose(){
        soundPool.release();

        if(mediaPlayer != null && !mpIsReleased){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }

            mediaPlayer.release();
            mpIsReleased = true;
        }

        soundManager = null;
    }
}
