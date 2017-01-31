package unidue.ub.statistics.series;

/**
 * Plain old java object holding the combination of time and count.
 * 
 * @author Frank L\u00FCtzenkirchen
 * @version 1
 */
public class TimeAndCount {
	
    private long time;

    private int count;

    /**
     * setting a time-and-count pair
     * 
     * @param time
     *            time in milliseconds
     * @param count
     *            number of objects
     * 
     */
    public TimeAndCount(long time, int count) {
        this.time = time;
        this.count = count;
    }

    /**
     * returns the count
     * 
     * @return count the count
     */
    public int getCount() {
        return count;
    }

    /**
     * returns the time.
     * 
     * @return time the time in milliseconds
     */
    public long getTime() {
        return time;
    }

    /**
     * sets the count.
     * 
     * @param count the count
     */
    public void setCount(int count) {
        this.count = count;
    }
}