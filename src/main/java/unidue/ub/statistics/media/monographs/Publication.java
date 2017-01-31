package unidue.ub.statistics.media.monographs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * Representation object of the basic bibliographic information of one document
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class Publication {

	private final static Namespace NSMABXML = Namespace
			.getNamespace("http://www.ddb.de/professionell/mabxml/mabxml-1.xsd");

	private final int LEVENTHRESHOLD = 100;

	private String docNumber;

	private String isbn;

	private ArrayList<String> authors;

	private ArrayList<String> title;

	private String publisher;

	private String place;

	private String year;

	private String type;

	/**
	 * creates a new <code>Publication</code>-object
	 *
	 * @param docNumber
	 *            the document number
	 * @param authors
	 *            the authors of the publication
	 * @param title
	 *            the title of the publication
	 * 
	 */
	public Publication(String docNumber, ArrayList<String> authors, ArrayList<String> title) {
		this.docNumber = docNumber;
		this.authors = authors;
		this.title = title;
		this.type = "basic";
	}

	/**
	 * creates a new <code>Publication</code>-object from a document
	 *
	 * @param document
	 *            the document
	 * 
	 */
	public Publication(Manifestation document) {
		List<Element> fields = document.getMAB().getChildren("feld", NSMABXML);
		ArrayList<String> authorsMAB = new ArrayList<>();
		ArrayList<String> titleMAB = new ArrayList<>();
		for (Element field : fields) {
			String fieldNumber = field.getAttributeValue("nr");
			if (fieldNumber.equals("100")) {
				List<Element> ufs = field.getChildren("uf", NSMABXML);
				for (Element uf : ufs) {
					if (uf.getAttributeValue("code").equals("p"))
						authorsMAB.add(uf.getContent().toString());
				}
			} else if (fieldNumber.equals("104")) {
				List<Element> ufs = field.getChildren("uf", NSMABXML);
				for (Element uf : ufs) {
					if (uf.getAttributeValue("code").equals("p"))
						authorsMAB.add(uf.getContent().toString());
				}
			}
			try {
				int fieldNumberInt = Integer.parseInt(fieldNumber);
				if (fieldNumberInt >= 108 && fieldNumberInt <= 196) {
					List<Element> ufs = field.getChildren("uf", NSMABXML);
					for (Element uf : ufs) {
						if (uf.getAttributeValue("code").equals("p"))
							authorsMAB.add(uf.getContent().toString());
					}
				}
			} catch (Exception e) {
			}
			if (fieldNumber.equals("331")) {
				List<Element> ufs = field.getChildren("uf", NSMABXML);
				for (Element uf : ufs) {
					if (uf.getAttributeValue("code").equals("a"))
						titleMAB.add(uf.getContent().toString());
				}
			} else if (fieldNumber.equals("335")) {
				List<Element> ufs = field.getChildren("uf", NSMABXML);
				for (Element uf : ufs) {
					if (uf.getAttributeValue("code").equals("a"))
						titleMAB.add(uf.getContent().toString());
				}
			}
		}
		this.docNumber = document.getDocNumber();
		this.authors = authorsMAB;
		this.title = titleMAB;
	}

	/**
	 * creates a new <code>Publication</code>-object
	 *
	 * @param docNumber
	 *            the document number
	 * @param isbn
	 *            the isbn of the publication
	 * @param authors
	 *            the authors of the publication
	 * @param title
	 *            the title of the publication
	 * @param publisher
	 *            the publisher of the publication
	 * @param place
	 *            the place where the publication was published
	 * @param year
	 *            the year the publication was published
	 * 
	 */
	public Publication(String docNumber, String isbn, ArrayList<String> authors, ArrayList<String> title,
			String publisher, String place, String year) {
		this.isbn = isbn;
		this.authors = authors;
		this.title = title;
		this.publisher = publisher;
		this.place = place;
		this.year = year;
		this.docNumber = docNumber;
		this.type = "monography";
	}

	// can be extended to different types of publications.

	/**
	 * returns the document number of the publication
	 *
	 * @return docNumber the document number
	 */
	public String getDocNumber() {
		return docNumber;
	}

	/**
	 * returns the type of the publication
	 *
	 * @return type the type of the publication
	 */
	public String getType() {
		return type;
	}

	/**
	 * returns the ISBN of the publication
	 *
	 * @return isbn the ISBN of the publication
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * returns the authors of the publication
	 *
	 * @return authors the authors of the publication
	 */
	public ArrayList<String> getAuthors() {
		return authors;
	}

	/**
	 * returns the title of the publication
	 *
	 * @return title the title of the publication
	 */
	public ArrayList<String> getTitle() {
		return title;
	}

	/**
	 * returns the publisher of the publication
	 *
	 * @return publisher the publisher of the publication
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * returns the place the publication was published
	 *
	 * @return place the place the publication was published
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * returns the year the publication was published
	 *
	 * @return year the year the publication was published
	 */
	public String getYear() {
		return year;
	}

	/**
	 * sets the document number of the publication
	 *
	 * @param newDocNumber
	 *            the document number
	 */
	public void setDocNumber(String newDocNumber) {
		this.docNumber = newDocNumber;
	}

	/**
	 * sets the ISBN of the publication
	 *
	 * @param newIsbn
	 *            the ISBN
	 */
	public void setIsbn(String newIsbn) {
		this.isbn = newIsbn;
	}

	/**
	 * adds an author to the author list
	 *
	 * @param author
	 *            the name of the author
	 */
	public void addAuthor(String author) {
		authors.add(author);
	}

	/**
	 * sets the authors of the publication
	 *
	 * @param newAuthors
	 *            list of author names
	 */
	public void setAuthors(ArrayList<String> newAuthors) {
		this.authors = newAuthors;
	}

	/**
	 * adds a part to the title
	 *
	 * @param titlePart
	 *            part of title to be added
	 */
	public void addTitle(String titlePart) {
		title.add(titlePart);
	}

	/**
	 * sets the title of the publication
	 *
	 * @param newTitle
	 *            the title of the publication
	 */
	public void setTitle(ArrayList<String> newTitle) {
		this.title = newTitle;
	}

	/**
	 * sets the publisher of the publication
	 *
	 * @param newPublisher
	 *            the name of the publisher
	 */
	public void setPublisher(String newPublisher) {
		this.publisher = newPublisher;
	}

	/**
	 * sets the place the publication was published
	 *
	 * @param newPlace
	 *            the name of the place
	 */
	public void setPlace(String newPlace) {
		this.isbn = newPlace;
	}

	/**
	 * sets the year the publication was published
	 *
	 * @param newYear
	 *            the year
	 */
	public void setYear(String newYear) {
		this.isbn = newYear;
	}

	/**
	 * returns the Levenshtein distance comparing the authors and title of two
	 * publications
	 *
	 * @param other
	 *            other <code>Publication</code>-object to be compared to
	 * @return levDist Levenshtein distance
	 */
	public int getLevDist(Publication other) {
		// compares author and title and returns the total Levenhtein distance.
		int authorLeven = 0;
		if (this.getAuthors().size() == other.getAuthors().size()) {
			for (int i = 0; i < authors.size(); i++)
				authorLeven += StringUtils.getLevenshteinDistance(authors.get(i), other.getAuthors().get(i));
		} else {
			authorLeven = LEVENTHRESHOLD;
		}
		int titleLeven = 0;

		// for title comparison take only the first items of the list, in case
		// of different depth of catalogueing with respect to sub-titles.
		for (int i = 0; i < Math.min(title.size(), other.getTitle().size()); i++)
			titleLeven += StringUtils.getLevenshteinDistance(this.getTitle().get(i), other.getTitle().get(i));
		int totalLeven = authorLeven + titleLeven;
		return totalLeven;
	}

}
