package com.example.killerpad.sound;

import android.content.Context;
import android.media.SoundPool;

import com.example.killerpad.R;

public class AudioManager {
    private static AudioManager audioManager;
    private SoundPool soundPool;
    private int dashSound;
    private int shotSound;
    private int turboSound;

    private AudioManager(Context context){
        soundPool = new SoundPool(2, android.media.AudioManager.STREAM_MUSIC, 0);
        loadSounds(context);
    }

    private void loadSounds(Context context) {
        dashSound = soundPool.load(context, R.raw.dash, 2);
        shotSound = soundPool.load(context, R.raw.shot, 1);
        //TODO Ask about turbo sound
        //turboSound = soundPool.load(context, R.raw.turbo, 2);
    }

    /**
     * Metodo para obtener una instancia del audio manager
     * @param context activity context where the class will be used
     * @return audio manager
     */
    public static AudioManager getPlayer(Context context){
        if (audioManager == null){ //if there is no instance available... create new one
            audioManager = new AudioManager(context);
        }
        return audioManager;
    }

    /**
     * Reproduce el sonido de disparo
     */
    public void playShotSound(){
        soundPool.play(shotSound, 1, 1, 1, 0,1f);
    }

    /**
     * Reproduce el sonido del dash (?)
     */
    public void playDashSound(){
        soundPool.play(dashSound, 1, 1, 0, 0,1f);
    }

    /*public void playTurboSound(){
        soundPool.play(turboSound, 1, 1, 0, 0,1f);
    }

    public void stopTurboSound() {soundPool.stop(turboSound);}*/

    /**
     * Closes the audio manager instance
     */
    public void close(){
        soundPool.release();
        soundPool = null;
        audioManager = null;
    }
}
