package com.example.a0321;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.os.Handler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import android.widget.Spinner;
import android.widget.ArrayAdapter;


import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private SpeechRecognizer speechRecognizer;
    private TextView textViewResult,textreceive,textViewResult2,translateResult;;


    private static  TextView can1;
    private static TextView can2;

    private static TextView can3;


    private boolean isListening = false;
    private boolean isTextScrolling = false;
    private String stringVoiceText1 = "";
    private String stringVoiceText2 = "";

    private String stringVoiceText3 = "";
    private String stringVoicePartialText;

    private String selectedLanguage = "en-US";
    private RequestQueue requestQueue;

    private static  String SERVER_IP ;
    private static final int SERVER_PORT = 9000; // 向client 發送data
    private static final int SERVER_PORT2 = 9005; // 用於向server 送出 on off language


    private static final int MYOPEN_PORT = 9050;
    private  static final int passiveLanguageport = 5090; // 用於被client 改變language

    private static final int Startupportforclient = 8020;
    private CountDownLatch latch;
    private  String translatedText;

    private ArrayList<Message> messages = new ArrayList<>();
    private MessageAdapter adapter;

    private RecyclerView recyclerView;

    private Spinner languagebox;
    private  Spinner spinnerQandA;

    private  Button clearalllist;

    private ImageView miclogo;
    private boolean isimageA = true;
    private int imageA_Resource = R.drawable.mic_off2; // 替换为你的图像 A 的资源 ID
    private int imageB_Resource = R.drawable.mic_on;

    private Button dellasttext;
    private Map<String, String> qaMap = new HashMap<>();



    private ArrayAdapter<String> adapter1;

    private static final String FILE_NAME = "demo.txt";
    private static final int PICK_FILE_REQUEST = 1;
    private static List<String> question = new ArrayList<>();

    private static List<FqaModel> demolist = new ArrayList<>();

    private  String addornot;
    private String staffsidemic;
    private  String conversationstatus = "0";
    private  LanguageList languageListwhole = new LanguageList();

    private  String BASE_URL= "http://antares.ksstec.com.hk:57001/";
    private  List<FqaModel> fqaModels = new ArrayList<>();
    private  List<TranslateList> TransModels = new ArrayList<>();
    private  List<TranslateList> TransModelsforusing = new ArrayList<>();
    private Intent speechRecognizerIntent;
    private  int resetornot = 0;

    private Handler handler = new Handler();
    private int countdown = 3;
    private int resumecounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        latch = new CountDownLatch(1);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);

        dellasttext= findViewById(R.id.clearlast);
        dellasttext.setBackgroundColor((Color.parseColor("#009183")));
        dellasttext.setOnClickListener(V->{
            deleteLastMessage();
            sendToSocket2("del last message");
            sendToDB(getCurrentTime()+" Deleted last message");
        });

        new Thread(this::startServerforlanguageboxdata).start();

        Button btnEnglish = findViewById(R.id.btn_en);
        Button btnJapanese = findViewById(R.id.btn_ja);
        Button btnKorean = findViewById(R.id.btn_ko);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult2 = findViewById(R.id.textViewResult2);
        translateResult = findViewById(R.id.transalteResult); //
        textreceive = findViewById(R.id.textreceive);  //

        //buttonStart = findViewById(R.id.buttonStart);
        can1=findViewById(R.id.bubbleTextView);
        can2=findViewById(R.id.bubbleTextView2);
        can3=findViewById(R.id.bubbleTextView3);
        clearalllist= findViewById(R.id.clearlist);

        spinnerQandA=findViewById(R.id.spinnerQandA);

        api2();
        api();
        Log.d("fqamodel size", String.valueOf(fqaModels.size()));
        sendToSocketforlanguageboxdata("appleapledemo123");


        Log.d("MainActivity", "api2()TranslateList的 " + TransModels.size());


        //--------------------------------

        languagebox=findViewById(R.id.languagebox);

        List<String> languagelist = new ArrayList<>();
        languagelist.add("其他語言▼");
        for(int i =0;i<languageListwhole.getLanguageList().size();i++){
            languagelist.add(languageListwhole.getLanguageList().get(i));
        }


        adapter1 = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, languagelist){
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
                String selectedItem = (String) parent.getItemAtPosition(position);

                if (position != 0) {
                    selectedText.setTextColor(Color.parseColor("#ffffff"));
                    languagebox.setBackgroundResource(R.drawable.rad_bluegreen);

                    if(selectedItem.equals("阿拉伯語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));
                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="ar";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("ar");

                    }
                    if(selectedItem.equals("德語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));
                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="de";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("de");

                    }
                    if(selectedItem.equals("法語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="fr";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("fr");

                    }
                    if(selectedItem.equals("印度語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="hi";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("hi");

                    }
                    if(selectedItem.equals("印尼語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="id";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("id");

                    }
                    if(selectedItem.equals("馬來西亞語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="ms";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("ms");

                    }
                    if(selectedItem.equals("荷蘭語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="nl";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("nl");

                    }
                    if(selectedItem.equals("俄語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="ru";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("ru");

                    }
                    if(selectedItem.equals("泰語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="th";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("th");

                    }
                    if(selectedItem.equals("越南語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="vi";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
                        sendToSocket2("vi");

                    }
                    if(selectedItem.equals("英語")){
                        btnEnglish.setTextColor(Color.parseColor("#003D58"));
                        btnJapanese.setTextColor(Color.parseColor("#003D58"));
                        btnKorean.setTextColor(Color.parseColor("#003D58"));

                        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                        selectedLanguage="en";
                        Toast.makeText(parent.getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();
                        sendToDB(getCurrentTime()+" change to "+selectedLanguage);
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

        spinnerlanguage();


        miclogo = findViewById(R.id.miclog);

        miclogo.setImageResource(imageA_Resource);

        miclogo.setOnClickListener(view -> {
            if(isimageA ){
                miclogo.setImageResource(imageB_Resource);
            }else{
                miclogo.setImageResource(imageA_Resource);
            }
            isimageA=!isimageA;

        });

        spinnerQandA = findViewById(R.id.spinnerQandA);

        clearalllist.setBackgroundColor(Color.parseColor("#009183"));





        can1.setOnClickListener(v -> {
            if("1".equals(conversationstatus)){
                addMessage(fqaModels.get(0).getValue(), "1");
                translateLanguage(fqaModels.get(0).getValue(),selectedLanguage);
            }



        });

        can2.setOnClickListener(v -> {
            if("1".equals(conversationstatus)){
                addMessage(fqaModels.get(1).getValue(), "1");
                translateLanguage(fqaModels.get(1).getValue(),selectedLanguage);
            }


        });

        can3.setOnClickListener(v -> {
            if("1".equals(conversationstatus)){
                addMessage(fqaModels.get(2).getValue(), "1");
                translateLanguage(fqaModels.get(2).getValue(),selectedLanguage);
            }


        });




        btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

        btnEnglish.setTextColor(Color.WHITE);

        btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色


        btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色




        // 設置初始按鈕背景和文字顏色
        //buttonStart.setBackgroundColor(Color.parseColor("#808080"));
        //buttonStart.setTextColor(Color.WHITE);

        textreceive.setTextColor(Color.BLUE);
        textViewResult2.setTextColor(Color.BLUE);



        // 初始化 Volley 的 RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }




        // 檢查並請求錄音權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
        new Thread(this::getIP).start();




        new Thread(this::startSocket).start();  //startServerForLanguage
        new Thread(this::startServerForLanguage).start();




        // 初始化 SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-HK");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        // 設置 RecognitionListener
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                runOnUiThread(() -> textViewResult.setText("正在聆聽..."));
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                runOnUiThread(() -> textViewResult.setText("處理中..."));
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "未找到匹配";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "未檢測到語音";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "辨識器忙碌";
                        break;
                    default:
                        message = "發生錯誤: " + error;
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
                        if("1".equals(addornot)){
                            Log.e("MainActivity", "transusing的" + TransModelsforusing.size() );


                        currentText=currentText.replaceAll("金鐘站","MTR Admiralty Station");

                        currentText=currentText.replaceAll("東湧","東涌");
                        currentText=currentText.replaceAll("鰂魚湧","鰂魚涌");
                        addMessage(currentText, "1");}
                        for(int i =0;i<TransModelsforusing.size();i++){
                            currentText=currentText.replaceAll(TransModelsforusing.get(i).getKey(),TransModelsforusing.get(i).getValue());
                        }
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
                        //textViewResult2.setText(displayText);


                    }
                });

            }


            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        clearalllist.setOnClickListener(v->{
            //clearmessage();
            sendToSocket2("del all message");
            sendToDB(getCurrentTime()+" Deleted all message");


            translateLanguage("請問有什麼幫到你",selectedLanguage); // remark
            if ("0".equals(conversationstatus)) {
                clearalllist.setEnabled(false);
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                    stringVoiceText1 = "";
                    stringVoiceText2 = "";
                    speechRecognizer.startListening(speechRecognizerIntent);
                    clearalllist.setText("Tap to end conversation");
                    clearalllist.setBackgroundColor(Color.parseColor("#003D58"));
                    sendToSocket2("1on1");
                    addornot = "1";
                    conversationstatus = "1";
                    addMessage("請問有什麼幫到你","1");
                    isListening = true;
                    miclogo.setImageResource(imageB_Resource);

                    sendToDB(getCurrentTime()+" Opened mic");
                    new Handler().postDelayed(() -> {
                        clearalllist.setEnabled(true);


                    }, 800);
                    Toast.makeText(MainActivity.this, "Mic On", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "音訊權限被拒絕", Toast.LENGTH_SHORT).show();
                }


            } else if("1".equals(conversationstatus)) {
                clearalllist.setEnabled(false);
                speechRecognizer.stopListening();

                translatedText ="";
                miclogo.setImageResource(imageA_Resource);
                if (textreceive != null) {  // 添加空值檢查
                    textreceive.setText("");
                }
                textViewResult2.setText("");
                clearalllist.setText("Tap to start conversation");
                clearalllist.setBackgroundColor(Color.parseColor("#009183"));
                sendToSocket2("0off0");

                clearmessage();
                addornot="0";
                conversationstatus = "0";
                isListening = false;
                sendToSocket2("please rank");
                btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

                btnJapanese.setTextColor(Color.WHITE);
                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                btnKorean.setTextColor(Color.parseColor("#003D58"));
                selectedLanguage = "ja";
                btnJapanese.setBackgroundColor(Color.parseColor("#009183"));
                Toast.makeText(MainActivity.this, "Mic Off", Toast.LENGTH_SHORT).show();
                languagebox.setSelection(0);

                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
                sendToDB("END cloesed mic");

                new Handler().postDelayed(() -> {
                    clearalllist.setEnabled(true);


                }, 7000); // 3000毫秒后执行

            }
            isimageA=!isimageA;


        });


        // 開始/停止聆聽按鈕
        miclogo.setOnClickListener(v -> {
            if (!isListening) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                    stringVoiceText1 = "";
                    stringVoiceText2 = "";
                    speechRecognizer.startListening(speechRecognizerIntent);



                    staffsidemic = "1";
                    isListening = true;
                    miclogo.setImageResource(imageB_Resource);
                    sendToDB(getCurrentTime()+" Opened mic");
                    Toast.makeText(MainActivity.this, "Mic On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "音訊權限被拒絕", Toast.LENGTH_SHORT).show();
                }
            } else {
                speechRecognizer.stopListening();

                translatedText ="";
                miclogo.setImageResource(imageA_Resource);
                if (textreceive != null) {  // 添加空值檢查
                    textreceive.setText("");
                }
                textViewResult2.setText("");
                //sendToSocket2("0off0"); no controller client side

                staffsidemic = "0";
                isListening = false;
                sendToDB("END cloesed mic");

            }
            isimageA=!isimageA;

            // 切換按鈕背景和文字顏色

        });

        setupLanguageButtons();

        btnJapanese.setBackgroundColor(Color.parseColor("#009183"));

        languagebox.setSelection(0);

        btnJapanese.setTextColor(Color.WHITE);
        btnEnglish.setTextColor(Color.parseColor("#003D58"));
        btnKorean.setTextColor(Color.parseColor("#003D58"));

        selectedLanguage = "ja";
        Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();

        sendToDB(getCurrentTime()+" changed to Japanese");

        hideSystemUI();
    }


    private void setupLanguageButtons() {
        Button btnEnglish = findViewById(R.id.btn_en);
        Button btnJapanese = findViewById(R.id.btn_ja);
        Button btnKorean = findViewById(R.id.btn_ko);


        btnEnglish.setOnClickListener(v -> {
            btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
            btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

            btnEnglish.setTextColor(Color.WHITE);
            btnJapanese.setTextColor(Color.parseColor("#003D58"));
            btnKorean.setTextColor(Color.parseColor("#003D58"));
            selectedLanguage = "fil";
            btnEnglish.setBackgroundColor(Color.parseColor("#009183"));
            languagebox.setSelection(0);



            Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
            sendToSocket2("fil");
            sendToDB(getCurrentTime()+" changed to English");
        });

        btnJapanese.setOnClickListener(v -> {
            btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
            btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色


            btnJapanese.setBackgroundColor(Color.parseColor("#009183"));

            languagebox.setSelection(0);

            btnJapanese.setTextColor(Color.WHITE);
            btnEnglish.setTextColor(Color.parseColor("#003D58"));
            btnKorean.setTextColor(Color.parseColor("#003D58"));

            selectedLanguage = "ja-JP";
            Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
            sendToSocket2("ja");
            sendToDB(getCurrentTime()+" changed to Japanese");
        });

        btnKorean.setOnClickListener(v -> {
            btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
            btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
            btnKorean.setBackgroundColor(Color.parseColor("#009183"));

            languagebox.setSelection(0);

            btnKorean.setTextColor(Color.WHITE);
            btnEnglish.setTextColor(Color.parseColor("#003D58"));
            btnJapanese.setTextColor(Color.parseColor("#003D58"));

            selectedLanguage = "ko-KR";
            Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();
            sendToSocket2("ko");
            sendToDB(getCurrentTime()+" change to korean");
        });


    }

    private void translateLanguage(String text, String selectedLanguage) {
        String url = "https://translation.googleapis.com/language/translate/v2?key=AIzaSyCxsExtPVzq7F69diSIgDRoPuiBfP5IoWw";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("q", text);
            //String googleLanguage = getGoogleLanguage(selectedLanguage);
            jsonBody.put("hl", "zh-HK"); //
            jsonBody.put("sl", "auto"); //
            jsonBody.put("target", selectedLanguage);
            jsonBody.put("format", "text");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                             translatedText = response.getJSONObject("data")
                                    .getJSONArray("translations")
                                    .getJSONObject(0)
                                    .getString("translatedText");
                            translateResult.setText(translatedText);
                            sendToSocket(translatedText);


                        } catch (JSONException e) {
                            Log.e("JSONParse", "解析 JSON 錯誤: " + e.getMessage());
                            Toast.makeText(MainActivity.this, "解析翻譯回應錯誤", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("TranslateError", "錯誤: " + error.getMessage());
                        Toast.makeText(MainActivity.this, "翻譯失敗: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
            Log.e("JSONCreate", "創建 JSON 錯誤: " + e.getMessage());
            Toast.makeText(MainActivity.this, "創建翻譯請求錯誤", Toast.LENGTH_SHORT).show();
        }
    }



    private void sendToSocket(String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
                Log.d("Socket", "已發送: " + message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("Socket", "發送消息錯誤: " + e.getMessage());
            }
        }).start();
    }

    private void sendToSocket2(String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT2 );
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
                Log.d("Socket", "已發送: " + message);
            } catch (Exception e) {
                Log.e("Socket", "發送消息錯誤: " + e.getMessage());
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
                        | View.SYSTEM_UI_FLAG_FULLSCREEN //
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void startSocket() {
        new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(MYOPEN_PORT);
                Log.d("SocketServer", "服務器已啟動在端口 " + MYOPEN_PORT);
                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "服務器已啟動在端口 " + MYOPEN_PORT, Toast.LENGTH_SHORT).show());
                while (!isDestroyed()) {  // 檢查 Activity 是否銷毀
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String message = in.readLine();
                        Log.d("SocketServer", "收到: " + message);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
                        runOnUiThread(() -> {
                            if (textreceive != null) {  // 添加空值檢查
                                textreceive.setText(message);
                                if("1".equals(addornot)){
                                    addMessage(message,"0");
                                }

                            }
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

    private void startServerForLanguage() {
        new Thread(() -> {
            Button btnEnglish = findViewById(R.id.btn_en);
            Button btnJapanese = findViewById(R.id.btn_ja);
            Button btnKorean = findViewById(R.id.btn_ko);

            try (ServerSocket serverSocket = new ServerSocket(passiveLanguageport)) {
                Log.d("SocketServer", "Server started on port " + passiveLanguageport + "用於被client controll language");
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Server started on port " + passiveLanguageport, Toast.LENGTH_SHORT).show());

                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                        String message = in.readLine();
                        Log.d("SocketServer", "Received on port " + passiveLanguageport + ": " + message);
                        runOnUiThread(() -> {

                            if (message.equals("fil")) {
                                selectedLanguage = "fil";
                                btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                                btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色


                                btnEnglish.setBackgroundColor(Color.parseColor("#009183"));
                                btnEnglish.setTextColor(Color.WHITE);
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                btnKorean.setTextColor(Color.parseColor("#003D58"));

                                languagebox.setSelection(0);

                                addMessageforchangelanguage("<< 切換菲律賓語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }

                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            } else if (message.equals("ja")) {
                                selectedLanguage = "ja";
                                btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF"));
                                btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色


                                btnJapanese.setBackgroundColor(Color.parseColor("#009183"));
                                btnJapanese.setTextColor(Color.WHITE);

                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                languagebox.setSelection(0);

                                addMessageforchangelanguage("<< 切換日語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }else if (message.equals("ko")) {
                                selectedLanguage = "ko-KR";
                                btnEnglish.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#BFBFBF")); // 浅灰色

                                btnKorean.setTextColor(Color.WHITE);
                                btnKorean.setBackgroundColor(Color.parseColor("#009183")); //
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                languagebox.setSelection(0);

                                addMessageforchangelanguage("<< 切換韓語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("ar")) {
                                selectedLanguage = "ar";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色b

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));


                                languagebox.setSelection(1);
                                addMessageforchangelanguage("<< 切換阿拉伯語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }

                            else if (message.equals("de")) {
                                selectedLanguage = "de";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));
                                addMessageforchangelanguage("<< 切換德語 >>","0");

                                languagebox.setSelection(3);
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("fr")) {
                                selectedLanguage = "fr-FR";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));

                                languagebox.setSelection(2);
                                addMessageforchangelanguage("<< 切換法語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("hi")) {
                                selectedLanguage = "hi";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));

                                addMessageforchangelanguage("<< 切換印度語 >>","0");
                                languagebox.setSelection(4);
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("id")) {
                                selectedLanguage = "id";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));


                                addMessageforchangelanguage("<< 切換印尼語 >>","0");
                                languagebox.setSelection(5);
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("ms")) {
                                selectedLanguage = "ms";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));

                                addMessageforchangelanguage("<< 切換馬來西亞語 >>","0");
                                languagebox.setSelection(6);
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("nl")) {
                                selectedLanguage = "nl";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));


                                languagebox.setSelection(7);
                                addMessageforchangelanguage("<< 切換荷蘭語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("ru")) {
                                selectedLanguage = "ru";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));


                                languagebox.setSelection(8);
                                addMessageforchangelanguage("<< 切換俄語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("th")) {
                                selectedLanguage = "th";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));


                                languagebox.setSelection(9);
                                addMessageforchangelanguage("<< 切換泰語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("vi")) {
                                selectedLanguage = "vi";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));


                                languagebox.setSelection(10);
                                addMessageforchangelanguage("<< 切換越南語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if (message.equals("en")) {
                                selectedLanguage = "en";
                                btnEnglish.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnJapanese.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色
                                btnKorean.setBackgroundColor(Color.parseColor("#D3D3D3")); // 浅灰色

                                btnKorean.setTextColor(Color.parseColor("#003D58"));
                                btnEnglish.setTextColor(Color.parseColor("#003D58"));
                                btnJapanese.setTextColor(Color.parseColor("#003D58"));


                                languagebox.setSelection(11);
                                addMessageforchangelanguage("<< 切換英語 >>","0");
                                if("0".equals(conversationstatus)){
                                    clearalllist.performClick();
                                }
                                Toast.makeText(MainActivity.this, selectedLanguage, Toast.LENGTH_SHORT).show();


                            }
                            else if(message.equals("ENABLE button")){
                                clearalllist.setEnabled(true);
                            }
                        });
                    } catch (IOException e) {
                        Log.e("SocketServer", "Error in handling client on port " + passiveLanguageport + ": " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                Log.e("SocketServer", "Error starting server on port " + passiveLanguageport + ": " + e.getMessage());
            }
        }).start();
    }
    private void getIP() {
        new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(Startupportforclient);
                Log.d("SocketServer", "服务已启动在端口 " + Startupportforclient);

                while (!isDestroyed()) {  // 检查 Activity 是否销毁
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String message = in.readLine();
                        if (message != null) {
                            SERVER_IP = message; // 将接收到的值赋给 SERVER_IP
                            Log.d("SocketServer", "接收到的 SERVER_IP: " + SERVER_IP);

                        }
                    } catch (IOException e) {
                        Log.e("SocketServer", "处理客户端错误: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                Log.e("SocketServer", "启动服务器错误: " + e.getMessage());
            } finally {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.e("SocketServer", "关闭服务器错误: " + e.getMessage());
                    }
                }
            }
        }).start();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "權限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "權限被拒絕", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMessage(String text, String selfOrNot) {
        messages.add(new Message(text, selfOrNot));

        adapter.notifyDataSetChanged(); // 刷新 RecyclerView
        recyclerView.scrollToPosition(messages.size() - 1);
        sendToDB(getCurrentTime()+" "+text);

    }
    private void addMessageforchangelanguage(String text, String selfOrNot) {
        messages.add(new Message(text, selfOrNot));

        adapter.notifyDataSetChanged(); // 刷新 RecyclerView
        recyclerView.scrollToPosition(messages.size() - 1);
        sendToDB(getCurrentTime()+" "+text);

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

                if (lastMessage.getSelfOrNot().equals("1")) {
                    lastMessage.setDeleted(true); // 设置删除标志
                    break; // 找到目标消息，退出循环
                }

                index--; // 移动到上一条消息
            }

            adapter.notifyDataSetChanged(); // 通知 Adapter 数据已更改
        }
    }

    private void loadQAMap() {
        // Construct the file path
        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(documentsDir, "qa.txt");

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); // Specify UTF-8 encoding
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    qaMap.put(key, value);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading qa.txt: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        setupSpinner();
    }


    private void setupSpinner() {
        List<String> question = new ArrayList<>();
        question.add("Frequent Q&A   ▼");
        question.addAll(qaMap.keySet()); // Add keys to the list

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner_item, // 默认 Spinner 样式
                question);

        // 3. 设置下拉列表的样式 (可选)
        arrayAdapter.setDropDownViewResource(R.layout.c2);

        // 4. 将 Adapter 设置给 Spinner
        spinnerQandA.setAdapter(arrayAdapter);

        spinnerQandA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuestionKey = (String) parent.getItemAtPosition(position);

                // 检查是否选择了 Q1, Q2 或 Q3 (or any key from qaMap)
                if (qaMap.containsKey(selectedQuestionKey)) {
                    // 触发 addMessage 方法，selfOrNot 为 1
                    String selectedQuestionValue = qaMap.get(selectedQuestionKey);
                    addMessage(selectedQuestionValue, "1");

                    // 可选: 将 Spinner 重置为 "Frequent Q&A"

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void sendToDB(String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(InetAddress.getByName("antares.ksstec.com.hk"), 57002);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
                Log.d("Sockettodb", "已發送: " + message);
            } catch (Exception e) {
                Log.e("Sockettodb", "發送消息錯誤: ", e);
            }
        }).start();
    }

    private void startServerforlanguageboxdata() {
        new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8810);
                Log.d("SocketServer", "服务已启动在端口 8810");

                while (!isDestroyed()) {  // 检查 Activity 是否销毁
                    try {
                        Socket clientSocket = serverSocket.accept();
                        handleClient(clientSocket);  // 处理客户端连接
                    } catch (IOException e) {
                        Log.e("SocketServer", "处理客户端错误: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                Log.e("SocketServer", "启动服务器错误: " + e.getMessage());
            } finally {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.e("SocketServer", "关闭服务器错误: " + e.getMessage());
                    }
                }
            }
        }).start();
    }
    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String receivedString = in.readLine();

            // Process the received string
            processReceivedString(receivedString);

            // Update TextView on the main thread



            Log.d("SocketServerwhole", "Received string: " + receivedString);
        } catch (IOException e) {
            Log.e("SocketServer", "Error receiving string: ", e);
        }
    }

    private static void processReceivedString(String receivedString) {
        // Split the received string into lines


        String[] lines = receivedString.split(",,");

        Log.d("SocketServerlines", "長度為" + lines);

        // Iterate through each line and process it
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // Split each line into key and value based on the "=" delimiter
            String[] parts = line.split("=", 2); // Limit to 2 parts to handle values with "="

            // Ensure that the line contains both a key and a value
            if (parts.length == 2) {
                String key = parts[0].trim(); // Trim whitespace from key
                String value = parts[1].trim(); // Trim whitespace from value

                // Create a new FqaModel object and set the key and value
                FqaModel fqaModel = new FqaModel();
                fqaModel.setKey(key);
                fqaModel.setValue(value);

                // Add the FqaModel object to the demolist
                demolist.add(fqaModel);

                Log.d("SocketServerlanguage", "Added to demolist: Key=" + key + ", Value=" + value);
                Log.d("SocketServerlanguage123", "Added to demolist的(1)=" + demolist.get(i).getKey());
            } else {
                Log.w("SocketServerlanguage", "Skipping invalid line: " + line);  // Log invalid lines
            }
        }for(int i=0;i<demolist.size();i++){
            //question.add(demolist.get(i).getKey());

        }


        // Optionally, log the contents of demolist for verification
        Log.d("SocketServer", "demolist size: " + demolist.size());
        for (FqaModel model : demolist) {
            Log.d("SocketServer", "Key: " + model.getKey() + ", Value: " + model.getValue());
        }
    }
    private void sendToSocketforlanguageboxdata(String message) {
        new Thread(() -> {
            try (Socket socket = new Socket(InetAddress.getByName("192.168.10.83"), 8825);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
                Log.d("Socket", "已發送: " + message);
            } catch (Exception e) {
                Log.e("Socket", "發送消息錯誤: ", e);
            }
        }).start();
    }

    private void spinnerlanguage(){
        question.add("Frequent Q&A       ▼");



        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.customfqa, // 默认 Spinner 样式
                question);

        // 3. 设置下拉列表的样式 (可选)
        arrayAdapter.setDropDownViewResource(R.layout.c2);

        // 4. 将 Adapter 设置给 Spinner
        spinnerQandA.setAdapter(arrayAdapter);

        spinnerQandA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuestion = (String) parent.getItemAtPosition(position);

                // 检查是否选择了 Q1, Q2 或 Q3
                if (!(selectedQuestion.equals("Frequent Q&A       ▼"))) {
                    if("1".equals(conversationstatus)){
                        selectedQuestion=fqaModels.get(position+2).getValue();
                        translateLanguage(fqaModels.get(position+2).getValue(),selectedLanguage);
                        addMessage(selectedQuestion, "1");
                        spinnerQandA.setSelection(0);
                    }
                    // 触发 addMessage 方法，selfOrNot 为 1

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选择任何项目时调用
            }
        });
    }

    private  void api(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://antares.ksstec.com.hk:57001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<FqaModel>> call = apiService.getData();
        call.enqueue(new Callback<List<FqaModel>>() {
            @Override
            public void onResponse(Call<List<FqaModel>> call, Response<List<FqaModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FqaModel>f1 = response.body();
                    for (int i = 0; i < f1.size(); i++) {
                        Log.d("MainActivity", "f1的 " + f1.size());

                        fqaModels.add(f1.get(i));

                    }
                    Log.d("MainActivity", "fqamodels的 " + fqaModels.size());
                    Log.d("MainActivity", "fqamodels的 " + fqaModels.get(4).getValue());
                    can1.setText(fqaModels.get(0).getKey());
                    can2.setText(fqaModels.get(1).getKey());
                    can3.setText(fqaModels.get(2).getKey());
                    for(int i = 3;i<fqaModels.size();i++){
                        question.add(fqaModels.get(i).getKey());
                    }


                } else {
                    Log.e("MainActivity", "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<FqaModel>> call, Throwable t) {
                Log.e("MainActivity", "Error fetching data", t);
            }
        });
    }

    private void api2() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://antares.ksstec.com.hk:57001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<TranslateList>> call = apiService.getData2();
        call.enqueue(new Callback<List<TranslateList>>() {
            @Override
            public void onResponse(Call<List<TranslateList>> call, Response<List<TranslateList>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TranslateList> t1 = response.body();
                    for (int i = 0; i < t1.size(); i++) {
                        Log.d("MainActivity", "t1的 " + t1.size());
                        TransModels.add(t1.get(i));
                    }
                    Log.d("MainActivity", "TranslateList的 " + TransModels.size());

                            TransModelsforusing=TransModels;
                    Log.d("MainActivity", "TransModelsforusing的 " + TransModelsforusing.size());



                } else {
                    Log.e("MainActivity", "TranslateList的 Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<TranslateList>> call, Throwable t) {
                Log.e("MainActivity", "Error fetching data", t);
            }
        });
    }

    private void api3(DataFetchCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://antares.ksstec.com.hk:57001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<TranslateList>> call = apiService.getData2();
        call.enqueue(new Callback<List<TranslateList>>() {
            @Override
            public void onResponse(Call<List<TranslateList>> call, Response<List<TranslateList>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TranslateList> t1 = response.body();
                    for (TranslateList item : t1) {
                        TransModels.add(item);
                    }
                    callback.onDataFetched(TransModels); // 調用回調
                } else {
                    Log.e("MainActivity", "TranslateList的 Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<TranslateList>> call, Throwable t) {
                Log.e("MainActivity", "Error fetching data", t);
            }
        });
    }
    private void startCountdown() {
        clearalllist.setEnabled(false);
        countdown = 3;

        final Runnable countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (countdown > 0) {
                    clearalllist.setText(String.valueOf(countdown));
                    countdown--;
                    handler.postDelayed(this, 1000);
                } else {
                    clearalllist.setText("Click Me");
                    clearalllist.setEnabled(true);
                }
            }
        };

        handler.post(countdownRunnable);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(resumecounter>0){
            question.clear();
        }
        resumecounter++;
        Log.e("MainActivity", "straff  Resume client 的ip 是" + SERVER_IP );
        Log.e("MainActivity", "resumecounter" + resumecounter );
        Toast.makeText(MainActivity.this, "trigger resume resumecounter : "+resumecounter, Toast.LENGTH_SHORT).show();
        // put your code here...

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}