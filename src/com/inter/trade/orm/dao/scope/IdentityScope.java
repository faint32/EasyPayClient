package com.inter.trade.orm.dao.scope;

public interface IdentityScope<K, T>
{

	T get(K key);

	void put(K key, T entity);

	T getNoLock(K key);

	void putNoLock(K key, T entity);

	boolean detach(K key, T entity);

	void remove(K key);

	void remove(Iterable<K> key);

	void clear();

	void lock();

	void unlock();

	void reserveRoom(int count);

}