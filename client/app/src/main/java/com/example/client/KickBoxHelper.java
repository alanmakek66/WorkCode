package com.example.client;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class KickBoxHelper {

    private Dialog kickBoxDialog;
    private Activity activity; // 保存 Activity 的引用
    private  MainActivity m1 =new MainActivity();

    public static String conversationrank;


    public KickBoxHelper(Activity activity) {
        this.activity = activity;
    }

    public void showKickBox() {
        kickBoxDialog = new Dialog(activity);
        kickBoxDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        kickBoxDialog.setContentView(R.layout.popup_kickbox);

        kickBoxDialog.dismiss();

        //
        kickBoxDialog.setCanceledOnTouchOutside(false);
        kickBoxDialog.setCancelable(false); //

        // 設定彈出視窗的背景為透明
        kickBoxDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        kickBoxDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        kickBoxDialog.getWindow().setGravity(Gravity.CENTER);

        TextView kickBoxTitle = kickBoxDialog.findViewById(R.id.kickBoxTitle);
        TextView kickBoxGreeting = kickBoxDialog.findViewById(R.id.kickBGreeting);


        if("fil".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("Salamat sa paggamit ng mga serbisyo ng MTR. Masiyahan sa iyong paglalakbay!  ");
            kickBoxTitle.setText("Paano mo ire-rate ang iyong karanasan?");

        }
        if("ja-JP".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("MTRをご利用いただきありがとうございます。楽しい旅を！");
            kickBoxTitle.setText("あなたの体験をどのように評価しますか?");

        }
        if("ko-KR".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("MTR 서비스를 이용해 주셔서 감사합니다. 즐거운 여행 되세요!");
            kickBoxTitle.setText("귀하의 경험을 어떻게 평가하시겠습니까?");

        }
        if("ar".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("شكرًا لاستخدامك خدمات MTR. استمتع برحلتك!");
            kickBoxTitle.setText("كيف تقيم تجربتك؟");

        }
        if("de".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("Vielen Dank, dass Sie die Dienste von MTR nutzen. Gute Reise!");
            kickBoxTitle.setText("Wie würden Sie Ihre Erfahrung bewerten?");

        }
        if("fr".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("Merci d'avoir choisi les services MTR. Bon voyage !");
            kickBoxTitle.setText("Comment évalueriez-vous votre expérience");

        }
        if("hi".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("एमटीआर सेवाओं का उपयोग करने के लिए धन्यवाद। अपनी यात्रा का आनंद लें!");
            kickBoxTitle.setText("आपके द्वारा आपके अनुभव का मूल्यांकन किस तरह किया जाएगा");

        }
        if("id".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("Terima kasih telah menggunakan layanan MTR. Nikmati perjalanan Anda!");
            kickBoxTitle.setText("Bagaimana Anda menilai pengalaman Anda?");

        }
        if("ms".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("Terima kasih kerana menggunakan perkhidmatan MTR. Nikmati perjalanan anda!");
            kickBoxTitle.setText("Bagaimana anda menilai pengalaman anda");

        }
        if("nl".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("Bedankt voor uw gebruik van de MTR-diensten. Fijne reis!");
            kickBoxTitle.setText("Hoe zou u uw ervaring beoordelen?");

        }
        if("ru".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("Спасибо за использование услуг MTR. Приятного путешествия!");
            kickBoxTitle.setText("Как бы вы оценили свой опыт?");

        }
        if("th".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("ขอบคุณที่ใช้บริการ MTR ขอให้เดินทางอย่างมีความสุข!");
            kickBoxTitle.setText("คุณจะให้คะแนนประสบการณ์ของคุณอย่างไร");

        }
        if("vi".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("Cảm ơn bạn đã sử dụng dịch vụ MTR. Chúc bạn có chuyến đi vui vẻ!");
            kickBoxTitle.setText("Bạn đánh giá trải nghiệm của mình như thế nào?");


        }
        if("en".equals(MainActivity.abovelanguage)){
            kickBoxGreeting.setText("Thank you for using MTR services. Enjoy your journey!");
            kickBoxTitle.setText("How would you rate your experience?");


        }


        // 取得 ImageView 的參考
        ImageView kickImage1 = kickBoxDialog.findViewById(R.id.kickImage1);
        ImageView kickImage2 = kickBoxDialog.findViewById(R.id.kickImage2);
        ImageView kickImage3 = kickBoxDialog.findViewById(R.id.kickImage3);
        ImageView kickImage4 = kickBoxDialog.findViewById(R.id.kickImage4);
        ImageView kickImage5 = kickBoxDialog.findViewById(R.id.kickImage5);

        // 設定點擊事件監聽器
        kickImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationrank = "1";
                kickImage1.setImageResource(R.drawable.rate_1_pressed);
                Toast.makeText(activity, "rank : "+conversationrank, Toast.LENGTH_SHORT).show();
                m1.sendToSocket2("ENABLE button");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        kickBoxDialog.dismiss(); // 1.5秒後關閉對話框
                    }
                }, 500);
            }
        });

        kickImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationrank = "2";
                kickImage2.setImageResource(R.drawable.rate_2_pressed);
                Toast.makeText(activity, "rank : "+conversationrank, Toast.LENGTH_SHORT).show();
                m1.sendToSocket2("ENABLE button");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        kickBoxDialog.dismiss(); // 1.5秒後關閉對話框
                    }
                }, 500);
            }
        });

        kickImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationrank = "3";
                kickImage3.setImageResource(R.drawable.rate_3_pressed);
                Toast.makeText(activity, "rank : "+conversationrank, Toast.LENGTH_SHORT).show();
                m1.sendToSocket2("ENABLE button");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        kickBoxDialog.dismiss(); // 1.5秒後關閉對話框
                    }
                }, 500);
            }
        });
        kickImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationrank = "4";
                kickImage4.setImageResource(R.drawable.rate_4_pressed);
                Toast.makeText(activity, "rank : "+conversationrank, Toast.LENGTH_SHORT).show();
                m1.sendToSocket2("ENABLE button");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        kickBoxDialog.dismiss(); // 1.5秒後關閉對話框
                    }
                }, 500);
            }
        });
        kickImage5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationrank = "5";
                kickImage5.setImageResource(R.drawable.rate_5_pressed);
                Toast.makeText(activity, "rank : "+conversationrank, Toast.LENGTH_SHORT).show();
                m1.sendToSocket2("ENABLE button");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        kickBoxDialog.dismiss(); // 1.5秒後關閉對話框
                    }
                }, 500);
            }
        });

        kickBoxDialog.show();

        // 設定計時器，6 秒後關閉彈出視窗
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (kickBoxDialog != null && kickBoxDialog.isShowing()) {
                    kickBoxDialog.dismiss();
                    m1.sendToSocket2("ENABLE button");
                    Toast.makeText(activity, "6 秒時間到！", Toast.LENGTH_SHORT).show();
                }
            }
        }, 6000); // 6000 毫秒 = 6 秒
    }


}
