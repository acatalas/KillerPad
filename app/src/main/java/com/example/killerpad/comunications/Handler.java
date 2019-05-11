package com.example.killerpad.comunications;

import android.content.SharedPreferences;
import android.util.Log;
import com.example.killerpad.PadActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import static android.content.Context.MODE_PRIVATE;

public class Handler implements Runnable {
    private PadActivity padA;
    private Socket socket;
    private String user;
    private String recieverId;
    private String senderId;
    private int port;
    private BufferedReader in;
    private PrintWriter out;
    private boolean alive;
    public boolean connected;

    private static String TAG = "handler";

    public Handler(PadActivity activity, String user, String ip, int port) {
        this.padA = activity;
        this.user = user;
        this.recieverId = ip;
        this.port = port;
        this.alive = true;
        this.connected = false;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setConnection() {

        // Mientras no hay conexión intentar conectarse...
        while (this.socket == null) {
            try {
                socket = new Socket(this.recieverId, this.port);
                Thread.sleep(200);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        senderId = socket.getLocalAddress().getHostAddress();

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException e) {
            socket = null;
            e.printStackTrace();
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
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "handler run excepcion");
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
            padA.cuentaAtras();
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

    private void sendDisconnectMessage(){
        out.println(Message.DISCONNECTION_COMMAND);
    }

    public void listenServer() {

        try {
            String data = this.in.readLine();

            Log.d(TAG, data);

            // Si el mensaje no es null se procesa.
            if (data != null) {
                Log.d(TAG, "datos recibidos = " + data);
                processGameMessage(data);
            }
        } catch (IOException e) {
            //Si salta excepción se notifica al user.
            Log.d(TAG, "handler listenServer: IOexception!!!!");
            this.padA.alertUser();//OK
        }
    }

    //Implementa el protocolo de aplicación, discrimina los mensajes y gestiona el comando.
    private void processGameMessage(String data) {
        Message message = Message.readMessage(data);

        switch (message.getCommand()) {
            case Message.PAD_CONNECTED:
                //Cuando se establezca conexión el dialog del Spinner se oculta.
                padA.getSpinner().cancel();
                //TODO: pad Connected
            case Message.PAD_NOT_CONNECTED:
                //TODO: pad not connected
            case Message.DAMAGE_COMMAND: // cuando la nave recibe daño
                padA.vibrar(300);
                //TODO: setDamage(message.getDamage())
                break;
            case Message.KILL_COMMAND: // cuando la nave mata, suma puntos
                padA.updateScores(1);
                //TODO: cuantos puntos suma la nave cuando mata a alguien
                break;
            case Message.DEATH_COMMAND: // morir
                padA.vibrar(1500);
                padA.mayBeYouWantRestart();
                //padA.cuentaAtras();
                cuentaAtras();
                break;
        }
    }

    private void sendConnectionMessage() {
        // Cargar el color de la nave de shared preferences
        // >> Hex color value
        SharedPreferences prefs = this.padA.getSharedPreferences("savedPrefs", MODE_PRIVATE);
        String color = prefs.getString("color", "ffffff");
        color = "#" + color;

        //Envia un mensaje utilizando el protocolo de la aplicación para crear un mando nuevo
        // mandando como parámetros el usuario, el color y la ip destino y origen
        Message message = Message.Builder
                .builder(Message.CONNECTION_FROM_PAD, senderId)
                .withReceiverId(recieverId)
                .withConnection(ConnectionResponse.Builder.builder()
                        .withColor(color)
                        .withUserName(user)
                        .withShipType("1").build())
                .build();

        //Convierte el mensaje a JSON
        String json = Message.convertMessageToJson(message);
        Log.d("JSON", json);
        out.println(json);
    }

    //Envia una accion al mando
    public void sendKillerAction(String action){
        Message message = Message.Builder.builder(Message.ACTION_COMMAND, senderId)
                .withAction(KillerAction.Builder.builder(action).build())
                .build();

        String json = Message.convertMessageToJson(message);
        Log.d("JSON", json);

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
        Log.d("JSON", json);

        out.println(json);
    }

}
