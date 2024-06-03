package com.example.raduinoandroid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    public static Retrofit getRetrofit(){
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

                        return new Retrofit.Builder()
                                        .baseUrl("http://10.72.170.237:8080")
                                        .addConverterFactory(GsonConverterFactory.create(gson))
                                        .build();
    }
}
