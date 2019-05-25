package com.example.killerpad.comunications;

import android.content.SharedPreferences;
import android.util.Log;
import com.example.killerpad.PadActivity;
import com.example.killerpad.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static android.content.Context.MODE_PRIVATE;

public class Handler implements Runnable {
    private PadActivity padActivity;
    private Socket socket;
    private String user;
    private String recieverIp;
    private String senderId;
    private int port;
    private BufferedReader in;
    private PrintWriter out;
    private boolean alive;
    public boolean connected;

    public Handler(PadActivity activity, String user, String ip, int port) {
        this.padActivity = activity;
        this.user = user;
        this.recieverIp = ip;
        this.port = port;
        alive = true;
        connected = false;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public boolean isConnected() {return connected;}

    public void setConnection() {

        // Mientras no hay conexión intentar conectarse...
        while (this.socket == null) {
            try {
                socket = new Socket(this.recieverIp, this.port);
                Thread.sleep(200);
            } catch (InterruptedException | IOException e) {
                Log.d("HANDLER_SET_CONNECTION", e.toString());
            }
        }

        senderId = socket.getLocalAddress().getHostAddress();

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            socket.setSoTimeout(3500);
        } catch (IOException e) {
            socket = null;
            Log.d("HANDLER_SET_TIMEOUT", e.toString());
        }

        connected = true;
    }

    //Comienza el hilo y entra en setConnection donde intentará establecer conexión
    // y crear los elementos necesarios (socket, printwriter, bufferedreader, etc) para
    // mantener la comunicación entre servidor y cliente.
    @Override
    public void run() {

        setConnection();

        sendConnectionMessage();

        //El hilo entra en este bucle de comunicación determinado por la variable "alive"
        while (alive) {
            try {
                //Con el método listenServer escucha e interpreta los mensajes del servidor.
                listenServer();
                sendTimeoutMessage();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.d("HANDLER_ERROR_ALIVE", e.toString());
            }
        }

    }

    //Método que se invoca cuando se recibe el mensaje "ded" para indicar la muerte del usuario.
    //Irá actualizando la cuenta atrás de 10  a 0 a cada segundo pasado utilizando el método cuentaAtras.
    private void cuentaAtras() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 10; i >= 0; i--) {
            padActivity.cuentaAtras();
            try {
                Thread.sleep(1100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para cerrar la conexión entre servidor y cliente. Envia un mensaje avisando al servidor
    public void disconnect() {

        sendDisconnectMessage();

        alive = false;

        try {
            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenServer() {

        try {
            String data = in.readLine();

            // Si el mensaje no es null se procesa.
            if (data != null) {
                processGameMessage(data);
            }
        } catch (IOException e) {
            //Si salta excepción se notifica al user.
            Log.d("HANDLER_ERROR_READ", e.getMessage());
            this.padActivity.showAlertDialog(R.string.error_connexion);//OK
            disconnect();
        }
    }

    //Implementa el protocolo de aplicación, discrimina los mensajes y gestiona el comando.
    private void processGameMessage(String data) {
        //Ignore status requests
        if(data.equalsIgnoreCase(Message.STATUS_REQUEST)){
            return;
        }

        Log.d("HANDLER_RECIEVE", data);

        Message message = Message.readMessage(data);

        switch (message.getCommand()) {
            case Message.PAD_CONNECTED:
                padActivity.getSpinner().cancel(); //Cuando se establezca conexión el dialog del Spinner se oculta.
                break;

            case Message.PAD_NOT_CONNECTED:
                padActivity.getSpinner().dismiss();
                padActivity.showAlertDialog(R.string.error_game_started);
                disconnect();

            case Message.DAMAGE_COMMAND: // cuando la nave recibe daño
                padActivity.vibrar(300);
                //TODO: setDamage(message.getDamage())
                break;

            case Message.KILL_COMMAND: // cuando la nave mata, suma puntos
                padActivity.updateScores(1);
                //TODO: cuantos puntos suma la nave cuando mata a alguien
                break;

            case Message.DEATH_COMMAND: // morir
                padActivity.vibrar(1500);
                padActivity.mayBeYouWantRestart();
                //padA.cuentaAtras();
                cuentaAtras();
                break;
        }
    }

    private void sendConnectionMessage() {
        // Cargar el color de la nave de shared preferences
        // >> Hex color value
        SharedPreferences prefs = this.padActivity.getSharedPreferences("savedPrefs", MODE_PRIVATE);
        String color = prefs.getString("color", "ffffff");
        String shipType = prefs.getString("ship", ConnectionResponse.ShipType.OCTANE.name());
        color = "#" + color;

        //Envia un mensaje utilizando el protocolo de la aplicación para crear un mando nuevo
        // mandando como parámetros el usuario, el color y la ip destino y origen
        Message message = Message.Builder
                .builder(Message.CONNECTION_FROM_PAD, senderId)
                .withReceiverId(recieverIp)
                .withConnection(ConnectionResponse.Builder.builder()
                        .withColor(color)
                        .withUserName(user)
                        .withShipType(ConnectionResponse.ShipType.valueOf(shipType)).build())
                .build();

        //Convierte el mensaje a JSON
        String json = Message.convertMessageToJson(message);
        Log.d("HANDLER_SEND", json);
        out.println(json);
    }

    private void sendTimeoutMessage() {
        out.println(Message.STATUS_REQUEST);
    }

    private void sendDisconnectMessage(){
        out.println(Message.DISCONNECTION_COMMAND);
        Log.d("HANDLER_SEND", Message.DISCONNECTION_COMMAND);
    }

    //Envia una accion al mando
    public void sendKillerAction(String action){
        Message message = Message.Builder.builder(Message.ACTION_COMMAND, senderId)
                .withAction(KillerAction.Builder.builder(action).build())
                .build();

        String json = Message.convertMessageToJson(message);
        Log.d("HANDLER_SEND", json);

        out.println(json);
    }

    public void sendKillerAction(String action, float xSpeed, float ySpeed){
        Message message = Message.Builder.builder(Message.ACTION_COMMAND, senderId)
                .withAction(KillerAction.Builder.builder(action)
                        .withSpeedX(xSpeed)
                        .withSpeedY(ySpeed)
                        .build())
                .build();

        String json = Message.convertMessageToJson(message);
        Log.d("HANDLER_SEND", json);

        out.println(json);
    }

}
