package project4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import project4.SemanticNetworkAttribute;
import project4.SemanticNetworkObj;
import project4.RavensAttribute;
import project4.RavensObject;
import project4.RavensFigure;
import project4.FillerValue;
import project4.FrameFiller;


/*******************************************************************************
 * MatchUtil is a Service class which is responsible for doing most of the 
 * calculations required when matching Objects in one Frame to the next.  
 * 
 * @author Craig Graham
 *******************************************************************************/
public class MatchUtil {

	/*** Constant to mark when an object in a frame is deleted */
	private static final String DELETEDOBJECT = "DELETED";

	/*******************************************************************************
	 * Default empty constructor. 
	 * 
	 *******************************************************************************/
	public MatchUtil() {
		
	}
    
    /**************************************************************************************************
     * Helper method to parse out all of the objects and it's  attributes into a HashMap for 
	 * easier comparison.  This is an overloaded method.
     * 
     * @param figure - The SemanticNetwork object to parse out
     * @return HashMap: Where the Key is the String Name of the objects in the SemanticNetwork and the values
     * 					is another HashMap where the String key	is the name of a Attribute and the value
     * 					is the value of the HashMap.
     ***********************************************************************************************/
    public HashMap<String, HashMap<String, String>> getValuesMap(List<SemanticNetworkObj> figure) {
    	HashMap<String, HashMap<String, String>> toReturn = new HashMap<String, HashMap<String, String>>();
		
		//Step through each object and pull it's attributes and their values
    	for(SemanticNetworkObj obj : figure)  {
    		String name = obj.getName();
    		HashMap<String, String> valueMap = new HashMap<String, String>();
    		for(SemanticNetworkAttribute attr : obj.getAttributes()) {
    			valueMap.put(attr.getName(), attr.getNewVal());
    		}
    		toReturn.put(name, valueMap);  		
    	}
    	return toReturn;
    }
    /**************************************************************************************************
     * Helper method to parse out all of the objects and it's attributes into a HashMap for easier 
	 * comparison.  This is an overloaded method.
     * 
     * @param figure - The Frame object to parse out
     * @return HashMap: Where the Key is the String Name of the objects in the Frame and the values
     * 					is another HashMap where the String key	is the name of a Attribute and the value
     * 					is the value of the HashMap.
     ***********************************************************************************************/
    public HashMap<String, HashMap<String, String>> getValuesMap(Frame figure) {
    	HashMap<String, HashMap<String, String>> toReturn = new HashMap<String, HashMap<String, String>>();
    	for(FrameFiller obj : figure.getFillers())  {
    		String name = obj.getName();
    		HashMap<String, String> valueMap = new HashMap<String, String>();
    		for(FillerValue attr : obj.getAttributes()) {
    			valueMap.put(attr.getName(), attr.getVal());
    		}
    		toReturn.put(name, valueMap);  		
    	}
    	return toReturn;
    }
    
    /**************************************************************************************************
     * Helper method to parse out all of the objects and it's attributes into a HashMap for easier 
	 * comparison. This is an overloaded method.
     * 
     * @param figure - The RavenFigure object to parse out
     * @return HashMap: Where the Key is the String Name of the objects in the Figure and the values
     * 					is another HashMap where the String key	is the name of a Attribute and the value
     * 					is the value of the HashMap.
     ***********************************************************************************************/
    public HashMap<String, HashMap<String, String>> getValuesMap(RavensFigure figure) {
    	HashMap<String, HashMap<String, String>> toReturn = new HashMap<String, HashMap<String, String>>();
    	for(RavensObject obj : figure.getObjects())  {
    		if( obj != null)  {
	    		String name = obj.getName();
	    		HashMap<String, String> valueMap = new HashMap<String, String>();
	    		for(RavensAttribute attr : obj.getAttributes()) {
	    			valueMap.put(attr.getName(), attr.getValue());
	    		}
	    		toReturn.put(name, valueMap);  		
	    		
    		}
    	}
    	return toReturn;
    }

   
	
	/*******************************************************************************************
	 * This method is used as part of the process for matching the objects in one RavenFigure with 
	 * the object in another RavenFigure.  The goal is to determine which objects in each of the 
	 * figures share the greatest number of common attributes/attribute values and then pair those
	 * objects together. This process attempts to follow Analogical Reasoning, but doesn't 
	 * implement it fully as this is a problem with a single domain.
	 * This method depends on pass-by-reference to make changes to the Maps and is an overloaded 
	 * method. 
	 *
	 * @param first - SemanticNetwork representing the first RanvensFigure
	 * @param second - SemanticNetwork representing the second RanvensFigure
	 *******************************************************************************************/
	public void matchObjects(List<SemanticNetworkObj> first, List<SemanticNetworkObj> second) {
	
		//This map is structures so that the first key is an object in the first figure.  That key's value
		//is a map of each of the objects in the second figure and the similarity score that object was 
		//awarded compared to the object in the first frame
		Map<String, HashMap<String, Integer>> objectMatchMap = new HashMap<String, HashMap<String, Integer>>();
		
		int firstFillerCount = first.size();
		int secondFillerCount = second.size();
		
		//If the agent is only dealing with one object, our work is done!
		if(firstFillerCount == 1 && secondFillerCount == 1)  {
			second.get(0).setName(first.get(0).getName());
		}
		else {
		
			//Determine how many objects were added or deleted between the frames so that we wont
			//match an object in one frame to multiple objects in another frame
			int deleted = firstFillerCount - secondFillerCount > 0 ? firstFillerCount - secondFillerCount : 0;
			int added = secondFillerCount - firstFillerCount > 0 ? secondFillerCount - firstFillerCount : 0;
			
			//Step through each object in the first figure and compare it's attributes to
	    	//those in the second figure
	    	for(SemanticNetworkObj firstObj : first) {
	
				//Pull the object from the first Network
	    		List<SemanticNetworkAttribute> firstAttrs = firstObj.getAttributes();
	    		
	    		HashMap<String, Integer> objectSimularityMap = new HashMap<String, Integer>();
	    		
	    		for(SemanticNetworkObj secondObj : second) {
	    			
					//Pull the object from the second Network
	    			List<SemanticNetworkAttribute> secondAttrs = secondObj.getAttributes();
	    			
	    			int simularityScore = 0;
	    			
	    			//Test if they were added/deleted the same
	    			if(secondObj.getExistsNew() == firstObj.getExistsNew()) {
	    				simularityScore += 10;
	    			}
	    			if(secondObj.getExistsOld() == firstObj.getExistsOld()) {
	    				simularityScore += 10;
	    			}
	    			
					//Compare the attributes to determine how similar the objects are
	    			for(SemanticNetworkAttribute firstAttr : firstAttrs) {
	    				
	    				String firstValueNew = firstAttr.getNewVal();
	    				String firstValueOld = firstAttr.getOldVal();
	    				
	    				for(SemanticNetworkAttribute secondAttr : secondAttrs) {
	    					
	    					String secondValueNew = secondAttr.getNewVal();
	    					String secondValueOld = secondAttr.getOldVal();
	    					
							//If the attributes are similar, award the correct weighted score
	    					if(firstAttr.getName().equals(secondAttr.getName())) {
	    						if(secondValueOld.equals(firstValueOld) || secondValueNew.equals(firstValueNew)) {
	    							switch (firstAttr.getName()) {
	    								case "shape":
	    									simularityScore += 5;
	    								break;
	    								case "fill" :
	    									simularityScore += 3;
	    								break;
	    								default:
	    									simularityScore += 1;
	    								break;
	    								
	    							}
	    						}
	    					}
	    				}
	    			}
					
					//Keep track of the scores for each potential object mapping in order to determine
					//which is the best later on
	    			objectSimularityMap.put(secondObj.getName(), Integer.valueOf(simularityScore));
	    			objectMatchMap.put(firstObj.getName(), objectSimularityMap);
	    		}
	    	}
			
			//Determine which object in the second figure earned the best similarity score for each
			//object in the first figure
	    	HashMap<String, String> bestMatchesMap = findBestMatches(objectMatchMap, added, deleted);
	    	
	    	//Based on the determined best mapping, change the names of the objects to that
			//matching objects have the same name in each figure
	    	for(String matchName : bestMatchesMap.keySet()) {
	    		for(SemanticNetworkObj obj : first) {
	    			if(obj.getName().equals(matchName)) {
	    				obj.setName(bestMatchesMap.get(matchName));
	    			}
	    		}
				
				//Change any attribute which references an object which has had it's name changed
				//to reference the new name (ie Above:A)
	    		replaceFillerValues(first, bestMatchesMap.get(matchName), matchName);
	    	}
		}
	}
	
	/****************************************************************************************************
	 * This method is used as part of the process for matching the objects in one RavenFigure with 
	 * the object in another RavenFigure.  The goal is to determine which objects in each of the 
	 * figures share the greatest number of common attributes/attribute values and then pair those
	 * objects together. This process attempts to follow Analogical Reasoning, but doesn't 
	 * implement it fully as this is a problem with a single domain.
	 * This method depends on pass-by-reference to make changes to the Maps and is an overloaded 
	 * method. 
	 *
	 * @param first - Map representing the first RanvensFigure
	 * @param second - Map representing the second RanvensFigure
	 *****************************************************************************************************/
	public void matchObjects(HashMap<String, HashMap<String, String>> first, HashMap<String, HashMap<String, String>> second) {
		
		//This map is structures so that the first key is an object in the first figure.  That key's value
		//is a map of each of the objects in the second figure and the similarity score that object was 
		//awarded compared to the object in the first frame
		Map<String, HashMap<String, Integer>> objectMatchMap = new HashMap<String, HashMap<String, Integer>>();
		int firstFillerCount = first.size();
		int secondFillerCount = second.size();
		
		//If the agent is only dealing with one object, our work is done!
		if(firstFillerCount == 1 && secondFillerCount == 1)  {
			String nameInFirst = "";
			String nameInSecond = "";
			for(String objNameInFirst : first.keySet()) {
				nameInFirst = objNameInFirst;
			}
			for(String objNameInSecond : second.keySet()) {
				nameInSecond = objNameInSecond;
			}
			HashMap<String, String> temp = second.get(nameInSecond);
			second.remove(nameInSecond);
			second.put(nameInFirst, temp);
			replaceFillerValues(second, nameInSecond, nameInFirst);
		}
		else {
			int deleted = firstFillerCount - secondFillerCount > 0 ? firstFillerCount - secondFillerCount : 0;
			int added = secondFillerCount - firstFillerCount > 0 ? secondFillerCount - firstFillerCount : 0;
			
			
			//Step through each object in the first figure and compare it's attributes to
	    	//those in the second figure
	    	for(String fillerInFirst : first.keySet()) {
	
				HashMap<String, String> firstFillers = first.get(fillerInFirst);
	    		
	    		HashMap<String, Integer> objectSimularityMap = new HashMap<String, Integer>();
	    		
	    		for(String fillerInSecond : second.keySet()) {
	    			
	    			HashMap<String, String> secondFillers = second.get(fillerInSecond);
	    			
	    			int simularityScore = 0;
	    			
	    			if(firstFillers != null) {
	    				
		    			for(String firstValueName : firstFillers.keySet()) {
		    				
		    				String firstValue = firstFillers.get(firstValueName);
		    				
		    				if(secondFillers != null) {
			    				for(String secondValueName : secondFillers.keySet()) {
			    					
			    					String secondValue = secondFillers.get(secondValueName);
			    					
									//If the attributes are similar, award the correct weighted score
			    					if(secondValueName.equals(firstValueName)) {
			    						if(secondValue.equals(firstValue)) {
			    							switch (firstValueName) {
			    								case "shape":
			    									simularityScore += 5;
			    								break;
			    								case "fill" :
			    									simularityScore += 3;
			    								break;
			    								default:
			    									simularityScore += 1;
			    								break;
			    								
			    							}
			    						}
			    					}
			    				}
		    				}
		    			}
	    			}
					//Keep track of the scores for each potential object mapping in order to determine
					//which is the best later on
	    			objectSimularityMap.put(fillerInSecond, Integer.valueOf(simularityScore));
	    			objectMatchMap.put(fillerInFirst, objectSimularityMap);
	    		}
	    	}
			
			//Determine which object in the second figure earned the best similarity score for each
			//object in the first figure
	    	HashMap<String, String> bestMatchesMap = findBestMatches(objectMatchMap, added, deleted);
	    	
			//Based on the determined best mapping, change the names of the objects to that
			//matching objects have the same name in each figure
	    	for(String matchName : bestMatchesMap.keySet()) {
	    		String value = bestMatchesMap.get(matchName);
	    		HashMap<String, String> deletedMap = new HashMap<String, String>();
	    		deletedMap.put(DELETEDOBJECT, DELETEDOBJECT);
	    		HashMap<String, String> temp = value.equals(DELETEDOBJECT) ? deletedMap : second.get(bestMatchesMap.get(matchName));
				second.remove(bestMatchesMap.get(matchName));
				second.put(matchName, temp);
				
				//Change any attribute which references an object which has had it's name changed
				//to reference the new name (ie Above:A)
	    		replaceFillerValues(second, bestMatchesMap.get(matchName), matchName);
	    	}
		}
	}
	
	
	/**********************************************************************************
	 * Change any attribute which references an object which has had it's name changed
	 * to reference the new name (ie Above:A) 
	 * This method depends on pass-by-reference and is an overloaded method.
	 *
	 * @param filler - Frame values which need to be checked if updates to filler values
	 *					should occur based on filler matching.
	 * @param oldName - Old name of the object before matching took place
	 * @param newName - New name of the object after matching took place
	 ***********************************************************************************/
	private void replaceFillerValues(HashMap<String, HashMap<String, String>> filler, String oldName, String newName) {
	
		//Search the Frame's values to see if the object's old name is the 
		//value for any of the fillers
		for(String fillerName : filler.keySet()) {
			HashMap<String, String> temp = filler.get(fillerName);
			
			//Keep track of those fillers we should replace because we cannot
			//modify an array while iterating it
			List<String> toReplace = new ArrayList<String>();
			
			if(temp != null) {
				for(String valueName : temp.keySet()) {
					String value = temp.get(valueName);
					if(value.equals(oldName)) {
						toReplace.add(valueName);
					}
				}
				
				//Make the changes we captured
				for(String replace : toReplace) {
					temp.remove(replace);
					temp.put(replace, newName);
				}
			}
		}
	}
	
	/**********************************************************************************
	 * Change any attribute which references an object which has had it's name changed
	 * to reference the new name (ie Above:A) 
	 * This method depends on pass-by-reference and is an overloaded method.
	 *
	 * @param filler - Network values which need to be checked if updates to filler values
	 *					should occur based on filler matching.
	 * @param oldName - Old name of the object before matching took place
	 * @param newName - New name of the object after matching took place
	 ***********************************************************************************/
	private void replaceFillerValues(List<SemanticNetworkObj> network, String oldName, String newName) {
		
		for(SemanticNetworkObj obj : network) {
			for(SemanticNetworkAttribute attr : obj.getAttributes()) {
				String value = attr.getOldVal();
				String newValue = attr.getNewVal();
				if(value.equals(oldName)) {
					attr.setNewVal(newName);
				}
				if(newValue.equals(oldName)) {
					attr.setOldVal(newName);
				}
			}
		}
	}

	/***************************************************************************************************
	 * Given a map which contains an object in the first figure as the key and a map of all the objects
	 * in the second figure and their similarity score to the key aka. object in the first figure, 
	 * determine which object in the second figure is the most likely match to the object in the first.
	 *
	 * @param objectMatchMap - Map of objects in the first figure to all objects in the second with scores
	 * @param added - the number of objects added from the first figure to the second
	 * @param deleted - the number of objects deleted from the first figure to the second
	 *
	 * @return HashMap<String, String> - Map of object names in the first figure to their matching name
										in the second figure
	 ***************************************************************************************************/
	private HashMap<String, String> findBestMatches(Map<String, HashMap<String, Integer>> objectMatchMap,
													int added, int deleted) {
		
		//Map to hold the final matches for the objects in the first figure to those in the second
		HashMap<String, String> finalMatches = new HashMap<String, String>();
		
		//Map to keep track of the objects which have already been mapped and the score that justified that 
		//those mappings
		HashMap<String, HashMap<String, Integer>> prevMatches = new HashMap<String, HashMap<String, Integer>>();
		
		//Go through each of the mapping scores and determine the best match for the object represented
		//by the key in the map
    	for(String objectName : objectMatchMap.keySet()) {
    		
			//Determine the next best match for the object in the second figure,
    		String best = findNextBestMatch(objectName, objectMatchMap, prevMatches);
    		
    		//Capture that match
    		finalMatches.put(objectName, best);
    		HashMap<String, Integer> temp = new HashMap<String, Integer>();
    		temp.put(best, objectMatchMap.get(objectName).get(best));
    		prevMatches.put(objectName, temp);
    	}
		
		//Determine if any objects have been mapped to twice
    	Map<String, ArrayList<String>> duplicates = findDuplicates(finalMatches);
		
		//Account for objects which were mapped twice and consider deleted objects
    	accountForAddDelete(duplicates, objectMatchMap, finalMatches, added, deleted);
    	
    	return finalMatches;
	}
	
	
	/*****************************************************************************************************
	 * This method accounts for objects which were mapped twice and considers deleted objects in order to
	 * determine which object is the most similar when duplicate mappings exist.
	 *
	 * @param duplicates - Objects in the second figure which have been mapped to more then one object
	 *						in the first figure.
	 * @param objectMatchMap - Map of objects in the first figure to all objects in the second with scores
	 * @param finalMatches -  Map of object names in the first figure to their matching name
	 *						  in the second figure
	 * @param added - the number of objects added from the first figure to the second
	 * @param deleted - the number of objects deleted from the first figure to the second
	 ********************************************************************************************************/
	private void accountForAddDelete(Map<String, ArrayList<String>> duplicates,
			Map<String, HashMap<String, Integer>> objectMatchMap,
			HashMap<String, String> finalMatches, int added, int deleted) {
		
		//Store the similarity score info for each duplicate set
		//Key is the object which was mapped to multiple times, value is one of the mapped values
		//and it's similarity score
		Map<String, HashMap<String, Integer>> simMap = new HashMap<String, HashMap<String, Integer>>();
		
		//Get all of the values which have been mapped TO by multiple different objects
		for(String multiple : duplicates.keySet()) {
			
			//Get each of the objects which have mapped to "multiple"
			List<String> competition = duplicates.get(multiple);
			
			//Get the similarity score for each mapped object to "multiple"
			for(String comp : competition) {
				
				//Get the similarity score from the objectMatchMap
				int simScore = getSimularityScore(comp, multiple, objectMatchMap);
				
				//Store the competition and it's similarity score value
				HashMap<String, Integer> compMap = simMap.containsKey(multiple) ? simMap.get(multiple) : new HashMap<String, Integer>();
				compMap.put(comp, Integer.valueOf(simScore));
				simMap.put(multiple, compMap);
			}
			
		}
		
		//Adjust the final mapping to remove duplicate mappings based on the objects which
		//have been determined to be deleted or not as good of a match
		adjustDuplicateMappings(simMap, finalMatches, added, deleted);
		
	}

	/************************************************************************
	 * Given that there are some objects in the first figure which were 
	 * matched to the same object in the second set, determine which object
	 * in the first figure should most likely be mapped to the duplicated
	 * object.
	 *
	 *@param Map<String, HashMap<String, Integer>> simMap - Mapping which
			  breaks down the scores for the objects which are currently
			  mapped to the same object
	 *@param HashMap<String, String> finalMatches - final matches for objects
			 in the first figure to objects in the second figure
	 *@param deleted - the number of objects deleted from the first 
					   figure to the second
	 *@param added - the number of objects added from the first 
					   figure to the second
	*************************************************************************/
	private void adjustDuplicateMappings(
			Map<String, HashMap<String, Integer>> simMap,
			HashMap<String, String> finalMatches, int added, int deleted) {
			
		//Only look for objects to remove all mappings from if there have
		//been objects deleted from the first figure to the second
		if(deleted > 0) {
		
			//Step through the similarities calculated previously and
			//Determine if we can set one of the objects to map to nothing
			for(String multiple : simMap.keySet()) {
				HashMap<String, Integer> compMap = simMap.get(multiple);
				String worstMatch = "";
				int worstScore = Integer.MAX_VALUE;
				
				//Determine the worst similarity score in the competition set
				for(String compName : compMap.keySet()) {
					int currentScore = compMap.get(compName).intValue();
					if(currentScore < worstScore) {
						worstScore = currentScore;
						worstMatch = compName;
					}
				}
				
				//Adjust final matches for deleted objects
				finalMatches.remove(worstMatch);
				finalMatches.put(worstMatch, DELETEDOBJECT);
			}
		}
		for(String name : finalMatches.keySet()) {
			if(finalMatches.get(name) == null) {
				finalMatches.remove(name);
				finalMatches.put(name, DELETEDOBJECT);
			}
		}
		
	}

	/********************************************************************************
	 * Helper method to return the simularity score for an object mapping 
	 * from the given HashMap
	 *
	 *@param String comp - one of the objects competing to be mapped to the
							the duplicate mapped object
	 *@param String multiple - object which has been mapped multiple times
	 *@param HashMap<String, Hashmap<String, Integer>> - Map to pull the score from
	*********************************************************************************/
	private int getSimularityScore(String comp, String multiple,
			Map<String, HashMap<String, Integer>> objectMatchMap) {
			
		int toReturn = 0;
		
		//Pull the score
		HashMap<String, Integer> scores = objectMatchMap.get(comp);
		toReturn = scores.get(multiple).intValue();
		
		return toReturn;	
	}

	/************************************************************************
	 * Find any collect a list of objects in the first figure which have 
	 * been mapped to the same object in the second figure.
	 *
	 *@param - HashMap<String, String> finalMatches - the current mappings of
	 *			objects in the first figure (key) to objects in the second
	 *			figure (value)
	 *@return Map<String, ArrayList<String>> - Map where the key is the name
	 *		  of an object in the second figure and the value is a list of 
	 *		  objects in the first figure which have been mapped to it
	*************************************************************************/
	private Map<String, ArrayList<String>> findDuplicates(HashMap<String, String> finalMatches) {
		Map<String, ArrayList<String>> duplicates = new HashMap<String, ArrayList<String>>();
		for(String match : finalMatches.keySet()) {
			String matchValue = finalMatches.get(match);
			if(getCount(finalMatches.values(), matchValue) > 1) {
					
				//matchValue is the object which has been mapped to multiple times
				ArrayList<String> tmp = duplicates.containsKey(matchValue) ? duplicates.get(matchValue) : new ArrayList<String>();
				
				//match is one of the multiple different objects which have been mapped to matchValue
				tmp.add(match);
				
				duplicates.put(matchValue, tmp);
			}
		}
		return duplicates;
	}
	
	/************************************************************************
	 * Simple helper method to return the count of how many times the 
	 * String element is present in the Collection of Strings.
	 *
	 *@param Collection<String> array - Collection to iterate over and count
	 *@param String - the value that we are looking for in the collection
	 *
	 *@return int - the count of how many times the String is present
	*************************************************************************/
	private int getCount(Collection<String> array, String element) {
		int toReturn = 0;
		for(String str : array) {
			if(str.equals(element)) {
				toReturn++;
			}
		}
		return toReturn;
	}
	
	/************************************************************************
	 * Find the best match for the object with the given name in the first
	 * figure using the mapping data collected in the object match map.
	 *
	 *@param String objectName - Name of the object in the first figure
								to find the match for
	 *@param Map<String, HashMap<String, Integer>> objectMatchMap -  
			Map of objects in the first figure to all objects in the 
			second with scores
	 *@param HashMap<String, HashMap<String, Integer>> prevMatches - 
	 *		 Map of objects in the first map which have already been
	 *       matched
	 *@return String - 
	*************************************************************************/
	private String findNextBestMatch(String objectName, Map<String, HashMap<String, Integer>> objectMatchMap, 
									 HashMap<String, HashMap<String, Integer>> prevMatches) {
		
		int iter = 0;
		boolean bestFound = false;
		String best = "";
		int bestScore = 0;
		
		//Until the best match has been found, move to the next best match.
		//This is to account for the situation when the best match, is a better match for
		// a different object.
		while(!bestFound) {
    		HashMap<String, Integer> possibleMatches = objectMatchMap.get(objectName);
    		
    		//Step through each object and find out what it's best match is
    		best = findXBestMatch(possibleMatches, iter);
    		    		
    		//Test to see if another object has already taken it with a better score...
    		String comparison = trueBest(best, Integer.valueOf(bestScore), possibleMatches.get(best), prevMatches);
    		String comparison2 = getXRankedMatch(iter, objectMatchMap, best);
    		
			//Check to see if this match is the best match for the object
    		if(comparison != null) {
    			if(iter > possibleMatches.size()) {
    				comparison2 = objectName;
    			}
    			if(comparison.equals(best) && comparison2.equals(objectName)) {
    				bestFound = true;
    			}
    		}else {
    			if(comparison2.equals(objectName)) {
    				bestFound = true;
    			}
    		}
			
			//if the best match isn't this match, move on to the next best match
    		iter += 1;
		}
		
		//Add this match to the previous matches map
		HashMap<String, Integer> tmp = new HashMap<String, Integer>();
		tmp.put(best, bestScore);
		prevMatches.put(objectName, tmp);
		return best;
	}
	
	
	/************************************************************************
	 * Helper method to rank the given matches by similarity score and then
	 * return the X best match
	 *
	 *@param iter - the rank of the match looking to be retrieved
	 *@param objectMatchMap - Map of objects in the first figure to all objects 
							  in the second with scores
	 *@return String - the object name of the X best match
	*************************************************************************/
	private String getXRankedMatch(int iter, Map<String, HashMap<String, Integer>> objectMatchMap, String best) {
			
		//List of all of the matches for this object, sorted descending by their similarity score 
		List<HashMap<String, Integer>> rankedMatches = new ArrayList<HashMap<String, Integer>>();
		
		//Create the an array of the matches ranked by similarity score for the 
		//current object being iterated on
		for(String objectName : objectMatchMap.keySet()) {
			HashMap<String, Integer> matches = objectMatchMap.get(objectName);
			for(String matchName : matches.keySet()) {
				Integer score = matches.get(matchName);
					if(matchName.equals(best)) {
						//Place the match in the array in the correct index based on score
						addToRankedArray(score.intValue(), objectName, rankedMatches);
					}
			}
		}
		
		//Get the match at the requested rank
		String toReturn = "";
		try {
			toReturn = (String) rankedMatches.get(iter).keySet().toArray()[0];
		} catch(IndexOutOfBoundsException e) {
		
			//If we are here then there is a hole in the logic and
			//the agent can't figure out the mapping - so just guess.
			int size = rankedMatches.size();
			Random rand = new Random();
			int last = rand.nextInt(size);
			toReturn = (String) rankedMatches.get(last).keySet().toArray()[0];
		}
		return toReturn;
	}

	/************************************************************************
	 * Add the given match to the to the array in the correct position 
	 * according to similarity score in a descending order. 
	 * this method depends on pass-by-reference to modify the array.
	 *
	 *@param calcBest - the score of the match to add
	 *@param calcBestName - the name of the match to add
	 *@param rankedMatches - the array to add the match to
	*************************************************************************/
	private void addToRankedArray(int calcBest, String calcBestName,
			List<HashMap<String, Integer>> rankedMatches) {
			
		//if there are other elements in the array, find the correct spot for this one
		if(rankedMatches.size() > 1) {
			for(int i = 0; i < rankedMatches.size() -1; i++) {
				HashMap<String, Integer> rankedCurrent = rankedMatches.get(i);
				HashMap<String, Integer> rankedNext = rankedMatches.get(i+1);
				int current = 0;
				int next = 0;
				for(String rankedName : rankedCurrent.keySet()) {
					current = rankedCurrent.get(rankedName).intValue();
				}
				for(String rankedName : rankedNext.keySet()) {
					next = rankedNext.get(rankedName).intValue();
				}
				if(calcBest > current && calcBest <= next) {
					HashMap<String, Integer> tmp = new HashMap<String, Integer>();
					tmp.put(calcBestName, Integer.valueOf(calcBest));
					rankedMatches.add(i, tmp);
				}
			}
			
		//If there is only one, see how this one compares
		}else if(rankedMatches.size() == 1) {
			HashMap<String, Integer> tmp = new HashMap<String, Integer>();
			tmp.put(calcBestName, Integer.valueOf(calcBest));
			HashMap<String, Integer> added = rankedMatches.get(0);
			int addedValue = 0;
			for(String addedName : added.keySet()) {
				addedValue = added.get(addedName).intValue();
			}
			if(addedValue >= calcBest) {
				rankedMatches.add(1, tmp);
			}else {
				rankedMatches.add(0, tmp);
			}
		}
		//if there are no elements in the array, our work is done
		else {
			HashMap<String, Integer> tmp = new HashMap<String, Integer>();
			tmp.put(calcBestName, Integer.valueOf(calcBest));
			rankedMatches.add(tmp);
		}
		
	}

	/************************************************************************
	 * Determine if the score for this potential match is truly the best
	 * match for the given object.  Regardless if it is or not, return
	 * what the best match is.
	 * 
	 *@param best - Name of the object which is the current best match
	 *@param bestScore - Score of the best match
	 *@param score - Score of the competition
	 *@param prevMatches - Map of objects in the first map which have already been
	 *       			   matched
	*************************************************************************/
	private String trueBest(String best, Integer bestScore,
			Integer score, HashMap<String, HashMap<String, Integer>> prevMatches) {
		
		for(String objName : prevMatches.keySet()) {
			HashMap<String, Integer> currentMatch = prevMatches.get(objName);
			for(String currentBest : currentMatch.keySet()) {
			
				//The competitor is not as good of a match, return the current best
				if(currentBest.equals(best) && score.intValue() <= bestScore.intValue()) {
					return currentBest;
					
				//The competitor is a better match, return the competitor
				}else if(currentBest.equals(best) && score.intValue() > bestScore.intValue()) {
					return best;
				}
			}
		}
		
		return null;
	}

	/************************************************************************
	 * Returns the "X" best match for an object in the second frame.  For 
	 * example this method can return the second best match or the sixth
	 * best match.
	 * 
	 * @param possibleMatches: The list of possible matches to search to
	 * 						   find the match
	 * @param x - the rank of match to return
	 * @return String - Object name of the match
	*************************************************************************/
	private String findXBestMatch(HashMap<String, Integer> possibleMatches, int x) {
		List<HashMap<String, Integer>> rankedMatches = new ArrayList<HashMap<String, Integer>>();
		for(String possible : possibleMatches.keySet()) {
			Integer score = possibleMatches.get(possible);
			if(rankedMatches.size() > 1) {
				for(int i = 0; i < rankedMatches.size() -1; i++) {
					HashMap<String, Integer> rankedCurrent = rankedMatches.get(i);
					HashMap<String, Integer> rankedNext = rankedMatches.get(i+1);
					int current = 0;
					int next = 0;
					for(String rankedName : rankedCurrent.keySet()) {
						current = rankedCurrent.get(rankedName).intValue();
					}
					for(String rankedName : rankedNext.keySet()) {
						next = rankedNext.get(rankedName).intValue();
					}
					if(score > current && score <= next) {
						HashMap<String, Integer> tmp = new HashMap<String, Integer>();
						tmp.put(possible, Integer.valueOf(score));
						rankedMatches.add(i, tmp);
					}
				}
			}else if(rankedMatches.size() == 1) {
				HashMap<String, Integer> tmp = new HashMap<String, Integer>();
				tmp.put(possible, Integer.valueOf(score));
				HashMap<String, Integer> added = rankedMatches.get(0);
				int addedValue = 0;
				for(String addedName : added.keySet()) {
					addedValue = added.get(addedName).intValue();
				}
				if(addedValue >= score) {
					rankedMatches.add(1, tmp);
				}else {
					rankedMatches.add(0, tmp);
				}
			}
			else {
				HashMap<String, Integer> tmp = new HashMap<String, Integer>();
				tmp.put(possible, Integer.valueOf(score));
				rankedMatches.add(tmp);
			}
		}
		String toReturn = "";
		try {
			toReturn = (String) rankedMatches.get(x).keySet().toArray()[0];
		} catch(IndexOutOfBoundsException e) {
			int size = rankedMatches.size();
			Random rand = new Random();
			int last = rand.nextInt(size);
			toReturn = (String) rankedMatches.get(last).keySet().toArray()[0];
		}
		return toReturn;
		
	}
		
	
    

   
    
    

   
}
