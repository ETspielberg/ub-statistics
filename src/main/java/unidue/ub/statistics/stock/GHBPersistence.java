package unidue.ub.statistics.stock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mycore.common.MCRCache;
import org.mycore.common.config.MCRConfiguration;
import org.mycore.frontend.servlets.MCRServlet;

/**
 * Persisting the xml files for the Gesamthochschulsystematik.
 * 
 * @author Frank L\u00FCtzenkirchen
 **/
public class GHBPersistence extends MCRServlet {

	private static final long serialVersionUID = 1L;

	private static String dataDir = MCRConfiguration.instance().getString("ub.statistics.localResourcesDir");

	private static MCRCache<String, Element> files = new MCRCache<String, Element>(100, "Aufstellungssystematik");

	/**
	 * loads the xml file from the disk and provides the corresponding
	 * org.jdom2.element
	 * 
	 * @param filename
	 *            the filename of the file to be loaded
	 * 
	 * @return root the org.jdom2.element
	 * @throws IOException exception while reading file
	 * @throws JDOMException exception while parsing file to JDOM object
	 */
	public static synchronized Element loadFile(String filename) throws IOException, JDOMException {
		File file = new File(dataDir, filename);
		Element root = (Element) (files.getIfUpToDate(filename, file.lastModified()));
		if (root == null) {
			Document doc = new SAXBuilder().build(file);
			root = doc.detachRootElement();
			files.put(filename, root);
		}
		return (Element) (root.clone());
	}

	/**
	 * saves the org.jdom2.element as xml file with the given filename to the
	 * disk
	 * 
	 * @param filename
	 *            the filename of the file
	 * @param root
	 *            org.jdom2.element to be saved
	 * 
	 */
	static synchronized void saveFile(String filename, Element root) throws IOException {
		Document doc = new Document(root);
		FileOutputStream fos = new FileOutputStream(new File(dataDir, filename));
		XMLOutputter xo = new XMLOutputter();
		xo.setFormat(Format.getPrettyFormat().setEncoding("UTF-8"));
		xo.output(doc, fos);
		fos.close();

		files.remove(filename);
	}

	/**
	 * lists all the available systematik files
	 * 
	 * @return list list of files on disk
	 * 
	 */
	public static ArrayList<String> listFiles() {
		File xmlDirectory = new File(dataDir);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.startsWith("systematik-")) {
					return true;
				} else {
					return false;
				}
			}
		};
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(xmlDirectory.list(filter)));
		return names;
	}
}
