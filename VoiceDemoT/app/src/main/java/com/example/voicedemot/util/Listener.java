package com.example.voicedemot.util;

/**
 * Created by chenyiAlone on 2018/4/10.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.voicedemot.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class Listener {
    // 上下文
    private Context mContext;
    //语音听写对象
    public  SpeechRecognizer mspeechRecognizer;
    //结果储存Map
    private HashMap<String,String> resultMap = new HashMap<String,String>();
    //小提示Toast
    private Toast toast;
    //显示到输入框的方法
    private EditText meditText;
    //设置监听回调的对象
    private ListenCall mlistencall;

    private SharedPreferences sharedPreferences;



    //缓存数据的名称
    private static final String TAG = "Listener";

    public static final String PRIVATE_SETTING="com.iflytek.setting";
    //语音识别引擎的类型
    private final String engineType = SpeechConstant.TYPE_CLOUD;

    public interface Config {
        String IFLYTEK_APPID = "5abf618b";

        String VAD_BOS_VALUE = "4000";
        String VAD_EOS_VALUE = "1000";
        String ASR_PTT_VALUE = "0";
    }

    private InitListener initLister = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };
    public void stopListen(){
        mspeechRecognizer.stopListening();
    }

    public boolean isListening(){
            return mspeechRecognizer.isListening();
    }

    /**
     *
     * @param listenCall    语音识别的回调对象
     */
    public void setListenCall(ListenCall listenCall){
        this.mlistencall = listenCall;
    }


    /**
     * 构造器
     * @param context   Context对象
     */
    public Listener(Context context) {
        Log.d("tag54", "初始化失败,错ss 误码：" );
        // 上下文
        mContext = context;
        // 初始化合成对象
        mspeechRecognizer = SpeechRecognizer.createRecognizer(mContext,initLister);
        initat();
        toast = Toast.makeText(mContext,"",Toast.LENGTH_LONG);
    }


    /**
     * 成员初始化
     */
    public void initat(){
        mspeechRecognizer = SpeechRecognizer.createRecognizer(mContext,initLister);
        sharedPreferences = mContext.getSharedPreferences(PRIVATE_SETTING, Activity.MODE_PRIVATE);
    }


    /**
     * 设置EditText
     */
    public void setEditText(EditText meditText){
        this.meditText = meditText;
    }


    /**
     * 提醒显示str的内容
     * @param str   要现实的内容
     */
    public void showTip(String str){
        toast.setText(str);
        toast.show();
    }

    //重载方法，用于直接设置监听回调对象的监听方法
    public void listen(ListenCall listenCall){
        setListenCall(listenCall);
        resultMap.clear();
        setParamer();

        int res = mspeechRecognizer.startListening(recognizerListener);
        if (res != ErrorCode.SUCCESS) {
            Log.d(TAG, "听写失败,错误码：" + res);
        } else {
            showTip(mContext.getString(R.string.text_begin));

        }

    }
    //已设置回调的监听
    public void listen(){

        resultMap.clear();
        setParamer();

        int res = mspeechRecognizer.startListening(recognizerListener);
        if (res != ErrorCode.SUCCESS) {

            Log.d(TAG, "听写失败,错误码：" + res);
        } else {
            showTip(mContext.getString(R.string.text_begin));

        }

    }



    private RecognizerListener recognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("请开始作答");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            mlistencall.listenfinish("");
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("作答结束");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String answer = printResult(results,isLast);
            if (isLast) {
                if(null!=mlistencall){
                    mlistencall.listenfinish(answer);
                }
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("正在聆听，音量:" + volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };


    /**
     * SpeechRecognizer设置参数
     */
    public void setParamer(){

        // 清空参数
        mspeechRecognizer.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mspeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, engineType);

        // 设置返回结果格式
        mspeechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mspeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语言区域
        mspeechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mspeechRecognizer.setParameter(SpeechConstant.VAD_BOS, Listener.Config.VAD_BOS_VALUE);

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mspeechRecognizer.setParameter(SpeechConstant.VAD_EOS, Listener.Config.VAD_EOS_VALUE);

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mspeechRecognizer.setParameter(SpeechConstant.ASR_PTT, Listener.Config.ASR_PTT_VALUE);
    }


    /**
     * @param result    RecognizerResult返回结果
//     * @param meditText
     */
    public String printResult(RecognizerResult result,boolean isLast){
        String value = JsonParser.parseIatResult(result.getResultString());
        String sn = null;
        try {
            JSONObject jsobj = new JSONObject(result.getResultString());
            sn = jsobj.getString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultMap.put(sn,value);
        StringBuffer sb = new StringBuffer();
        for(String key:resultMap.keySet()){
            sb.append(resultMap.get(key));
        }

        Log.d(TAG,"-->"+sb.toString());
        sn = sb.toString().replace("。","".trim());

        if(isLast){
            meditText.setText(sn);
            //添加判定
            showTip(sn);
        }
        return sn;
        }

    /**
     * 语音识别的回调接口
     */
    public interface ListenCall{
        /**
         * @param result    语音识别返回的结果
         */
        void listenfinish(String result);

    }
}