package com.example.killerpad.comunications;

import android.content.Context;
import android.util.Log;
import com.example.killerpad.PadActivity;
import com.example.killerpad.R;
import com.example.killerpad.preferences_manager.SharedPreferencesManager;
import com.example.killerpad.sound.SoundManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Alejandra
 * Class that handles the connection to the killergame
 */
public class PadHandler implements Runnable {
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

    public PadHandler(PadActivity activity, String user, String ip, int port) {
        this.padActivity = activity;
        this.user = user;
        this.recieverIp = ip;
        this.port = port;
        alive = true;
        connected = false;
    }

    public boolean isConnected() {return connected;}

    /**
     * Sets the connection to the killer game
     */
    public void setConnection() {

        while (this.socket == null) {
            try {
                socket = new Socket(this.recieverIp, this.port);
                Thread.sleep(200);
            } catch (InterruptedException | IOException e) {
                Log.d("HANDLER_SET_CONNECTION", e.toString());
            }
        }

        //Sets the ip of the device running the game
        senderId = socket.getLocalAddress().getHostAddress();

        try {
            //Opens the streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            //Sets timeout
            socket.setSoTimeout(3500);
        } catch (IOException e) {
            socket = null;
            Log.d("HANDLER_SET_TIMEOUT", e.toString());
        }

        connected = true; //Connected is now true
    }

    /**
     * Comienza el hilo y entra en setConnection donde intentará establecer conexión
     * y crear los elementos necesarios (socket, printwriter, bufferedreader, etc) para
     * mantener la comunicación entre servidor y cliente.
     * */
    @Override
    public void run() {

        setConnection(); //Sets the socket

        sendConnectionMessage(); //Sends initial connection message

        //El hilo entra en este bucle de comunicación determinado por la variable "alive"
        while (alive) {
            try {
                listenServer();
                sendTimeoutMessage(); //Sends timeout message every 100 ms
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Log.d("HANDLER_ERROR_ALIVE", e.toString());
            }
        }

    }

    /**
     * Reads the messages sent to the server
     */
    public void listenServer() {

        try {
            String data = in.readLine();

            // Si el mensaje no es null se procesa.
            if (data != null) {
                processGameMessage(data);
            }

        } catch (IOException e) {

            Log.d("HANDLER_ERROR_READ", e.getMessage());
            this.padActivity.showAlertDialog(R.string.error_connexion);
            disconnect();
        }
    }

    /**
     * Reads the incoming messages and decides what to do
     * @param data Line read from input stream
     */
    private void processGameMessage(String data) {
        //Ignore status requests
        if(data.equalsIgnoreCase(Message.STATUS_REQUEST)){
            return;
        }

        Log.d("HANDLER_RECEIVE", data);

        Message message = Message.readMessage(data);

        switch (message.getCommand()) {
            case Message.PAD_CONNECTED:
                padActivity.getSpinner().dismiss(); //Cuando se establezca conexión el dialog del Spinner se oculta.
                break;

            case Message.PAD_NOT_CONNECTED:
                padActivity.getSpinner().dismiss();
                padActivity.showAlertDialog(R.string.error_game_started);
                disconnect();

            case Message.KILL_COMMAND: // cuando la nave mata, suma puntos
                padActivity.updateScores(1);
                break;

            case Message.HEALTH_COMMAND:
                padActivity.setHealth(message.getHealth());
                padActivity.vibrar(100);
                break;

            case Message.DEATH_COMMAND: // morir
                padActivity.setHealth(0);
                padActivity.vibrar(1500);
                padActivity.saveScore();
                padActivity.showDeathAnimation();
                break;

            case Message.WIN_COMMAND:
                padActivity.vibrar(1500);
                padActivity.saveScore();
                padActivity.showVictoryAnimation();
                break;

            case Message.POWER_UP_COMMAND:
                SoundManager.getInstance(padActivity.getApplicationContext()).playPowerUpSound();
        }
    }

    /**
     * Sends the connection message with the ship type, the color and the username to the ip
     * of the killergame, and specifying the pad's IP address
     */
    private void sendConnectionMessage() {
        Context context = padActivity.getApplicationContext();

        String color = SharedPreferencesManager.getString(context, //Gets color
                SharedPreferencesManager.COLOR_KEY,
                "#FF0000");

        String shipType = SharedPreferencesManager.getString(context,   //Gets the type of ship
                SharedPreferencesManager.SHIP_KEY,
                ShipType.OCTANE.name());

        //Send the message by creating the message object with the connectionResponse object inside
        Message message = Message.Builder
                .builder(Message.CONNECTION_FROM_PAD, senderId)
                .withReceiverId(recieverIp)
                .withConnection(ConnectionResponse.Builder.builder()
                        .withColor(color)
                        .withUserName(user)
                        .withShipType(ShipType.valueOf(shipType)).build())
                .build();

        //Convert message to JSON
        String json = Message.convertMessageToJson(message);
        Log.d("HANDLER_SEND", json);
        out.println(json);
    }

    /**
     * Sends the timeout message to inform the killer game that the pad is still connected
     */
    private void sendTimeoutMessage() {
        out.println(Message.STATUS_REQUEST);
    }

    /**
     * Sends a disconnection message to the killer game
     */
    private void sendDisconnectMessage(){
        out.println(Message.DISCONNECTION_COMMAND);
        Log.d("HANDLER_SEND", Message.DISCONNECTION_COMMAND);
    }

    /**
     * Sends an action that doesn't require any additional parameters except fot the action itself
     * @param action KillerAction to send
     */
    public void sendKillerAction(String action){
        Message message = Message.Builder.builder(Message.ACTION_COMMAND, senderId)
                .withAction(KillerAction.Builder.builder(action).build())
                .build();

        String json = Message.convertMessageToJson(message);
        Log.d("HANDLER_SEND", json);

        out.println(json);
    }

    /**
     * Sends a killer action that requires the speed values
     * @param action KillerAction to send
     * @param xSpeed Speed on x axis
     * @param ySpeed Speed on y axis
     */
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

    /**
     * Sens the disconnection message and closes the socket
     */
    public void disconnect() {

        alive = false;

        try {
            if (this.socket != null) {
                sendDisconnectMessage();
                this.socket.close();
                this.socket = null;
            }
        } catch (IOException e) {
        }
    }

}
