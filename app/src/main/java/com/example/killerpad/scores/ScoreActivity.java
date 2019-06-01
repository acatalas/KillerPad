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

public class ScoreActivity extends Activity {

    private Set<String> scores;
    private TreeMap<Integer, String> orderedScores;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_scores);
        recyclerView = findViewById(R.id.score_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        scores = SharedPreferencesManager.getScores(this);

        //Initialize with custom comparator to show reverse order
        orderedScores = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });

        //saves the scores to the
        orderedScores.putAll(mapSetToTable(scores));

        // specify an adapter (see also next example)
        adapter = new ScoreAdapter(orderedScores);
        recyclerView.setAdapter(adapter);
    }

    private TreeMap<Integer, String> mapSetToTable(Set<String> set){
        TreeMap<Integer, String> scoreTable = new TreeMap<>();
        String userName;
        Integer punct;

        for(String score : set){
            userName = score.split(",")[1];
            punct = new Integer(score.split(",")[0]);
            scoreTable.put(punct, userName);
        }
        return scoreTable;
    }

}
