package controller;

public class longPair implements Comparable<longPair>{
	long first;
	long second;
	
	public longPair( long _first, long _second){
		first=_first;
		second=_second;
	}
	
	public long getFirst(){
		return first;
	}
	
	public long getSecond(){
		return second;
	}
	
	public int compareTo(longPair num){
		return  (int)(this.getFirst()-num.getFirst());
	}
	
}
