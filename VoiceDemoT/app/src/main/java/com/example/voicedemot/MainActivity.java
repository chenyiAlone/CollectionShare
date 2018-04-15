package com.example.voicedemot;

/**
 * Created by chenyiAlone on 2018/4/10.
 */

import com.example.voicedemot.util.PassThrough;
import com.example.voicedemot.util.Speaker;
import com.example.voicedemot.util.Listener;
import com.example.voicedemot.util.Subject;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.Map;


/**
 * MainActivity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    public static Speaker mspeacker;
    public static Listener mlistener;
    public StringBuffer sb = new StringBuffer();
    private static int score;
    //出题数
    private static final int questionNum = 10;
    private static final String TAG = "MainActivity";
    private static ArrayList<String> questions = new ArrayList<String>();
    private static ArrayList<String> answers= new ArrayList<String>();
    public static int count = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5abf618b");
        initView();

    }
    public void initView(){
        findViewById(R.id.button).setOnClickListener(this);
        editText=(EditText)findViewById(R.id.meditText);
        mspeacker = new Speaker(this);
        mlistener = new Listener(this);
        mlistener.setListenCall(defaultlistenCall);
        mlistener.setEditText(editText);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.button:
                questions.clear();
                answers.clear();
                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Bundle data = msg.getData();
                        String val = data.getString("value");
                    }
                };

                Runnable runnable = new Runnable(){
                    @Override
                    public void run() {
                        // TODO: http request.
                        Subject usersDao = new Subject();
                        Map<String,String> map = usersDao.chouti();
                        for (String key : map.keySet()){
                            questions.add(key);
                            answers.add(map.get(key));
                        }
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("value","请求结果");
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                };

                Thread t = new Thread(runnable);
                t.start();

                count=0;
                score=0;
                sb.setLength(0);
                mspeacker.speak("开始闯关，下面你将听到十个题目，请认真作答", new Speaker.SpeakCall() {
                    @Override
                    public void speckfinish() {
                        mspeacker.speak(questions.get(count),defaultspeakCall);
                    }
                });
                break;
            default:
                break;
        }
    }

    //默认的合成回调对象
    Speaker.SpeakCall defaultspeakCall = new Speaker.SpeakCall(){
        @Override
        public void speckfinish() {
            mlistener.listen();
        }
    };

    //默认的监听回调对象
    Listener.ListenCall defaultlistenCall = new Listener.ListenCall() {
        @Override
        public void listenfinish(String result) {
            String str = PassThrough.getResult(result);
            switch (PassThrough.check(result, answers.get(count))) {
                case 1:
                    //答案正确的回调对象
                    count++;
                    score++;
                    if(count<questionNum){
                        mspeacker.speak("恭喜你回答正确，请听下一题", new Speaker.SpeakCall() {
                            @Override
                            public void speckfinish() {
                                mspeacker.speak(questions.get(count), defaultspeakCall);
                            }
                        });
                    }else {
                        mspeacker.speak("恭喜你回答正确,闯关结束,您最后的的得分是"+score+"分");
                        editText.setText(sb.toString());
                    }

                    break;
                case 0:
                    //
                    //没有识别到结果的回调对象
                    mspeacker.speak("抱歉未识别到您的答案，请重新作答", new Speaker.SpeakCall() {
                        @Override
                        public void speckfinish() {
                            mlistener.listen();
                        }
                    });

                    break;
                case -1:
                    //回答错误的回调对象
                    sb.append((count+1)+"."+    questions.get(count)+"\n\n您的回答是:\n"+str+"\n正确答案是:\n"+answers.get(count)+"\n\n");
                    count++;
                    if(count<questionNum) {
                        mspeacker.speak("很抱歉回答错误，请听下一题", new Speaker.SpeakCall() {
                            @Override
                            public void speckfinish() {
                                mspeacker.speak(questions.get(count), defaultspeakCall);
                            }
                        });
                    }else {
                        mspeacker.speak("很抱歉回答错误,闯关结束，谢谢参与,您最后的得分是"+score+"分");
                        editText.setText(sb.toString());
                    }

                    break;
                default:
                    break;
            }
        }

    };
}

