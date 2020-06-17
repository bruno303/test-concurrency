package com.example.demo.locker;

public interface Locker<T extends Comparable<T>> {

	void lock(T object);
	void release(T object);
	boolean tryLock(T object);

}
