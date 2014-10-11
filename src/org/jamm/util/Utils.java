package org.jamm.util;

import java.util.Random;

public class Utils {
    private final static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String GetRandomStr(int length) {
        Random rnd = new Random(System.currentTimeMillis());
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int val = rnd.nextInt(chars.length());
            buf.append(chars.charAt(val));
        }
        return buf.toString();
    }
}
