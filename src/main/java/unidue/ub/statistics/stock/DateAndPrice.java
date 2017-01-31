package unidue.ub.statistics.stock;

/**
 * Plain old java object holding the combination of date and price.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class DateAndPrice {

	private String date;

	private double price;

	/**
	 * setting a date-and-price pair
	 * 
	 * @param date
	 *            the date of purchase
	 * @param price
	 *            the price of an item
	 * 
	 */
	public DateAndPrice(String date, double price) {
		this.date = date;
		this.price = price;
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
	 * returns the date of purchase
	 * 
	 * @return date the date of purchase
	 */
	public String getDate() {
		return date;
	}

	/**
	 * sets the price of an item
	 * 
	 * @param price
	 *            the price of an item
	 */
	public void setPrice(double price) {
		this.price = price;
	}
}