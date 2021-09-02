package com.example.acageneractivweb.util.idgenerator;

public final class GroupIdGenerator {

    private static int CURRENT = 0;

    public static int getNextId() {
        return ++CURRENT;
    }

    private GroupIdGenerator() {

    }

    public static int getCURRENT() {
        return CURRENT;
    }
    public static void setCURRENT(int CURR) {
         CURRENT = CURR;
    }
}
