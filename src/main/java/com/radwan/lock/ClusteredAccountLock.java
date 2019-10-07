package com.radwan.lock;

/**
 * This class is used to lock the Accounts in a clustered environment
 *
 * Redis can be used to implement the clustered lock, example :
 * https://dzone.com/articles/distributed-java-locks-with-redis
 */
public class ClusteredAccountLock implements AccountLock{

    @Override
    public boolean readLock() throws InterruptedException {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean writeLock() throws InterruptedException {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void readUnlock() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void writeUnlock() {
        throw new RuntimeException("not implemented yet");
    }
}
