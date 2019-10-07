package com.radwan.lock;

public interface AccountLock {

    boolean readLock() throws InterruptedException;
    boolean writeLock() throws InterruptedException;
    void readUnlock();
    void writeUnlock();
}
