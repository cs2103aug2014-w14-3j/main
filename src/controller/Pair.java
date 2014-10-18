package controller;

public class Pair implements Comparable<Pair>{
	Integer first;
	Task second;
	
	public Pair( Integer _first, Task _second){
		first=_first;
		second=_second;
	}
	
	public Integer getFirst(){
		return first;
	}
	
	public Task getSecond(){
		return second;
	}
	
	
	public int compareTo(Pair pair){
		return (int) this.getFirst()- (int)pair.getFirst();
	}
	

	
}
