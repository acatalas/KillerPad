package com.example.killerpad;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import com.example.killerpad.comunications.PadHandler;
import com.example.killerpad.comunications.Message;
import com.example.killerpad.sound.SoundManager;

public class ButtonsFragment extends Fragment implements View.OnClickListener {
    private Button bSend;
    private Button bDash;
    private Button bDisabled;
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

        this.bDisabled = v.findViewById(R.id.disabledSend);
        bDisabled.setOnClickListener(this);

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
            if(bullets==0){
                boardFragment.reloadAnimation();
                soundManager.playReloadSound();
                bSend.setVisibility(View.INVISIBLE);
                bDisabled.setVisibility(View.VISIBLE);

                //Duerme la ejecución hasta que se acaba la animación
                new CountDownTimer(2000,100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        System.out.println("Intervalo");
                    }

                    @Override
                    public void onFinish() {
                        System.out.println("Sigo la ejecución");
                        bDisabled.setVisibility(View.INVISIBLE);
                        boardFragment.bulletCounter(bullets);
                        bSend.setVisibility(View.VISIBLE);
                        setBullets(5);
                    }
                }.start();
            }
            else{
                boardFragment.bulletCounter(this.bullets);
                this.bullets--;
            }

         }
         //boton que solo se muestra cuando se está ejecutando la animación de recarga
         else if (button == R.id.disabledSend){
             soundManager.playNoBulletSound();
        }
    }

    public void setBullets(int bullets){
        this.bullets = bullets;
    }
}
