package com.dub.spring.stronglyConnectedComponents;


public interface Stack<T> {
	
	/** A generic stack interface */
	
	public void push(T obj); 
	
	public T top();
	
	public T pop();
	
	public boolean isEmpty();
	
}
