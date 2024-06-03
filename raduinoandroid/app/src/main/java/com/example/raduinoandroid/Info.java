package com.example.raduinoandroid;

public class Info {
    private Object tentId;
    private int humidity;
    private int temperature;
    private double latitude;
    private double longitude;

    public  Info(Object tentId, int humidity, int temperature, double latitude, double longitude){
        this.tentId= tentId;
        this.humidity = humidity;
        this.temperature = temperature;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}



