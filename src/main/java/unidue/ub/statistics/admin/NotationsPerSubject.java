package unidue.ub.statistics.admin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Plain old java object holding a subject and the corresponding description and range of notation.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@Entity
public class NotationsPerSubject {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Lob
    private String description;

    private String notations;
    
	private String subjectID;
	
	/**
     * general constructor
     * 
     */
    public NotationsPerSubject() {
    }
	
	/**
	 * returns the subject ID
	 * 
	 * @return subjectID the Id of the subject represented by the range of notations
	 */
	public String getSubjectID() {
		return subjectID;
	}

	/**
	 * sets the subject ID
	 * 
	 * @param subjectID the Id of the subject represented by the range of notations
	 * @return NotationsPerSubject the updated <code>NotationsPerSubject</code>-object
	 */
	public NotationsPerSubject setSubjectID(String subjectID) {
		this.subjectID = subjectID;
		return this;
	}

	/**
	 * returns the description
	 * 
	 * @return description the description of the range of notations
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * sets the description
	 * 
	 * @param description the description of the range of notations
	 * @return NotationsPerSubject the updated <code>NotationsPerSubject</code>-object
	 */
	public NotationsPerSubject setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * returns the notations
	 * 
	 * @return notations the range of notations
	 */
	public String getNotations() {
		return notations;
	}

	/**
	 * sets the notations
	 * 
	 * @param notations the range of notations
	 * @return NotationsPerSubject the updated <code>NotationsPerSubject</code>-object
	 */
	public NotationsPerSubject setNotations(String notations) {
		this.notations = notations;
		return this;
	}

	

}
