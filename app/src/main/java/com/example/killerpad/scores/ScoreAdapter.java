package com.example.killerpad.scores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.killerpad.R;
import java.util.TreeMap;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {
    private final TreeMap<Integer, String> data;
    private final Integer[] keys;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ScoreViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUser;
        public TextView tvScore;
        public ScoreViewHolder(View v) {
            super(v);
            tvUser = v.findViewById(R.id.user_score);
            tvScore = v.findViewById(R.id.score_score);
        }
    }

    public ScoreAdapter(TreeMap<Integer, String> map) {
        data = map;
        keys = data.keySet().toArray(new Integer[data.size()]);
    }

    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_score, viewGroup, false);

        return new ScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder scoreViewHolder, int i) {
        scoreViewHolder.tvUser.setText(data.get(i));
        scoreViewHolder.tvScore.setText(keys[i] + "");
    }

    @Override
    public long getItemId(int position) {
        return keys[position];
    }


    @Override
    public int getItemCount() {

        return data.size();
    }
}