package com.example.killerpad;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.killerpad.color_picker.ColorPickerDialog;
import com.example.killerpad.color_picker.OnColorChangedListener;
import com.example.killerpad.comunications.ConnectionResponse;


/**
 * guardar el color
 */

public class MenuFragment extends Fragment{

    private Button btnStartGame;
    private ImageButton btnColorPicker;
    private ImageButton btnShipPicker;
    private ImageButton btnSettings;

    //Dialogs
    private ColorPickerDialog colorPickerDialog;
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
        //SharedPreferences prefs = getContext().getSharedPreferences ( "savedPrefs", MODE_PRIVATE);
        //((TextView) v.findViewById(R.id.tops)).setText(prefs.getString("topScore","0"));

        //Start game button
        btnStartGame = v.findViewById(R.id.go_to_pad);
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfigDialog();
            }
        });

        //Color picker button
        btnColorPicker = v.findViewById(R.id.btnColorPicker);
        btnColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog();
            }
        });

        //Ship button
        btnShipPicker = v.findViewById(R.id.btnShipPicker);
        btnShipPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showShipPickerDialog();
            }
        });

        //Settings button
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

    private void addShipListeners(){
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
        setConnectionFields(etUsername,"user", "User");
        setConnectionFields(etIp,"ip", "192.168.0.162");
        setConnectionFields(etPort,"port", "8000");

        //añade los listener para los botones aceptar y cancelar.
        bAceptar.setOnClickListener(new PadActivityListener());
        bCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configurationDialog.dismiss();
            }
        });
    }

    //invocado por bColorPicker
    //(ventana para elegir color.)
    //Inicializa el atributo de clase colorDialog, le establece el layout
    // y llama a addColorListeners() para establecer los listeners a cada uno de sus botones.
    private void showColorPickerDialog() {
        int color;
        try{
            color = Color.parseColor(getSharedPreferences("color", "#FF0000"));
        } catch (IllegalArgumentException ex){
            color = Color.RED;
        }

        colorPickerDialog = new ColorPickerDialog(getContext(),
                new ColorListener(), color);
        colorPickerDialog.show();

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

    // pasado una clave y un editText, utilizando loadPreferences carga el valor de la clave
    // y lo carga en el editText.
    private void setConnectionFields(EditText et, String key, String defaultValue) {
        et.setText(((MenuActivity) getActivity()).loadPreferences(key, defaultValue));
    }

    // Pasado una clave y un valor, utilizando savePreferences() almacena
    // el valor con la clave dada en SharedPreferences.
    private void saveSharedPreferences(String key, String value) {
        ((MenuActivity) getActivity()).savePreferences(key, value);
    }

    //Gets a shared prefernce with a default value in case of failiure
    private String getSharedPreferences(String key, String defaultValue){
        return ((MenuActivity) getActivity()).loadPreferences(key, defaultValue);
    }

    //Color listener class
    private class ColorListener implements OnColorChangedListener {

        @Override
        public void colorChanged(int color) {
            colorPickerDialog.dismiss();
            saveSharedPreferences("color", String.format("#%06X", (0xFFFFFF & color)));
        }
    }

    //listener del botón aceptar del dialog de configuración de conexión.
    private class PadActivityListener implements View.OnClickListener {

        Intent intent;
        String user;
        String ip;
        int port;

        @Override
        public void onClick(View v) {

            // cierra el dialogo de conexion
            configurationDialog.cancel();

            // crea un nuevo intent para ir al PadActivity
            intent = new Intent(getActivity(), PadActivity.class);

            //Recupera los valores de los campos de edit del dialogo de conexion
            getConfigurationDialogValues();

            //Añade los valores recuperados del dialogo de conexion al intent
            addExtrasToIntent();

            //Guarda los valores a las sharedPreferences
            saveConnectionConfiguration();

            // inicia la actividad del pad
            startActivity(intent);

            // acaba la menu activity
            getActivity().finish();
        }

        // pasa la información (user, ip, port) utilizando putExtra para el intent
        private void addExtrasToIntent(){
            intent.putExtra("user", user);
            intent.putExtra("ip", ip);
            intent.putExtra("port", port);
        }

        //  recupera los valores de los editText(Parseados) del dialog
        private void getConfigurationDialogValues(){

            user =  ((EditText) configurationDialog
                    .findViewById(R.id.username)).getText().toString();
            ip =    ((EditText) configurationDialog
                    .findViewById(R.id.ip)).getText().toString();
            port =  Integer.parseInt(((EditText) configurationDialog
                    .findViewById(R.id.puerto)).getText().toString());
        }

        // guarda las configuraciones en las shared preferences
        private void saveConnectionConfiguration(){

            saveSharedPreferences("user", user);
            saveSharedPreferences("ip", ip);
            saveSharedPreferences("port", String.valueOf(port));
        }


    }
}
