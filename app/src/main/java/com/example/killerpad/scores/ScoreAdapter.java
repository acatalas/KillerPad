package com.example.killerpad.scores;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.killerpad.R;
import java.util.TreeMap;

/**
 * @author Alejandra
 * Adapter class to automatically generate the list of scores
 */
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {
    private final TreeMap<Integer, String> data;
    private final Integer[] keys;

    //ViewHolder that maps the fields where the data will be placed
    public static class ScoreViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUser;
        public TextView tvScore;
        public ScoreViewHolder(View v) {
            super(v);
            tvUser = v.findViewById(R.id.user_score);
            tvScore = v.findViewById(R.id.score_score);
        }
    }

    /**
     * Adapter for the recycled view found in the score activity
     * @param map
     */
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

    /**
     * Gets the score found in the position passed by parameter
     * @param position Position in the list of an element
     * @return Id (score) of the item
     */
    @Override
    public long getItemId(int position) {
        return keys[position];
    }

    @Override
    public int getItemCount() {

        return data.size();
    }
}