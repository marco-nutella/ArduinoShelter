package com.example.raduinoandroid;

public class Info {
    private int tentId;
    private int humidity;
    private int temperature;
    private String country;
    private String city;
    private String region;

    public  Info(int tentId, int humidity, int temperature, String country, String city, String region){
        this.tentId= tentId;
        this.humidity = humidity;
        this.temperature = temperature;
        this.country = country;
        this.city = city;
        this.region = region;
    }
}



