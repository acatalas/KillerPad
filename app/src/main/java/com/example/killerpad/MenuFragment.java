package com.example.killerpad;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.killerpad.comunications.ConnectionResponse;

import static android.content.Context.MODE_PRIVATE;

import java.util.ArrayList;


/**
 * guardar el color
 */

public class MenuFragment extends Fragment implements View.OnClickListener {

    private Button btnStartGame;
    private ImageButton btnColorPicker;
    private ImageButton btnShipPicker;
    private ImageButton btnSettings;

    //Dialogs
    private Dialog colorDialog;
    private Dialog shipDialog;
    private Dialog configurationDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // carga el layout
        View v = inflater.inflate(R.layout.fragment_menu, container, false);

        // almacenamos en una variable las shared preferences
        // y recuperamos el textview y establecemos el valor recuperado de las topscores de sharedpreferences.
        SharedPreferences prefs = getContext().getSharedPreferences ( "savedPrefs", MODE_PRIVATE);
        //((TextView) v.findViewById(R.id.tops)).setText(prefs.getString("topScore","0"));

        btnStartGame = (Button) v.findViewById(R.id.go_to_pad);
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfigDialog();
            }
        });

        btnColorPicker = v.findViewById(R.id.btnColorPicker);
        btnColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });

        // almacenamos el boton para elegir que nave queremos
        btnShipPicker = v.findViewById(R.id.btnShipPicker);
        btnShipPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showShipPickerDialog();
            }
        });

        btnSettings = v.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {

            boolean visible = false;

            @Override
            public void onClick(View v) {
                visible = !visible;
                if(visible){
                    btnShipPicker.setVisibility(View.VISIBLE);
                    btnColorPicker.setVisibility(View.VISIBLE);
                }else {
                    btnShipPicker.setVisibility(View.INVISIBLE);
                    btnColorPicker.setVisibility(View.INVISIBLE);
                }

            }
        });

        return v;
    }

    //almacena el surfaceview (ShipView) y utilizando su método para actualizar el color de la nave
    // pasando por parámetro el color recuperado de shared preferences
    /*public void updateShipViewColor(){
        ShipView shipView = ((ShipView) ((MenuActivity)getActivity()).findViewById(R.id.shipView));
        shipView.updateColor((String) getContext()
                .getSharedPreferences("savedPrefs",Context.MODE_PRIVATE)
                .getString("color","ffffff"));
    }*/

    // almacenamos en un arraylist todos los botones de color que se encuentran en el dialog del color picker
    private void addColorListeners() {
        ArrayList<Button> arrButtons = new ArrayList<>();

        arrButtons.add((Button) this.colorDialog.findViewById(R.id.orangeButton));
        arrButtons.add((Button) this.colorDialog.findViewById(R.id.limaButton));
        arrButtons.add((Button) this.colorDialog.findViewById(R.id.yellowButton));

        arrButtons.add((Button) this.colorDialog.findViewById(R.id.fucsiaButton));
        arrButtons.add((Button) this.colorDialog.findViewById(R.id.whiteButton));
        arrButtons.add((Button) this.colorDialog.findViewById(R.id.purpleButton));

        arrButtons.add((Button) this.colorDialog.findViewById(R.id.blueButton));
        arrButtons.add((Button) this.colorDialog.findViewById(R.id.redButton));
        arrButtons.add((Button) this.colorDialog.findViewById(R.id.aquaMarineButton));


        //recorremos el arraylist entero, añadiendo el listener a cada elemento botón.
        for (int button = 0; button < arrButtons.size(); button++) {
            arrButtons.get(button).setOnClickListener(this);
        }


    }

    private void addShipListeners(){

        // Almacenamos en un ArrayList los diversos tipos de naves con las que podremos jugar

        ImageView batmobileButton = shipDialog.findViewById(R.id.batmobile);
        ImageView octaneButton = shipDialog.findViewById(R.id.octane);
        ImageView marauderButton = shipDialog.findViewById(R.id.marauder);

        batmobileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSharedPreferences("ship", ConnectionResponse.ShipType.BATMOBILE.name());
                shipDialog.cancel();
            }
        });

        octaneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSharedPreferences("ship", ConnectionResponse.ShipType.OCTANE.name());
                shipDialog.cancel();
            }
        });

        marauderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSharedPreferences("ship", ConnectionResponse.ShipType.MARAUDER.name());
                shipDialog.cancel();
            }
        });

    }

    public void connectPadActivity(View v){

        //método para el listener del botón aceptar del dialog de configuración de conexión.
        //recibimos como parámetro el botón.

        Intent intent;
        String user;
        String ip;
        int port;

        // cierra el dialogo
        this.configurationDialog.cancel();

        // crea un nuevo intent
        intent = new Intent(getActivity(), PadActivity.class);

        //  recupera los valores de los editText(Parseados) del dialog
        user = ((EditText) this.configurationDialog.findViewById(R.id.username)).getText().toString();
        ip = ((EditText) this.configurationDialog.findViewById(R.id.ip)).getText().toString();
        port = Integer.parseInt(((EditText) this.configurationDialog.findViewById(R.id.puerto)).getText().toString());

        // pasa la información (user, ip, port) utilizando putExtra para el intent
        intent.putExtra("user", user);
        intent.putExtra("ip", ip);
        intent.putExtra("port", port);

        // guarda las configuraciones en las shared preferences
        saveSharedPreferences("user", user);
        saveSharedPreferences("ip", ip);
        saveSharedPreferences("port", String.valueOf(port));

        // inicia la actividad del pad
        startActivity(intent);

        // acaba la menu activity
        getActivity().finish();
    }

    //invocado por showConfigDialog() para rellenar los campos (user,ip,puerto)
    // con la última configuración (sharedpreferences).
    private void loadConfigurationDialog() {
        ImageButton bAceptar;
        ImageButton bCancelar;

        EditText etUsername;
        EditText etIp;
        EditText etPort;

        // recueramos los botones del dialog de configuración
        bAceptar = this.configurationDialog.findViewById(R.id.btn_accept);
        bCancelar = this.configurationDialog.findViewById(R.id.btn_cancel);

        // editText de cada campo.
        etUsername = this.configurationDialog.findViewById(R.id.username);
        etIp = this.configurationDialog.findViewById(R.id.ip);
        etPort = this.configurationDialog.findViewById(R.id.puerto);

        // carga las configuraciones con las shared preferences
        loadSharedPreferences("user", etUsername);
        loadSharedPreferences("ip", etIp);
        loadSharedPreferences("port", etPort);

        //añade los listener para los botones aceptar y cancelar.
        bAceptar.setOnClickListener(this);
        bCancelar.setOnClickListener(this);

    }

    //invocado por bColorPicker
    //(ventana para elegir color.)
    //Inicializa el atributo de clase colorDialog, le establece el layout
    // y llama a addColorListeners() para establecer los listeners a cada uno de sus botones.
    private void showColorPickerDialog() {
        this.colorDialog = new Dialog(this.getContext());
        this.colorDialog.setContentView(R.layout.dialog_color_picker);
        this.addColorListeners();
        this.colorDialog.show(); //muestra el dialogo

    }

    private void showShipPickerDialog(){
        this.shipDialog = new Dialog(this.getContext());
        this.shipDialog.setContentView(R.layout.dialog_ship_picker);
        this.addShipListeners();
        this.shipDialog.show();
    }

    //invocado por goToPad button.
    //(ventana para establecer la configuración de conexión y conectarse).
    private void showConfigDialog() {
        this.configurationDialog = new Dialog(this.getContext());
        this.configurationDialog.setContentView(R.layout.dialog_connect);

        this.loadConfigurationDialog();
        this.configurationDialog.show();

    }

    // Pasado una clave y un valor, utilizando savePreferences() almacena
    // el valor con la clave dada en SharedPreferences.
    private void saveSharedPreferences(String key, String value) {
        ((MenuActivity) getActivity()).savePreferences(key, value);
    }

    // pasado una clave y un editText, utilizando loadPreferences carga el valor de la clave
    // y lo carga en el editText.
    private void loadSharedPreferences(String key, EditText et) {
        et.setText(((MenuActivity) getActivity()).loadPreferences(key));
    }

    @Override
    public void onClick(View v) {

    //discrima el evento a asignar por la id de la view pasada por parametro.

        switch (v.getId()) {

            // dialog de los botones de colores.
            case R.id.orangeButton:
                saveSharedPreferences("color", "ff9800");
                //updateShipViewColor();
                colorDialog.dismiss();
                break;

            case R.id.blueButton:
                saveSharedPreferences("color", "257EFF");
                //updateShipViewColor();
                colorDialog.dismiss();
                break;

            case R.id.limaButton:
                saveSharedPreferences("color", "A7FF18");
                //updateShipViewColor();
                colorDialog.dismiss();
                break;

            case R.id.redButton:
                saveSharedPreferences("color", "FF0000");
                //updateShipViewColor();
                colorDialog.dismiss();
                break;

            case R.id.aquaMarineButton:
                saveSharedPreferences("color", "11bfb9");
                //updateShipViewColor();
                colorDialog.dismiss();
                break;

            case R.id.fucsiaButton:
                saveSharedPreferences("color", "f24694");
                //updateShipViewColor();
                colorDialog.dismiss();
                break;

            case R.id.whiteButton:
                saveSharedPreferences("color", "ffffff");
                //updateShipViewColor();
                colorDialog.dismiss();
                break;

            case R.id.yellowButton:
                saveSharedPreferences("color", "F0EB3B");
                //updateShipViewColor();
                colorDialog.dismiss();
                break;

            case R.id.purpleButton:
                saveSharedPreferences("color", "6E28E0");
                //updateShipViewColor();
                colorDialog.dismiss();
                break;

            //botón cancelar
            case R.id.btn_cancel:
                this.configurationDialog.cancel();
                break;

            //botón aceptar
            case R.id.btn_accept:
                this.connectPadActivity(v);
                break;

        }
    }


}
