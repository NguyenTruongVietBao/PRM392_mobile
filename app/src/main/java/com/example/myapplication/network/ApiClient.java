package com.example.myapplication.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.data.models.CartItem;
import com.example.myapplication.network.serializers.CartItemDeserializer;

//Cấu hình Retrofit
public class ApiClient {
    private static Retrofit retrofit;
    private static ApiService apiService;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp client configuration
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(Constants.TIMEOUT_DURATION, TimeUnit.SECONDS)
                    .readTimeout(Constants.TIMEOUT_DURATION, TimeUnit.SECONDS)
                    .writeTimeout(Constants.TIMEOUT_DURATION, TimeUnit.SECONDS)
                    .build();

            // Create custom Gson with CartItem deserializer
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(CartItem.class, new CartItemDeserializer())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofitInstance().create(ApiService.class);
        }
        return apiService;
    }
}
