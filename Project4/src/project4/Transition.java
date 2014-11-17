package project4;

import java.util.HashMap;
import java.util.Map;

/*************************************************************
 * This class is represents the changes a Frame undergoes from
 * one figure to another by keeping track of any modifications
 * which are made to fillers and their values
 * 
 * @author Craig
 *************************************************************/
public class Transition {

	/*** Map of frame names and the change it underwent */
	Map<String, Change> changeMap;
	
	/*** The frame/object this transition is mapping*/
	String objectId;
	
	/*************************************************************
	 * Constructor 
	 *@param objectId - the unique id of the object/Frame this
	 * 					transition represents
	 *************************************************************/
	public Transition(String objectId) {
		
		changeMap = new HashMap<String, Change>();
		this.objectId = objectId;
	}
	
	/*****************************************************************
	* Capture the change that one of the fillers underwent in this
	* Frame during the transition from one frame to the next
	*
	*@param name - the name of the filler
	*@param type - the type of change that occurred (See Change Constants)
	*@param oldValue - the previous value of the filler
	*@param newValue - the new value of the filler
	******************************************************************/
	public void setChange(String name, String type, String oldValue, String newValue) {
		changeMap.put(name, new Change(name, type, oldValue, newValue));
	}
	
	/******************************************************************
	* Determine if an attribute/filler by the given name exists in the
	* map of changes for this transition
	*
	*@param attrName - the name of the attribute/filler
	*@return boolean - if the name exists or not
	******************************************************************/
	public boolean checkAttrExists(String attrName) {
		boolean toReturn = false;
		for(String name : changeMap.keySet()) {
			if(attrName.equals(name)) {
				toReturn = true;
				break;
			}
		}
		return toReturn;
	}
	/******************************************************************
	* Determine if the given object/Frame was deleted during the 
	* transition.
	*
	*@param objectId - the unique id of the object
	*@return boolean - if the object has been deleted in the transition.
	********************************************************************/
	public boolean isObjectDeleted(String objectId) {
		boolean toReturn = false;
		Change change = null;
		if((change = changeMap.get(objectId)) != null) {
			if(change.getChange().equals(Change.OBJDELETED)) {
				toReturn = true;
			}
		}
		return toReturn;
	}

	/*****************************************************************
	* Getters and Setters
	******************************************************************/
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public Change getValue(String name) {
		return changeMap.get(name);
	}
	
	public Map<String, Change> getChanges() {
		return this.changeMap;
	}
}
