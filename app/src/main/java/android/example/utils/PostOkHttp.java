package android.example.utils;

import android.util.Log;

import com.google.gson.Gson;
import android.example.models.ChatRequest;
import android.example.models.Context;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class PostOkHttp {
    private final String TAG="PostOkHTTP";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public String post(String url, String json,String cookie) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        System.out.println("BODY" + body.contentType());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("cookie",cookie)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String textJson(String textMessage, Context context,boolean initialRequest)
    {
        Gson gson = new Gson();
        ChatRequest request = new ChatRequest();
        Log.d(TAG,"INITIALREQUEST"+initialRequest);

        if(initialRequest)
          return gson.toJson(request);

        else {
            request.setText(textMessage);
            request.setContext(context);
            return gson.toJson(request);

        }

    }
}
