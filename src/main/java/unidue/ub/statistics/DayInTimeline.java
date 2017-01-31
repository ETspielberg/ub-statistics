package unidue.ub.statistics;

/**
 * Plain old java object holding the individual counters for different user
 * groups and for different stock groups.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class DayInTimeline {

	private String day;

	private int studentLoans;

	private int internLoans;

	private int externLoans;

	private int happLoans;

	private int elseLoans;

	private int stock;

	private int stockLBS;

	private int stockLendableNonLBS;

	private int stockLendable;

	private int stockNonLendable;

	private int stockDeleted;

	private int studentRequests;

	private int internRequests;

	private int externRequests;

	private int happRequests;

	private int elseRequests;

	/**
	 * Builds a new instance of a <code>DayInTimline</code>-object, setting the
	 * individual counters to 0.
	 * 
	 */

	public DayInTimeline() {
		day = "";
		studentLoans = 0;
		internLoans = 0;
		externLoans = 0;
		happLoans = 0;
		elseLoans = 0;
		stock = 0;
		stockLBS = 0;
		stockLendableNonLBS = 0;
		stockLendable = 0;
		stockNonLendable = 0;
		stockDeleted = 0;
		studentRequests = 0;
		internRequests = 0;
		externRequests = 0;
		happRequests = 0;
		elseRequests = 0;
	}

	// studentLoas
	/**
	 * sets the value of items which are loaned by students at this day.
	 * 
	 * @param studentLoans
	 *            new value of items loaned by students
	 * 
	 * @return dit the updated <code>DayInTimeline</code>
	 */
	public DayInTimeline setStudentLoans(int studentLoans) {
		this.studentLoans = studentLoans;
		return this;
	}

	/**
	 * retrieves the value of items which are loaned by students at this day.
	 * 
	 * @return studentLoans the value of items which are loaned by students
	 * 
	 * @return studentLoans the value of items which are loaned by students
	 */
	public int getStudentLoans() {
		return studentLoans;
	}

	// externLoans
	/**
	 * sets the value of items which are loaned by external users at this day.
	 * 
	 * @param externLoans
	 *            new value of items loaned by external users
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setExternLoans(int externLoans) {
		this.externLoans = externLoans;
		return this;
	}

	/**
	 * retrieves the value of items which are loaned by external users at this
	 * day.
	 * @return externLoans the value of items which are loaned by external users
	 * 
	 */
	public int getExternLoans() {
		return externLoans;
	}

	// internLoans
	/**
	 * sets the value of items which are loaned by non-student members of the
	 * university at this day.
	 * 
	 * @param internLoans
	 *            new value of items loaned by non-student members of the
	 *            university
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setInternLoans(int internLoans) {
		this.internLoans = internLoans;
		return this;
	}

	/**
	 * retrieves the value of items which are loaned by non-student members of
	 * the university at this day.
	 * 
	 * @return internLoans the value of items which are loaned by non-student members of
	 * the university
	 */
	public int getInternLoans() {
		return internLoans;
	}

	// happLoans
	/**
	 * sets the value of items which are located in permanent loan in scientific
	 * departments.
	 * 
	 * @param happLoans
	 *            new value of items located in permanent loan in scientific
	 *            departments
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setHappLoans(int happLoans) {
		this.happLoans = happLoans;
		return this;
	}

	/**
	 * retrieves the value of items which are located in permanent loan in
	 * scientific departments at this day.
	 * 
	 * @return happLoans the value of items which are located in permanent loan in
	 * scientific departments
	 */
	public int getHappLoans() {
		return happLoans;
	}

	// elseLoans
	/**
	 * sets the value of items which are loaned by other users at this day.
	 * 
	 * @param elseLoans
	 *            new value of items loaned by other users
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setElseLoans(int elseLoans) {
		this.elseLoans = elseLoans;
		return this;
	}

	/**
	 * retrieves the value of items which are loaned by other users at this day.
	 * 
	 * @return elseLoans the value of items which are loaned by other users
	 */
	public int getElseLoans() {
		return elseLoans;
	}

	// stock
	/**
	 * sets the value of all items at this day.
	 * 
	 * @param stock
	 *            new value of all items
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setStock(int stock) {
		this.stock = stock;
		return this;
	}

	/**
	 * retrieves the value of items at this day.
	 * 
	 * @return stock the value of items
	 */
	public int getStock() {
		return stock;
	}

	// stockLendable
	/**
	 * sets the value of all circulation items at this day.
	 * 
	 * @param stockLendable
	 *            new value of all circulation items
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setStockLendable(int stockLendable) {
		this.stockLendable = stockLendable;
		return this;
	}

	/**
	 * retrieves the value of circulation items.
	 * 
	 * @return stockLendable the value of circulation items
	 */
	public int getStockLendable() {
		return stockLendable;
	}

	// stockLBS
	/**
	 * sets the value of items in the textbook collection at this day.
	 * 
	 * @param stockLBS
	 *            new value of items in the textbook collection
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setStockLBS(int stockLBS) {
		this.stockLBS = stockLBS;
		return this;
	}

	/**
	 * retrieves the value of items in the textbook collection.
	 * 
	 * @return stockLBS the value of items in the textbook collection
	 */
	public int getStockLBS() {
		return stockLBS;
	}

	// stockDeleted
	/**
	 * sets the value of items being deleted up to that day.
	 * 
	 * @param stockDeleted
	 *            new value of items being deleted
	 * @return dit the updated <code>DayInTimeline</code>  
	 */
	public DayInTimeline setStockDeleted(int stockDeleted) {
		this.stockDeleted = stockDeleted;
		return this;
	}

	/**
	 * retrieves the value of deleted items.
	 * 
	 * @return stockDeleted the value of deleted items
	 */
	public int getStockDeleted() {
		return stockDeleted;
	}

	// stockLendableNonLBS
	/**
	 * sets the value of circulation items being not part of the textbook
	 * collection at this day.
	 * 
	 * @param stockLendableNonLBS
	 *            new value of circulating items being not part of the textbook
	 *            collection
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setStockLendableNonLBS(int stockLendableNonLBS) {
		this.stockLendableNonLBS = stockLendableNonLBS;
		return this;
	}

	/**
	 * retrieves the value of circulation items being not part of the textbook
	 * collection at this day.
	 * 
	 * @return stockLendableNonLBS the value of circulation items being not part of the textbook
	 * collection 
	 */
	public int getStockLendableNonLBS() {
		return stockLendableNonLBS;
	}

	// stockNonLendable
	/**
	 * sets the value of non-circulation items at this day.
	 * 
	 * @param stockNonLendable
	 *            new value of circulating items being not part of the textbook
	 *            collection
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setStockNonLendable(int stockNonLendable) {
		this.stockNonLendable = stockNonLendable;
		return this;
	}

	/**
	 * retrieves the value of non-circulation items.
	 * 
	 * @return stockNonLendable the value of non-circulation items
	 */
	public int getStockNonLendable() {
		return stockNonLendable;
	}

	/**
	 * retrieves the value of items requested by students at this day.
	 * 
	 * @return studentRequests the value of items requested by students
	 */
	public int getStudentRequests() {
		return studentRequests;
	}

	/**
	 * sets the value of items which are requested by students at this day.
	 * 
	 * @param studentRequests
	 *            new value of items requested by students
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setStudentRequests(int studentRequests) {
		this.studentRequests = studentRequests;
		return this;
	}

	/**
	 * retrieves the value of items requested by non-student members of the
	 * university at this day.
	 * 
	 * @return internRequests the value of items requested by non-student members of the
	 * university
	 */
	public int getInternRequests() {
		return internRequests;
	}

	/**
	 * sets the value of items which are requested by non-student members of the
	 * university at this day.
	 * 
	 * @param internRequests
	 *            new value of items requested by non-student members of the
	 *            university
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setInternRequests(int internRequests) {
		this.internRequests = internRequests;
		return this;
	}

	/**
	 * retrieves the value of items requested by external users at this day.
	 * 
	 * @return externRequests the value of items requested by external users
	 */
	public int getExternRequests() {
		return externRequests;
	}

	/**
	 * sets the value of items which are requested by external users at this
	 * day.
	 * 
	 * @param externRequests
	 *            new value of items loaned by external users
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setExternRequests(int externRequests) {
		this.externRequests = externRequests;
		return this;
	}

	/**
	 * retrieves the value of items requested to be located in permanent loan in
	 * scientific departments at this day.
	 * 
	 * @return happRequests the value of items requested to be located in permanent loan in
	 * scientific departments
	 */
	public int getHappRequests() {
		return happRequests;
	}

	/**
	 * sets the value of items which are requested to be located in permanent
	 * loan in scientific departments at this day.
	 * 
	 * @param happRequests
	 *            new value of items requested to be located in permanent loan
	 *            in scientific departments
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setHappRequests(int happRequests) {
		this.happRequests = happRequests;
		return this;
	}

	/**
	 * retrieves the value of items requested by other users at this day.
	 * 
	 * @return elseRequests the value of items requested by other users
	 */
	public int getElseRequests() {
		return elseRequests;
	}

	/**
	 * sets the value of items which are requested by other users at this day.
	 * 
	 * @param elseRequests
	 *            new value of items loaned by other users
	 * @return dit the updated <code>DayInTimeline</code> 
	 */
	public DayInTimeline setElseRequests(int elseRequests) {
		this.elseRequests = elseRequests;
		return this;
	}

	/**
	 * retrieves the string with the date at this day.
	 * 
	 * @return day the date of this day
	 */
	public String getDay() {
		return day;
	}

	/**
	 * sets the day these parameters are set for.
	 * 
	 * @param day
	 *            String representing the day.
	 * @return dit the updated <code>DayInTimeline</code> 
	  */
	public DayInTimeline setDay(String day) {
		this.day = day;
		return this;
	}

	/**
	 * adds another <code>DayInTimeline</code> counter. all the individual
	 * counters are summed up, the day is kept from the original one.
	 * 
	 * @param dit
	 *            new value of items loaned by students
	 * @return dit added DayInTimeline
	 */
	public DayInTimeline plus(DayInTimeline dit) {
		studentLoans += dit.getStudentLoans();
		internLoans += dit.getInternLoans();
		externLoans += dit.getExternLoans();
		happLoans += dit.getHappLoans();
		elseLoans += dit.getElseLoans();
		stock += dit.getStock();
		stockLBS += dit.getStockLBS();
		stockLendableNonLBS += dit.getStockLendableNonLBS();
		stockLendable += dit.getStockLendable();
		stockNonLendable += dit.getStockNonLendable();
		stockDeleted += dit.getStockDeleted();
		studentRequests += dit.getStudentRequests();
		internRequests += dit.getInternRequests();
		externRequests += dit.getElseRequests();
		happRequests += dit.getHappRequests();
		elseRequests += dit.getElseRequests();
		return this;
	}

	/**
	 * multiplies all counters with a given number of days.
	 * 
	 * @param days
	 *            days the individual counters are multiplied with
	 * @return dit multiplied <code>DayInTimeline</code>
	 */
	public DayInTimeline times(int days) {
		DayInTimeline product = new DayInTimeline();
		product.setStudentLoans(studentLoans * days).setInternLoans(internLoans * days)
				.setExternLoans(externLoans * days).setHappLoans(happLoans * days).setElseLoans(elseLoans * days)
				.setStock(stock * days).setStockLBS(stockLBS * days).setStockLendableNonLBS(stockLendableNonLBS * days)
				.setStockLendable(stockLendable * days).setStockNonLendable(stockNonLendable * days)
				.setStudentRequests(studentRequests * days).setInternRequests(internRequests * days)
				.setExternRequests(externRequests * days).setHappRequests(happRequests * days)
				.setElseRequests(elseRequests * days);
		return product;
	}

	/**
	 * retrieves the value of all items requested by all user groups at this
	 * day.
	 * 
	 * @return allRequests sum over all types of request
	 */
	public int getAllRequests() {
		int allRequests = studentRequests + internRequests + externRequests + happRequests + elseRequests;
		return allRequests;
	}

	/**
	 * retrieves the value of all items loaned by all user groups at this day.
	 * 
	 * @return allLoans sum over all types of loan
	 */
	public int getAllLoans() {
		int allLoans = studentLoans + internLoans + externLoans + happLoans + elseLoans;
		return allLoans;
	}

}
