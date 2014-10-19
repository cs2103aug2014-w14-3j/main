package controller;

public class Triple implements Comparable<Triple>{
	int first;
	int second;
	Task	third;
	
	public Triple(int _first, int _second,Task _third){
		first=_first;
		second=_second;
		third=_third;
	}
	
	public int getFirst(){
		return first;
	}
	
	public int getSecond(){
		return second;
	}
	
	public Task getThird(){
		return third;
	}
	
	
	public int compareTo(Triple triple){
		if (this.getFirst() !=triple.getFirst()){
			return this.getFirst()-triple.getFirst();
		}else {
			return this.getSecond()-triple.getSecond();
		}
	}
	


}
