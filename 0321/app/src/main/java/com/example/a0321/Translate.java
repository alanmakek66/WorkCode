package com.example.a0321;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Translate {
    private String GOOGLE_TRANSLATE_API_KEY = "YOUR_API_KEY"; // 替换为你的 API 密钥
    private RequestQueue requestQueue;
    private Context context;

    public Translate(Context context, RequestQueue requestQueue) {
        this.context = context;
        this.requestQueue = requestQueue;
    }

    public void translateLanguage(String text, String selectedLanguage) {
        String url = "https://translation.googleapis.com/language/translate/v2?key=" + GOOGLE_TRANSLATE_API_KEY; // Google Translate API URL
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("q", text);
            String googleLanguage = getGoogleLanguage(selectedLanguage);
            jsonBody.put("source", "zh-TW"); // 提取源语言
            jsonBody.put("target", googleLanguage); // 目标语言设置为中文
            jsonBody.put("format", "text");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String translatedText = response.getJSONObject("data").getJSONArray("translations")
                                        .getJSONObject(0).getString("translatedText");
                                sendToSocket(translatedText); // 发送翻译后的文本到 Socket
                            } catch (JSONException e) {
                                Log.e("JSONParse", "Error parsing JSON response: " + e.getMessage());
                                Toast.makeText(context, "Error parsing translation response", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("TranslateError", "Error: " + error.getMessage());
                            Toast.makeText(context, "Translation failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest); // 发送请求

        } catch (JSONException e) {
            Log.e("JSONCreate", "Error creating JSON object: " + e.getMessage());
            Toast.makeText(context, "Error creating translation request", Toast.LENGTH_SHORT).show();
        }
    }

    private String getGoogleLanguage(String selectedLanguage) {
        switch (selectedLanguage) {
            case "en-US":
                return "en";
            case "ja-JP":
                return "ja";
            case "ko-KR":
                return "ko";
            case "fr-FR":
                return "fr";
            default:
                return "en"; // 默认语言
        }
    }

    private void sendToSocket(String translatedText) {
        // 实现发送到 Socket 的逻辑
    }
}
