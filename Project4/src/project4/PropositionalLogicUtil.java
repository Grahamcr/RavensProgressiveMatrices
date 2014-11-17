package project4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**********************************************************************
 * The Propositional Logic Util is responsible for finding patterns
 * within rows or columns which should be applied to the answer as well.
 * For example if a row/column always has a square, circle and pac-man,
 * and the last row of the Matrix has a pac-man and square then we know
 * that the answer should contain a circle. 
 * 
 * @author Craig Graham
 ***********************************************************************/
public class PropositionalLogicUtil {

	/*******************************************************************
	 * Default Constructor
	 * 
	 *******************************************************************/
	public PropositionalLogicUtil() {
		
	}
	/*********************************************************************
	 * See if the same object changes the attribute the same way three times, 
	 * if it does we have found a pattern
	 * 
	 * In Logic terms (In(Figure1, ObjectA) && ChangeAttr(ObjectA)) && 
	 *				  (In(Figure2, ObjectA) && ChangeAttr(ObjectA)) &&
	 *			 	  (In(Figure3, ObjectA) && ChangeAttr(ObjectA))
	 *
	 * @param one - Frame representation of the the first figure
	 * @param two - Frame representation of the the second figure
	 * @param three - Frame representation of the the third figure
	 * @param attrName - String name of the attribute to see if a pattern
	 * 					exists for
	 * @return Pattern - a representation of the pattern for a object
	 *********************************************************************/
	public Pattern findThreeShapePattern(HashMap<String, HashMap<String, String>> one, 
			HashMap<String, HashMap<String, String>> two, HashMap<String, HashMap<String, String>> three,
			String attrName) {
		List<String> toReturn = null;
		String objectName = "";
		//Go through the first figure and get each object by name
		for(String objName : one.keySet()) {
			
			//Get the attribute values for the object in each figure in the row/col
			HashMap<String, String> fig1ObjValues = one.get(objName);
			HashMap<String, String> fig2ObjValues = two.get(objName);
			HashMap<String, String> fig3ObjValues = three.get(objName);
			
			//Get the value of the attribute, if it exists
			String shape1 = getAttribute(fig1ObjValues, attrName);
			String shape2 = getAttribute(fig2ObjValues, attrName);
			String shape3 = getAttribute(fig3ObjValues, attrName);
			
			//Check to see if the attribute was found for each object, otherwise this wont' work...
			if(shape1 != null && shape2 != null && shape3 != null) {
				
				//See if they are all different, we will check if they are all different in the 
				//same way in a different step
				if((!shape1.equals(shape2) && !shape1.equals(shape3) && !shape2.equals(shape3)) ||
					(shape1.equals(shape2) && shape1.equals(shape3) && shape2.equals(shape3))) {
					toReturn = new ArrayList<String>();
					toReturn.add(shape1);
					toReturn.add(shape2);
					toReturn.add(shape3);
					objectName = objName;
					break;
				}
			}
		}
		
		return new Pattern(objectName, toReturn);
	}
	/***********************************************************************************
	 * At this point the agent has idetnitied that a pattern for shapes does exist
	 * within columns and rows, but now it needs to find out which shape is missing
	 * from the col/row the possible answer belongs in
	 * 
	 * @param one - Frame representation of the the first figure
	 * @param two - Frame representation of the the second figure
	 * @param pattern - Pattern: a representation of the pattern for a object
	 * @return String: The missing value in the pattern for the row/column
	 **********************************************************************************/
	public String findMissingPatternValue(HashMap<String, HashMap<String, String>> one, 
			HashMap<String, HashMap<String, String>> two, Pattern pattern, String attribute) {
		
		String toReturn = null;
		String attr1 = null;
		String attr2 = null;
	
		//Get the attribute values for the two objects in the row/col
		HashMap<String, String> fig1ObjValues = one.get(pattern.getObjectName());
		HashMap<String, String> fig2ObjValues = two.get(pattern.getObjectName());
		
		//Get the value of the attribute, if it exists
		attr1 = getAttribute(fig1ObjValues, attribute);
		attr2 = getAttribute(fig2ObjValues, attribute);

		//Determine which attribute in the pattern is missing for the given row/col
		toReturn = removeKnownObjects(attr1, attr2, pattern.getPattern());
		
		return toReturn;
	}
	
	/*******************************************************************************
	 * Temporarily modify the array to figure out which attribute is missing
	 * 
	 * @param one - Frame representation of the the first figure
	 * @param two - Frame representation of the the second figure
	 * @param pattern - Pattern: a representation of the pattern for a object
	 * @return String: The missing value in the pattern for the row/column
	 ********************************************************************************/
	private String removeKnownObjects(String one, String two, List<String> pattern) {
		String toReturn = null;
		pattern.remove(one);
		pattern.remove(two);
		if(pattern.size() == 1) {
			toReturn = pattern.get(0);
		}
		pattern.add(one);
		pattern.add(two);
		return toReturn;
	}
	
	/*******************************************************************************
	 * Helper Method to get the attribute by name from the hashmap
	 * 
	 * @param attrValues - HashMap of attribute values
	 * @param name - name of the attribute we are looking for
	 * 
	 * @return - String value of the attribute or "none" if not found
	 ********************************************************************************/
	private String getAttribute(HashMap<String, String> attrValues, String name) {
		String value = attrValues != null ? attrValues.get(name) : null;
		return value == null ? "none" : value;
	}
	
	/*******************************************************************************
	 * Check to see if the Object has the given attribute
	 * 
	 * @param nextValues - Frame representation of the object to check
	 * @param missingAttr - The attribute value
	 * @param name  - The Attribute name
	 * @return - boolean if the object has the attribute or not
	 ********************************************************************************/
	public boolean checkHasAttr(
			HashMap<String, HashMap<String, String>> nextValues,
			String missingAttr, String name) {
		boolean toReturn = false;
		//Go through the first figure and get each object by name
		for(String objName : nextValues.keySet()) {
			
			//Get the attribute values for the two objects in the row/col
			HashMap<String, String> fig1ObjValues = nextValues.get(objName);
		
			String shape1 = getAttribute(fig1ObjValues, name);
			
			if(shape1.equals(missingAttr)) {
				toReturn = true;
				break;
			}
		}
			
		return toReturn;
	}
}
