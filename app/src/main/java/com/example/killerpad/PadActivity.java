package com.example.killerpad;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.killerpad.comunications.PadHandler;
import com.example.killerpad.comunications.Message;
import com.example.killerpad.preferences_manager.SharedPreferencesManager;
import com.example.killerpad.sound.SoundManager;

public class PadActivity extends AppCompatActivity implements JoystickView.JoystickListener {

    private PadHandler handler;
    private int score;
    private BoardFragment boardFragment;
    private ButtonsFragment buttonsFragment;

    private Dialog spinner;
    private Dialog alertDialog;
    private Dialog exitConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pad);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll().build());

        //Recupera los valores de los extras para que el objeto Handler pueda establecer la conexión.
        Bundle extras = getIntent().getExtras();

        String user = extras.getString("user");
        String ip = extras.getString("ip");
        int port = extras.getInt("port");

        score = 0;

        // Crear los fragments
        createFragments();
        //crea el dialog Spinner (Loading animation) que se mantendra hasta que se establezca la conexión.
        createSpinner();

        // Crear el handler para establecer la conexión y arranca su hilo
        this.handler = new PadHandler(this, user, ip, port);

        new Thread(this.handler).start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide(); //Oculta la barra con el nombre de la aplicación.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissAllDialogs();
        handler.disconnect();

    }

    public Fragment getBoardFragment(){
        return boardFragment;
    }

    public Fragment getButtonsFragment(){
        return buttonsFragment;
    }

    /* Crear un dialog informando de un error de conexión.
    Dispone de un botón para volver al menú.
    Utilizamos runOnUi para invocar al hilo que ha creado la jerarquía de vistas para evitar errores.
    Ya que como vamos a modificar vistas que forman parte de la jerarquía de vistas creada por PadActivity
    Por lo que el único hilo que puede realizar estas modificaciones es el de PadActivity.*/
    public void showAlertDialog (final int message) {

        runOnUiThread(new Runnable() {
            private TextView errorMessage;
            private LayoutInflater inflater;
            private View dialogView;
            private ImageButton acceptButton;

            @Override
            public void run() {
                //Crea el dialog
                alertDialog = new Dialog(PadActivity.this);
                inflater = PadActivity.this.getLayoutInflater();
                dialogView = inflater.inflate(R.layout.dialog_alert, null);
                alertDialog.setContentView(dialogView);

                //Set error message
                errorMessage = dialogView.findViewById(R.id.textErrorMessage);
                errorMessage.setText(message);

                //Recupera boton aceptar
                acceptButton = dialogView.findViewById(R.id.btn_accept);
                acceptButton.setOnClickListener(new GoToMenuListener());

                //Muestra el dialog
                if (!isFinishing()) { alertDialog.show(); }
            }
        });

    }

    public void showVictoryAnimation(){

        SoundManager.getInstance(getApplicationContext()).playVictorySound();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setContentView(R.layout.victory_animation);

                //imageview del logo
                final TextView titulo = (TextView) findViewById(R.id.victory_text);
                //imageview del fondo
                final ImageView back = (ImageView) findViewById(R.id.spacebackground);

                //animacion background, creamos el objectanimator y le pasamos la view a animar y su propiedad
                ObjectAnimator scroll = ObjectAnimator.ofInt(back,"scrollX",
                        (back.getDrawable().getIntrinsicWidth() - Resources.getSystem().getDisplayMetrics().widthPixels));

                //le indicamos que el scroll sera el ancho de toda la imagen menos el tamaño de la pantalla.
                scroll.setDuration(4000); //durara 4 segundos

                //carga la animacion  del logo
                Animation zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_animation);

                //añadimos los listeners...
                zoom.setAnimationListener(new AnimationToMenuListener());

                scroll.start();  //la iniciamos

                titulo.startAnimation(zoom);
            }
        });

    }

    public void showDeathAnimation(){

        SoundManager.getInstance(getApplicationContext()).playDeathSound();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                setContentView(R.layout.death_animation);

                //imageview del logo
                TextView titulo = (TextView) findViewById(R.id.death_text);

                //imageview del fondo
                ImageView back = (ImageView) findViewById(R.id.spacebackground);

                //animacion background, creamos el objectanimator y le pasamos la view a animar y su propiedad
                ObjectAnimator scroll = ObjectAnimator.ofInt(back,"scrollX",
                        (back.getDrawable().getIntrinsicWidth() - Resources.getSystem().getDisplayMetrics().widthPixels));

                //le indicamos que el scroll sera el ancho de toda la imagen menos el tamaño de la pantalla.
                scroll.setDuration(4000); //durara 4 segundos
                scroll.start();  //la iniciamos

                Animation zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_animation);

                //añadimos los listeners...
                zoom.setAnimationListener(new AnimationToMenuListener());

                titulo.startAnimation(zoom);


            }
        });

    }

    //Método que crea el dialog con la animación del spinner loading.
    public void createSpinner(){

        //Crea el dialog
        spinner = new Dialog(this);
        spinner.setContentView(R.layout.dialog_spinner);
        //se deshabilita la función para cancelarlo
        spinner.setCancelable(false);

        //Recupera el botón cancelar del dialog y le asigna el listener.
        ImageButton cancel = spinner.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new GoToMenuListener());

        //muestra el dialog
        spinner.show();
    }

    public void createFragments(){

        //Método que hace las transacciones de los fragments

        FragmentManager fm = getSupportFragmentManager();

        boardFragment = (BoardFragment) fm.findFragmentById(R.id.board_container);
        if (boardFragment == null) {
            boardFragment = new BoardFragment();
            fm.beginTransaction()
                    .add(R.id.board_container, boardFragment).commit();

        }
        Fragment joystick_fragment = fm.findFragmentById(R.id.joystick_container);
        if (joystick_fragment == null) {
            joystick_fragment = new JoystickFragment();
            fm.beginTransaction()
                    .add(R.id.joystick_container, joystick_fragment).commit();
        }

        buttonsFragment = (ButtonsFragment) fm.findFragmentById(R.id.buttons_container);
        if (buttonsFragment == null) {
            buttonsFragment = new ButtonsFragment();
            fm.beginTransaction()
                    .add(R.id.buttons_container, buttonsFragment).commit();
        }

        Fragment boost_fragment = fm.findFragmentById(R.id.boost_container);
        if (boost_fragment == null){
            boost_fragment = new BoostFragment();
            fm.beginTransaction().add(R.id.boost_container, boost_fragment).commit();
        }

    }

    private void dismissAllDialogs(){
        if(alertDialog != null){
            alertDialog.dismiss();
        }
        if(exitConfirmation != null){
            exitConfirmation.dismiss();
        }
        if(spinner != null){
            spinner.dismiss();
        }
    }

    /*public void cuentaAtras() {

        //Método para ir actualizando la cuenta atrás del dialog Restart.
        //Cuando la cuenta atrás acaba habilita el botón Reaparecer.


        //Utilizamos runOnUi para invocar al hilo que ha creado la jerarquía de vistas para evitar errores.
        // Ya que como vamos a modificar vistas que forman parte de la jerarquía de vistas creada por PadActivity
        // Por lo que el único hilo que puede realizar estas modificaciones es el de PadActivity.

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textCuentaAtras = restart.findViewById(R.id.cuantatras);
                FloatingActionButton bCancel = restart.findViewById(R.id.botonCancelRestart);
                FloatingActionButton bRestart = restart.findViewById(R.id.botonRestart);

                int cuenta = Integer.parseInt(textCuentaAtras.getText().toString()) - 1;
                String text = String.valueOf(cuenta);

                if (cuenta >= 0) {
                    textCuentaAtras.setText(text);
                } else {
                    bRestart.setClickable(true);
                    TextView textRestart = (restart.findViewById(R.id.textoRestart));
                    textRestart.setText("¿Quieres volver a jugar?");
                }

            }
        });

    }*/

    //Método que crea un dialog para mostrar una cuenta atrás cuando el jugador muere.
    //El usuario cuenta con dos botones: Reaparecer y Salir.
    // Cuando se crea el dialog el botón Reaparecer está deshabilitado.
    // El botón Salir vuelve al menú.
    /*public void mayBeYouWantRestart(){

        //Utilizamos runOnUi para invocar al hilo que ha creado la jerarquía de vistas para evitar errores.
        // Ya que como vamos a modificar vistas que forman parte de la jerarquía de vistas creada por PadActivity
        // Por lo que el único hilo que puede realizar estas modificaciones es el de PadActivity.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                restart = new Dialog(PadActivity.this);
                restart.setContentView(R.layout.dialog_restart);
                restart.setCancelable(false);

                FloatingActionButton bRestart = restart.findViewById(R.id.botonRestart);
                //bRestart.setOnClickListener(PadActivity.this);
                bRestart.setClickable(false);

                FloatingActionButton bCancel = restart.findViewById(R.id.botonCancelRestart);
                //bCancel.setOnClickListener(PadActivity.this);

                restart.show();
                cuentaAtras();
            }
        });

    }*/

    //Método para cortar la conexión y volver al menú.
    public void disconnect(){
        if(handler.isConnected()) {
            handler.disconnect();
        }

        //Go to menu
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    //Método llamado por updateScores para almacenar la puntuación si hay record.
    public void saveScore(){
        String user = SharedPreferencesManager
                .getString(this, SharedPreferencesManager.USER_KEY, "User");
        SharedPreferencesManager.addScore(this, user, score);
    }

    //Recuperamos el boardfragment para poder acceder a su método getScoreTV para obtener la puntuación actual
    // y la parseamos a int.
    public void updateScores(final int points) {
        FragmentManager fm = getSupportFragmentManager();
        BoardFragment bf = (BoardFragment) fm.findFragmentById(R.id.board_container);

        final TextView text = bf.getScoreTV();

        this.score += points;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //se actualiza la puntuación de la vista
                text.setText("SCORE: " + score);
            }
        });
    }

    //Método para vibrar la durante el periodo de tiempo en milisegundos pasado por parámetro.
    // (Requiere permisos para vibrar en manifest)
    public void vibrar(int duration) {
        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(duration);
    }

    public PadHandler getHandler() {
        return this.handler;
    }

    public Dialog getSpinner() {
        return spinner;
    }

    public void setHealth(int health){
        boardFragment.updateHealth(health);
    }

    public void goToMenu(){
        startActivity(new Intent(this, MenuActivity.class));
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int source) {
        handler.sendKillerAction(Message.MOVEMENT_COMMAND, xPercent, yPercent);
    }

    private class GoToMenuListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            disconnect();
            goToMenu();
            finish();
        }
    }

    private class AnimationToMenuListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        //listener cuando acabe la animación.
        @Override
        public void onAnimationEnd(Animation animation) {
            startActivity(new Intent(PadActivity.this, MenuActivity.class));
            finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    protected class CancelDialogListener implements View.OnClickListener{

        private Dialog dialog;

        public CancelDialogListener(Dialog dialog){
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    }

}

