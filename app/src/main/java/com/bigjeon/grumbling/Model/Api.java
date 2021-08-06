package com.bigjeon.grumbling.Model;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Api {
    @Headers({"Authorization: key=" + "AAAAUNAswdg:APA91bESlGHhZpI1cYhbjU0kI49XE4PhkhyBkUzZWNiLAH79dWsrnyJMY9KKvkYN6JZzGn1Ke_6_K2PTbxNRD6wHEUGRE51ghvavzDp34IEbFE_gsDUGRse8hyJVUr7Mn1kXgV4Wkq4c", "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(
            @Body Model root);
}
