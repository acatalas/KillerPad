package com.example.killerpad.comunications;

public enum ShipType {
    MARAUDER("MARAUDER", 150 ),
    BATMOBILE("BATMOBILE", 30),
    OCTANE("OCTANE", 90);

    private final String type;
    private final int health;

    ShipType(String type, int health) {
        this.type = type;
        this.health = health;
    }

    public String getType() {
        return type;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {
        return type;
    }
}