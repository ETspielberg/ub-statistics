package unidue.ub.statistics.stockcontrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.xml.transform.TransformerException;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServletJob;
import org.xml.sax.SAXException;

import unidue.ub.statistics.frontend.FachRefServlet;

/**
 * Lists the stock control properties file on disk.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile")
public class SCPManagementServlet extends FachRefServlet {

	private static final long serialVersionUID = 1L;

    private final static String userDir;

    private static final Logger LOGGER = Logger.getLogger(SCPManagementServlet.class);

    static {
        MCRConfiguration config = MCRConfiguration.instance();
        userDir = config.getString("ub.statistics.userDir");
    }

    /**
     * reads in the stockControl files in the user directory and writes the found information into an xml file to be display by XSLT transformation
     * 
     * 
     * @param job
     *            <code>MCRServletJob</code>
     */
    protected void doGetPost(MCRServletJob job) throws ServletException, IOException, JDOMException, TransformerException, SAXException {
    	Element output = prepareOutput(job,"profileOverview","profile");
        String who = job.getRequest().getUserPrincipal().getName();
        File userFile = new File(userDir + "/" + who, "user_data.xml");
        if (userFile.exists()) {
            SAXBuilder builder = new SAXBuilder();
            org.jdom2.Document document;
            document = (org.jdom2.Document) builder.build(userFile);

            Element rootNode = document.getRootElement();
            String username = rootNode.getChild("details").getChild("fullname").getValue();
            org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
            if (currentUser.hasRole("fachreferent")) {
                String friendlyName = (String) job.getRequest().getSession().getAttribute("friendlyName");
                output.setAttribute("loggedInAs",friendlyName);
            }
            Element subjects = rootNode.getChild("subjects");
            List<Element> list = subjects.getChildren();

            
            output.setAttribute("username", username);
            output.setAttribute("user", who);
            Element referent = new Element("referent");
            output.addContent(referent);
            Element substitute = new Element("substitute");
            output.addContent(substitute);
            
            Element csvFiles = new Element("csvFiles");
            output.addContent(csvFiles);

            File stockControlDirectory = new File(userDir + "/" + who +"/stockControl");
            if (!stockControlDirectory.exists()) {
                stockControlDirectory.mkdir();
                List<File> oldFiles = new ArrayList<>();
                List<File> newFiles = new ArrayList<>();
                oldFiles.add(new File(userDir, "csv.xml"));
                newFiles.add(new File(userDir + "/" + who, "csv.xml"));
                oldFiles.add(new File(userDir, "default.xml"));
                newFiles.add(new File(userDir + "/" + who, "default.xml"));
                InputStream is = null;
                OutputStream os = null;
                for (int i = 0; i < oldFiles.size(); i++) {
                    try {
                        is = new FileInputStream(oldFiles.get(i));
                        os = new FileOutputStream(newFiles.get(i));
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    } finally {
                        is.close();
                        os.close();
                    }
                }
            }
            
            for (String name : Arrays.asList(stockControlDirectory.list())) {
                if (name.equals("default.xml") || name.equals("csv.xml")) continue;
                boolean referentSubject = false;
                StockControlProperties scp = new StockControlProperties(name.substring(0, name.indexOf(".")), who);
                for (Element subject : list) {
                    String subjectId = subject.getValue().trim();
                    if (subjectId.equals(name.substring(0, subjectId.length()))) {
                        if (subject.getAttributeValue("type").equals("referent")) scp.addStockControlToOutput(referent);
                        referentSubject = true;
                     }
                }
                if (!referentSubject) scp.addStockControlToOutput(substitute);
            }
            File uploads = new File(userDir + "/" + who + "/upload");
            if (!uploads.exists()) uploads.mkdirs();
            String[] files = uploads.list();
            if (files != null) {
            for (String name : Arrays.asList(files)) {
                csvFiles.addContent((new Element("file")).addContent(name));
            }
            }
            sendOutput(job,output);
            LOGGER.info("Finished output.");
        } else {
            job.getResponse().sendRedirect("../forms/User_Form.xed");
        }
    }

}
