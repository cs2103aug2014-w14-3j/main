package controller;
import java.util.Deque;
import java.util.LinkedList;



//FixedSizeStack, a stack of a fixed size, auto-update
public class FixedSizeStack<E> {
	int maxSize;
	Deque<E> stack;
	
	public FixedSizeStack(int _maxSize){
		maxSize=_maxSize;
		stack=new LinkedList<E>();
		
	}
	
	public int size(){
		return stack.size();
	}
	
	public int getMaxSize(){
		return maxSize;
	}
	
	public boolean empty(){
		if (stack.size()==0){
			return true;
		}else {
			return false;
		}
	}
	
	public E peek(){
		return stack.peekFirst();
	}
	
	public E pop(){
		return stack.removeFirst();
	}
	
	public void push(E item){
		if (stack.size()< maxSize){
			stack.addFirst(item);
		} else if (stack.size()==maxSize){
			stack.removeLast();
			stack.addFirst(item);
		} 
		
	}
}
