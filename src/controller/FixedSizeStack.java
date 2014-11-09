package controller;
import java.util.Deque;
import java.util.LinkedList;



//FixedSizeStack, a stack of a fixed size, auto-update
/**
 * Stack class to keep track of the states in the software.
 */
//@author A0112044B
public class FixedSizeStack<E> {
	int maxSize;
	Deque<E> stack;
	
	/**
	 * Constructor for FixedSizeStack object.
	 * 
	 * @param _maxSize	Size of stack.
	 */
	//@author
	public FixedSizeStack(int _maxSize){
		maxSize=_maxSize;
		stack=new LinkedList<E>();
		
	}
	
	/**
	 * Gets the size of stack.
	 * 
	 * @return	Size of stack.
	 */
	//@author
	public int size(){
		return stack.size();
	}
	
	/**
	 * Gets the maximum size of ??
	 * 
	 * @return	Maximum size.
	 */
	//@author
	public int getMaxSize(){
		return maxSize;
	}
	
	/**
	 * Checks if the stack is empty.
	 * 
	 * @return	true if stack is empty.
	 */
	//@author
	public boolean empty(){
		if (stack.size()==0){
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Peeks the top item of the stack.
	 * 
	 * @return	First item of the stack.
	 */
	//@author
	public E peek(){
		return stack.peekFirst();
	}
	
	/**
	 * Gets the top item of the stack.
	 * 
	 * @return	First item of the stack.
	 */
	public E pop(){
		return stack.removeFirst();
	}
	
	/**
	 * Inserts item into the stack.
	 * 
	 * @param item	Item to be inserted into the stack.
	 */
	//@author
	public void push(E item){
		if (stack.size()< maxSize){
			stack.addFirst(item);
		} else if (stack.size()==maxSize){
			stack.removeLast();
			stack.addFirst(item);
		} 
		
	}
}
