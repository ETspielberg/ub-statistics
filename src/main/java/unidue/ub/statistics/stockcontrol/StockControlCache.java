package unidue.ub.statistics.stockcontrol;

import org.mycore.common.MCRCache;

/**
 * Cache to store stock control properties.
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class StockControlCache {
		
	private static MCRCache<String, StockControlProperties> cache = new MCRCache<String, StockControlProperties>(200, "StockControlProperties");

	/**
     * saves stock control porperties in the cache
     * 
     * 
     * @param scp
     *            stock control properties
     */
	public static void store(StockControlProperties scp) {
        cache.put(scp.getStockControl(), scp);
    }

     /**
      * retrieves the stock control properties from the cache by the stock control key
      * 
      * 
      * @param stockControl
      *            stock control key
      * @return scp stock control properties
      */
     public static StockControlProperties get(String stockControl) {
        return cache.get(stockControl);
    }
}
