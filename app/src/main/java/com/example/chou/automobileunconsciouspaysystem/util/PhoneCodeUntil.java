package com.example.chou.automobileunconsciouspaysystem.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class PhoneCodeUntil {

    private static String getRandomChar(){//随机生成字符或者数字
        String value = "";
        Random random = new Random();
        int num = random.nextInt(2);//获取一个0-2之间的整数
        String randomResult = num % 2 ==0 ? "char":"num";//如果能被整除为2，则为字母。否则为数字
        if ("char".equals(randomResult)) {//如果是字符
            int charNum = random.nextInt(2)%2==0?65:97;
            int ascii = random.nextInt(26);
            value += (char)(ascii + charNum);
        }else if ("num".equals(randomResult)) {
            //是数字
            value += String.valueOf(random.nextInt(10));
        }
        return value;
    }


    private static Set<String> getStrOrNum(int length) {// 随机生成字符串
        Set<String> set = new HashSet<>();//创建一个String类型的Set
        for (int i = 0; i < length; i++) {//以所需长度为界限开始循环
            String value = getRandomChar();//调用随机字符生成函数
            set.add(value);//将字符添加到set中
        }
        //若生成的字符串没达到指定长度 继续生成
        if (set.size() < length) {
            String value = getRandomChar();
            set.add(value);
        }
        return set;//返回set
    }
    private static  String printSet(Set set){ //存放在set中的字符组拼接成字符串
        Iterator iterator = set.iterator();
        String value = "";
        while (iterator.hasNext()) {
            //String ele = (String) iterator.next();
            value += (String)iterator.next();
        }
        return value;
    }

    public static String getRandonString(int length){//返回生成的随机字符串
        String value= "";
        if (length > 0) {//如果所需要这么长度大于0，则开始生成二维码
            if (value.length() < length) {//如果当前所生成的验证码未达到要求长度
                Set<String> Code = getStrOrNum(length);//创建一个String类型的Set并开始生成
                value = printSet(Code);//将生成的验证码赋予value
            }
            return value;//返回生成的验证码
        }else{
            return value;
        }
    }


}

