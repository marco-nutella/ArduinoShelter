package com.example.raduinoandroid;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInfoInterface {

    @POST("api/info/add")
    Call<Integer> PostInfo(@Body Info info);


    @GET("api/info/all")
    Call<ArrayList<Info>> GetAll();
}
//@GET("/api/groups/by/name/{gru_name}")
//Call<ArrayList<Group>> GetGroupWithName(@Path("gru_name") String gru_name);

//@GET("/api/groups/by/tag/{tag_name}")
//Call<ArrayList<Group>> GetGroupWithTag(@Path("tag_name") String tag_name);