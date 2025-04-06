package com.earlybird.ticket.common.aop;

public class LoggingStatus {

    private final long startTimeMillis = System.currentTimeMillis();
    private int depthLevel = 0;

    public void increaseDepth() {
        depthLevel++;
    }

    public void decreaseDepth() {
        depthLevel--;
    }

    public String depthPrefix(String prefixString) {
        if (depthLevel == 1) {
            return "|" + prefixString;
        }
        String bar = "|" + " ".repeat(prefixString.length());
        return bar.repeat(depthLevel - 1) + "|" + prefixString;
    }

    public long calculateTakenTime() {
        return System.currentTimeMillis() - startTimeMillis;
    }
}
