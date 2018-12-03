package me.mcofficer.james.tools;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;

public class Translator {

    /**
     * @param sourceLang The source language, defaults to "auto"
     * @param targetLang The target language, defaults to "en"
     * @param query The text to be translated
     * @return The translated text.
     */
    public String translate(@Nullable String sourceLang, @Nullable String targetLang, String query) throws IOException{
        if (sourceLang == null)
            sourceLang = "auto";
        if (targetLang == null)
            targetLang = "en";

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("translate.googleapis.com")
                .addPathSegments("translate_a/single")
                .addQueryParameter("client", "gtx")
                .addQueryParameter("sl", sourceLang)
                .addQueryParameter("tl", targetLang)
                .addQueryParameter("dt", "t")
                .addQueryParameter("q", query)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JSONArray json = new JSONArray(response.body().string());
        return json.getJSONArray(0).getJSONArray(0).getString(0);
    }
}
