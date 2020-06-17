package com.example.demo.locker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

@Component
public class StringLocker implements Locker<String> {

	private Map<String, ReentrantLock> lockedObjects;
	
	public StringLocker() {
		lockedObjects = new HashMap<>();
	}

	@Override
	public void lock(String object) {
		ReentrantLock reentrantLock = getReentrantLockByKey(object);
		reentrantLock.lock();
	}
	
	private synchronized ReentrantLock getReentrantLockByKey(String key) {
		ReentrantLock reentrantLock;
		
		if (!lockedObjects.containsKey(key)) {
			reentrantLock = new ReentrantLock();
			lockedObjects.put(key, reentrantLock);
		} else {
			reentrantLock = lockedObjects.get(key);
		}
		
		return reentrantLock;
	}

	@Override
	public void release(String object) {
		ReentrantLock reentrantLock = getReentrantLockByKey(object);
		if (reentrantLock.isLocked()) {
			reentrantLock.unlock();
		}
	}
	
}
