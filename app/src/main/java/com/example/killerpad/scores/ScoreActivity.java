package com.example.killerpad.scores;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.killerpad.R;
import com.example.killerpad.preferences_manager.SharedPreferencesManager;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandra
 * Activity with all the logic of the scores screen, used to display the top ten scores achieved
 * on the device and the respective score (number of kills)
 */
public class ScoreActivity extends Activity {

    private TreeMap<Integer, String> orderedScores;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        recyclerView = findViewById(R.id.score_list);

        // since the score won't be changes while in the score screen, improves performance
        // by setting fixed size
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initialize with custom comparator to show reverse order
        orderedScores = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });

        //saves the scores into the treemap, giving them order
        orderedScores.putAll(mapSetToTable(SharedPreferencesManager.getScores(this)));

        // specify an adapter
        adapter = new ScoreAdapter(orderedScores);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Maps the set containing a string with the score and the user that obtained it, to
     * a TreeMap containing the score as key, and username as value
     */

    private TreeMap<Integer, String> mapSetToTable(Set<String> set){
        TreeMap<Integer, String> scoreTable = new TreeMap<>();
        String userName;
        Integer punct;

        for(String score : set){
            userName = score.split(",")[1];
            punct = Integer.valueOf(score.split(",")[0]);
            scoreTable.put(punct, userName);
        }
        return scoreTable;
    }

}
