package unidue.ub.statistics;

import org.mycore.common.MCRCache;

import unidue.ub.statistics.media.monographs.Manifestation;

/**
 * A cache holding <code>Document</code>-objects to be retrieved by other
 * Servlets.
 * 
 * @author Frank L\u00FCtzenkirchen
 * @version 1
 */
public class DocumentCache {
	
    private static MCRCache<String, Manifestation> cache = new MCRCache<String, Manifestation>(200, "documents");
    
    /**
     * stores a <code>Document</code>-object in the cache.
     *
     * @param document
     *            the <code>Document</code>-object to be stored in the cache.
     */
    public static void store(Manifestation document) {
        cache.put(document.getDocNumber(), document);
    }
    
    /**
     * retrieves a <code>Document</code>-object from the cache by the given document number.
     * 
     * @param docNumber
     *            the document number of the <code>Document</code>-object to be retrieved from the cache.
     * @return document
     *            the the <code>Document</code>-object retrieved from the cache by the given document number.
     */

    public static Manifestation get(String docNumber) {
        return cache.get(docNumber);
    }
}
