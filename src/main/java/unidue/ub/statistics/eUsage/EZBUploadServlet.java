package unidue.ub.statistics.eUsage;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;
import org.mycore.frontend.servlets.MCRServlet;
import org.mycore.frontend.servlets.MCRServletJob;

import unidue.ub.statistics.media.journal.Journal;
import unidue.ub.statistics.media.journal.JournalCollectionDAO;
import unidue.ub.statistics.media.journal.JournalDAO;
import unidue.ub.statistics.media.journal.JournalCollection;
import unidue.ub.statistics.media.journal.JournalTitle;
import unidue.ub.statistics.media.journal.JournalTitleDAO;

/**
 * Receives an uploaded csv file and saves it in the upload directory within the
 * user directory.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@WebServlet("/fachref/eMedia/ezbUpload")
@MultipartConfig
public class EZBUploadServlet extends MCRServlet {

	private static final Logger LOGGER = Logger.getLogger(EZBUploadServlet.class);

	private static final long serialVersionUID = 1;
	
	private static final Pattern yearPattern = Pattern.compile("((19|20)\\d\\d)");
	
	/**
	 * reads the necessary parameters from the http request, receives the file
	 * from the http post request and saves the file in the user directory.
	 * 
	 * 
	 * @param job
	 *            <code>MCRServletJob</code>
	 * @throws Exception thrown if one of the many things that can go wrong goes wrong ...
	 */
	protected void doPost(MCRServletJob job) throws Exception {
		Collection<Part> parts = job.getRequest().getParts();
		for (Part part : parts) {
			InputStream input = part.getInputStream();
			String filename = getFileName(part);
			Matcher matcher = yearPattern.matcher(filename);
			int year = LocalDate.now().getYear();
            if (matcher.find())
                year = Integer.parseInt(matcher.group());
			readCsv(input,year);
			
		}
		job.getResponse().sendRedirect("/eMedia/overview");
	}

	private void readCsv(InputStream csvFile,int year) throws SQLException {
	    HashSet<String> issnsCollection = new HashSet<String>();
		Scanner inputStream = new Scanner(new InputStreamReader(csvFile));
		Hashtable<String, JournalCollection> collections = new Hashtable<String, JournalCollection>();
		List<Journal> journals = new ArrayList<>();
		List<JournalTitle> journalTitles = new ArrayList<>();
		inputStream.nextLine();
		int numberOfCollections = 0;
		
		while (inputStream.hasNext()) {
			// get the individual parts of the string
			String line = inputStream.nextLine();
			String[] parts = line.split("\t");
			
			// if there are multiple values, delete the "" enclosing the list of values for the electronic and print issns 
			String eIssns = parts[5];
			if (eIssns.contains("\""))
				eIssns = eIssns.replace("\"", "");
			
			String pIssns = parts[6];
			if (pIssns.contains("\""))
				pIssns = pIssns.replace("\"", "");
			
			String subject = parts[4];
			if (subject.contains("\""))
				subject = subject.replace("\"", "");
			
			String zdbID = parts[7]; 
			String name = parts[1];
			if (name.contains("="))
				name = name.substring(0, name.indexOf("="));
			
			Journal journal = new Journal();
			journal.setZdbID(zdbID).setEzbID(parts[0]).setSubject(subject).setActualName(name).setLink(parts[12]);
			if (!eIssns.isEmpty())
				journal.addISSN(eIssns);
			if (!pIssns.isEmpty())
				journal.addISSN(pIssns);
			
			journals.add(journal);
			String anchor = "";
			try { 
				anchor = parts[13];
			} catch (Exception e1) {
			anchor = name;
			
			}
			if (collections.containsKey(anchor)) {
				JournalCollection pack = collections.get(anchor);
				if (!eIssns.isEmpty())
				    pack.addISSN(eIssns);
				if (!pIssns.isEmpty())
				    pack.addISSN(pIssns);
				collections.replace(anchor, pack);
			} else {
			    if (!pIssns.isEmpty() || !eIssns.isEmpty()) {
			        numberOfCollections++;
			        JournalCollection pack = new JournalCollection().setYear(year);
			        if (!eIssns.isEmpty())
			            pack.setIssns(eIssns);
			        if (!pIssns.isEmpty())
			            pack.addISSN(pIssns);
			        pack.setAnchor(anchor);
			        collections.put(anchor, pack);
			    }
			}
			journalTitles.addAll(buildJournalTitleList(eIssns, name, subject, anchor, zdbID, "electronic", issnsCollection));
			journalTitles.addAll(buildJournalTitleList(pIssns, name, subject, anchor, zdbID, "print", issnsCollection));
		}
		inputStream.close();
		LOGGER.info("found " + journals.size() + " journals with " + journalTitles.size() + " individual titles grouped in " + numberOfCollections + " collections");
        JournalTitleDAO.persistJournals(journalTitles);
		JournalCollectionDAO.persistCollections(collections);
		JournalDAO.persistJournals(journals);
	}
	
	private List<JournalTitle> buildJournalTitleList(String issns, String name, String subject, String anchor, String zdbID, String type, HashSet<String> issnsCollection) {
		List<String> issnList = new ArrayList<>();
		List<JournalTitle> journalTitles = new ArrayList<>();
		if (issns.contains(";"))
			issnList = Arrays.asList(issns.split(";"));
		else
			issnList.add(issns);
		for (String issn : issnList) {
			if (!issn.isEmpty() && !issnsCollection.contains(issn)) {
				JournalTitle journalTitle = new JournalTitle();	
				journalTitle.setName(name).setIssn(issn).setSubject(subject).setType(type).setAnchor(anchor).setZDBID(zdbID);
				journalTitles.add(journalTitle);
				issnsCollection.add(issn);
			}
		}
		return journalTitles;
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
