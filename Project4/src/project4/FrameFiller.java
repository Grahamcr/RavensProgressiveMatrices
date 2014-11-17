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
public class FrameFiller {

	/**Name of the object as given in the text file ***/
	private String name;
	
	/**Attributes which belong to this object ***/
	private List<FillerValue> attributes;
	

	/****************************************************
	 * Default Constructor which is responsible for
	 * instantiating the List
	 ***************************************************/
	public FrameFiller() {
		this.attributes = new ArrayList<FillerValue>();
	}
	

	/*****************************************************
	 * Get an attribute from the list by it's name
	 ****************************************************/
	public FillerValue checkAttrExists(String name) {
		FillerValue found = null;
		for(FillerValue attr : this.attributes) {
			if(attr.getName().equals(name)) {
				found = attr;
				break;
			}
		}
		return found;
	}

	/**********************************************************
	 * This method depends on pass-by reference 
	 * @param name
	 * @param newValue
	 **********************************************************/
	public void changeValue(String name, String newValue) {
		for(FillerValue value : attributes) {
			if(value.getName().equals(name)) {
				value.setVal(newValue);
				break;
			}
		}
	}
	
	/***********************************************************
	 * 
	 * @param name
	 ************************************************************/
	public void remove(String name) {
		FillerValue value = checkAttrExists(name);
		this.attributes.remove(value);
	}
	
	
	/*****************************************************
	 * Getters and Setters for Instance Variables
	 ******************************************************/
	public void  setName(String name)  {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void addAttr(FillerValue attr) {
		this.attributes.add(attr);
	}
	public List<FillerValue> getList() {
		return this.attributes;
	}
	public List<FillerValue> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<FillerValue> attributes) {
		this.attributes = attributes;
	}


	@Override
	public String toString() {
		return "[name=" + name + ", attributes=" + attributes + "]";
	}

	
}
