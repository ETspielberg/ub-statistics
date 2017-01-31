/**
 * 
 */
package unidue.ub.statistics.media.journal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.jdom2.Element;

/**
 * correlates the anchor of Journals to the order number
 * @author Eike Spielberg
 *
 */
@Entity
public class AnchorOrder {
	
	@Id
    @GeneratedValue
    private long id;
	
	private String anchor;
	
	private String orderNumber;
	
	private String name;
	
	private String runtime; 
	
	public AnchorOrder() {
		anchor = "";
		orderNumber="";
		name="";
		runtime= "";
	}

	/**
	 * returns the anchor of the collection
	 * @return the anchor
	 */
	public String getAnchor() {
		return anchor;
	}

	/**
	 * sets the anchor of the collection
	 * @param anchor the anchor to set
	 * @return the updated object
	 */
	public AnchorOrder setAnchor(String anchor) {
		this.anchor = anchor;
		return this;
	}

	/**
	 * returns the internal order number of the collection
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * sets the internal order number of the collection
	 * @param orderNumber the orderNumber to set
	 * @return the updated object
	 */
	public AnchorOrder setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
		return this;
	}

	/**
	 * returns the name of the collection
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of the collection
	 * @param name the name to set
	 * @return the updated object
	 */
	public AnchorOrder setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * returns the time range covered by this collection
	 * @return the runtime
	 */
	public String getRuntime() {
		return runtime;
	}

	/**
	 * sets the time range covered by this collection
	 * @param runtime the runtime to set
	 * @return the updated object
	 */
	public AnchorOrder setRuntime(String runtime) {
		this.runtime = runtime;
		return this;
	}
	
	/**
	 * adds the analysis org.jdom2.element to the desired parent element
	 * 
	 * @param parent
	 *            the parent org.jdom2.element the analysis shall be added to
	 */
	public void addToOutput(Element parent) {
		Element anchorOrder = new Element("anchorOrder");
		anchorOrder.addContent(new Element("id").setText(String.valueOf(id)));
		anchorOrder.addContent(new Element("name").setText(name));
		anchorOrder.addContent(new Element("anchor").setText(anchor));
		anchorOrder.addContent(new Element("runtime").setText(runtime));
		anchorOrder.addContent(new Element("orderNumber").setText(orderNumber));
		parent.addContent(anchorOrder);
	}

}
