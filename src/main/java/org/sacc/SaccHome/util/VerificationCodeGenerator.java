package org.sacc.SaccHome.util;

import java.util.Random;

/**
 * @author: 風楪fy
 * @create: 2021-07-18 03:07
 **/
public class VerificationCodeGenerator {
    public static String getVerificationCode(int n){
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char aChar = chars[new Random().nextInt(chars.length)];
            sb.append(aChar);
        }
        return sb.toString();
    }
}
