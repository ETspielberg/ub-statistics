package unidue.ub.statistics.csvHandling;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

/**
 * Receives an uploaded csv file and saves it in the upload directory within the
 * user directory.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/profile/csvUpload")
@MultipartConfig
public class CSVUploadServlet extends MCRServlet {

	private final static String userDir;

	static {
		MCRConfiguration config = MCRConfiguration.instance();
		userDir = config.getString("ub.statistics.userDir");
	}

	private static final Logger LOGGER = Logger.getLogger(CSVUploadServlet.class);

	private static final long serialVersionUID = 1;

	/**
	 * reads the necessary parameters from the http request, receives the file
	 * from the http post request and saves the file in the user directory.
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 */
	protected void doPost(MCRServletJob job) throws Exception {
		HttpServletRequest req = job.getRequest();
		String who = req.getUserPrincipal().getName();
		Collection<Part> parts = req.getParts();
		File uploadDir = new File(userDir + "/" + who + "/upload");
		if (!uploadDir.exists())
			uploadDir.mkdirs();
		byte[] buffer = new byte[8 * 1024];
		for (Part part : parts) {
			File uploadedFile = new File(uploadDir, getFileName(part));

			InputStream input = part.getInputStream();
			try {
				OutputStream output = new FileOutputStream(uploadedFile);
				try {
					int bytesRead;
					while ((bytesRead = input.read(buffer)) != -1) {
						output.write(buffer, 0, bytesRead);
					}
				} finally {
					output.close();
				}
			} finally {
				input.close();
			}
			LOGGER.info("saved csv-file " + uploadedFile);
		}
	}

	private String getFileName(Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
}
