package com.example.voicedemot.util;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenyiAlone on 2018/4/10.
 */

public class PassThrough {

    public static int check(String result,String answer){
        Set<String> set = new HashSet<String>();
        Pattern pattern = Pattern.compile("[abcdABCD]");
        Matcher m =pattern.matcher(result);
        while(m.find()) {
            set.add(m.group().toUpperCase());
        }

        if(set.isEmpty()) {
            return 0;
        }else if(set.size()==answer.trim().length() && set.contains(answer)){
            return 1;
        }else
            return -1;
    }
    public static String getResult(String result){
        Set<String> set = new HashSet<String>();
        Pattern pattern = Pattern.compile("[abcdABCD]");
        Matcher m =pattern.matcher(result);
        while(m.find()) {
            set.add(m.group().toUpperCase());
        }
        return set.toString();
    }

}
