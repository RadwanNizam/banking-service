package com.radwan.lock;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JVMAccountLock implements AccountLock{

    private Long LOCK_TIME_OUT = 15l;

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();

    @Override
    public boolean readLock() throws InterruptedException {
        return readLock.tryLock(LOCK_TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public boolean writeLock() throws InterruptedException {
        return writeLock.tryLock(LOCK_TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public void readUnlock() {
        readLock.unlock();
    }

    @Override
    public void writeUnlock() {
        writeLock.unlock();
    }

}
