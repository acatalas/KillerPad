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

    /**
     * Gets the value of the Shared Preferences defined by the key, with a default value
     * in case it is empty
     * @param context Context of the application
     * @param key   Key of the sharedPreferences
     * @param defaultValue  Value to return in case no value was found
     * @return  Value stored in shared preferences or default
     */
    public static String getString(Context context, String key, String defaultValue){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Saves the value in the specified shared preference
     * @param context Context of the application
     * @param key   Key of the sharedPreferences
     * @param value Value to save
     */
    public static void saveString(Context context, String key, String value){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    /**
     * Gets a list of all the scores
     * @param context   Context of the application
     * @return  Set of string representation of the score (score,user)
     */
    public static Set<String> getScores(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(SCORES_KEY, new HashSet<String>());
    }

    /**
     * Adds a score and the user that achieved the score into the set of sharedPreferences, only
     * saving top ten scores
     * @param context Context of the application
     * @param user User that achieved the score
     * @param score Score obtained
     */
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

    /**
     * Gets the lowest score, in case the number of scores is larger than 10, it will be replaced
     * @param scoreSet Set of scores
     * @return Lowest score
     */
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
