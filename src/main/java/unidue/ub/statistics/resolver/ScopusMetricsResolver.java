package unidue.ub.statistics.resolver;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.common.content.MCRStreamContent;

/**
 * Queries the Scopus API to
 * retrieve metrics on particular journals
 * 
 * 
 * @author Eike Spielberg
 */
public class ScopusMetricsResolver implements URIResolver {

    private final static String API_URL;

    private final static String API_KEY;

    static {
        //get parameters from mycore.properties
        MCRConfiguration config = MCRConfiguration.instance();
        API_URL = config.getString("MCR.api.ScopusURI");
        API_KEY = config.getString("MCR.api.ScopusAPI");
    }

    private final static Logger LOGGER = Logger.getLogger(ScopusMetricsResolver.class);

    /**
     * resolves the given string and returns the contents as source
     * @param href the given URI
     * @param base the base of the resolver
     * @return the returned contents as source
     */
    @Override
    public Source resolve(String href, String base) {
        String issn = href.substring(href.indexOf(":") + 1);
        if (issn.contains("-"))
            issn = issn.replace("-", "");
        LOGGER.info("Reading journal metrics with issn " + issn);
        String queryURL = API_URL + "/serial/title/issn/" + issn + "?apikey=" + API_KEY + "&httpAccept=application%2Fxml";
        HttpClient client = new HttpClient();
        GetMethod connection = new GetMethod(queryURL);
        try {
            client.executeMethod(connection);
            InputStream response = connection.getResponseBodyAsStream();
            return (new MCRStreamContent(response)).getSource();
        } catch (Exception e) {
            LOGGER.info("could not retrieve journal metrics");
            return null;
        }
    }

}
