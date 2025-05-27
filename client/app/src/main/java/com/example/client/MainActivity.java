package com.example.client;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private Intent speechRecognizerIntent; // 成員變量
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private SpeechRecognizer speechRecognizer;
    private TextView textViewResult;
    private TextView textViewResult2;
    private TextView translateResult;

    private boolean isListening = false;
    private boolean isTextScrolling = false;
    private String stringVoiceText1 = "";
    private String stringVoiceText2 = "";
    private String stringVoiceText3 = "";
    private String stringVoicePartialText;

    public static String selectedLanguage = "ja-JP";
    public static String abovelanguage;
    private RequestQueue requestQueue;

    private static String SERVER_IP;
    private static final int SERVER_PORT = 9050; // 自身的port

    private static final int SERVER_PORT2 = 5090; // 自身的port

    private static final int RECEIVE_PORT2 = 9000; // 新增的 Socket 接收字串

    private static final int Language_port = 9005; // 接收controll language and on/off

    private static final int startupport = 8020;

    private static final int gatewayipport = 8005;// 暫時不用

    private Spinner languagebox;
    private String translateanswer;


    private String stringVoiceTexta = "";
    private String stringVoiceTextb = "";
    private boolean isTextScrollingstaff = false;

    private StringBuilder accumulatedText = new StringBuilder();

    private WifiUtil w1;

    private ArrayList<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;

    private RecyclerView recyclerView;

    private CountDownLatch latch;

    private String ConnectOrNot;
    private final Handler handler = new Handler();
    private Runnable checkConnectRunnable;

    private ArrayAdapter<String> adapter1;

    private String selectedItem;
    private ImageView screensafer;
    private KickBoxHelper kickBoxHelper;
    private Languagelist languageListwhole = new Languagelist();
    private ImageView miclogo;
    private boolean isimageA = true;
    private int imageA_Resource = R.drawable.mic_off2; // 替换为你的图像 A 的资源 ID
    private int imageB_Resource = R.drawable.mic_on;
    private String addornot = "0";
    private TextView micstatus;
    private Button buttonforreset;

    private ServerSocket serverSocketdemo;
    private  ServerSocket serverSocket2;
    private int finishcounter = 0;

    private Button resetbutton;
    private String myidadress;
    private static final String FILE_NAME = "demo.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFile();
        String numberforreset=readFile();
        Log.e("MainActivity", "number : " + numberforreset);
        Toast.makeText(MainActivity.this,"number : "+numberforreset,Toast.LENGTH_SHORT).show();
        if(!("0".equals(numberforreset))&&!(numberforreset==null)&&Integer.valueOf(numberforreset)%2!=0){
            Toast.makeText(MainActivity.this," killer ",Toast.LENGTH_SHORT).show();
            System.exit(0);
        }

        Log.e("MainActivity", "number : " + numberforreset);

        latch = new CountDownLatch(1);
        initializeSpeechRecognizer();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);
        languagebox = findViewById(R.id.languagebox);
        screensafer = findViewById(R.id.screenSafer);
        micstatus = findViewById(R.id.micstatus);

        resetbutton = findViewById(R.id.resetbutton);

        resetbutton.setOnClickListener(view -> {
            System.exit(0);
        });

        kickBoxHelper = new KickBoxHelper(this);

        //initializeSpeechRecognizer();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // 初始化意圖
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                runOnUiThread(() -> textViewResult.setText("Listening..."));
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
                runOnUiThread(() -> textViewResult.setText("Processing..."));
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match found";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech detected";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "Recognizer busy";
                        break;
                    default:
                        message = "Error occurred: " + error;
                        break;
                }
                runOnUiThread(() -> {
                    textViewResult.setText(message);
                    if (isListening) {
                        speechRecognizer.startListening(speechRecognizerIntent);

                    }
                });
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                runOnUiThread(() -> {
                    if (matches != null && !matches.isEmpty()) {
                        // 获取最新的语音文本
                        String currentText = matches.get(0);

                        // 更新显示文本
                        textViewResult2.setText(currentText);
                        Log.e("MainActivity", "selectedLanguage現時" + selectedLanguage);


                        addMessage(currentText, "1");

                        translateLanguage(currentText, selectedLanguage);

                        // 添加消息

                    }
                    if (isListening) {
                        speechRecognizer.startListening(speechRecognizerIntent);
                    }
                });
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> partialMatches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                runOnUiThread(() -> {
                    if (partialMatches != null && !partialMatches.isEmpty()) {
                        if (isTextScrolling) {
                            isTextScrolling = false;
                            stringVoiceText1 = stringVoiceText2;
                            stringVoiceText2 = stringVoiceText3;
                            stringVoiceText3 = "";

                        }
                        stringVoicePartialText = partialMatches.get(0);
                        // 使用换行符分隔两行
                        String displayText;
                        if (stringVoiceText1.isEmpty()) {
                            displayText = stringVoicePartialText;
                        } else if (stringVoiceText2.isEmpty()) {
                            displayText = stringVoiceText1 + ". " + stringVoicePartialText;
                        } else {
                            displayText = stringVoiceText1 + ". " + stringVoiceText2 + ". " + stringVoicePartialText;
                        }



                    }
                });
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        w1 = new WifiUtil(this);

        if (checkWifiConnection2() == -1) {
            new AlertDialog.Builder(MainActivity.this).setIcon(R.mipmap.ic_launcher).setMessage("").setTitle("No network connection").show();
        }

        if (checkWifiConnection2() == 1) {
            myidadress = w1.getDeviceIpAddress();

            Log.d("myidadress", myidadress);

            new Thread(() -> {
                while (SERVER_IP == null) {
                    String gatewayIpAddress = w1.getGatewayIpAddress();
                    SERVER_IP = gatewayIpAddress;
                    Log.d("gatewayipaddress", gatewayIpAddress);
                    Log.d("SERVER_IP", SERVER_IP);
                    sendip(myidadress);

                    try {
                        Thread.sleep(5000); // 暂停 5 秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        // 初始化 UI 元素
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult2 = findViewById(R.id.textViewResult2);
        translateResult = findViewById(R.id.transalteResult);


        Button btnEnglish = findViewById(R.id.btn_en);
        Button btnJapanese = findViewById(R.id.btn_ja);
        Button btnKorean = findViewById(R.id.btn_ko);


        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
        btnEnglish.setTextColor(Color.parseColor("#003D58"));

        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
        btnJapanese.setTextColor(Color.parseColor("#003D58"));

        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
        btnKorean.setTextColor(Color.parseColor("#003D58"));
        btnEnglish.setOnClickListener(v -> {
            selectedLanguage = "fil";
            btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3"));
            btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色


            btnEnglish.setBackgroundColor(Color.parseColor("#009183"));


            btnEnglish.setTextColor(Color.WHITE);
            btnJapanese.setTextColor(Color.parseColor("#003D58"));
            btnKorean.setTextColor(Color.parseColor("#003D58"));
            languagebox.setSelection(0);
            abovelanguage = selectedLanguage;
            Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
            sendToSocket2("fil");
            addMessage("<<Lumipat sa Filipino>>", "1");
            initializeSpeechRecognizer();

            speechRecognizer.startListening(speechRecognizerIntent);

        });


        btnJapanese.setOnClickListener(v -> {
            selectedLanguage = "ja-JP";
            btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色


            btnJapanese.setBackgroundColor(Color.parseColor("#009183")); //
            btnJapanese.setTextColor(Color.WHITE);
            btnEnglish.setTextColor(Color.parseColor("#003D58"));
            btnKorean.setTextColor(Color.parseColor("#003D58"));
            languagebox.setSelection(0);

            abovelanguage = selectedLanguage;
            addMessage("<<日本語に切り替える>>", "1");
            sendToSocket2("ja");
            initializeSpeechRecognizer();
            //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja");
            speechRecognizer.startListening(speechRecognizerIntent);
        });
            btnJapanese.setBackgroundColor(Color.parseColor("#009183")); //
            btnJapanese.setTextColor(Color.WHITE);

        btnKorean.setOnClickListener(v -> {
            selectedLanguage = "ko-KR";
            btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色


            btnKorean.setBackgroundColor(Color.parseColor("#009183")); //
            btnKorean.setTextColor(Color.WHITE);
            btnEnglish.setTextColor(Color.parseColor("#003D58"));
            btnJapanese.setTextColor(Color.parseColor("#003D58"));
            languagebox.setSelection(0);

            abovelanguage = selectedLanguage;
            Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
            sendToSocket2("ko");
            addMessage("<<한국어로 전환>>", "1");

            initializeSpeechRecognizer();

            speechRecognizer.startListening(speechRecognizerIntent);
        });



        textViewResult2.setTextColor(Color.BLUE);

        miclogo = findViewById(R.id.miclog);

        miclogo.setImageResource(imageA_Resource);

        miclogo.setOnClickListener(view -> {
            if (isimageA) {
                miclogo.setImageResource(imageB_Resource);
                if (!isListening) {
                    stringVoiceText1 = "";
                    stringVoiceText2 = "";
                    addornot = "1";
                    speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));


                    isListening = true;
                    micstatus.setText("Mic On");

                }
            } else {
                miclogo.setImageResource(imageA_Resource);
                if (isListening) {
                    speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));

                    isListening = false;
                    addornot = "0";
                    micstatus.setText("Mic Off");
                }
            }
            isimageA = !isimageA;

        });

        //--------------------------
        List<String> languagelist = new ArrayList<>();
        languagelist.add("Others   ▼");
        for (int i = 0; i < languageListwhole.getLanguageList().size(); i++) {
            languagelist.add(languageListwhole.getLanguageList().get(i));
        }

        adapter1 = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, languagelist) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                return view;
            }
        };

        // 3. 设置下拉列表的样式 (可选)
        adapter1.setDropDownViewResource(R.layout.c2);

        // 4. 将 Adapter 设置给 Spinner
        languagebox.setAdapter(adapter1);

        languagebox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TextView selectedText = (TextView) view.findViewById(android.R.id.text1);
                selectedItem = (String) parent.getItemAtPosition(position);

                if (position != 0) {
                    selectedText.setTextColor(Color.parseColor("#ffffff"));
                    languagebox.setBackgroundResource(R.drawable.rad_bluegreen);

                    if (selectedItem.equals("عربي")) {

                        selectedLanguage = "ar";
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));
                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

                        abovelanguage = selectedLanguage;
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        addMessage("التبديل إلى اللغة العربية ", "1");
                        sendToSocket2("ar");


                    }
                    if (selectedItem.equals("Deutsch")) {

                        selectedLanguage = "de";
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));
                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

                        abovelanguage = selectedLanguage;
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        addMessage("<<Wechseln Sie zu Deutsch>>", "1");
                        sendToSocket2("de");

                    }
                    if (selectedItem.equals("Français")) {

                        selectedLanguage = "fr";
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

                        abovelanguage = selectedLanguage;
                        addMessage("<<Passer au français>>", "1");
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();

                        sendToSocket2("fr");

                    }
                    if (selectedItem.equals("हिन्दी")) {

                        selectedLanguage = "hi";
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

                        abovelanguage = selectedLanguage;
                        addMessage("<<हिंदी में स्विच करें>>", "1");
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();

                        sendToSocket2("hi");

                    }
                    if (selectedItem.equals("Indonesia")) {

                        selectedLanguage = "id";
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

                        abovelanguage = selectedLanguage;
                        addMessage("<<Beralih ke Bahasa Indonesia>>", "1");
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();

                        sendToSocket2("id");

                    }
                    if (selectedItem.equals("Malaysia")) {

                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage = "ms";
                        abovelanguage = selectedLanguage;
                        addMessage("<<Tukar kepada Bahasa Malaysia>>", "1");
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();

                        sendToSocket2("ms");

                    }
                    if (selectedItem.equals("Nederlands")) {

                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage = "nl";
                        abovelanguage = selectedLanguage;
                        addMessage("<<Schakel over naar Nederlands>>", "1");
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();

                        sendToSocket2("nl");

                    }
                    if (selectedItem.equals("Русский")) {

                        selectedLanguage = "ru";
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

                        abovelanguage = selectedLanguage;
                        addMessage("<<Переключить на русский>>", "1");
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();

                        sendToSocket2("ru");

                    }
                    if (selectedItem.equals("แบบไทย")) {
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage = "th";
                        abovelanguage = selectedLanguage;
                        addMessage("<<สลับเป็นภาษาไทย>>", "1");
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToSocket2("th");


                    }
                    if (selectedItem.equals("Tiếng Việt")) {

                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage = "vi";
                        abovelanguage = selectedLanguage;
                        addMessage("<<Chuyển sang tiếng Việt>>", "1");
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();

                        sendToSocket2("vi");

                    }
                    if (selectedItem.equals("English")) {

                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage = "en";
                        abovelanguage = selectedLanguage;
                        addMessage("<<Switch to English>>", "1");
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();

                        sendToSocket2("en");

                    }


                } else {
                    selectedText.setTextColor(Color.parseColor("#003D58")); // Revert to default color
                    languagebox.setBackgroundResource(R.drawable.rad_yellow);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // 初始化 Volley 的 RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // 檢查並請求錄音權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }


        new Thread(this::startServerPort9000).start();
        new Thread(this::startServer9005).start();//
        new Thread(this::startServerPortForGatewayIP).start();//





        languagebox.setSelection(0);
        selectedLanguage = "ja";
        abovelanguage = selectedLanguage;

        Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();



        hideSystemUI();
    }

    private void initializeSpeechRecognizer() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy(); // 停止當前識別器
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // 初始化意圖
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                runOnUiThread(() -> textViewResult.setText("Listening..."));
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
                runOnUiThread(() -> textViewResult.setText("Processing..."));
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match found";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech detected";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "Recognizer busy";
                        break;
                    default:
                        message = "Error occurred: " + error;
                        break;
                }
                runOnUiThread(() -> {
                    textViewResult.setText(message);
                    if (isListening) {
                        speechRecognizer.startListening(speechRecognizerIntent);

                    }
                });
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                runOnUiThread(() -> {
                    if (matches != null && !matches.isEmpty()) {
                        // 获取最新的语音文本
                        String currentText = matches.get(0);

                        // 更新显示文本
                        textViewResult2.setText(currentText);
                        Log.e("MainActivity", "selectedLanguage現時" + selectedLanguage);


                        addMessage(currentText, "1");

                        translateLanguage(currentText, selectedLanguage);

                        // 添加消息

                    }
                    if (isListening) {
                        speechRecognizer.startListening(speechRecognizerIntent);
                    }
                });
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> partialMatches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                runOnUiThread(() -> {
                    if (partialMatches != null && !partialMatches.isEmpty()) {
                        if (isTextScrolling) {
                            isTextScrolling = false;
                            stringVoiceText1 = stringVoiceText2;
                            stringVoiceText2 = stringVoiceText3;
                            stringVoiceText3 = "";

                        }
                        stringVoicePartialText = partialMatches.get(0);
                        // 使用换行符分隔两行
                        String displayText;
                        if (stringVoiceText1.isEmpty()) {
                            displayText = stringVoicePartialText;
                        } else if (stringVoiceText2.isEmpty()) {
                            displayText = stringVoiceText1 + ". " + stringVoicePartialText;
                        } else {
                            displayText = stringVoiceText1 + ". " + stringVoiceText2 + ". " + stringVoicePartialText;
                        }



                    }
                });
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });
    }

    private void setupLanguageButtons() {
        Button btnEnglish = findViewById(R.id.btn_en);
        Button btnJapanese = findViewById(R.id.btn_ja);
        Button btnKorean = findViewById(R.id.btn_ko);


        btnEnglish.setOnClickListener(v -> {
            selectedLanguage = "fil";
            btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3"));
            btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色


            btnEnglish.setBackgroundColor(Color.parseColor("#009183"));


            btnEnglish.setTextColor(Color.WHITE);
            btnJapanese.setTextColor(Color.parseColor("#003D58"));
            btnKorean.setTextColor(Color.parseColor("#003D58"));
            languagebox.setSelection(0);
            abovelanguage = selectedLanguage;
            Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
            sendToSocket2("fil");
            addMessage("<<Lumipat sa Filipino>>", "1");
            initializeSpeechRecognizer();
            speechRecognizer.startListening(speechRecognizerIntent);

        });

        btnJapanese.setOnClickListener(v -> {
            selectedLanguage = "ja-JP";
            btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色


            btnJapanese.setBackgroundColor(Color.parseColor("#009183")); //
            btnJapanese.setTextColor(Color.WHITE);
            btnEnglish.setTextColor(Color.parseColor("#003D58"));
            btnKorean.setTextColor(Color.parseColor("#003D58"));
            languagebox.setSelection(0);

            abovelanguage = selectedLanguage;
            addMessage("<<日本語に切り替える>>", "1");
            sendToSocket2("ja");
            initializeSpeechRecognizer();
            speechRecognizer.startListening(speechRecognizerIntent);
        });

        btnKorean.setOnClickListener(v -> {
            selectedLanguage = "ko-KR";
            btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色


            btnKorean.setBackgroundColor(Color.parseColor("#009183")); //
            btnKorean.setTextColor(Color.WHITE);
            btnEnglish.setTextColor(Color.parseColor("#003D58"));
            btnJapanese.setTextColor(Color.parseColor("#003D58"));
            languagebox.setSelection(0);

            abovelanguage = selectedLanguage;
            Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
            sendToSocket2("ko");
            addMessage("<<한국어로 전환>>", "1");
            initializeSpeechRecognizer();
            speechRecognizer.startListening(speechRecognizerIntent);
        });


    }

    public void translateLanguage(String text, String selectedLanguage) {
        String url = "https://translation.googleapis.com/language/translate/v2?key=" + "AIzaSyCxsExtPVzq7F69diSIgDRoPuiBfP5IoWw";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("q", text);

            jsonBody.put("source", selectedLanguage);
            jsonBody.put("target", "zh-TW");
            jsonBody.put("format", "text");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        try {
                            String translatedText = response.getJSONObject("data")
                                    .getJSONArray("translations")
                                    .getJSONObject(0)
                                    .getString("translatedText");
                            translateResult.setText(translatedText);
                            sendToSocket(translatedText);
                        } catch (JSONException e) {
                            Log.e("JSONParse", "Error parsing JSON response: " + e.getMessage());
                            Toast.makeText(MainActivity.this, "Error parsing translation response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("TranslateError", "Error: " + error.getMessage());
                        Toast.makeText(MainActivity.this, "Translation failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Log.e("JSONCreate", "Error creating JSON object: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Error creating translation request", Toast.LENGTH_SHORT).show();
        }
    }

    public void translateLanguagewithoutsocket(String text, String selectedLanguage) {
        new Thread(() -> {
            String url = "https://translation.googleapis.com/language/translate/v2?key=" + "AIzaSyCxsExtPVzq7F69diSIgDRoPuiBfP5IoWw";
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("q", text);
                jsonBody.put("source", abovelanguage);
                jsonBody.put("target", selectedLanguage);
                jsonBody.put("format", "text");

                // 使用主線程的請求隊列來執行網絡請求
                runOnUiThread(() -> {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonBody,
                            response -> {
                                try {
                                    String translatedText = response.getJSONObject("data")
                                            .getJSONArray("translations")
                                            .getJSONObject(0)
                                            .getString("translatedText");

                                    addMessage(translatedText, "1");

                                } catch (JSONException e) {
                                    Log.e("JSONParse", "Error parsing JSON response: " + e.getMessage());
                                    Toast.makeText(MainActivity.this, "Error parsing translation response", Toast.LENGTH_SHORT).show();
                                }
                            },
                            error -> {
                                Log.e("TranslateError", "Error: " + error.getMessage());
                                Toast.makeText(MainActivity.this, "Translation failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };

                    requestQueue.add(jsonObjectRequest);
                });

            } catch (JSONException e) {
                Log.e("JSONCreate", "Error creating JSON object: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error creating translation request", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private String getGoogleLanguage(String selectedLanguage) {
        if ("en-US".equals(selectedLanguage)) {
            selectedLanguage = "en";
        } else if ("ja-JP".equals(selectedLanguage)) {
            selectedLanguage = "ja";
        } else if ("ja-JP".equals(selectedLanguage)) {
            selectedLanguage = "ja";
        } else if ("ko-KR".equals(selectedLanguage)) {
            selectedLanguage = "ko";
        } else {
            selectedLanguage = selectedLanguage;
        }
        return selectedLanguage;
    }


    private void sendToSocket(String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
                Log.d("Socket", "傳送了123: " + message);
            } catch (Exception e) {
                Log.e("Socket", "Error in sending message: " + e.getMessage());
            }
        }).start();
    }

    public void sendToSocket2(String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT2);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
                Log.d("Socket", "Sent: " + message);
            } catch (Exception e) {
                Log.e("Socket", "Error in sending message: " + e.getMessage());
            }
        }).start();
    }

    private void sendip(String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(InetAddress.getByName(SERVER_IP), startupport);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
                Log.d("SocketStartUp", "Sent: " + message);
                //Toast.makeText(MainActivity.this, "已傳送ip"+message, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e("SocketStartUp", "Error in sending message: " + e.getMessage());
            }
        }).start();
    }


    private void startServerPort9000() {

        new Thread(() -> {
            serverSocket2 = null;
            try {
                serverSocket2 = new ServerSocket(RECEIVE_PORT2);
                Log.d("SocketServer", "服務器已啟動在端口 " + RECEIVE_PORT2);
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "服務器已啟動在端口 " + RECEIVE_PORT2, Toast.LENGTH_SHORT).show());
                while (!isDestroyed()) {  // 檢查 Activity 是否銷毀
                    try (Socket clientSocket = serverSocket2.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String message = in.readLine();
                        //in.reset();
                        Log.d("SocketServer", "收到: " + message);
                        //runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
                        runOnUiThread(() -> {
                            if (textViewResult2 != null) {  // 添加空值檢查
                                textViewResult2.setText(message);

                                addMessage(message, "0");


                            }
                        });
                    } catch (IOException e) {
                        Log.e("SocketServer", "處理客戶端錯誤: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                Log.e("SocketServer", "啟動服務器錯誤: " + e.getMessage());
            } finally {
                if (serverSocket2 != null && !serverSocket2.isClosed()) {
                    try {
                        serverSocket2.close();
                    } catch (IOException e) {
                        Log.e("SocketServer", "關閉服務器錯誤: " + e.getMessage());
                        Toast.makeText(this, "socket disconnect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).start();
    }

    private void startServer9005() {
        new Thread(() -> {
            Button btnEnglish = findViewById(R.id.btn_en);
            Button btnJapanese = findViewById(R.id.btn_ja);
            Button btnKorean = findViewById(R.id.btn_ko);

            try (ServerSocket serverSocket = new ServerSocket(Language_port)) {
                Log.d("SocketServer", "Server started on port " + Language_port);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Server started on port " + Language_port, Toast.LENGTH_SHORT).show());

                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String message = in.readLine();
                        Log.d("SocketServer", "Received on port " + Language_port + ": " + message);
                        runOnUiThread(() -> {
                            if (message.equals("fil")) {
                                selectedLanguage = "fil";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); //
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); //

                                btnEnglish.setTextColor(Color.WHITE);
                                btnEnglish.setBackgroundColor(Color.parseColor("#009183"));

                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                sendToSocket2("fil");
                                addMessage("<<Lumipat sa Filipino>>", "1");
                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(0);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();

                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("ja")) {
                                selectedLanguage = "ja-JP";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnJapanese.setTextColor(Color.WHITE);
                                btnJapanese.setBackgroundColor(Color.parseColor("#009183"));

                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                languagebox.setSelection(0);
                                addMessage("<<日本語に切り替える>>", "1");
                                sendToSocket2("ja-JP");

                                abovelanguage = selectedLanguage;
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();

                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("ko")) {
                                selectedLanguage = "ko-KR";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.WHITE);
                                btnKorean.setBackgroundColor(Color.parseColor("#009183")); //

                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                languagebox.setSelection(0);
                                addMessage("<<한국어로>>", "1");
                                sendToSocket2("ko");

                                abovelanguage = selectedLanguage;
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("ar")) {
                                selectedLanguage = "ar";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));

                                abovelanguage = selectedLanguage;

                                languagebox.setSelection(1);

                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("de")) {
                                selectedLanguage = "de";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));

                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(3);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("fr")) {
                                selectedLanguage = "fr-FR";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(2);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("hi")) {
                                selectedLanguage = "hi";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));

                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(4);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("id")) {
                                selectedLanguage = "id";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(5);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("ms")) {
                                selectedLanguage = "ms";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(6);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("nl")) {
                                selectedLanguage = "nl";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));


                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(7);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("ru")) {
                                selectedLanguage = "ru";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(8);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("th")) {
                                selectedLanguage = "th";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));

                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(9);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("vi")) {
                                selectedLanguage = "vi";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(10);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("en")) {
                                selectedLanguage = "en";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                abovelanguage = selectedLanguage;
                                languagebox.setSelection(11);
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                initializeSpeechRecognizer();
                                speechRecognizer.startListening(speechRecognizerIntent);

                            } else if (message.equals("1on1")) {
                                screensafermethod();
                                if (!isListening) {
                                    addornot = "1";
                                    stringVoiceText1 = "";
                                    stringVoiceText2 = "";
                                    initializeSpeechRecognizer();
                                    speechRecognizer.startListening(speechRecognizerIntent);
                                    Toast.makeText(MainActivity.this, "Mic On", Toast.LENGTH_SHORT).show();
                                    miclogo.setImageResource(imageB_Resource);


                                    isListening = true;
                                    micstatus.setText("Mic On");

                                }

                            } else if (message.equals("0off0")) {
                                selectedLanguage = "ja-JP";
                                addornot = "0";
                                stringVoiceText1 = "";
                                stringVoiceText2 = "";
                                textViewResult2.setText("");

                                speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
                                clearmessage();
                                Toast.makeText(MainActivity.this, "Mic Off", Toast.LENGTH_SHORT).show();
                                miclogo.setImageResource(imageA_Resource);
                                isListening = false;
                                micstatus.setText("Mic Off");
                                btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                                btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.WHITE);
                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setBackgroundColor(Color.parseColor("#009183"));
                                languagebox.setSelection(0);
                                speechRecognizer.destroy();
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                                screensafer.setVisibility(View.VISIBLE);


                            } else if (message.equals("del last message")) {
                                deleteLastMessage();
                            } else if (message.equals("del all message")) {
                                clearmessage();

                            } else if (message.equals("please rank")) {

                                kickBoxHelper.showKickBox();
                                screensafer.setVisibility(View.VISIBLE);

                            } else if (message.equals("connected_server")) {
                                ConnectOrNot = "connected_server";
                            }
                        });
                    } catch (IOException e) {
                        Log.e("SocketServer", "Error in handling client on port " + Language_port + ": " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                Log.e("SocketServer", "Error starting server on port " + Language_port + ": " + e.getMessage());
            }
        }).start();
    }

    private void startServerPortForGatewayIP() {


        new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(gatewayipport);
                Log.d("SocketServer", "服務器已啟動在端口 " + gatewayipport);
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "服務器已啟動在端口 " + gatewayipport, Toast.LENGTH_SHORT).show());
                while (!isDestroyed()) {  //
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String message = in.readLine();
                        //in.reset();
                        Log.d("SocketGateway", "收到: " + message);
                        //runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
                        runOnUiThread(() -> {
                            SERVER_IP = message;
                            Log.d("Gatewayis", "收到: " + SERVER_IP);


                        });
                    } catch (IOException e) {
                        Log.e("SocketServer", "處理客戶端錯誤: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                Log.e("SocketServer", "啟動服務器錯誤: " + e.getMessage());
            } finally {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.e("SocketServer", "關閉服務器錯誤: " + e.getMessage());
                        Toast.makeText(this, "socket disconnect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).start();
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // 允许用户交互时重新显示
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMessage(String text, String selfOrNot) {
        messages.add(new Message(text, selfOrNot));
        adapter.notifyDataSetChanged(); // 刷新 RecyclerView
        recyclerView.scrollToPosition(messages.size() - 1);

    }

    private void clearmessage() {
        messages.clear();
        adapter.notifyDataSetChanged(); // 刷新 RecyclerView
        recyclerView.scrollToPosition(messages.size() - 1);

    }

    public void deleteLastMessage() {
        if (!messages.isEmpty()) {
            int index = messages.size() - 1;

            while (index >= 0) {
                Message lastMessage = messages.get(index);

                if (lastMessage.getSelfOrNot().equals("0")) {
                    lastMessage.setDeleted(true); // 设置删除标志
                    break; // 找到目标消息，退出循环
                }

                index--; // 移动到上一条消息
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void startCheckingConnection(String gatewayip) {
        checkConnectRunnable = new Runnable() {
            @Override
            public void run() {
                // 每次循环检查 ConnectOrNot
                if (ConnectOrNot == null) {
                    String gatewayIpAddress = gatewayip; // 获取网关 IP
                    SERVER_IP = gatewayIpAddress;
                    Log.d("gatewayIpAddress", gatewayIpAddress);
                    Log.d("SERVER_IP", SERVER_IP);
                    sendip(SERVER_IP);
                    // 3 秒后再次执行
                    handler.postDelayed(this, 3000);
                } else {
                    // 如果 ConnectOrNot 不为 null，停止检查
                    handler.removeCallbacks(this);
                    Log.d("CheckConnect", "ConnectOrNot is set, stopping checks.");
                }
            }
        };
        handler.post(checkConnectRunnable); // 开始执行
    }

    private void screensafermethod() {
        Animation slideOutLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.drawable.slide_out_left);

        // 設定動畫監聽器
        slideOutLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 動畫開始時
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 動畫結束時，隱藏 View 並開始錄音
                screensafer.setVisibility(View.GONE);
                if (speechRecognizer != null) { // 確保 speechRecognizer 已初始化
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    speechRecognizer.startListening(intent);

                    isListening = true;
                } else {
                    Toast.makeText(MainActivity.this, "Speech recognizer not initialized", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // 動畫重複時
            }
        });

        // 啟動動畫
        screensafer.startAnimation(slideOutLeft);
    }

    private void showKickBoxDialog() {
        Dialog dialog = new Dialog(this);

        // 膨脹自定義佈局
        View kickBoxLayoutView = LayoutInflater.from(this).inflate(R.layout.popup_kickbox, null); // 替換 kick_box_layout 為你的 XML 檔案名稱
        dialog.setContentView(kickBoxLayoutView);

        // 找到 TextView 並更改其文字
        TextView kickBoxTitleTextView = kickBoxLayoutView.findViewById(R.id.kickBoxTitle);
        kickBoxTitleTextView.setText("New Title Here!"); // 更改文字

        dialog.show();
    }

    private int checkWifiConnection2() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiConnection != null && wifiConnection.isConnected()) {
            Toast.makeText(MainActivity.this, "Wi-Fi is connected..", Toast.LENGTH_SHORT).show();
            return 1;
        } else {
            Toast.makeText(MainActivity.this, "Wi-Fi is disconnected..", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }


    }

    @Override
    public void onResume() {

        super.onResume();

        Log.e("MainActivity", "trigger Resume" + SERVER_IP);
        Log.e("MainActivity", "finishcounter" + finishcounter);
        Toast.makeText(MainActivity.this, "trigger resume finishcounter : "+finishcounter, Toast.LENGTH_SHORT).show();
        // put your code here...


    }

    private String readFile() {
        try (FileInputStream fis = openFileInput(FILE_NAME)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            String text = new String(data);
            Toast.makeText(MainActivity.this, "trigger resume finishcounter : "+finishcounter, Toast.LENGTH_SHORT).show();
            return  text;
        } catch (IOException e) {
            e.printStackTrace();

        }return  null;
    }

    private void initializeFile() {
        File file = new File(getFilesDir(), FILE_NAME); // 獲取文件路徑

        if (!file.exists()) {
            //
            try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
                fos.write("0".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 文件存在，讀取當前數字並加 1
            try (FileInputStream fis = openFileInput(FILE_NAME)) {
                byte[] data = new byte[fis.available()];
                fis.read(data);
                String text = new String(data);
                int number = Integer.parseInt(text) + 1; // 將數字加一

                // 覆蓋文件內容
                try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
                    fos.write(String.valueOf(number).getBytes()); // 寫入新的數字
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}