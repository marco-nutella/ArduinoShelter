package com.example.raduinoandroid;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimerCall extends Worker {

    private final Context context;
    private Integer newid;
    private  Boolean result = false;

    public TimerCall(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        // Your code to be executed every 5 minutes
        SharedPreferences sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String city = sharedPref.getString("city", "Default City");
        String region = sharedPref.getString("region", "Default Region");
        String country = sharedPref.getString("country", "Default Country");
        String id = sharedPref.getString("tendId", "0");
        String humidity = sharedPref.getString("humidity", "0");
        String temperature = sharedPref.getString("temperature", "0");

        Info info = new Info(Integer.parseInt(id), Integer.parseInt(humidity), Integer.parseInt(temperature), country, city, region, false);

        try {
            RetrofitInfoInterface aa = RetrofitService.getRetrofit().create(RetrofitInfoInterface.class);
            aa.PostInfo(info).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    newid = response.body();
                    String text = "Change Id " + newid;
                    //sendBluetoothMessage(text);
                    result = true;
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            System.out.println(e);
        }
        // Add your code to use these values, for example:
        // Log.d("TimerCall", "City: " + city + ", Region: " + region + ", Country: " + country);
        if (result){return Result.success();}else{return Result.failure();}
    }
}