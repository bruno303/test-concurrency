package com.example.demo.locker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StringLocker implements Locker<String> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StringLocker.class);

	private Map<String, ReentrantLock> lockedObjects;
	
	public StringLocker() {
		lockedObjects = new HashMap<>();
	}

	@Override
	public void lock(String object) {
		ReentrantLock reentrantLock = getReentrantLockByKey(object);
		reentrantLock.lock();
		LOGGER.info("LockedObjects size: {}", lockedObjects.size());
	}
	
	@Override
	public boolean tryLock(String object) {
		ReentrantLock reentrantLock = getReentrantLockByKey(object);
		boolean locked = false;
		
		try {
			locked = reentrantLock.tryLock(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
		LOGGER.info("Locked: {}", locked);
		
		return locked;
	}
	
	private synchronized ReentrantLock getReentrantLockByKey(String key) {
		return lockedObjects.computeIfAbsent(key, k -> new ReentrantLock());
	}

	@Override
	public void release(String object) {
		ReentrantLock reentrantLock = getReentrantLockByKey(object);
		while (reentrantLock.isLocked()) {
			reentrantLock.unlock();
		}
	}
	
}
