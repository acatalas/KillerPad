package com.example.killerpad;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.killerpad.comunications.ConnectionResponse;
import com.example.killerpad.preferences_manager.SharedPreferencesManager;

public class BoardFragment extends Fragment{
    private TextView scoreTV;
    private ProgressBar healthTV;
    private ImageView shipTV;
    private String ship;
    private int health;
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

        this.scoreTV = (TextView) v.findViewById(R.id.scoreTV);
        this.healthTV = (ProgressBar) v.findViewById(R.id.healthTV);

        //Añadido la nave escogida al principio
        this.shipTV = (ImageView) v.findViewById(R.id.shipTV);

        ship = SharedPreferencesManager.getString(getContext(), SharedPreferencesManager.SHIP_KEY, ConnectionResponse.ShipType.OCTANE.name());
        if (ship == "MARAUDER"){
            this.shipTV.setBackgroundResource(R.drawable.marauder);
        }
        else if (ship == "BATMOBILE"){
            this.shipTV.setBackgroundResource(R.drawable.batmobile);
        }
        else{
            this.shipTV.setBackgroundResource(R.drawable.octane);
        }



        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        buttonsFragment = new ButtonsFragment();
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
            //buttonsFragment.setBullets(5);
            //TODO ANIMACIÓN DE RECARGA Y SONIDO DE RECARGA

            for (int i = 0; i<this.bulletsList.length;i++ ) {
                Log.i("Pad", "Bala nueva");
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

    public void updateHealth(int dmgDone){
        int actualHealth = this.healthTV.getProgress();
        int newHealth = actualHealth - dmgDone;
        this.healthTV.setProgress(newHealth);
    }



}

