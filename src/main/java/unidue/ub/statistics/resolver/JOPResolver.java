package unidue.ub.statistics.resolver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.JDOMSource;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.MCRCache;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.MCRSessionMgr;
import org.mycore.common.content.MCRByteContent;
import org.xml.sax.SAXException;

/**
 * Queries the "Journals Online &amp; Print" service to
 * retrieve information on online and print availability
 * of journals. Returns the "full xml" format from JOP.
 * 
 * http://www.zeitschriftendatenbank.de/services/journals-online-print
 * 
 * Usage: jop:{queryString} where queryString is same as in any web request to JOP
 * Usage: jop:session to get the last JOP response that was queried in the current user's session
 * 
 * @author Frank L\u00FCtzenkirchen
 */
public class JOPResolver implements URIResolver {

    private final static Logger LOGGER = Logger.getLogger(JOPResolver.class);

    /** A key to store JOP information in the current user's session */
    private final static String JOPKEY = "JOP";

    /** URL of the JOP full xml service, set via property JOP.FullXML.URL */
    private final static String jopFullURL;

    /** URL of the JOP brief xml service, set via property JOP.BriefXML.URL */
    private final static String jopBriefURL;

    /** Timeout (millis) for requests to JOP, set via property JOP.TimeOut (seconds) */
    private final static int timeout;

    /** Maximum age of JOP requests (millis) in cache, set via property JOP.Cache.MaxAge (seconds) */
    private final static int cacheMaxAge;
    
    private final static int cacheSize;

    /** A cache of JOP requests, where key is the query string */
    private MCRCache<String,Element> cache;
    
        
    static {
		MCRConfiguration config = MCRConfiguration.instance();
		jopFullURL = config.getString("JOP.FullXML.URL");
        jopBriefURL = config.getString("JOP.BriefXML.URL");
        timeout = config.getInt("JOP.TimeOut", 10) * 1000;
        cacheMaxAge = config.getInt("JOP.Cache.MaxAge", 10) * 1000;
        cacheSize = config.getInt("JOP.Cache.Size", 50);
	}

    
    public JOPResolver() {
        cache = new MCRCache<String,Element>(cacheSize, JOPKEY);
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        String query = href.substring(4);
        LOGGER.info(query);

        try {
            Element root = "session".equals(query) ? getJOPDataFromSession() : getJOPDataFromCache(query);
            if (root == null) {
                root = getJOPDataFromRemoteServer(query, jopFullURL);
                fixAtInTitles(root);
                root.setAttribute("query", query);
                addDataFromOpenURLQuery(root, query);

                // if (isElectronicDataState3(root))
                //    addElectronicDataTotalState(root, query);

                cache.put(query, root);
            }
            MCRSessionMgr.getCurrentSession().put(JOPKEY, root);
            return new JDOMSource(root);
        } catch (Exception ex) {
            throw new TransformerException(ex);
        }
    }

    /** The xpath under which the JOP brief response contains the total state of electronic availability */
    private final static String xpathToElectronicDataState = "/OpenURLResponseXML/Brief/ElectronicData/@state";

    /** The xpath under which the JOP full response contains the information on electronic availability */
    private final static String xpathToElectronicDataFull = "/OpenURLResponseXML/Full/ElectronicData";

    /** The additional xpath to check if any result has state '3' (partially licensed) */
    private final static String xpathPredicateState3 = "/ResultList[Result/@state='3']";

    /**
     * Returns true, if any electronic data is marked with state '3' (partially licensed) in the given
     * full XML response of JOP. 
     */
    private boolean isElectronicDataState3(Element fullXML) throws JDOMException {
        String path = xpathToElectronicDataFull + xpathPredicateState3;
        XPathExpression<Element> xPath = XPathFactory.instance().compile(path, Filters.element());
        boolean result = (null != xPath.evaluateFirst(fullXML));
        LOGGER.debug("electronic availability is marked as partially licensed? " + result);
        return result;
    }

    /**
     * Queries the JOP brief xml service to get the total status of electronic availability, and adds that state
     * to the ElectronicData element. In case total access is given by combining multiple licenses with state '3' 
     * (partially licensed), only the brief response contains the information that the combined license covers
     * the complete journal, if so. 
     */
    private void addElectronicDataTotalState(Element fullXML, String query) throws SAXException, IOException, JDOMException {
        Element briefData = getJOPDataFromRemoteServer(query, jopBriefURL);
        XPathExpression<Attribute> xPath = XPathFactory.instance().compile(xpathToElectronicDataState, Filters.attribute());
        Attribute state = xPath.evaluateFirst(briefData);
        LOGGER.debug("electronic availability total state from brief xml response is " + state.getValue());
        Element electronicData = XPathFactory.instance().compile(xpathToElectronicDataFull, Filters.element()).evaluateFirst(fullXML);
        state.detach();
        electronicData.setAttribute(state);
    }

    /** Remove all @ contained in "Title" elements that seem to separate to non-sort part of the title from the rest */
    private void fixAtInTitles(Element root) {
        List<Element> candidates = new ArrayList<Element>();

        Iterator<Element> titles = root.getDescendants(new ElementFilter("Title"));
        while (titles.hasNext()) {
            Element title = titles.next();
            String text = title.getText();
            if ((text != null) && text.contains("@"))
                candidates.add(title);
        }

        for (Element title : candidates)
            title.setText(title.getText().replaceAll("\\s@(\\S)", " $1"));
    }

    /**
     * Queries the Journals Online & Print service to retrieve the XML data
     * on journal availability.
     */
    private Element getJOPDataFromRemoteServer(String query, String baseURL) throws IOException, SAXException, JDOMException {
        String url = baseURL + "?" + query;
        GetMethod getMethod = new GetMethod(url);
        getMethod.setFollowRedirects(true);
        HttpClient client = new HttpClient();
        client.getParams().setConnectionManagerTimeout(timeout);
        client.getParams().setSoTimeout(timeout);

        int responseCode = client.executeMethod(getMethod);
        if (responseCode != 200) {
            LOGGER.error("JOP returned response code " + responseCode);
            throw new JOPException(getMethod);
        }

        byte[] response = getMethod.getResponseBody();
        getMethod.releaseConnection();

        Element jopData = new MCRByteContent(response).asXML().getRootElement();
        debugJOPData(jopData);
        return jopData;
    }

    /** 
     * A JOPException is thrown when requests to JOP return with HTTP errors.
     */
    public class JOPException extends RuntimeException {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/** The HTTP error code returned from the JOP request */
        private int errorCode;

        public JOPException(GetMethod getMethod) throws IOException {
            super(getResponseError(getMethod));
            this.errorCode = getMethod.getStatusCode();
        }

        /** Returns the HTTP error code returned from the JOP request 
         * @return the error Code
         * */
        public int getErrorCode() {
            return errorCode;
        }

    }

    private static String getResponseError(GetMethod getMethod) throws IOException {
        String msg = "Der JOP-Dienst von EZB/ZDB meldet einen Fehler:";
        String uri = getMethod.getURI().toString();
        String text = getMethod.getStatusText();
        String body = getMethod.getResponseBodyAsString();
        if (body == null)
            body = "";
        if (body.contains("<body>"))
            body = body.substring(body.indexOf("<body>") + 6);
        if (body.contains("</body>"))
            body = body.substring(0, body.indexOf("</body>"));
        return msg + "\n\n" + uri + "\n\n" + text + "\n\n" + body;
    }

    /**
     * Returns the JOP data that was last used in the current user's session (URI: jop:session)
     */
    private Element getJOPDataFromSession() throws TransformerException {
        Element jopData = (Element) (MCRSessionMgr.getCurrentSession().get(JOPKEY));
        if (jopData == null) {
            throw new TransformerException("No JOP data in session");
        }
        return jopData;
    }

    /**
     * Returns the JOP data that was cached for the given request query.
     * 
     * @return the JOP data from cache, if any, and only of it is not older than the maximum cache age.
     */
    private Element getJOPDataFromCache(String query) {
        long maxAge = System.currentTimeMillis() - cacheMaxAge;
        Element jopData = (Element) (cache.getIfUpToDate(query, maxAge));
        if (jopData != null)
            LOGGER.debug("JOP data retrieved from cache");
        return jopData;
    }

    private void debugJOPData(Element jopData) {
        if (!LOGGER.isDebugEnabled())
            return;

        XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
        String output = xout.outputString(jopData);
        LOGGER.debug("\n\n" + output + "\n\n");
    }

    /**
     * Parses the OpenURL request and adds the query parameters as XML element 
     * for later use in the order form. 
     *  
     * @param parent
     * @param query
     */
    private void addDataFromOpenURLQuery(Element parent, String query) {
        if (query == null)
            return;

        Element q = new Element("query");
        for (String pair : query.split("&")) {
            int pos = pair.indexOf("=");
            if ((pos == -1) || (pos == pair.length() - 1))
                continue;
            try {
                String name = pair.substring(0, pos);
                String value = URLDecoder.decode(pair.substring(pos + 1), "UTF-8");
                Element e = new Element(name).setText(value);
                q.addContent(e);
            } catch (UnsupportedEncodingException ignored) {
            }
        }

        if (!q.getChildren().isEmpty())
            parent.addContent(q);
    }
}
