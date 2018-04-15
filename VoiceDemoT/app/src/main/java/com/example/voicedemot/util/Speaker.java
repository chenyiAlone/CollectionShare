package com.example.voicedemot.util;

/**
 * Created by chenyiAlone on 2018/4/10.
 */

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * 语音合成
 */
public class Speaker {

    // 上下文
    private Context mContext;
    // 语音合成对象
    public static SpeechSynthesizer mspeechSynthesizer;
    //Tosat对象
    private Toast toast;

    private SpeakCall mspeakcall;
    // Log标签
    private static final String TAG = "Speaker";
    //发音人
    public final static String[] COLOUD_VOICERS_ENTRIES = {"小燕", "小宇", "凯瑟琳", "亨利", "玛丽", "小研", "小琪", "小峰", "小梅", "小莉", "小蓉", "小芸", "小坤", "小强 ", "小莹",
            "小新", "楠楠", "老孙",};
    public final static String[] COLOUD_VOICERS_VALUE = {"xiaoyan", "xiaoyu", "catherine", "henry", "vimary", "vixy", "xiaoqi", "vixf", "xiaomei",
            "xiaolin", "xiaorong", "xiaoqian", "xiaokun", "xiaoqiang", "vixying", "xiaoxin", "nannan", "vils",};


    /**
     * 构造器
     * @param context
     */
    public Speaker(Context context) {
        // 上下文
        mContext = context;
        //Toast的初始化
        toast = Toast.makeText(mContext,"",Toast.LENGTH_SHORT);
        // 初始化合成对象
        mspeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    Log.d(TAG, "初始化失败,错误码：" + code);
                }
                Log.d(TAG, "初始化失败,q错误码：" + code);
            }
        });
    }


    /**
     * 开始合成
     * @param text
     */
    public void speak(String text) {
        mspeakcall = null;
        // 非空判断
        if (TextUtils.isEmpty(text)) {
            return;
        }
        int code = mspeechSynthesizer.startSpeaking(text, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                showTip("没有安装语音+ code = " + code);
            } else {
                showTip("语音合成失败,错误码: " + code);
            }
        }
    }
    //重载方法用于设置合成回调方法的合成方法
    public void speak(String text, SpeakCall speakCall) {
        setSpeackCall(speakCall);
        // 非空判断
        if (TextUtils.isEmpty(text)) {
            return;
        }
        int code = mspeechSynthesizer.startSpeaking(text, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                showTip("没有安装语音+ code = " + code);
            } else {
                showTip("语音合成失败,错误码: " + code);
            }
        }
    }


    //设置合成回调对象
    public void setSpeackCall(SpeakCall speackCall){
        this.mspeakcall = speackCall;
    }

    /*
     * 停止语音播报
     */
    public static void stopSpeaking() {
        // 对象非空并且正在说话
        if (null != mspeechSynthesizer && mspeechSynthesizer.isSpeaking()) {
            // 停止说话
            mspeechSynthesizer.stopSpeaking();
        }
    }

    /**
     * 提醒显示str的内容
     * @param str   要显示的内容
     */
    public void showTip(String str){
        toast.setText(str);
        toast.show();
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {}

        @Override
        public void onSpeakPaused() {}

        @Override
        public void onSpeakResumed() {}

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // TODO 缓冲的进度
            Log.i(TAG, "缓冲 : " + percent);
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // TODO 说话的进度
            Log.i(TAG, "合成 : " + percent);
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Log.i(TAG, "播放完成");

                if(null!=mspeakcall){
                    mspeakcall.speckfinish();
                }
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
                Log.i(TAG, error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    /**
     * 参数设置
     */
    private void setParam() {
        // 清空参数
        mspeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        // 引擎类型 网络
        mspeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置发音人
        mspeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, COLOUD_VOICERS_VALUE[0]);
        // 设置语速
        mspeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        // 设置音调
        mspeechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
        // 设置音量
        mspeechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");
        // 设置播放器音频流类型
        mspeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");

        // mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/KRobot/wavaudio.pcm");
        // 背景音乐  1有 0 无
        // mTts.setParameter("bgs", "1");
    }

    /**
     * 语音合成的回调接口
     */
    public interface SpeakCall{
        void speckfinish();
    }
}