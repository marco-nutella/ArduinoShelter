package com.example.raduinoandroid;

public class Item {
    private String name;
    private int variable;

    public Item(String name, int variable) {
        this.name = name;
        this.variable = variable;
    }
    public String getName() {
        return name;
    }
    public int getVariable() {
        return variable;
    }
}