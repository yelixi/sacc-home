package org.sacc.SaccHome.util;

import java.util.Random;

/**
 * @author: 風楪fy
 * @create: 2021-07-22 04:26
 **/
public class SaltUtil {
    //生成随机盐的静态方法
    public static String getSalt(int n){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char aChar = chars[new Random().nextInt(chars.length)];
            sb.append(aChar);
        }
        return sb.toString();
    }
}
