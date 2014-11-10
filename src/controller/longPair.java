package controller;

/**
 * Class for pairs of type long.
 */
//@author A0112044B
public class longPair implements Comparable<longPair> {
	long first;
	long second;

	/**
	 * Constructs a pair of long numbers.
	 * 
	 * @param _first
	 *            First number of the pair.
	 * @param _second
	 *            Second number of the pair.
	 */

	//@author A0112044B

	public longPair(long _first, long _second) {
		first = _first;
		second = _second;
	}

	/**
	 * Gets the first long number of the pair.
	 * 
	 * @return First long number of the pair.
	 */

	//@author A0112044B

	public long getFirst() {
		return first;
	}

	/**
	 * Gets the second long number of the pair.
	 * 
	 * @return Second long number of the pair.
	 */
	//@author A0112044B

	public long getSecond() {
		return second;
	}

	/**
	 * Compares the two numbers in the pair.
	 * 
	 * @return Difference between the two numbers.
	 */

	//@author A0112044B

	public int compareTo(longPair num) {
		return (int) (this.getFirst() - num.getFirst());
	}

}
