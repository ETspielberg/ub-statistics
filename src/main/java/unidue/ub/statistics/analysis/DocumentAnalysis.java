package unidue.ub.statistics.analysis;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Plain old java object holding an analysis of one year and one document. The
 * fields can be persisted.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@Entity
public class DocumentAnalysis implements Comparable<DocumentAnalysis> {

	@Id
	@GeneratedValue
	private Long id;

	// char fields for description and comments
	private String description;

	private String shelfmark;

	private String notation;

	private String comment;

	// fields registered by event analyzer
	private int numberRequests;

	private int numberLoans;

	private int lastStock;

	// days in stock for different groups
	private int daysStockLBS;

	private int daysStockLendableNonLBS;

	private int daysStockNonLendable;

	private int daysStockDeleted;

	// counter for different parts of the stock
	private int itemsLBS;

	private int itemsLendableNonLBS;

	private int itemsNonLendable;

	private int itemsDeleted;

	private int actualItemsLBS;

	// days requested for different groups
	private int daysRequestedStudents;

	private int daysRequestedExtern;

	private int daysRequestedIntern;

	private int daysRequestedHapp;

	private int daysRequestedElse;

	// days loaned in different groups
	private int daysLoanedStudents;

	private int daysLoanedExtern;

	private int daysLoanedIntern;

	private int daysLoanedHapp;

	private int daysLoanedElse;

	// statistical data for time period analyzed
	private int days;

	private int year;

	private double timeStamp;

	// Transient fields, easier to determine, used to calculate values for the
	// persistent fields vie calculate()
	@Transient
	private int daysRequested;

	@Transient
	private int itemsTotal;

	@Transient
	private int daysStockAll;

	@Transient
	private int daysLoanedAll;

	@Transient
	private double meanRelativeLoan;

	@Transient
	private double meanStock;

	@Transient
	private int daysStockLendableAll;

	@Transient
	private int proposedDeletion;

	@Transient
	private int proposedPurchase;

	@Transient
	private int itemsLendable;

	/**
	 * Builds a new instance of a <code>DocumentAnalysis</code>-object, setting
	 * the individual counters to 0 and the text fields to an empty string.
	 * 
	 */
	public DocumentAnalysis() {
		// initialize all fields to be not null
		// initialize char fields for description and comments
		timeStamp = System.currentTimeMillis();
		description = "";
		shelfmark = "";
		notation = "";
		comment = "";

		// fields registered by event analyzer
		numberRequests = 0;
		numberLoans = 0;
		lastStock = 0;

		// days in stock for different groups
		daysStockLBS = 0;
		daysStockLendableNonLBS = 0;
		daysStockNonLendable = 0;
		daysStockDeleted = 0;

		// counter for different parts of the stock
		itemsLBS = 0;
		itemsTotal = 0;
		actualItemsLBS = 0;
		itemsDeleted = 0;
		itemsLendable = 0;

		// days requested for different groups
		daysRequestedStudents = 0;
		daysRequestedExtern = 0;
		daysRequestedIntern = 0;
		daysRequestedHapp = 0;
		daysRequestedElse = 0;

		// days loaned in different groups
		daysLoanedStudents = 0;
		daysLoanedExtern = 0;
		daysLoanedIntern = 0;
		daysLoanedHapp = 0;
		daysLoanedElse = 0;

		// statistical data for time period analyzed
		year = 2016;
		days = 365;

		// Transient fields, easier to determine, used to calculate values for
		// the persistent fields vie calculate()
		daysRequested = 0;
		itemsTotal = 0;
		daysStockAll = 0;
		daysLoanedAll = 0;
		meanRelativeLoan = 0;
		meanStock = 0;
		daysStockLendableAll = 0;
		proposedDeletion = 0;
		proposedPurchase = 0;
	}

	/**
	 * retrieves the value of days in stock for all circulation items which are
	 * not in the textbook collection.
	 * 
	 * @return int the value of days in stock for all circulation items which
	 *         are not in the textbook collection
	 */
	public int getDaysStockLendableNonLBS() {
		return daysStockLendableNonLBS;
	}

	/**
	 * set the corresponding counter
	 * 
	 * @param daysStockLendableNonLBS
	 *            number of days the counter is increased
	 */
	public void setDaysStockLendableNonLBS(int daysStockLendableNonLBS) {
		this.daysStockLendableNonLBS = daysStockLendableNonLBS;
	}

	/**
	 * retrieves the value of days loaned for all user groups.
	 * 
	 * @return int the value of days loaned for all user groups
	 */
	public int getDaysLoanedAll() {
		return daysLoanedAll;
	}

	/**
	 * set the corresponding counter
	 * 
	 * @param daysLoanedAll
	 *            number of days the counter is increased
	 */
	public void setDaysLoanedAll(int daysLoanedAll) {
		this.daysLoanedAll = daysLoanedAll;
	}

	/**
	 * retrieves the number of circulation items.
	 * 
	 * @return int the number of circulation items
	 */
	public int getItemsLendable() {
		return itemsLendable;
	}

	/**
	 * increases the number of circulation items.
	 * 
	 */
	public void increaseItemsLendable() {
		this.itemsLendable++;
	}

	/**
	 * decreases the number of circulation items.
	 * 
	 */
	public void decreaseItemsLendable() {
		this.itemsLendable--;
	}

	/**
	 * set the number of circulation items
	 * 
	 * @param itemsLendable
	 *            number of circulation items
	 */
	public void setItemsLendable(int itemsLendable) {
		this.itemsLendable = itemsLendable;
	}

	/**
	 * retrieves the timestamp when the analysis was performed.
	 * 
	 * @return double the timestamp in milliseconds
	 */
	public double getTimeStamp() {
		return timeStamp;
	}

	/**
	 * set the timestamp
	 * 
	 * @param timeStamp
	 *            timestamp of the time when the analysis was performed
	 */
	public void setTimeStamp(double timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * retrieves the description of the analysis.
	 * 
	 * @return String the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * set the description of the analysis
	 * 
	 * @param description
	 *            description of the analysis
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * retrieves the shelfmark for which the analysis was performed.
	 * 
	 * @return String the shelfmark
	 */
	public String getShelfmark() {
		return shelfmark;
	}

	/**
	 * set the shelfmark for which the analysis was performed.
	 * 
	 * @param shelfmark
	 *            shelfmark for which the analysis was performed
	 */
	public void setShelfmark(String shelfmark) {
		this.shelfmark = shelfmark;
	}

	/**
	 * retrieves the notation for which the analysis was performed.
	 * 
	 * @return String the notation
	 */
	public String getNotation() {
		return notation;
	}

	/**
	 * set the notation for which the analysis was performed
	 * 
	 * @param notation
	 *            notation for which the analysis was performed
	 */
	public void setNotation(String notation) {
		this.notation = notation;
	}

	/**
	 * retrieves the number of items in stock at the last day of the analysis.
	 * 
	 * @return int the number of items in stock at the last day of the analysis
	 */
	public int getLastStock() {
		return lastStock;
	}

	/**
	 * set the number of items in stock at the last day of the analysis.
	 * 
	 * @param lastStock
	 *            number of items in stock at the last day of the analysis
	 */
	public void setLastStock(int lastStock) {
		this.lastStock = lastStock;
	}

	/**
	 * retrieves the number of all circulation items which are not in the
	 * textbook collection.
	 * 
	 * @return int the number of all circulation items which are not in the
	 *         textbook collection
	 */
	public int getItemsLendableNonLBS() {
		return itemsLendableNonLBS;
	}

	/**
	 * set the number of all circulation items which are not in the textbook
	 * collection.
	 * 
	 * @param itemsLendableNonLBS
	 *            number of all circulation items which are not in the textbook
	 *            collection.
	 */
	public void setItemsLendableNonLBS(int itemsLendableNonLBS) {
		this.itemsLendableNonLBS = itemsLendableNonLBS;
	}

	/**
	 * increases the number of all circulation items which are not in the
	 * textbook collection.
	 * 
	 */
	public void increaseItemsLendableNonLBS() {
		this.itemsLendableNonLBS++;
	}

	/**
	 * decreases the number of all circulation items which are not in the
	 * textbook collection.
	 * 
	 */
	public void decreaseItemsLendableNonLBS() {
		this.itemsLendableNonLBS--;
	}

	/**
	 * retrieves the number of all deleted items.
	 * 
	 * @return int the number of all deleted items
	 */
	public int getItemsDeleted() {
		return itemsDeleted;
	}

	/**
	 * set the number of all deleted items.
	 * 
	 * @param itemsDeleted
	 *            number of all deleted items
	 */
	public void setItemsDeleted(int itemsDeleted) {
		this.itemsDeleted = itemsDeleted;
	}

	/**
	 * increases the number of all deleted items.
	 * 
	 */
	public void increaseItemsDeleted() {
		this.itemsDeleted++;
	}

	/**
	 * decreases the number of all deleted items.
	 * 
	 */
	public void decreaseItemsDeleted() {
		this.itemsDeleted--;
	}

	/**
	 * retrieves the number of non-circulation items.
	 * 
	 * @return int the number of non-circulation items
	 */
	public int getItemsNonLendable() {
		return itemsNonLendable;
	}

	/**
	 * set the number of non-circulation items.
	 * 
	 * @param itemsNonLendable
	 *            the number of non-circulation items
	 */
	public void setItemsNonLendable(int itemsNonLendable) {
		this.itemsNonLendable = itemsNonLendable;
	}

	/**
	 * increases the number of non-circulation items.
	 * 
	 */
	public void increaseItemsNonLendable() {
		this.itemsNonLendable++;
	}

	/**
	 * decreases the number of non-circulation items.
	 * 
	 */
	public void decreaseItemsNonLendable() {
		this.itemsNonLendable--;
	}

	/**
	 * retrieves the number of items in the textbook collection.
	 * 
	 * @return int the number of items in the textbook collection
	 */
	public int getItemsLBS() {
		return itemsLBS;
	}

	/**
	 * set the number of items in the textbook collection.
	 * 
	 * @param itemsLBS
	 *            number of items in the textbook collection
	 */
	public void setItemsLBS(int itemsLBS) {
		this.itemsLBS = itemsLBS;
	}

	/**
	 * increases the number of items in the textbook collection.
	 * 
	 */
	public void increaseItemsLBS() {
		this.itemsLBS++;
	}

	/**
	 * decreases the number of items in the textbook collection.
	 * 
	 */
	public void decreaseItemsLBS() {
		this.itemsLBS--;
	}

	/**
	 * retrieves the number of all items.
	 * 
	 * @return int the number of all items
	 */
	public int getItemsTotal() {
		return itemsTotal;
	}

	/**
	 * set the number of all items.
	 * 
	 * @param itemsTotal
	 *            number of all items
	 */
	public void setItemsTotal(int itemsTotal) {
		this.itemsTotal = itemsTotal;
	}

	/**
	 * increase the number of all items.
	 * 
	 */
	public void increaseItemsTotal() {
		this.itemsTotal++;
	}

	/**
	 * decrease the number of all items.
	 * 
	 */
	public void decreaseItemsTotal() {
		this.itemsTotal--;
	}

	/**
	 * retrieves the last number of items which are in the textbook collection.
	 * 
	 * @return int the last number of items which are in the textbook collection
	 */
	public int getActualItemsLBS() {
		return actualItemsLBS;
	}

	/**
	 * set the last number of items which are in the textbook collection
	 * 
	 * @param actualItemsLBS
	 *            last number of items which are in the textbook collection
	 */
	public void setActualItemsLBS(int actualItemsLBS) {
		this.actualItemsLBS = actualItemsLBS;
	}

	/**
	 * increases the last number of items which are in the textbook collection.
	 * 
	 */
	public void increaseActualItemsLBS() {
		this.actualItemsLBS++;
	}

	/**
	 * decreases the last number of items which are in the textbook collection.
	 * 
	 */
	public void decreaseActualItemsLBS() {
		this.actualItemsLBS--;
	}

	/**
	 * retrieves the comment of the analysis.
	 * 
	 * @return String the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * set the comment
	 * 
	 * @param comment
	 *            comment of the analysis
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * retrieves the value of days in stock for all circulation items.
	 * 
	 * @return int the value of days in stock for all circulation items
	 */
	public int getDaysStockLendableAll() {
		return daysStockLendableAll;
	}

	/**
	 * set the value of days in stock for all circulation items
	 * 
	 * @param daysStockLendableAll
	 *            value of days in stock for all circulation items
	 */
	public void setDaysStockLendableAll(int daysStockLendableAll) {
		this.daysStockLendableAll = daysStockLendableAll;
	}

	/**
	 * add the value of days in stock for all circulation items
	 * 
	 * @param daysStockLendableAll
	 *            value of days in stock for all circulation items to be added
	 */
	public void addDaysStockLendableAll(int daysStockLendableAll) {
		this.daysStockLendableAll += daysStockLendableAll;
	}

	/**
	 * retrieves the value of days in stock for all circulation items which are
	 * in the textbook collection.
	 * 
	 * @return int the value of days in stock for all circulation items which
	 *         are in the textbook collection
	 */
	public int getDaysStockLBS() {
		return daysStockLBS;
	}

	/**
	 * add the value of days in stock for all circulation items which are in the
	 * textbook collection
	 * 
	 * @param daysStockLBS
	 *            value of days in stock for all circulation items which are in
	 *            the textbook collection to be added
	 */
	public void addDaysStockLBS(int daysStockLBS) {
		this.daysStockLBS += daysStockLBS;
	}

	/**
	 * set the days in stock for all circulation items which are in the textbook
	 * collection
	 * 
	 * @param daysStockLBS
	 *            days in stock for all circulation items which are in the
	 *            textbook collection
	 */
	public void setDaysStockLBS(int daysStockLBS) {
		this.daysStockLBS = daysStockLBS;
	}

	/**
	 * retrieves the value of days in stock for all items.
	 * 
	 * @return int the value of days in stock for all items
	 */
	public int getDaysStockAll() {
		return daysStockAll;
	}

	/**
	 * set the value of days in stock for all items
	 * 
	 * @param daysStockAll
	 *            value of days in stock for all items
	 */
	public void setDaysStockAll(int daysStockAll) {
		this.daysStockAll = daysStockAll;
	}

	/**
	 * add the value of days in stock for all circulation items which are in the
	 * textbook collection
	 * 
	 * @param daysStockAll
	 *            value of days in stock for all circulation items which are in
	 *            the textbook collection to be added
	 */
	public void addDaysStockAll(int daysStockAll) {
		this.daysStockAll += daysStockAll;
	}

	/**
	 * retrieves the value of days in stock for all non-circulation items.
	 * 
	 * @return int the value of days in stock for all non-circulation items
	 */
	public int getDaysStockNonLendable() {
		return daysStockNonLendable;
	}

	/**
	 * set the value of days in stock for all non-circulation items
	 * 
	 * @param daysStockNonLendable
	 *            value of days in stock for all non-circulation items
	 */
	public void setDaysStockNonLendable(int daysStockNonLendable) {
		this.daysStockNonLendable = daysStockNonLendable;
	}

	/**
	 * add the value of days in stock for all non-circulation items
	 * 
	 * @param daysStockNonLendable
	 *            value of days in stock for all non-circulation items to be
	 *            added
	 */
	public void addDaysStockNonLendable(int daysStockNonLendable) {
		this.daysStockNonLendable += daysStockNonLendable;
	}

	/**
	 * retrieves the value of days in stock for all items which have been
	 * deleted.
	 * 
	 * @return int the value of days in stock for all items which have been
	 *         deleted
	 */
	public int getDaysStockDeleted() {
		return daysStockDeleted;
	}

	/**
	 * set the value of days in stock for all items which have been deleted
	 * 
	 * @param daysStockDeleted
	 *            value of days in stock for all items which have been deleted
	 */
	public void setDaysStockDeleted(int daysStockDeleted) {
		this.daysStockDeleted = daysStockDeleted;
	}

	/**
	 * add the value of days in stock for all items which have been deleted
	 * 
	 * @param daysStockDeleted
	 *            value of days in stock for all items which have been deleted
	 *            to be added
	 */
	public void addDaysStockDeleted(int daysStockDeleted) {
		this.daysStockDeleted += daysStockDeleted;
	}

	/**
	 * retrieves the value of days items were loaned by students.
	 * 
	 * @return int the value of days items were loaned by students
	 */
	public int getDaysLoanedStudents() {
		return daysLoanedStudents;
	}

	/**
	 * set the value of days items were loaned by students
	 * 
	 * @param daysLoanedStudents
	 *            value of days items were loaned by students
	 */
	public void setDaysLoanedStudents(int daysLoanedStudents) {
		this.daysLoanedStudents = daysLoanedStudents;
	}

	/**
	 * add the value of days items were loaned by students
	 * 
	 * @param daysLoanedStudentsNew
	 *            value of days items were loaned by students to be added
	 */
	public void addDaysLoanedStudents(int daysLoanedStudentsNew) {
		this.daysLoanedStudents += daysLoanedStudentsNew;
	}

	/**
	 * retrieves the value of days items were loaned by external users.
	 * 
	 * @return int the value of days items were loaned by external users
	 */
	public int getDaysLoanedExtern() {
		return daysLoanedExtern;
	}

	/**
	 * set the value of days items were loaned by external users
	 * 
	 * @param daysLoanedExtern
	 *            value of days items were loaned by external users
	 */
	public void setDaysLoanedExtern(int daysLoanedExtern) {
		this.daysLoanedExtern = daysLoanedExtern;
	}

	/**
	 * add the value of days items were loaned by external users
	 * 
	 * @param daysLoanedExternNew
	 *            value of days items were loaned by external users
	 */
	public void addDaysLoanedExtern(int daysLoanedExternNew) {
		this.daysLoanedExtern += daysLoanedExternNew;
	}

	/**
	 * retrieves the value of days items were loaned by non-student members of
	 * the university.
	 * 
	 * @return int the value of days items were loaned by non-student members of
	 *         the university
	 */
	public int getDaysLoanedIntern() {
		return daysLoanedIntern;
	}

	/**
	 * set the value of days items were loaned by non-student members of the
	 * university
	 * 
	 * @param daysLoanedIntern
	 *            value of days items were loaned by non-student members of the
	 *            university
	 */
	public void setDaysLoanedIntern(int daysLoanedIntern) {
		this.daysLoanedIntern = daysLoanedIntern;
	}

	/**
	 * add the value of days items were loaned by non-student members of the
	 * university
	 * 
	 * @param daysLoanedInternNew
	 *            value of days items were loaned by non-student members of the
	 *            university
	 */
	public void addDaysLoanedIntern(int daysLoanedInternNew) {
		this.daysLoanedIntern += daysLoanedInternNew;
	}

	/**
	 * retrieves the value of days items were located in research faculties.
	 * 
	 * @return int the value of days items were located in research faculties
	 */
	public int getDaysLoanedHapp() {
		return daysLoanedHapp;
	}

	/**
	 * set the value of days items were located in research faculties
	 * 
	 * @param daysLoanedHapp
	 *            value of days items were located in research faculties
	 */
	public void setDaysLoanedHapp(int daysLoanedHapp) {
		this.daysLoanedHapp = daysLoanedHapp;
	}

	/**
	 * add the value of days items were located in research faculties
	 * 
	 * @param daysLoanedHappNew
	 *            value of days items were located in research faculties
	 */
	public void addDaysLoanedHapp(int daysLoanedHappNew) {
		this.daysLoanedHapp += daysLoanedHappNew;
	}

	/**
	 * retrieves the value of days items were loaned by other users.
	 * 
	 * @return int the value of days items were loaned by other users
	 */
	public int getDaysLoanedElse() {
		return daysLoanedElse;
	}

	/**
	 * set the value of days items were loaned by other users
	 * 
	 * @param daysLoanedElse
	 *            value of days items were loaned by other users
	 */
	public void setDaysLoanedElse(int daysLoanedElse) {
		this.daysLoanedElse = daysLoanedElse;
	}

	/**
	 * add the value of days items were loaned by other users
	 * 
	 * @param daysLoanedElseNew
	 *            value of days items were loaned by other users
	 */
	public void addDaysLoanedElse(int daysLoanedElseNew) {
		this.daysLoanedElse += daysLoanedElseNew;
	}

	/**
	 * retrieves the value of days items were requested by students.
	 * 
	 * @return int the value of days items were requested by students
	 */
	public int getDaysRequestedStudents() {
		return daysRequestedStudents;
	}

	/**
	 * set the value of days items were requested by students
	 * 
	 * @param daysRequestedStudents
	 *            value of days items were requested by students
	 */
	public void setDaysRequestedStudents(int daysRequestedStudents) {
		this.daysRequestedStudents = daysRequestedStudents;
	}

	/**
	 * add the value of days items were requested by students
	 * 
	 * @param daysRequestedStudentsNew
	 *            value of days items were requested by students
	 */
	public void addDaysRequestedStudents(int daysRequestedStudentsNew) {
		this.daysRequestedStudents += daysRequestedStudentsNew;
	}

	/**
	 * retrieves the value of days items were loaned by external users.
	 * 
	 * @return int the value of days items were loaned by external users
	 */
	public int getDaysRequestedExtern() {
		return daysRequestedExtern;
	}

	/**
	 * set the value of days items were loaned by external users
	 * 
	 * @param daysRequestedExtern
	 *            value of days items were loaned by external users
	 */
	public void setDaysRequestedExtern(int daysRequestedExtern) {
		this.daysRequestedExtern = daysRequestedExtern;
	}

	/**
	 * add the value of days items were loaned by external users
	 * 
	 * @param daysRequestedExternNew
	 *            value of days items were loaned by external users
	 */
	public void addDaysRequestedExtern(int daysRequestedExternNew) {
		this.daysRequestedExtern += daysRequestedExternNew;
	}

	/**
	 * retrieves the value of days items were loaned by non-student members of
	 * the university.
	 * 
	 * @return int the value of days items were loaned by non-student members of
	 *         the university
	 */
	public int getDaysRequestedIntern() {
		return daysRequestedIntern;
	}

	/**
	 * set the value of days items were loaned by non-student members of the
	 * university
	 * 
	 * @param daysRequestedIntern
	 *            value of days items were loaned by non-student members of the
	 *            university
	 */
	public void setDaysRequestedIntern(int daysRequestedIntern) {
		this.daysRequestedIntern = daysRequestedIntern;
	}

	/**
	 * add the value of days items were loaned by non-student members of the
	 * university
	 * 
	 * @param daysRequestedInternNew
	 *            value of days items were loaned by non-student members of the
	 *            university
	 */
	public void addDaysRequestedIntern(int daysRequestedInternNew) {
		this.daysRequestedIntern += daysRequestedInternNew;
	}

	/**
	 * retrieves the value of days items were located to be in research
	 * faculties.
	 * 
	 * @return int the value of days items were located to be in research
	 *         faculties
	 */
	public int getDaysRequestedHapp() {
		return daysRequestedHapp;
	}

	/**
	 * set the value of days items were located to be in research faculties
	 * 
	 * @param daysRequestedHapp
	 *            value of days items were located to be in research faculties
	 */
	public void setDaysRequestedHapp(int daysRequestedHapp) {
		this.daysRequestedHapp = daysRequestedHapp;
	}

	/**
	 * add the value of days items were located to be in research faculties
	 * 
	 * @param daysRequestedHappNew
	 *            value of days items were located to be in research faculties
	 */
	public void addDaysRequestedHapp(int daysRequestedHappNew) {
		this.daysRequestedHapp += daysRequestedHappNew;
	}

	/**
	 * retrieves the value of days items were loaned by other users.
	 * 
	 * @return int the value of days items were loaned
	 */
	public int getDaysRequestedElse() {
		return daysRequestedElse;
	}

	/**
	 * set the value of days items were loaned by other users
	 * 
	 * @param daysRequestedElse
	 *            value of days items were loaned by other users
	 */
	public void setDaysRequestedElse(int daysRequestedElse) {
		this.daysRequestedElse = daysRequestedElse;
	}

	/**
	 * add the value of days items were loaned by other users
	 * 
	 * @param daysRequestedElseNew
	 *            value of days items were loaned by other users
	 */
	public void addDaysRequestedElse(int daysRequestedElseNew) {
		this.daysRequestedElse += daysRequestedElseNew;
	}

	/**
	 * retrieves the value of days items were requested in this year.
	 * 
	 * @return int the value of days items were requested
	 */
	public int getDaysRequested() {
		return daysRequested;
	}

	/**
	 * set the value of days items were requested in this year.
	 * 
	 * @param daysRequested
	 *            value of days items were requested
	 */
	public void setDaysRequested(int daysRequested) {
		this.daysRequested = daysRequested;
	}

	/**
	 * add the value of days items were requested in this year.
	 * 
	 * @param daysRequestedNew
	 *            value of days items were requested
	 */
	public void addDaysRequested(int daysRequestedNew) {
		this.daysRequested += daysRequestedNew;
	}

	/**
	 * retrieves the number of requests in this year.
	 * 
	 * @return int the number of requests
	 */
	public int getNumberRequests() {
		return numberRequests;
	}

	/**
	 * set the number of requests in this year.
	 * 
	 * @param numberRequests
	 *            number of requests
	 */
	public void setNumberRequests(int numberRequests) {
		this.numberRequests = numberRequests;
	}

	/**
	 * add the number of requests in this year.
	 * 
	 * @param numberRequestsNew
	 *            number of requests
	 */
	public void addNumberRequests(int numberRequestsNew) {
		this.numberRequests += numberRequestsNew;
	}

	/**
	 * increases the number of requests in this year.
	 * 
	 */
	public void increaseNumberRequests() {
		this.numberRequests++;
	}

	/**
	 * retrieves the number of loans in this year.
	 * 
	 * @return int the number of loans
	 */
	public int getNumberLoans() {
		return numberLoans;
	}

	/**
	 * set the number of loans in this year.
	 * 
	 * @param numberLoans
	 *            number of loans
	 */
	public void setNumberLoans(int numberLoans) {
		this.numberLoans = numberLoans;
	}

	/**
	 * increases the number of loans in this year.
	 * 
	 */
	public void increaseNumberLoans() {
		this.numberLoans++;
	}

	/**
	 * add the number of loans in this year.
	 * 
	 * @param numberLoansNew
	 *            number of loans
	 */
	public void addNumberLoans(int numberLoansNew) {
		this.numberLoans += numberLoansNew;
	}

	/**
	 * retrieves the value of days this analysis was performed for.
	 * 
	 * @return int the value of days
	 */
	public int getDays() {
		return days;
	}

	/**
	 * set the corresponding counter
	 * 
	 * @param days
	 *            number of days the counter is increased
	 */
	public void setDays(int days) {
		this.days = days;
	}

	/**
	 * retrieves the year this analysis was performed for.
	 * 
	 * @return int the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * set the year
	 * 
	 * @param year
	 *            number of days the counter is increased
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * retrieves the mean relative loan in this year.
	 * 
	 * @return double the mean relative loan
	 */
	public double getMeanRelativeLoan() {
		return meanRelativeLoan;
	}

	/**
	 * set the proposed deletion
	 * 
	 * @param propsedDeletionNew
	 *            proposed deletion
	 */
	public void setProposedDeletion(int propsedDeletionNew) {
		this.proposedDeletion = propsedDeletionNew;
	}

	/**
	 * retrieves the value of proposed deletions in this year.
	 * 
	 * @return int the value of proposed deletions
	 */
	public int getProposedDeletion() {
		return proposedDeletion;
	}

	/**
	 * set the corresponding counter
	 * 
	 * @param propsedPurchaseNew
	 *            number of days the counter is increased
	 */
	public void setProposedPurchase(int propsedPurchaseNew) {
		this.proposedPurchase = propsedPurchaseNew;
	}

	/**
	 * retrieves the value of proposed purchases in this year.
	 * 
	 * @return int the value of proposed purchases
	 */
	public int getProposedPurchase() {
		return proposedPurchase;
	}

	/**
	 * resets all the values to 0.
	 * 
	 */
	public void reset() {
		numberRequests = 0;
		numberLoans = 0;
		lastStock = 0;

		// days in stock for different groups
		daysStockLBS = 0;
		daysStockLendableNonLBS = 0;
		daysStockNonLendable = 0;
		daysStockDeleted = 0;

		// days requested for different groups
		daysRequestedStudents = 0;
		daysRequestedExtern = 0;
		daysRequestedIntern = 0;
		daysRequestedHapp = 0;
		daysRequestedElse = 0;

		// days loaned in different groups
		daysLoanedStudents = 0;
		daysLoanedExtern = 0;
		daysLoanedIntern = 0;
		daysLoanedHapp = 0;
		daysLoanedElse = 0;

		// statistical data for time period analyzed
		year = 2016;
		days = 365;

		// Transient fields, easier to determine, used to calculate values for
		// the persistent fields vie calculate()
		daysRequested = 0;
		itemsTotal = 0;
		daysStockAll = 0;
		daysLoanedAll = 0;
		meanRelativeLoan = 0;
		meanStock = 0;
		daysStockLendableAll = 0;
		proposedDeletion = 0;
		proposedPurchase = 0;
	}

	/**
	 * calculates the the value of days in stock for circulation items which are
	 * not in the textbook collection, the value of days items were loaned, the
	 * mean relative loaned and the mean stock and .
	 * 
	 */
	public void calculate() {
		this.daysStockLendableNonLBS = daysStockLendableAll - daysStockLBS;
		this.daysLoanedAll = daysLoanedElse + daysLoanedStudents + daysLoanedIntern + daysLoanedExtern + daysLoanedHapp;

		if (daysStockLendableAll != 0)
			this.meanRelativeLoan = (double) daysLoanedAll / (double) daysStockLendableAll;
		else
			this.meanRelativeLoan = 0;

		if (days != 0)
			this.meanStock = (double) daysStockAll / (double) days;
		else
			this.meanStock = 0;
	}

	/**
	 * adds another <code>DocumentAnalysis</code>-object to this one, assuming
	 * the same document, but different years. The numbers of items are taken
	 * from the later one, the days in stock, days loaned and days requested are
	 * summed up.
	 * 
	 * @param secondAnalysis
	 *            the analysis of the same document in another year added to the
	 *            first one
	 */
	public void addYearlyDocumentAnalysis(DocumentAnalysis secondAnalysis) {
		this.days += secondAnalysis.getDays();

		this.daysStockLBS += secondAnalysis.getDaysStockLBS();
		this.daysStockAll += secondAnalysis.getDaysStockAll();
		this.daysStockLendableAll += secondAnalysis.getDaysStockLendableAll();
		this.daysStockLendableNonLBS += secondAnalysis.getDaysStockLendableNonLBS();
		this.daysStockNonLendable += secondAnalysis.getDaysStockNonLendable();

		this.daysLoanedStudents += secondAnalysis.getDaysLoanedStudents();
		this.daysLoanedExtern += secondAnalysis.getDaysLoanedExtern();
		this.daysLoanedIntern += secondAnalysis.getDaysLoanedIntern();
		this.daysLoanedHapp += secondAnalysis.getDaysLoanedHapp();
		this.daysLoanedHapp += secondAnalysis.getDaysLoanedHapp();

		this.daysRequestedStudents += secondAnalysis.getDaysRequestedStudents();
		this.daysRequestedExtern += secondAnalysis.getDaysRequestedExtern();
		this.daysRequestedIntern += secondAnalysis.getDaysRequestedIntern();
		this.daysRequestedHapp += secondAnalysis.getDaysRequestedHapp();
		this.daysRequestedHapp += secondAnalysis.getDaysRequestedHapp();

		if (secondAnalysis.getYear() > this.year) {
			this.lastStock = secondAnalysis.getLastStock();
			this.itemsTotal = secondAnalysis.getItemsTotal();
			this.itemsLBS = secondAnalysis.getItemsLBS();
			this.actualItemsLBS = secondAnalysis.getActualItemsLBS();
			this.itemsDeleted = secondAnalysis.getItemsDeleted();
		}

	}

	/**
	 * adds another <code>DocumentAnalysis</code>-object to this one, assuming
	 * the different documents. The numbers of items are summed up as well as
	 * the days in stock, days loaned and days requested.
	 * 
	 * @param secondAnalysis
	 *            the analysis of another document added to the first one
	 */
	public void addEditionalDocumentAnalysis(DocumentAnalysis secondAnalysis) {
		this.days = secondAnalysis.days;

		this.daysStockLBS += secondAnalysis.getDaysStockLBS();
		this.daysStockAll += secondAnalysis.getDaysStockAll();
		this.daysStockLendableAll += secondAnalysis.getDaysStockLendableAll();
		this.daysStockLendableNonLBS += secondAnalysis.getDaysStockLendableNonLBS();
		this.daysStockNonLendable += secondAnalysis.getDaysStockNonLendable();

		this.daysLoanedAll += secondAnalysis.getDaysLoanedAll();
		this.daysLoanedStudents += secondAnalysis.getDaysLoanedStudents();
		this.daysLoanedExtern += secondAnalysis.getDaysLoanedExtern();
		this.daysLoanedIntern += secondAnalysis.getDaysLoanedIntern();
		this.daysLoanedHapp += secondAnalysis.getDaysLoanedHapp();

		this.daysRequestedStudents += secondAnalysis.getDaysRequestedStudents();
		this.daysRequestedExtern += secondAnalysis.getDaysRequestedExtern();
		this.daysRequestedIntern += secondAnalysis.getDaysRequestedIntern();
		this.daysRequestedHapp += secondAnalysis.getDaysRequestedHapp();
		this.daysRequestedElse += secondAnalysis.getDaysRequestedElse();

		this.lastStock += secondAnalysis.getLastStock();
		this.itemsTotal += secondAnalysis.getItemsTotal();
		this.itemsLBS += secondAnalysis.getItemsLBS();
		this.actualItemsLBS += secondAnalysis.getActualItemsLBS();
		this.itemsDeleted += secondAnalysis.getItemsDeleted();
		// calculate();
	}

	/**
	 * compares this <code>DocumentAnalysis</code>-object to another one.
	 * returns 0, when the shelfmarks are identical.
	 * 
	 * @param other
	 *            analysis the analysis to be compared to this one
	 * @return int 0 if the shelfmarks of boh analysis are identical
	 */
	public int compareTo(DocumentAnalysis other) {
		return this.shelfmark.compareTo(other.shelfmark);
	}
}
