package com.example.killerpad;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.killerpad.comunications.Handler;
import com.example.killerpad.comunications.Message;
import com.example.killerpad.sound.SoundManager;

public class BoostFragment extends Fragment {

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
        bBoost.setOnTouchListener(new BoostListener());

        this.activity = (PadActivity)getActivity();

        return v;
    }

    /*@Override
    public void onTouch(View v) {

        int button = v.getId();

        //botón de dash
        if (button == R.id.boost_btn) {
            this.handler.sendKillerAction(Message.TURBO_START_COMMAND);
        }
        this.handler.sendKillerAction(Message.TURBO_END_COMMAND);
    }*/

    private class BoostListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {

                handler.sendKillerAction(Message.TURBO_START_COMMAND);
                SoundManager.getInstance(getActivity()).startTurboSound();

            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                handler.sendKillerAction(Message.TURBO_END_COMMAND);
                SoundManager.getInstance(getActivity()).stopTurboSound();
            }

            return true;
        }

    }
}
