package com.example.killerpad.comunications;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Class that builds and represents an action sent to the killerGame, such as movement or a shot
 * This class was created by Marc, and ported to the pad
 */
@JsonInclude(Include.NON_DEFAULT)
public class KillerAction {

    private String command;
    private double speedX;
    private double speedY;

    public KillerAction(){

    }

    private KillerAction(final KillerAction.Builder builder){
        this.command = builder.command;
        this.speedX = builder.speedX;
        this.speedY = builder.speedY;
    }

    public String getCommand() {
        return command;
    }

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public static class Builder {

        private String command;
        private double speedX;
        private double speedY;

        public Builder(final String command) {
            this.command = command;
        }

        public static Builder builder(final String command) {
            return new Builder(command);
        }

        public Builder withSpeedX(final double speedX) {
            this.speedX = speedX;
            return this;
        }

        public Builder withSpeedY(final double speedY) {
            this.speedY = speedY;
            return this;
        }

        public KillerAction build() {
            return new KillerAction(this);
        }
    }

}