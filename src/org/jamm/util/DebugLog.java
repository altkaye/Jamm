package org.jamm.util;

public class DebugLog {
    public static boolean SHOWS = true;

    public static void d(String message) {
        if (SHOWS) {
            System.out.println(message);
        }
    }
}
