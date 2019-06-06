package com.example.killerpad;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class BoardFragment extends Fragment{
    private TextView scoreTV;
    private ProgressBar healthTV;
    private ImageView shipTV;
    private String ship;
    private int health;
    private static String TAG = "handler";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_board, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences ( "savedPrefs", MODE_PRIVATE);

        this.scoreTV = (TextView) v.findViewById(R.id.scoreTV);
        this.healthTV = (ProgressBar) v.findViewById(R.id.healthTV);


        //Añadido la nave escogida al principio
        this.shipTV = (ImageView) v.findViewById(R.id.shipTV);

        ship = prefs.getString("ship","octane");
        if (ship == "MARAUDER"){
            this.shipTV.setBackgroundResource(R.drawable.marauder);
        }
        else if (ship == "BATMOBILE"){
            this.shipTV.setBackgroundResource(R.drawable.batmobile);
        }
        else{
            this.shipTV.setBackgroundResource(R.drawable.octane);
        }


        //this.bulletTV = (ImageView) v.findViewById(R.id.bulletTV);

        // Volver al menu
        /*this.exitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForConfirmation();
            }
        });*/

        return v;
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

    public TextView getScoreTV(){
        return scoreTV;
    }

    public void updateHealth(int dmgDone){
        int actualHealth = this.healthTV.getProgress();
        int newHealth = actualHealth - dmgDone;
        this.healthTV.setProgress(newHealth);
    }
}

