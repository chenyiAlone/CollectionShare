我的学习记录
==
## 1.voiceDomeT 科大讯飞语音问答
    基于`科大讯飞`实现的模拟益智闯关问答的小Demo，使用了科大讯飞的SDK实现语音识别和语音合成,实现识别和合成自动衔接
    使用该Demo需要注意该Demo连接了数据库，`Subject`中需要重新设置数据库的连接
    
>这里的Demo只是实现了简单的问答，对答案的判定也只是单纯的ABCD判断，当然你如果在结果判断中加入更多的内容判断也未尝不可
>这里的主要的思想就是重新指定识别和合成对象的回调方法内容，代码如下
```java
 /**
 * 语音识别的回调接口
 */
public interface ListenCall{
  /**
   * @param result    语音识别返回的结果
   */
  void listenfinish(String result);
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
                //在这里调用该类中持有的自定义接口引用的方法
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
```
##### 再通过设置持有的接口的引用来修改回调监听方法时的后续动作，用其与语音合成动作连接实现循环
```java
  
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
```
#####  我解决问题的成写在了微博中，欢迎参考 [我的CSDN](http://blog.csdn.net/chenyiAlone "悬停显示")
