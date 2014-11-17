package project4;

/*******************************************************************************
 * The Change class is used to represent the change in a filler's value as it
 * transitions from one frame to the next.
 *
 *@author Craig Graham
 *******************************************************************************/
public class Change {

	/***Name of the filler that changed*/
	private String name;
	
	/*** The type of the change */
	private String change;
	
	/*** Old value of the filler in the first frame */
	private String oldValue;
	
	/*** New value of the filler in the second frame */
	private String newValue;
	
	/*** Change type constants */
	final static String NOCHANGE = "NoChange";
	final static String DELETED = "Deleted";
	final static String CHANGE = "Change";
	final static String OBJDELETED = "ObjectDeleted";
	
	/*********************************************************************************
	* Constructor for the class that requires values for all instance variables
	*********************************************************************************/
	public Change(String name, String change, String oldValue, String newValue) {
		this.name = name;
		this.change = change;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	/*******************************************************************************
	 * Getters and Setters for the class instance variables
	 *******************************************************************************/
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChange() {
		return change;
	}
	public void setChange(String change) {
		this.change = change;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	
	
}
