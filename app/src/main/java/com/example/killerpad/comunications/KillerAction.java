package com.example.killerpad.comunications;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class KillerAction {

    private String command;
    private String direction;
    private float speedX;
    private float speedY;

    public KillerAction(){

    }

    private KillerAction(final KillerAction.Builder builder){
        this.command = builder.command;
        this.direction = builder.direction;
        this.speedX = builder.speedX;
        this.speedY = builder.speedY;
    }

    public String getCommand() {
        return command;
    }

    public String getDirection() {
        return direction;
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public static class Builder {

        private String command;
        private String direction;
        private float speedX;
        private float speedY;

        public Builder(final String command) {
            this.command = command;
        }

        public static Builder builder(final String command) {
            return new Builder(command);
        }

        public Builder withDirection(final String direction) {
            this.direction = direction;
            return this;
        }

        public Builder withSpeedX(final float speedX) {
            this.speedX = speedX;
            return this;
        }

        public Builder withSpeedY(final float speedY) {
            this.speedY = speedY;
            return this;
        }

        public KillerAction build() {
            return new KillerAction(this);
        }
    }

}