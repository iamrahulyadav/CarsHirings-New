package com.carshiring.webservices;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class RetrofitApiBuilder {

    public final static String CarHires_BASE_URL = "https://carshiring.com/webservice/";
    public final static String CarGates_BASE_URL = "https://carsgates.com/";
    private final static String CarGates_BASE_WEBSERVICE_URL = CarGates_BASE_URL +"webservices/";
    public final static String IMG_BASE_URL ="https://carshiring.com/upload/";

    public static RetroFitApis getCargHiresapis() {

        OkHttpClient httpClient=new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000,TimeUnit.SECONDS)
                .readTimeout(30000,TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request;
                        request=chain.request().newBuilder().addHeader("content-type",
                                                            "application/x-www-form-urlencoded").build();
                        return chain.proceed(request);
                    }
                })
                .build();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(CarHires_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        return retrofit.create(RetroFitApis.class);
    }

    public static  RetroFitApis getCarGatesapi() {
        OkHttpClient client =new OkHttpClient.Builder()
                .writeTimeout(10000,TimeUnit.SECONDS)
                .readTimeout(10000,TimeUnit.SECONDS)
                .connectTimeout(10000,TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request;
                        request=chain.request()
                                .newBuilder()
                                .addHeader("content-type","application/x-www-form-urlencoded")
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
        Retrofit retrofit=new Retrofit.Builder().baseUrl(CarGates_BASE_WEBSERVICE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(RetroFitApis.class);
    }
}
