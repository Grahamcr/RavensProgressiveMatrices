package project4;

import java.util.ArrayList;
import java.util.List;

/*************************************************************
 * This class is used to represent an object within a Semantic
 * Network which has been calculated to show the differences 
 * between two Raven's Figures.  A list of SemanticNetworkObjs
 * is equal to a Semantic Network.
 * 
 * @author Craig
 *************************************************************/
public class SemanticNetworkObj {

	/**Name of the object as given in the text file ***/
	private String name;
	
	/**Attributes which belong to this object ***/
	private List<SemanticNetworkAttribute> attributes;
	
	/**boolean if this object exists in the first figure ***/
	private boolean existsNew;
	
	/**boolean if this object exists in the second figure ***/
	private boolean existsOld;
	
	/****************************************************
	 * Default Constructor which is responsible for
	 * instantiating the List
	 ***************************************************/
	public SemanticNetworkObj() {
		this.attributes = new ArrayList<SemanticNetworkAttribute>();
	}
	
	/*****************************************************
	 * Return the count of how many of the attributes in
	 * This SeamnticNetwork have changed
	 * 
	 * @return
	 *****************************************************/
	public int getCount() {
		int total = 0;
		for(SemanticNetworkAttribute rra : attributes) {
			total += rra.getChange() ? 1 : 0;
		}
		return total;
	}
	
	/*****************************************************
	 * Get an attribute from the list by it's name
	 ****************************************************/
	public SemanticNetworkAttribute checkAttrExists(String name) {
		SemanticNetworkAttribute found = null;
		for(SemanticNetworkAttribute attr : this.attributes) {
			if(attr.getName().equals(name)) {
				found = attr;
				break;
			}
		}
		return found;
	}
	
	/*****************************************************
	 * Getters and Setters for Instance Variables
	 ******************************************************/
	public boolean getExistsOld() {
		return this.existsOld;
	}
	public boolean getExistsNew() {
		return this.existsNew;
	}
	public void setExistsNew(boolean exists) {
		this.existsNew = exists;
	}
	public void setExistsOld(boolean exists) {
		this.existsOld = exists;
	}
	public void  setName(String name)  {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void addAttr(SemanticNetworkAttribute attr) {
		this.attributes.add(attr);
	}
	public List<SemanticNetworkAttribute> getList() {
		return this.attributes;
	}
	public List<SemanticNetworkAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<SemanticNetworkAttribute> attributes) {
		this.attributes = attributes;
	}


	@Override
	public String toString() {
		return "[name=" + name + ", attributes="
				+ attributes + ", existsNew=" + existsNew + ", existsOld="
				+ existsOld + "]";
	}
	
	
}
