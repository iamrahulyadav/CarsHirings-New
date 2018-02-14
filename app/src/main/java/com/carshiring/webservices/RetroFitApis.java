package com.carshiring.webservices;

import com.carshiring.models.Category;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public interface RetroFitApis {
    @FormUrlEncoded
    @POST("token")
    Call<ApiResponse> token(@Field("grant_type") String grant_type,
                            @Field("client_id") String id,
                            @Field("client_secret") String secret);
    @FormUrlEncoded
    @POST("signup")
    Call<ApiResponse> signup(@Field("email") String email,
                             @Field("password") String password);
    @FormUrlEncoded
    @POST("lang_list")
    Call<ApiResponse> lang_list(@Field("") String s);
    @FormUrlEncoded
    @POST("forgot_pass")
    Call<ApiResponse> forgot_pass(@Field("email") String email);
    @FormUrlEncoded

    @POST("change_pass")
    Call<ApiResponse> change_pass(@Field("oldpass") String oldpass,
                                  @Field("newpass") String newpass,
                                  @Field("userid") String userid);
    @FormUrlEncoded
    @POST("login")
    Call<ApiResponse> login(@Field("username") String username,
                            @Field("password") String password);
    @FormUrlEncoded
    @POST("category_list")
    Call<ApiResponse> category_list(@Body Category cat);

    @FormUrlEncoded
    @POST("webservice/search")
    Call<ApiResponse> search(@Field("access_token") String access_token,
                             @Field("pick_city") String pick_city,
                             @Field("pick_date") String pick_date,
                             @Field("pick_houre") String pick_hour,
                             @Field("pick_minute") String pick_minute,
                             @Field("drop_city") String drop_city,
                             @Field("drop_date") String drop_date,
                             @Field("drop_houre") String drop_hour,
                             @Field("drop_minute") String drop_minute,
                             @Field("driver_age") String driver_age,
                             @Field("use_current_location") int useCurrentLocation,
                             @Field("sameas_pick_location") int useSameDropLocation,
                             @Field("between_driver_age") int betweenDriverAge,
                             @Field("lat") double lat,
                             @Field("long") double lng,
                             @Field("location_code") String location_code,
                             @Field("location_iata") String location_iata,
                             @Field("location_type") String location_type,
                             @Field("location_code_drop") String location_code_drop,
                             @Field("location_iata_drop") String location_iata_drop,
                             @Field("location_type_drop") String location_type_drop,
                             @Field("language_code") String language_code);

    @FormUrlEncoded
    @POST("webservice/location")
    Call<ApiResponse> location(@Field("access_token") String access_token,
                                    @Field("keyword") String keyword,
                                    @Field("language_code") String language_code);
    @FormUrlEncoded
    @POST("webservice/car_detail")
    Call<ApiResponse> car_detail(@Field("access_token") String access_token,@Field("id_context") String id_context, @Field("type") String type,@Field("day") String day);

}
