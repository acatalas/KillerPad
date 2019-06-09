package com.example.killerpad.comunications;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Class that builds the message, main object that is sent and received by the handler
 * This class was created by Marc, and ported to the pad
 */
@JsonInclude(Include.NON_DEFAULT)
public class Message {
    public static final String ACTION_COMMAND = "action";
    public static final String CONNECTION_FROM_PAD = "pad-connect";
    public static final String DASH_COMMAND = "pad_dash";
    public static final String DEATH_COMMAND = "pad_dead";
    public static final String DISCONNECTION_COMMAND = "bye";
    private static final String EMPTY_STRING = "";
    public static final String HEALTH_COMMAND = "pad_health";
    public static final String KILL_COMMAND = "pad_kill";
    public static final String MOVEMENT_COMMAND = "pad_move";
    public static final String PAD_CONNECTED = "padConnected";
    public static final String PAD_NOT_CONNECTED = "padNotConnected";
    public static final String POWER_UP_COMMAND = "pad_powerup";
    public static final String SHOOT_COMMAND = "pad_shoot";
    public static final String STATUS_REQUEST = "ok";
    public static final String TURBO_END_COMMAND = "pad_turbo_end";
    public static final String TURBO_START_COMMAND = "pad_turbo_start";
    public static final String WIN_COMMAND = "pad_win";



    private String command;
    private String senderId;
    private String receiverId;
    private KillerAction action;
    private ConnectionResponse connectionResponse;
    private int damage;
    private int health;

    public Message() {
    }

    private Message(final Message.Builder builder) {
        this.command = builder.command;
        this.senderId = builder.senderId;
        this.receiverId = builder.receiverId;
        this.action = builder.action;
        this.connectionResponse = builder.connectionResponse;
        this.damage = builder.damage;
        this.health = builder.health;
    }

    /**
     * Converts a JSON string into the message object
     *
     * @param jsonStr String containing a JSON representation of the object
     * @return message object with all the information
     */
    public static Message readMessage(final String jsonStr) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonStr, Message.class);
        } catch (IOException ex) {
            return Message.Builder.builder(EMPTY_STRING, EMPTY_STRING).build();
        }
    }

    /**
     * Converts a Message object to JSON string
     *
     * @param message Message object
     * @return String with the information in the message object
     */
    public static String convertMessageToJson(final Message message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(message);
        } catch (Exception ex) {
        }
        return EMPTY_STRING;
    }

    public String getCommand() {
        return command;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public KillerAction getAction() {
        return action;
    }

    public ConnectionResponse getConnectionResponse() {
        return this.connectionResponse;
    }

    public int getDamage() {
        return damage;
    }

    public int getHealth() {
        return health;
    }

    public static class Builder {

        private String command;
        private String senderId;
        private String receiverId;
        private KillerAction action;
        private ConnectionResponse connectionResponse;
        private int damage;
        private int health;

        public Builder(final String command, final String senderId) {
            this.command = command;
            this.senderId = senderId;
        }

        public static Builder builder(final String command, final String senderId) {
            return new Builder(command, senderId);
        }

        public Builder withReceiverId(final String receiverId) {
            this.receiverId = receiverId;
            return this;
        }

        public Builder withAction(final KillerAction action) {
            this.action = action;
            return this;
        }

        public Builder withConnection(final ConnectionResponse connectionResponse) {
            this.connectionResponse = connectionResponse;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
