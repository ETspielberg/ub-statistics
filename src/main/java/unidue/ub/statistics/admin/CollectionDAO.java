package unidue.ub.statistics.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServletJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Methods to access the index of collections to allow retrieval of regions by JPA queries.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/admin/buildCollectionIndex")
public class CollectionDAO extends FachRefServlet implements Job {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(CollectionDAO.class);
    
    private final static String dataDir;

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        dataDir = config.getString("ub.statistics.localResourcesDir");
    }

    /**
    * updates the index of collections
    * 
    * 
    * @param job
    *            <code>MCRServletJob</code>
    * @exception IOException exception while reading systematik.xml file from disk
    * @exception JDOMException exception upon parsing the systematik.xml file
    */
	public void doGetPost(MCRServletJob job) throws IOException, JDOMException {
    	try {
            buildIndex();
            LOGGER.info("built CollectionIndex");
        } catch (IOException | JDOMException e) {
            LOGGER.info("error building Index");
        }
    	job.getResponse().sendRedirect("overview?message=builtCollectionIndex");
    }

    /**
     * execute method to be called by the quartz framework.
     * 
     * @param context
     *            job execution context from the quartz framework
     * 
     */
    @Override
    public void execute(JobExecutionContext context) {
        try {
            buildIndex();
            LOGGER.info("built CollectionIndex");
        } catch (IOException | JDOMException e) {
            LOGGER.info("error building Index");
        }
    }


    /**
     * retrieves a <code>Collection</code> from the database by the name of the collection.
     * 
     * @param name
     *            the name of the collection
     * @return collection a <code>Collection</code> object
     * 
     */
    public Collection getCollectionByName(String name) {
        EntityManager em = Persistence.createEntityManagerFactory("tools").createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Collection> q = cb.createQuery(Collection.class);
        Root<Collection> c = q.from(Collection.class);
        q.select(c).where(cb.equal(c.<String>get("name"), name));
        TypedQuery<Collection> query = em.createQuery(q);
        Collection collection = query.getSingleResult();
        return collection;
    }

    /**
     * retrieves a <code>Collection</code> from the database by the name of the corresponding map.
     * 
     * @param map
     *            the file name of the map depicting the floor
     * @return collection a <code>Collection</code> object
     * 
     */
    public Collection getCollectionByMapName(String map) {
        EntityManager em = Persistence.createEntityManagerFactory("tools").createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Collection> q = cb.createQuery(Collection.class);
        Root<Collection> c = q.from(Collection.class);
        q.select(c).where(cb.equal(c.<String>get("map"), map));
        TypedQuery<Collection> query = em.createQuery(q);
        Collection collection = query.getSingleResult();
        return collection;
    }

    
     /**
     * reads in the collections from a collections.xml file and builds the corresponding index.
     * 
     * @exception IOException exception while reading systematik.xml file from disk
     * @exception JDOMException exception upon parsing the systematik.xml file
     */
    public void buildIndex() throws IOException, JDOMException {
        EntityManager em = Persistence.createEntityManagerFactory("tools").createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            em.createQuery("DELETE FROM Collection").executeUpdate();
        } catch (Exception exc1) {
            LOGGER.info("error in deleting notations (per subject)");
        }
        tx.commit();
        
        File input  = new File(dataDir,"collections.xml"); 
        Document collectionsListXML = new SAXBuilder().build(new FileInputStream(input));
        List<Element> collectionsXML = collectionsListXML.detachRootElement().clone().getChildren("collection");
        tx.begin();
        for (Element collectionXML : collectionsXML) {
        	Collection collection = new Collection();
        	collection.setCollections(collectionXML.getChildText("places"))
        	.setMap(collectionXML.getChildText("mapName"))
        	.setName(collectionXML.getChildText("name"));
        	em.persist(collection);
        }
        tx.commit();
        em.close();
    }
}
