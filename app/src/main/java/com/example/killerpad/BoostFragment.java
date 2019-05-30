package com.example.killerpad;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.killerpad.comunications.Handler;
import com.example.killerpad.comunications.Message;

public class BoostFragment extends Fragment implements View.OnClickListener {

    private Button bBoost;
    private PadActivity activity;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_boost, container, false);

        //almacena el handler.
        this.handler = ((PadActivity) this.getActivity()).getHandler();

        //Añade los Listeners al botón de boost
        this.bBoost = v.findViewById(R.id.boost_btn);
        bBoost.setOnClickListener(this);

        this.activity = (PadActivity)getActivity();

        return v;
    }

    @Override
    public void onClick(View v) {

        int button = v.getId();

        //botón de dash
        if (button == R.id.boost_btn) {
            this.handler.sendKillerAction(Message.TURBO_START_COMMAND);
        }
        this.handler.sendKillerAction(Message.TURBO_END_COMMAND);
    }
}