package com.campuslaundry.model;

/** A physical washing or drying machine that can be booked. */
public class Machine {
    private final int id;
    private final String name;
    private final MachineType type;
    private boolean inService; // false = broken / under maintenance

    public Machine(int id, String name, MachineType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.inService = true;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public MachineType getType() { return type; }
    public boolean isInService() { return inService; }
    public void setInService(boolean inService) { this.inService = inService; }

    @Override
    public String toString() {
        return name + " (" + type + ")" + (inService ? "" : " [OUT OF SERVICE]");
    }
}