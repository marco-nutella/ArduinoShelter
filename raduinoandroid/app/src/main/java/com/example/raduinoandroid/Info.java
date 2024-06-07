package com.example.raduinoandroid;

public class Info {
    private int tentId;
    private int humidity;
    private int temperature;
    private String country;
    private String city;
    private String region;
    private Boolean emergency;

    public  Info(int tentId, int humidity, int temperature, String country, String city, String region, Boolean emergency){
        this.tentId= tentId;
        this.humidity = humidity;
        this.temperature = temperature;
        this.country = country;
        this.city = city;
        this.region = region;
        this.emergency = emergency;
    }
}



