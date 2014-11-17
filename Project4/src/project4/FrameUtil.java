package project4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**************************************************************************
 * This Utility is capable of completing calculations using Frames, Means
 * End Analysis and Generate & Test. The agent will use this utility to 
 * calculate a Frame to represent each Figure in a Matrix.  Then, the Utility
 * can determine which transitions a Figure would need to undergo in order
 * to look exactly like another Frame.  These transitions are then represented
 * by another Frame.  The transitions frame can then be applied to the starting
 * Figure to generate what this utility believes should be the correct answer.
 * Finally this utility can determine how similar two Frames are using 
 * a weighted scale
 *   
 * @author Craig Graham
 ***************************************************************************/
public class FrameUtil {

	
	
	/******************************************************************************
	 * Using Frames to represent a Raven's Figure, add the transformations or
	 * attribute changes Figure A underwent horizontally and when 
	 * applicable, vertically. 
	 * This method counts on pass-by-reference to make changes to the object
	 * calcFrame. 
	 * 
	 * @param frame - The starting point frame (ie. Raven's Figure A)
	 * @param transitions - The transformations the starting frame underwent 
	 * 						in the problem set.
	 * @param calcFrame - The Raven's Frame which will be the "calculated"
	 * 					  answer for the problem. 
	 *****************************************************************************/
	public void applyTransformation(Frame frame, TransitionList transitions, Frame calcFrame) {
		
		//Add all of the objects and attributes of Frame A to the Calculated Frame
		//So that the Calculated Frame starts at the same point
		addFillers(frame.getFillers(), calcFrame);
		
		//Step through each transition found to be applied to Frame A in the Matrix
		for(Transition t : transitions.getAll()) {
		
			//Get the object the Transition was applied to
			String objectName = t.getObjectId();
			
			//Get the filler in the Calculated Frame
			FrameFiller filler = calcFrame.getFillerByName(objectName);
			
			//Step through each of the changes made to the filler in the frame
			for(String fillName : t.getChanges().keySet()) {
			
				Change change = t.getChanges().get(fillName);
				
				//Depending on the change, take the appropriate action to reflect it
				//in the Calculated Frame.
				switch (change.getChange()) {
				
					//If the object is deleted, remove the filler from the fame
					case Change.OBJDELETED:
						calcFrame.remove(filler);
					break;
					
					//Update the filler value to reflect the change to the object attribute
					case Change.CHANGE:
						//The object this attribute belongs to could have already been deleted
						if(filler != null) {
							
							//Account for the fact that changes in rotation are different then boolean values
							if(fillName.equals("angle")) {
								int amt = Integer.valueOf(change.getOldValue()).intValue() - Integer.valueOf(change.getNewValue()).intValue();
								FillerValue val = filler.checkAttrExists("angle");
								if(val != null) {
									int fillVal = Integer.valueOf(val.getVal()).intValue();
									int newVal = fillVal + amt;
									filler.changeValue(fillName, String.valueOf(newVal));
								}else {
									filler.changeValue(fillName, String.valueOf(amt));
								}
							}else if(fillName.equals("fillCount")) {
								int oldAmt = Integer.valueOf(change.getOldValue()).intValue();
								int newAmt = Integer.valueOf(change.getNewValue()).intValue();
								double fillChange = 0.0;
								//Check to see how the example semantic network object's fill attribute changed
								if(oldAmt == 0)  {
									fillChange = newAmt;
								}else if(newAmt == 0) {
									fillChange = oldAmt;
								}else {
									fillChange = ((double) (newAmt / oldAmt));
									if(fillChange > 1) {
										fillChange -= 1;
									}
									fillChange = fillChange * 100;
								}
								FillerValue val = filler.checkAttrExists("fillCount");
								if(val != null) {
									int fillVal = Integer.valueOf(val.getVal()).intValue();
									int newVal = ((int) (fillVal + fillChange));
									filler.changeValue(fillName, String.valueOf(newVal));
								}else {
									filler.changeValue(fillName, String.valueOf(fillChange));
								}
							}else {
								filler.changeValue(fillName, change.getNewValue());	
							}
						}
					break;
					
					//This is just here for debugging purposes and future enhancements 
					case Change.NOCHANGE:
			
					break;
					
					//If the attribute for the object has been deleted remove the filler
					case Change.DELETED:
						//The object this attribute belongs to could have already been deleted
						if(filler != null) {
							filler.remove(change.getName());
							if(filler.getName().equals(Change.DELETED)) {
								calcFrame.remove(filler);
							}
						}
					break;
				}
			}
		}
	}
	
	  /****************************************************************************
     * Helper method to create a Frame representation for a given RavenFigure
     * 
     * @param figureA - Raven's Figure object to create Frame for
     * 
     * @return Frame: The Data Representation Frame for the figure
     *****************************************************************************/
    public Frame convertToFrame(HashMap<String, HashMap<String, String>> figureAValues) {
    	
    	Frame toReturn = new Frame();
    	
    	//List of differences to return
    	List<FrameFiller> fillers = new ArrayList<FrameFiller>();

    	//Step through each object in the first figure and compare it's attributes to
    	//those in the second figure
    	for(String objectName : figureAValues.keySet()) {
    		
    		//Get the attributes and their values for this object in each figure
    		HashMap<String, String> firstFigObjs = figureAValues.get(objectName);
    		
    			if(firstFigObjs != null) {
	    			//Create a Result object to hold the difference between the two objects
		    		FrameFiller filler = new FrameFiller();
		    		
		    		//Set what we know so far, that it exists in both and it's name
		    		filler.setName(objectName);
		    		
		    		//Step through each attribute the object in the first figure has and
		    		//compare the attribute values to the same object in the second figure
		    		for(String attrName : firstFigObjs.keySet()) {
		    			
		    			//Create a Result Attribute object to hold the differences found
		    			FillerValue value = new FillerValue();
		    			
		    			//Set the name of the attribute
		    			value.setName(attrName);
		    			
		    			//Capture the value this attribute has in the first figure
		    			value.setVal(firstFigObjs.get(attrName));
			    		
		    			//Add the filler value 
		    			filler.addAttr(value);
		    			
		    		}
		    			    		
		    		//Add the filler object to the list
		    		fillers.add(filler);	
    			}
    	}
    	
    	toReturn.setFillers(fillers);
    	
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
    
    /*****************************************************************************
	 * Add all of the objects and attributes of a Frame to the Calculated Frame
	 * So that the Calculated Frame starts at the same point.
	 * This method depends on pass-by-reference to makes changes to the Calculated
	 * Frame
	 *
	 * @param List<FrameFillers>: List of all the attributes and their values to
	 *							  to be applied to the calculated frame
	 * @param Frame - calcFrame - The frame which the attributes should be added to
	 *****************************************************************************/
	private void addFillers(List<FrameFiller> fillers, Frame calcFrame) {
		
		//List to hold all the fillers/attributes which should be applied to the frame
		List<FrameFiller> toAdd = new ArrayList<FrameFiller>();
		
		//If the calculated frame already has some fillers, make sure we do
		//not add duplicates
		if(calcFrame.getFillers() != null && calcFrame.getFillers().size() > 0)  {
		
			//Determine which fillers do not already exist in the Frame
			//and prepare to add them
			for(FrameFiller newFF : fillers) {
				for(FrameFiller existing : calcFrame.getFillers()) {
					if(!existing.getName().equals(newFF.getName())) {
						toAdd.add(newFF);
					}
				}
			}

		//If this is the first time fillers have been added, just add all of them
		}else {
			toAdd.addAll(fillers);
		}
		
		//Add the fillers to the frame
		calcFrame.addFillers(toAdd);
		
	}
	
	 /**************************************************************************************************
		 * This method is part of the Generate and Test process which is completed by the agent.  At this
		 * point the agent has already generated the frame it thinks should be the answer using Means End
		 * Anaylsis.  The responsibility of this method is to determine how similar that calculated frame
		 * is to one of the frames in the possible answer set. 
		 *
	     * @param nextAnswer - RavensFigure which comes from the question set to be compared to the 
		 *  				   Calculated Frame
	     * @param calculatedFrame - Frame which was calculated from the Example Matrix using Means End 
		 * 							Analysis
		 *
	     * @return int - The similarity score for the two 
	     **************************************************************************************************/
		public int compareFrames(HashMap<String, HashMap<String, String>> first,
				HashMap<String, HashMap<String, String>> second) {
					
	    	
			//Similarity score
			int score = 0;
			
			//Step through each object in the first figure and compare it's attributes to
			//those in the second figure
			for(String fillerInFirst : first.keySet()) {
				try {
					HashMap<String, String> firstFillers = first.get(fillerInFirst);
					
					for(String fillerInSecond : second.keySet()) {
						try {
							HashMap<String, String> secondFillers = second.get(fillerInSecond);
							
							for(String firstValueName : firstFillers.keySet()) {
								try {
									String firstValue = firstFillers.get(firstValueName);
									
									for(String secondValueName : secondFillers.keySet()) {
										try {
											String secondValue = secondFillers.get(secondValueName);
											
											//Test if the attributes are similar and if so
											//give the appropriate weighted score.
											if(secondValueName.equals(firstValueName)) {
												if(secondValue.equals(firstValue)) {
													switch (firstValue) {
														case "shape":
															score += 5;
														break;
														case "overlaps":
														case "fillCount":
															score += 4;
														break;
														case "fill":
															score += 2;
														break;
														case "inside":
														case "outside":
														case "above":
														case "below":
															score += 3;
														break;
														case "angle":
															score += 10;
														break;
														default:
															score += 1;
														break;
													}
													
												}
											}
										}catch(NullPointerException e) {
											//Just continue on...it is only a mismatch
										}
									}
								}catch(NullPointerException e) {
									//Just continue on...it is only a mismatch
								}
							}
						}catch(NullPointerException e) {
							//Just continue on...it is only a mismatch
						}
					}
				}catch(NullPointerException e) {
					//Just continue on...it is only a mismatch
				}
			}
				
			//Give the Means End Score more weight since the Generate and Test Score
			//Will always be higher because it has more attributes to test. 
			return score * Agent.MEANSENDSWEIGHT;
		}
		/****************************************************************************
		 * Given two RavenFigure objects determine the differences that exist
		 * between the objects in each figure. The result is a list of "Result"
		 * objects where each object represents the differences that object occurred
		 * as it transitioned from figureA to figureB.
		 * 
		 * @param figureA
		 *            - First Raven's Figure object to compare
		 * @param figureB
		 *            - Second Raven's Figure object to compare
		 * 
		 * @return - TransitionList: List of the differences each object has
		 *         between FigureA & FigureB
		 *****************************************************************************/
		public TransitionList calcTransitions(HashMap<String, HashMap<String, String>> figureAValues, HashMap<String, HashMap<String, String>> figureBValues) {

			TransitionList toReturn = new TransitionList();


			// Step through each object in the first figure and compare it's
			// attributes to those in the second figure
			for (String objectName : figureAValues.keySet()) {

				Transition transition = new Transition(objectName);

				// Get the attributes and their values for this object in each
				// figure
				HashMap<String, String> firstFigObjs = figureAValues
						.get(objectName);
				HashMap<String, String> secondFigObjs = figureBValues
						.get(objectName);

				// See if the second figure has the object found to exist in the
				// first figure
				if (firstFigObjs != null && secondFigObjs != null) {

					// Step through each attribute the object in the first figure
					// has and
					// compare the attribute values to the same object in the second
					// figure
					for (String attrName : firstFigObjs.keySet()) {

						// Check to see if the object in the second figure has the
						// same attribute
						if (secondFigObjs != null &&  secondFigObjs.get(attrName) != null) {

							// Capture the value the attribute has in the second
							// figure
							String newValue = secondFigObjs.get(attrName);
							String oldValue = firstFigObjs.get(attrName);

							if (newValue.equals(oldValue)) {
								transition.setChange(attrName, Change.NOCHANGE,
										null, null);
							} else {
								transition.setChange(attrName, Change.CHANGE,
										oldValue, newValue);
								
								if(attrName.equals("fill")) {
				    				
				    				Transition fillTransition = new Transition(objectName);
				    				String old = String.valueOf(getFillCount(firstFigObjs.get(attrName).split(",")));
				    				String newVal = String.valueOf(getFillCount(secondFigObjs.get(attrName).split(",")));
					    			fillTransition.setChange("fillCount", Change.CHANGE, old, newVal);			    			
					    			toReturn.add(fillTransition);
				    			}
								
							}

						} else {
							transition.setChange(attrName, Change.DELETED, null,
									null);
						}

					}

					// Step through the attributes for this object in the second
					// figure to see if it has attributes the object in the first
					// figure does not have
					for (String oldAttrName : secondFigObjs.keySet()) {

						// Check to see if the attribute has already been captured
						if (!transition.checkAttrExists(oldAttrName)) {

							transition.setChange(oldAttrName, Change.DELETED, null,
									null);

						}
					}

					// If the object doesn't exist, then create a dummy object
					// to represent an object which is in the first figure, but
					// does not exist in the second
				} else {
					transition.setChange(objectName, Change.OBJDELETED, null, null);
				}
				toReturn.add(transition);
			}

			// Step through the objects in the second figure to see if
			// any of those objects do not appear in the first figure
			for (String objectNameOld : figureBValues.keySet()) {

				// See if the object in the second figure is also in the first
				HashMap<String, String> figureAObjVals = figureAValues
						.get(objectNameOld);

				// If the object in the second is not in the first, create a dummy
				// object
				if (figureAObjVals == null) {

					Transition transition = new Transition(objectNameOld);

					transition.setChange(objectNameOld, Change.OBJDELETED, null,
							null);

					toReturn.add(transition);
				}
			}

			return toReturn;
		}
}
