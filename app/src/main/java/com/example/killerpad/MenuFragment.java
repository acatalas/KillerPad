package com.example.killerpad;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.killerpad.color_picker.ColorPickerDialog;
import com.example.killerpad.color_picker.OnColorChangedListener;
import com.example.killerpad.preferences_manager.SharedPreferencesManager;
import com.example.killerpad.scores.ScoreActivity;


/**
 * guardar el color
 */

public class MenuFragment extends Fragment{

    private Button btnStartGame;
    private ImageButton btnColorPicker;
    private ImageButton btnShipPicker;
    private ImageButton btnSettings;
    private ImageButton btnScores;
    private ImageButton btnHelp;

    //Dialogs
    private ColorPickerDialog colorPickerDialog;
    private ShipDialog shipDialog;
    private Dialog configurationDialog;
    private Dialog helpDialog;

    private BoardFragment boardFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shipDialog = new ShipDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // carga el layout
        View v = inflater.inflate(R.layout.fragment_menu, container, false);

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

        //Help button
        btnHelp = v.findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showHelpDialog();
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

        btnScores = v.findViewById(R.id.btnScores);
        btnScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScoreActivity.class));
            }
        });

        return v;
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
        setConnectionFields(etUsername, SharedPreferencesManager.USER_KEY, "User");
        setConnectionFields(etIp, SharedPreferencesManager.IP_KEY, "192.168.0.162");
        setConnectionFields(etPort,SharedPreferencesManager.PORT_KEY, "8000");

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
        int color = Color.parseColor(
                SharedPreferencesManager.getString(
                        getActivity(),
                        SharedPreferencesManager.COLOR_KEY, "#FF0000"));

        colorPickerDialog = new ColorPickerDialog(getContext(), new ColorListener(), color);
        colorPickerDialog.show();

    }

    private void showShipPickerDialog(){
        shipDialog = new ShipDialog(getContext());

        shipDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.semiTransparentWhite)));

        shipDialog.show();
    }

    //invocado por goToPad button.
    //(ventana para establecer la configuración de conexión y conectarse).
    private void showConfigDialog() {
        configurationDialog = new Dialog(this.getContext());
        configurationDialog.setContentView(R.layout.dialog_connect);
        loadConfigurationDialog();
        configurationDialog.show();

    }

    //Invocado por btnHelp button.
    // Ventana modal que muestra al usuario como funciona el juego.
    private void showHelpDialog(){
        this.helpDialog = new Dialog(this.getContext());
        this.helpDialog.setContentView(R.layout.dialog_help);
        this.helpDialog.show();
    }

    // pasado una clave y un editText, utilizando loadPreferences carga el valor de la clave
    // y lo carga en el editText.
    private void setConnectionFields(EditText et, String key, String defaultValue) {
        et.setText(SharedPreferencesManager.getString(getContext(), key, defaultValue));
    }

    //Color listener class
    private class ColorListener implements OnColorChangedListener {

        @Override
        public void colorChanged(final int color) {
            colorPickerDialog.dismiss();
            SharedPreferencesManager.saveString(getActivity(),
                    SharedPreferencesManager.COLOR_KEY,
                    String.format("#%06X", (0xFFFFFF & color)));
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

            SharedPreferencesManager.saveString(getActivity(),
                    SharedPreferencesManager.USER_KEY, user);

            SharedPreferencesManager.saveString(getActivity(),
                    SharedPreferencesManager.IP_KEY, ip);

            SharedPreferencesManager.saveString(getActivity(),
                    SharedPreferencesManager.PORT_KEY, String.valueOf(port));

        }
    }
}
