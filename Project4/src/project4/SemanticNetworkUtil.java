package project4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**************************************************************************
 * This Utility is capable of completing calculations using Semantic Networks
 * and Generate & Test. The agent will use this utility to calculate the
 * Semantic Network for the differences between two frames in a Matrix.
 * Then, the agent can ask this utility to compare those two Semantic
 * Networks to determine how similar they are using a weighted scale
 *   
 * @author Craig Graham
 ***************************************************************************/
public class SemanticNetworkUtil {


    /**********************************************************************************************
     * Helper method to convert a Map which represents a RavenFigure object to a SemanticNetwork
     * which will also represent the same RavenFigure object.
	 * 
     * @param values - The HashMap which will be converted to the SemeanticNetwork
     * 
     * @return List<SemanticNetwork> - The SemanticNetwork which is equal to the Map representation
     ***********************************************************************************************/
    public List<SemanticNetworkObj> convertMapToNetwork(HashMap<String, HashMap<String, String>> values) {
    	List<SemanticNetworkObj> toReturn = new ArrayList<SemanticNetworkObj>();
		
		//Step through each of the objects in the map
    	for(String objectName : values.keySet()) {
		
    		HashMap<String, String> network = values.get(objectName);
    		SemanticNetworkObj obj = new SemanticNetworkObj();
    		obj.setName(objectName);
			
			//Capture each of the attributes of the object in the Semantic Network
    		List<SemanticNetworkAttribute> attributes = new ArrayList<SemanticNetworkAttribute>();
    		for(String attributeVal : network.keySet()) {
    			SemanticNetworkAttribute attr = new SemanticNetworkAttribute();
    			attr.setName(attributeVal);
    			attr.setNewVal( network.get(attributeVal));
    			attributes.add(attr);
    		}
			
			//Set the object's calculated attributes
    		obj.setAttributes(attributes);
    		toReturn.add(obj);
    	}
		
    	return toReturn;
    }
    
    /****************************************************************
     * Helper method used to see if the differences (Semantic Network)
     * between a set of two figures is similar/the same as the differences 
     * (Semantic Network) that exist between a second set of two figures.
     * For example the Semantic Network of figures A & B is the same
     * as the Semantic Network for figures C & 1.
     * 
     * @param baseline - The Semantic Network between the 
     * 					 objects in Figure A and Figure B
     * @param test - The Semantic Network between the objects in
     * 				 objects in Figure C and possible answer "i"
     * @return - integer: The score given to the possible answer where
     * 			          the higher the score the closer the Semantic Networks
     * 					  between A&B/C&i are considered to be 
     ******************************************************************/
    public int compareDifference(List<SemanticNetworkObj> baseline, List<SemanticNetworkObj> test)  {
		
		//TODO: Map the objects to one another    	

		
    	int toReturn = 0;
    	List<String> alreadyTested = new ArrayList<String>();
    	//Start off with the simple test if the amount of changes is the same
    	if(baseline.size() == test.size())  {
    		
    		//Next start comparing each of the objects and seeing if the differences
    		//between the starting figure and the resulting figure are similar for both sets 
    		//of figures
    		for(int i = 0; i < baseline.size(); i++) {
    			SemanticNetworkObj baseObj = baseline.get(i);
    			SemanticNetworkObj testObj = getObjectByName(baseObj.getName(), test);
    			alreadyTested.add(baseObj.getName());
    			if(testObj != null) {
    				toReturn += scoreDifference(baseObj, testObj);
    			}else {
    				//The object doesn't exist in the test figure
    				toReturn -= 1;
    			}
    		}
    		
    		for(int i = 0; i < test.size(); i++) {
    			SemanticNetworkObj testObj = test.get(i);
    			if(!alreadyTested.contains(testObj.getName())) {
    				alreadyTested.add(testObj.getName());
	    			SemanticNetworkObj baseObj = getObjectByName(testObj.getName(), baseline);
	    			if(baseObj != null) {
	    				toReturn += scoreDifference(baseObj, testObj);
	    			}else {
	    				//The object doesn't exist in the baseline figure
	    				toReturn -= 1;
	    			}
    			}
    		}
    	}else {
    		toReturn = -1;
    	}
    	
    	return toReturn;
    }
    /**********************************************************
     * Compare the Semantic Networks of the two Figure Sets
     * and determine how similar the differences between them
     * are.
     * 
     * @param baseObj - Semantic Network for the question prompt
     * @param testObj - Semantic Network for the possible answer
     * 
     * @return int - the weighted score for how similar the
     * 				  two argument networks are.
     **********************************************************/
    private int scoreDifference(SemanticNetworkObj baseObj, SemanticNetworkObj testObj) {
    	int toReturn = 0;
    	//Test the object consistency between figures for both sets
		toReturn += baseObj.getExistsNew() == testObj.getExistsNew() ? 1 : -1;
		toReturn += baseObj.getExistsOld() == testObj.getExistsOld() ? 1 : -1;
	
		//Now start comparing attributes, by first acquiring the list of them
		List<SemanticNetworkAttribute> baseAttrs = baseObj.getList();
		List<SemanticNetworkAttribute> testAttrs = testObj.getList();
		
		//Now the fun part....Start comparing the changes between each different attribute
		//Start by keeping track of the attributes tested so far
		List<String> testedAttr = new ArrayList<String>();
		
		//Next see if there are the same amount attribute changes for each set
		if(baseAttrs.size() == testAttrs.size()) {
			toReturn += 1;
		}else {
			toReturn -= 1;
		}
		
		//Step through each of the attributes in the figures A&B.
		toReturn += scoreObjAttributes(baseAttrs, testAttrs, testedAttr);
		
		//Step through each of the attributes in the figures C&i.
		toReturn += scoreObjAttributes(testAttrs, baseAttrs, testedAttr);
			
		// Test object fill similarities
		toReturn += scoreObjectFill(testAttrs, baseAttrs);
		
    	return toReturn;
    }
    
    /**************************************************************************
     * A helper method to see how similar attributes with multiple values 
     * are to one another.  This method tests how many of the values the
     * two attribute value lists share and returns the score. 
     * 
     * @param base - SemanticNetworkAttribute from the Question Example 
     * 				 Semantic Network
     * @param test - SemanticNetworkAttribute from the Question Prompt 
     * 				 Semantic Network
     * 
     * @return int - similarity score for the two attributes 
     ***************************************************************************/
    private int scoreMultipleValueAttr(SemanticNetworkAttribute base, SemanticNetworkAttribute test) {
    	int toReturn = 0;
    	//If the attribute has multiple values, split and count
		String[] baseNew = base.getNewVal().split(",");
		String[] baseOld = base.getOldVal().split(",");
		String[] testNew = test.getNewVal().split(",");
		String[] testOld = test.getOldVal().split(",");
		
		//Determine which list is longer for the new and old value of the attribute
    	String[] longerNew = baseNew.length > testNew.length ? baseNew : testNew;
    	String[] longerOld = baseOld.length > testOld.length ? baseOld : testOld;
    	ArrayList<String> shorterNew = baseNew.length > testNew.length ?  new ArrayList<String>(Arrays.asList(testNew)) :  new ArrayList<String>(Arrays.asList(baseNew));
    	ArrayList<String> shorterOld = baseOld.length > testOld.length ? new ArrayList<String>(Arrays.asList(testOld)) : new ArrayList<String>(Arrays.asList(baseOld));
    	
    	
    	//Step through each value in the longer list and see if 
    	//it exists in the shorter list
    	for(String next : longerNew) {
    		if(shorterNew.contains(next)) {
    			toReturn += 5;
    		}
    	}
    	for(String next : longerOld) {
    		if(shorterOld.contains(next)) {
    			toReturn += 5;
    		}
    	}
    	
    	return toReturn;
    	
    }
    /**********************************************************************************
     * More of a "Custom" helper method, this function is responsible for the specific
     * task of testing it the objects are filled according to the same pattern.
     * For example if the first Semantic Network increased it's fill by 50%, so 
     * should the answer network.
     * 
     * @param testAttrs - List of SemanticNetowrkAttributes from the object in the 
     * 					  possible answer Semantic Network
     * @param testAttrs - List of SemanticNetowrkAttributes from the object in the 
     * 					  question example Semantic Network
     * @return score - weight int score given to the simularity that exists between
     * 					the two objects
     ************************************************************************************/
    private int scoreObjectFill(List<SemanticNetworkAttribute> testAttrs, List<SemanticNetworkAttribute> baseAttrs) {
    	
    	//Local Storage Variables
    	int toReturn = 0;
    	double baseChange = 0.0;
    	double testChange = 0.0;
    	
    	//Get the fill attributes for both of the objects
    	SemanticNetworkAttribute baseFill = getAttrByName(baseAttrs, "fill");
    	SemanticNetworkAttribute testFill = getAttrByName(testAttrs, "fill");
    	
    	//If the object doesn't have fill, the result will be null so we need a "fake" attribute
    	testFill = testFill == null ? new SemanticNetworkAttribute("fill", "no", "no", false, 0) : testFill;
    	baseFill = baseFill == null ?  new SemanticNetworkAttribute("fill", "no", "no", false, 0) : baseFill;
    	
    	//If the attribute has multiple values, split and count
		String[] baseNewSplit = baseFill.getNewVal().split(",");
		String[] baseOldSplit = baseFill.getOldVal().split(",");
		int baseOldChange = getFillCount(baseOldSplit);
		int baseNewChange = getFillCount(baseNewSplit);
		
		//Check to see how the example semantic network object's fill attribute changed
		if(baseNewChange == 0)  {
			baseChange = baseOldChange;
		}else if(baseOldChange == 0) {
			baseChange = baseNewChange;
		}else {
			baseChange = ((double) (baseNewChange / baseOldChange));
			if(baseChange > 1) {
				baseChange -= 1;
			}
			baseChange = baseChange * 100;
		}
		
		//If the attribute has multiple values, split and count
		String[] testNewSplit = testFill.getNewVal().split(",");
		String[] testOldSplit = testFill.getOldVal().split(",");
		int testOldChange = getFillCount(testOldSplit);
		int testNewChange = getFillCount(testNewSplit);
		
		//Check to see how the possible answer semantic network object's fill attribute changed
		if(testNewChange == 0)  {
			testChange = testOldChange;
		}else if(testOldChange == 0) {
			testChange = testNewChange;
		}else {
			testChange = ((double) testNewChange / testOldChange);
			if(testChange > 1) {
				testChange -= 1;
			}
			testChange = testChange * 100;
		}
		
		
		//Compare the changes between the fill attribute of the objects 
		//within the two semantic networks
		if(baseChange == testChange) {
			toReturn += 10;
		}
		if(baseOldChange == testOldChange) {
			toReturn += 1;
		}
		if(baseNewChange == testNewChange) {
			toReturn += 1;
		}
    	    	
    	return toReturn;
    	
    }
    
    /****************************************************************************
     * Given two RavenFigure objects determine the differences that exist between
     * the objects in each figure.  The result is a list of "Result" objects 
     * where each object represents the differences that object occurred as it
     * transitioned from figureA to figureB. 
     * 
     * @param figureA - First Raven's Figure object to compare
     * @param figureB - Second Raven's Figure object to compare
     * 
     * @return - List<SemanticNetwork>: List of the differences each object has
     * 									 between FigureA & FigureB
     *****************************************************************************/
    public List<SemanticNetworkObj> calculateDifference(HashMap<String, HashMap<String, String>> figureAValues,
    		HashMap<String, HashMap<String, String>> figureBValues) {
    	
    	//List of differences to return
    	List<SemanticNetworkObj> toReturn = new ArrayList<SemanticNetworkObj>();
    	    	
    	//Step through each object in the first figure and compare it's attributes to
    	//those in the second figure
    	for(String objectName : figureAValues.keySet()) {
    		
    		//Get the attributes and their values for this object in each figure
    		HashMap<String, String> firstFigObjs = figureAValues.get(objectName);
    		HashMap<String, String> secondFigObjs = figureBValues.get(objectName);
    		
    		//See if the second figure has the object found to exist in the first figure
    		if(secondFigObjs != null) {
    			
    			//Create a Result object to hold the difference between the two objects
	    		SemanticNetworkObj resultObj = new SemanticNetworkObj();
	    		
	    		//Set what we know so far, that it exists in both and it's name
	    		resultObj.setExistsNew(true);
	    		resultObj.setExistsOld(true);
	    		resultObj.setName(objectName);
	    		
	    		//Step through each attribute the object in the first figure has and
	    		//compare the attribute values to the same object in the second figure
	    		if(firstFigObjs != null) {
		    		for(String attrName : firstFigObjs.keySet()) {
		    			
		    			//Create a Result Attribute object to hold the differences found
		    			SemanticNetworkAttribute rra = new SemanticNetworkAttribute();
		    			
		    			//Set the name of the attribute
		    			rra.setName(attrName);
		    			
		    			//Capture the value this attribute has in the first figure
		    			rra.setOldVal(firstFigObjs.get(attrName));
		    			
		    			//Set the initial value of the change between the two figures
		    			boolean change = false;
		    			
		    			//Check to see if the object in the second figure has the same attribute
		    			if(secondFigObjs.get(attrName) != null) {
		    				
		    				//Capture the value the attribute has in the second figure
			    			rra.setNewVal(secondFigObjs.get(attrName));
			    			
			    			//Calculate the change in the attribute from the first figure to the second
			    			change = !(rra.getOldVal().equals(rra.getNewVal()));
			    			
			    			//Safely Calculate the percentage the attribute change (If applicable)
			    			try {
			    				if(Integer.valueOf(rra.getNewVal()) != 0 && Integer.valueOf(rra.getOldVal()) !=0 ) {
			    					rra.setPercent(Integer.valueOf(rra.getOldVal())/Integer.valueOf(rra.getNewVal()));
			    				}
			    			}catch(NumberFormatException e) {
			    				//nothing to see here... just playing it safe
			    			}catch(ArithmeticException e) {
			    				//Ok, it is strange if we get here... try it the other way? (This is dangerous) 
			    				rra.setPercent(Integer.valueOf(rra.getNewVal())/Integer.valueOf(rra.getOldVal()));
			    			}
		    			}else {
		    				
		    				//Attribute does not exist in the new figure
		    				rra.setNewVal("DNE");
		    			}
		    			rra.setChange(change);
		    			resultObj.addAttr(rra);
		    		}
	    		}
	    		
	    		//Step through the attributes for this object in the second
	    		//figure to see if it has attributes the object in the first
	    		//figure does not have
	    		for(String oldAttrName : secondFigObjs.keySet()) {
	    			
	    			//Check to see if the attribute has already been captured
	    			if(resultObj.checkAttrExists(oldAttrName) == null) {
	    				
	    				//Create a "dummy"entry to keep track of this new attribute
	    				SemanticNetworkAttribute oldrra = new SemanticNetworkAttribute();
	    				oldrra.setName(oldAttrName);
	    				oldrra.setOldVal("DNE");
	    				oldrra.setNewVal(secondFigObjs.get(oldAttrName));
	    				oldrra.setChange(false);
	    				resultObj.addAttr(oldrra);
	    			}
	    		}
	    		
	    		//Add the dummy object to the list
	    		toReturn.add(resultObj);
	    		
	    	//If the object doesn't exist, then create a dummy object
	    	//to represent an object which is in the first figure, but
	    	//does not exist in the second
	    	}else {
	    		SemanticNetworkObj obj = new SemanticNetworkObj();
	    			obj.setName(objectName);
	    			obj.setExistsNew(false);
	    			obj.setExistsOld(true);
	    			toReturn.add(obj);
	    	}
    	}
    	
    	//Step through the objects in the second figure to see if
		//any of those objects do not appear in the first figure
    	for(String objectNameOld : figureBValues.keySet()) {
    		
    		//See if the object in the second figure is also in the first
    		HashMap<String, String> figureAObjVals = figureAValues.get(objectNameOld);
    		
    		//If the object in the second is not in the first, create a dummy object
    		if(figureAObjVals == null) {
    			
    			//Create the dummy object and it to the list
	    		SemanticNetworkObj resultObj = new SemanticNetworkObj();
	    		resultObj.setExistsNew(true);
	    		resultObj.setExistsOld(false);
	    		resultObj.setName(objectNameOld);
	    		toReturn.add(resultObj);
    		}
    	}
    	
    	return toReturn;
	}
    
    /***********************************************************
     * This method serves as the main workhorse of the 
     * comparing function.  It is responsible for checking if the
     * calculated difference between attributes is the same for the
     * example network as it is for the possible answer network.
     * 
     * @param baseAttrs - List<SemanticNetworkAttribute>: the attribute
     * 					  differences found in the example network
     * 					  of figures.
     * @param testAttrs - List<SemanticNetworkAttribute>: the attribute
     * 					  differences found in the prompt/answer network
     * 					  of figures.
     * 
     * @param testedAttr - Attributes previously tested for the 
     * 					   current object
     * 
     * @return int - The "likeness" score given to the two networks
     * 				 of attributes.
     ***************************************************************/
    private int scoreObjAttributes(List<SemanticNetworkAttribute> baseAttrs, List<SemanticNetworkAttribute> testAttrs, List<String> testedAttr) {
    			
    	int toReturn = 0;
    	//Step through each of the attributes in the figures C&i.
		for(int q = 0; q < testAttrs.size(); q++) {
			try {
				
				//Get the next attribute from the possible answer difference set
				SemanticNetworkAttribute testAttrFT = testAttrs.get(q);
				
				//Only test this attribute if we haven't already done so to this point
				if(!testedAttr.contains(testAttrFT.getName())) { 
					
					//Get the same attribute in the difference set for Figures A&B
					SemanticNetworkAttribute baseAttrFT = getAttrByName(baseAttrs, testAttrFT.getName());
					
					//Add this to the list of tested attributes to avoid double testing
					testedAttr.add(testAttrFT.getName());
					
					//If the attribute doesn't exist for both objects in each difference
					//set, dock points
					if(testAttrFT == null || baseAttrFT == null) {
						toReturn -= 1;
					}
					//Check to see if the change between attributes is the same for
					//each set of two figures
					else {
						
						//Test if both Semantic Networks have the attribute changing value
						if(testAttrFT.getChange() == baseAttrFT.getChange()) {
							toReturn += 5;
						}
												
						//Test the similarities between attributes with multiple values
						toReturn += scoreMultipleValueAttr(baseAttrFT, testAttrFT);
					}
				}
				
			//If the objects have a different amount of attributes, dock points
			}catch(IndexOutOfBoundsException ex2) {
				toReturn -=1;
			}
		}
		
		return toReturn;
    }
    /*********************************************************************
     * Helper method to get a SemanticNetwork based on its name 
     * from a List of Objects
     * 
     * @param name - String: Name of the Object
     * @param objs - List<SemanticNetwork>: 	List of objects to search
     * 
     * @return- SemanticNetwork: value is null if the network is not found.
     ***********************************************************************/
    private SemanticNetworkObj getObjectByName(String name, List<SemanticNetworkObj> objs) {
    	SemanticNetworkObj toReturn = null;
    	for(SemanticNetworkObj obj : objs) {
    		if(obj.getName().equals(name)) {
    			toReturn = obj;
    			break;
    		}
    	}
    	return toReturn;
    	
    }
    /**********************************************************
     * Helper method used to get the attribute with the
     *  name provided in the provided list of attributes.
     *  
     * @param attrs - List of attributes to search
     * @param name - The name of the attribute to find
     * @return - SemanticNetworkAttribute: null if not found
     **********************************************************/
    private SemanticNetworkAttribute getAttrByName(List<SemanticNetworkAttribute> attrs, String name) {
    	
    	//Object to return after search completes 
    	SemanticNetworkAttribute toReturn = null;
    	
    	//Step through the provided list until the attribute object requested is found
    	for(SemanticNetworkAttribute rra : attrs) {
    		if(rra.getName() != null && rra.getName().equals(name))  {
    			toReturn = rra;
    			break;
    		}
    	}
    	return toReturn;
    }
    /*************************************************************************
     * Based on the string values given, determine how much of the object
     * is filled in.
     * 
     * @param values - String[]: value of the "fill" attribute
     * 
     * @return int: about in percent
     *************************************************************************/
    private int getFillCount(String[] values)  {
    	int toReturn = 0;
    	for(String value : values) {
    		if(value.contains("half")) {
    			toReturn += 50;
    		}else if(value.equals("no")) {
    			toReturn = 0;
    			break;
    		}else if(value.equals("yes")) {
    			toReturn = 5;
    			break;
    		}else if(value.contains("right") || value.contains("left")){
    			toReturn += 2;
    		}
    	}
    	return toReturn;
    }
}
