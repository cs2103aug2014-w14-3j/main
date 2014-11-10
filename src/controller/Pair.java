package controller;

/**
 * Class for pairs of integers.
 */
//@author A0112044B
public class Pair {
	int first;
	int second;

	/**
	 * Constructs a pair of integers.
	 * 
	 * @param _first
	 *            First integer of the pair.
	 * @param _second
	 *            Second integer of the pair.
	 */
	public Pair(int _first, int _second) {
		first = _first;
		second = _second;
	}

	/**
	 * Gets the first integer of the pair.
	 * 
	 * @return First integer of the pair.
	 */
	public int getFirst() {
		return first;
	}

	/**
	 * Gets the second integer of the pair.
	 * 
	 * @return Second integer of the pair.
	 */
	public int getSecond() {
		return second;
	}

}
