package controller;

/**
 * Class for implementing a Triple of integers.
 */
//@author A0112044B
public class Triple implements Comparable<Triple> {
	int first;
	int second;
	Task third;

	/**
	 * Constructs a triple of integers.
	 * 
	 * @param _first
	 *            First integer;.
	 * @param _second
	 *            Second integer.
	 * @param _third
	 *            Third integer.
	 */
	public Triple(int _first, int _second, Task _third) {
		first = _first;
		second = _second;
		third = _third;
	}

	/**
	 * Gets the first integer.
	 * 
	 * @return First integer.
	 */
	public int getFirst() {
		return first;
	}

	/**
	 * Gets the second integer.
	 * 
	 * @return Second integer.
	 */
	public int getSecond() {
		return second;
	}

	/**
	 * Gets the third integer.
	 * 
	 * @return Third integer.
	 */
	public Task getThird() {
		return third;
	}

	/**
	 * Compares all three integers.
	 * 
	 * @param triple
	 *            Triple object containing three integers.
	 * @return Difference between the first two integers.
	 */
	public int compareTo(Triple triple) {
		if (this.getFirst() != triple.getFirst()) {
			return this.getFirst() - triple.getFirst();
		} else {
			return this.getSecond() - triple.getSecond();
		}
	}

}
