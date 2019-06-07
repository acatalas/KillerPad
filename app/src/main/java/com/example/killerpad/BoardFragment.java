package com.example.killerpad;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.killerpad.comunications.ShipType;
import com.example.killerpad.preferences_manager.SharedPreferencesManager;
import com.example.killerpad.sound.SoundManager;

public class BoardFragment extends Fragment{
    private TextView scoreTV;
    private ProgressBar healthTV;
    private ImageView shipTV;
    private ShipType ship;
    private TextView reloadView;
    private ProgressBar boostTV;
    private int boost;
    private int health;
    private int color;
    private static String TAG = "handler";

    private ButtonsFragment buttonsFragment;
    private int[] bulletsList = new int[] {R.id.bullet1TV,R.id.bullet2TV,R.id.bullet3TV,R.id.bullet4TV,R.id.bullet5TV};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_board, container, false);

        scoreTV = v.findViewById(R.id.scoreTV);

        reloadView = v.findViewById(R.id.reloadTV);

        //Añadido la nave escogida al principio
        shipTV = v.findViewById(R.id.shipTV);

        color = Color.parseColor(SharedPreferencesManager.getString(getContext(),
                SharedPreferencesManager.COLOR_KEY, "#FF0000"));

        ship = ShipType.valueOf(
                SharedPreferencesManager.getString(getContext(),
                        SharedPreferencesManager.SHIP_KEY,
                        ShipType.OCTANE.name()));



        healthTV = v.findViewById(R.id.healthTV);

        if(ship != ShipType.MARAUDER){

            healthTV.setMax(ship.getHealth());
        }

        health = ship.getHealth();

        boostTV = v.findViewById(R.id.boostTV);
        boost = 100;

        drawShip();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        buttonsFragment = (ButtonsFragment) ((PadActivity)(getActivity())).getButtonsFragment();
    }

    private void askForConfirmation() {

        //Método para terminar la partida: Se cierra la actividad, vuelve al menú y se corta la conexión.
        //Se crea un dialog con dos botones: Aceptar y Cancelar.

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_exit);

        ImageButton btnAceptar = dialog.findViewById(R.id.btn_accept);
        ImageButton btnCancelar = dialog.findViewById(R.id.btn_cancel);

        //evento al pular boton aceptar: configurar ursName, ip, puerto
        btnAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.cancel();
                ((PadActivity) getActivity()).disconnect();

            }
        });

        //evento al pular boton aceptar: configurar ursName, ip, puerto
        btnCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void bulletCounter(int bullet){
        if (bullet == 0){
            for (int i = 0; i<this.bulletsList.length;i++ ) {
                ImageView bulletView = (ImageView) getView().findViewById(bulletsList[i]);
                bulletView.setVisibility(View.VISIBLE);
            }
        }
        else{
            ImageView bulletView = (ImageView) getView().findViewById(bulletsList[bullet-1]);
            bulletView.setVisibility(View.INVISIBLE);
        }
    }

    public TextView getScoreTV(){
        return scoreTV;
    }

    public void updateHealth(int health){
        if(health > this.health){
            SoundManager.getInstance(getActivity()).playPowerUpSound();
        } else {
            SoundManager.getInstance(getActivity()).playHitSound();
        }

        if(ship == ShipType.MARAUDER){
            this.healthTV.setProgress((int)((health * 100) / ship.getHealth()));

        } else {
            this.healthTV.setProgress(health);
        }

        this.health = health;

    }

    private void drawShip(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap shipBitmap;

        if(ship == ShipType.BATMOBILE){
            shipBitmap = ShipDialog.replaceGreenColor(BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.batmobile, options), color);

        }else if(ship == ShipType.OCTANE){
            shipBitmap = ShipDialog.replaceGreenColor(BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.octane, options), color);
        } else {
            shipBitmap = ShipDialog.replaceGreenColor(BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.marauder, options), color);
        }


        shipTV.setImageBitmap(shipBitmap);
    }

    public void reloadAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(2000);
        reloadView.startAnimation(alphaAnimation);
    }

    public void updateBoost(int boost){
        this.boostTV.setProgress(this.boost);
    }



}

