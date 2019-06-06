package com.example.killerpad.preferences_manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesManager {

    public static String PREFS_NAME = "savedPrefs";
    public static String SCORES_KEY = "scores";
    public static String USER_KEY = "user";
    public static String PORT_KEY = "port";
    public static String IP_KEY = "ip";
    public static String COLOR_KEY = "color";
    public static String SHIP_KEY = "ship";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static String getString(Context context, String key, String defaultValue){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void saveString(Context context, String key, String value){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    public static Set<String> getScores(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(SCORES_KEY, new HashSet<String>());
    }

    public static void addScore(Context context, String user, int score){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> scoreSet = sharedPreferences.getStringSet(SCORES_KEY, new HashSet<String>());

        //Solo se guardan diez puntuaciones a la vez
        if(scoreSet.size() < 10){
            //Añade la puntuacion al set
            scoreSet.add(score + "," + user);

            //Elimina las scores para que detecte los cambios, ya que solo detecta si la "referencia" cambia
            sharedPreferences.edit().remove(SCORES_KEY).apply();

            //Crea una copia para  que detecte que es un objeto nuevo
            Set<String> copy = new HashSet<>(scoreSet);

            //Guarda las puntuaciones
            sharedPreferences.edit().putStringSet(SCORES_KEY, copy).apply();

        } else if(score > (score = Integer.parseInt(getLowestScore(scoreSet).split(",")[0]))){
            scoreSet.remove(getLowestScore(scoreSet));
            scoreSet.add(score + "," + user);
            sharedPreferences.edit().remove(SCORES_KEY).apply();
            Set<String> copy = new HashSet<>(scoreSet);
            sharedPreferences.edit().putStringSet(SCORES_KEY, copy).apply();
        }
    }

    private static String getLowestScore(Set<String> scoreSet){
        int lowestScore = 100; //nunca habra 100 jugadores juajua
        int currentScore;
        String lowestScoreUser = "";

        for(String score : scoreSet){
            currentScore = Integer.parseInt(score.split(",")[0]);
            if (currentScore < lowestScore){
                lowestScore = currentScore;
                lowestScoreUser = score;
            }
        }
        return lowestScoreUser;
    }
}
