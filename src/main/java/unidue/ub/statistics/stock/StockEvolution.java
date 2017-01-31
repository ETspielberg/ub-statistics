package unidue.ub.statistics.stock;

/**
 * Plain old java object holding the time, counter, collection, price and
 * notation.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class StockEvolution implements Comparable<StockEvolution> {

	private long time;

	private int counter;

	private String collection;

	private double price;

	private String systemCode;

	/**
	 * setting a stock evolution set
	 * 
	 * @param time
	 *            the time of change
	 * @param counter
	 *            the counter of an item
	 * @param collection
	 *            the collection of an item
	 * @param price
	 *            the price of an item
	 * @param systemCode
	 *            the notation of the item
	 * 
	 */
	public StockEvolution(long time, int counter, String collection, double price, String systemCode) {
		this.time = time;
		this.counter = counter;
		this.collection = collection;
		this.price = price;
		this.systemCode = systemCode;
	}

	/**
	 * returns the time of change
	 * 
	 * @return time the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * returns the counter of an item
	 * 
	 * @return counter the counter
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * returns the collection of an item
	 * 
	 * @return time the time
	 */
	public String getCollection() {
		return collection;
	}

	/**
	 * returns the price
	 * 
	 * @return price the price of an item
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * returns the time of change
	 * 
	 * @return time the time
	 */
	public String getSystemCode() {
		return systemCode;
	}

	/**
	 * sets the time of change
	 * 
	 * @param newTime
	 *            the time
	 */
	public void setTime(long newTime) {
		this.time = newTime;
	}

	/**
	 * sets the counter of an item
	 * 
	 * @param newCounter
	 *            the counter
	 */
	public void setCounter(int newCounter) {
		this.counter = newCounter;
	}

	/**
	 * sets the collection of an item
	 * 
	 * @param newCollection
	 *            the collection
	 */
	public void setCollection(String newCollection) {
		this.collection = newCollection;
	}

	/**
	 * sets the price of an item
	 * 
	 * @param newPrice
	 *            the price
	 */
	public void setPrice(double newPrice) {
		this.price = newPrice;
	}

	/**
	 * sets the notation of an item
	 * 
	 * @param newSystemCode
	 *            the notation
	 */
	public void setSystemCode(String newSystemCode) {
		this.systemCode = newSystemCode;
	}

	/**
	 * compares the time fields of two <code>StockEvolution</code>-objects
	 * 
	 * @param other
	 *            a second <code>StockEvolution</code>-objects
	 * @return 1, if second is before, -1 if it is after else 0
	 */
	public int compareTo(StockEvolution other) {
		if (this.time > other.time)
			return 1;
		else if (this.time < other.time)
			return -1;
		else
			return 0;
	}

}
