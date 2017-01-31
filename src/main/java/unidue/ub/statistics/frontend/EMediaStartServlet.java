/**
 * 
 */
package unidue.ub.statistics.frontend;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

/**
 * Displays the starting page for the journal module 
 * @author Eike Spielberg
 * @version 1
 *
 */
@WebServlet("/fachref/eMedia")
public class EMediaStartServlet extends FachRefServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * prepares the starting page for the journal module
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
	protected void doGetPost(MCRServletJob job) throws IOException, TransformerException, SAXException, JDOMException {
		Element output = prepareOutput(job,"eMedia","eMedia");
		sendOutput(job,output);
	}
}
