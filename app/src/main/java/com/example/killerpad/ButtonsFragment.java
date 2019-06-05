package com.example.killerpad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.killerpad.comunications.PadHandler;
import com.example.killerpad.comunications.Message;
import com.example.killerpad.sound.SoundManager;

public class ButtonsFragment extends Fragment implements View.OnClickListener {
    private Button bSend;
    private Button bDash;
    private PadActivity activity;
    private PadHandler handler;
    private SoundManager soundManager;
    private int bullets = 5;

    private BoardFragment boardFragment;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_buttons, container, false);

        soundManager = SoundManager.getInstance(getActivity());

        //almacena el handler.
        this.handler = ((PadActivity) this.getActivity()).getHandler();

        //Añade los Listeners a los botones de disparo y dash
        this.bSend = v.findViewById(R.id.send);
        bSend.setOnClickListener(this);

        this.bDash = v.findViewById(R.id.dash);
        bDash.setOnClickListener(this);

        this.activity = (PadActivity)getActivity();

        boardFragment = (BoardFragment) activity.getBoardFragment();

        return v;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        soundManager.dispose();
    }

    @Override
    public void onClick(View v) {

        int button = v.getId();

        //botón de dash
        if (button == R.id.dash) {
            this.handler.sendKillerAction(Message.DASH_COMMAND);
            soundManager.playDashSound();
        }
        //botón de disparo
         else if (button == R.id.send) {
            this.handler.sendKillerAction(Message.SHOOT_COMMAND);
            soundManager.playShootSound();
            boardFragment.bulletCounter(this.bullets);
            if(bullets==0){
                setBullets(5);
            }
            else{
                this.bullets--;
            }

        }
    }

    public void setBullets(int bullets){
        this.bullets = bullets;
    }
}
