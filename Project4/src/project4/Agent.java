package project4;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import project4.CPair;
import project4.CoordinateObject;
import project4.Frame;
import project4.FrameUtil;
import project4.MatchUtil;
import project4.Pattern;
import project4.PropositionalLogicUtil;
import project4.RavensFigure;
import project4.SemanticNetworkObj;
import project4.SemanticNetworkUtil;
import project4.TransitionList;

/******************************************************************************
 * The Agent class is responsible for attempting to solve the Raven's Progressive
 * Matrices Problem.  This Agent uses two approaches to score the what it
 * believes to be the correct answer and the combines those scores to give it's
 * best guess.  The first approach uses Semantic Networks to represent the 
 * transitions between Raven's figures and the Generate and Test method to find
 * which answer has a similar Semantic Network for the transitions it undergoes.
 * The second approach uses Frames to represent the Raven's Figures and then
 * determines the transitions required to go from the starting figure, A, to 
 * the answer figure, D, using Mean Ends Analysis. The agent then calculates
 * which figure is most similar to the frame it calculated to be the answer
 * based on it's analysis using a weighted score. 
 * 
 *@author Craig Graham 
 ******************************************************************************/
public class Agent {
	/*** Utility class used to complete matching calculations*/
	private MatchUtil matchUtil;
	
	/*** Utility class used to sove the matrix using Semantic Networks*/
	private SemanticNetworkUtil smUtil;
	
	/*** Utility class used to solve the matrix using Frames*/
	private FrameUtil frameUtil;
	
	/*** Utility class used to find patterns in columns/rows*/
	private PropositionalLogicUtil logicUtil;
	
	/*** Utility class used to discover what objects are in a image*/
	private ImageProcessor imageProcessor;
	
	public static int SEMANTICWEIGHT = 5;
	public static int PATTERNWEIGHT = 7;
	public static int MEANSENDSWEIGHT = 9;

	/******************************************************************************
	 * Constructor to instantiate the RavenUtil that will be used to complete
	 * the calculations. 
	 ******************************************************************************/
	public Agent() {
		matchUtil = new MatchUtil();
		smUtil = new SemanticNetworkUtil();
		frameUtil = new FrameUtil();
		logicUtil = new PropositionalLogicUtil();
		imageProcessor = new ImageProcessor();
	}
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return a String representing its
     * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName().
     * 
     * In addition to returning your answer at the end of the method, your Agent
     * may also call problem.checkAnswer(String givenAnswer). The parameter
     * passed to checkAnswer should be your Agent's current guess for the
     * problem; checkAnswer will return the correct answer to the problem. This
     * allows your Agent to check its answer. Note, however, that after your
     * agent has called checkAnswer, it will *not* be able to change its answer.
     * checkAnswer is used to allow your Agent to learn from its incorrect
     * answers; however, your Agent cannot change the answer to a question it
     * has already answered.
     * 
     * If your Agent calls checkAnswer during execution of Solve, the answer it
     * returns will be ignored; otherwise, the answer returned at the end of
     * Solve will be taken as your Agent's answer to this problem.
     * 
     * @param problem the VisualRavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public String Solve(VisualRavensProblem problem) {
		String bestAnswer = "";
		if (problem.getProblemType().equals("2x1 (Image)")) {
			bestAnswer = solve2x1(problem);
		} else if (problem.getProblemType().equals("2x2 (Image)")) {
			bestAnswer = solve2x2(problem);
		} else if(problem.getProblemType().equals("3x3 (Image)")) {
			bestAnswer = solve3x3(problem);
		}
		System.out.println(" - Finished!");
		return bestAnswer;
    }
    
    /*****************************************************************************
     * process a image to determine the figure and it's attributes
     *****************************************************************************/
    public RavensFigure processFigure(VisualRavensFigure figure, String name) {
    	RavensFigure toReturn = null;
    	try{
    		File file = new File(figure.getPath());
			BufferedImage in = ImageIO.read(file);
			BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = newImage.createGraphics();
			g.drawImage(in, 0, 0, null);
			g.dispose();
			toReturn = imageProcessor.processImage(in, name);
		}catch(IOException io) {
			
		}
    	return toReturn;
    }
    
    /******************************************************************************
	 * Method to solve a 2x1 Matrix 
	 * @param problem - Raven's Problem to solve
	 * @return String - the agent's best answer for the problem
	 ******************************************************************************/
	public String solve2x1(VisualRavensProblem problem) {
		System.out.print("Solving 2x1 Question: " + problem.getName());
		// For cases when multiple "best" answers exist
		List<String> tiedAnswers = new ArrayList<String>();
		boolean tieExists = false;

		String bestAnswer = "";
		int bestScore = -999;

		// Get each of the figures in the questions
		HashMap<String, VisualRavensFigure> questionSet = problem.getFigures();

		// Get the example figures (A&B) and the prompt figure (C)
		RavensFigure figureA = processFigure(questionSet.get("A"), "A");
		RavensFigure figureB = processFigure(questionSet.get("B"), "B");
		RavensFigure figureC = processFigure(questionSet.get("C"), "C");

		// Break the object and it's attributes down into an iterable object
		// where the key is object name, and the value is a map of the object's attribute
		// names and the corresponding attribute values
		HashMap<String, HashMap<String, String>> figureAValues = matchUtil
				.getValuesMap(figureA);
		HashMap<String, HashMap<String, String>> figureBValues = matchUtil
				.getValuesMap(figureB);
		HashMap<String, HashMap<String, String>> figureCValues = matchUtil
				.getValuesMap(figureC);

		//match the objects using Analogical Reasoning
		matchUtil.matchObjects(figureAValues, figureBValues);
		matchUtil.matchObjects(figureBValues, figureCValues);
		
		// Calculate the transitions from A -> B and A -> C
		TransitionList transitions = new TransitionList();
		transitions.addAll(frameUtil.calcTransitions(figureAValues, figureBValues));

		//The Frames Approach
		Frame frameA = frameUtil.convertToFrame(figureAValues);
		Frame calculatedFrame = new Frame("calculated");
		frameUtil.applyTransformation(frameA, transitions, calculatedFrame);

		HashMap<String, HashMap<String, String>> calculatedFrameValues = matchUtil
				.getValuesMap(calculatedFrame);
		
		//The Semantic Network approach
		//Determine the differences which exist between Figure A and Figure B
    	List<SemanticNetworkObj> baselineDiffAB = smUtil.calculateDifference(figureAValues, figureBValues);
		
		// Step through each of the possible answers and make Semantic Network and Frame Comparisons
		for (int i = 1; i <= 6; i++) {
			
			// Get the next possible answer
			RavensFigure nextAnswer = processFigure(questionSet.get(String.valueOf(i)), String.valueOf(i));
			
			HashMap<String, HashMap<String, String>> nextValues = matchUtil
					.getValuesMap(nextAnswer);

			//Use Analogical Reasoning to match objects
			matchUtil.matchObjects(figureCValues, nextValues);
			
			
			//Frames approach
			int score = frameUtil.compareFrames(nextValues, calculatedFrameValues);

			//Semantic Network Approach - 
    		//Determine the differences which exist between Figure C and the possible answer "i"
    		List<SemanticNetworkObj> testDiffCD = smUtil.calculateDifference(figureCValues, nextValues);
    		
    		//Compare the differences between A&B to those between C&Answer "i"
    		score += smUtil.compareDifference(baselineDiffAB, testDiffCD);
    		
			// Capture if this score is better than the previous best
			if (score > bestScore) {
				bestScore = score;
				bestAnswer = String.valueOf(i);
				tieExists = false;
			}

			// If the scores are the same, prepare to guess...
			else if (score == bestScore) {
				tieExists = true;
				tiedAnswers.add(String.valueOf(i));
				if (!tiedAnswers.contains(bestAnswer)) {
					tiedAnswers.add(bestAnswer);
				}
			}
		}

		// If there is a tie, just make a random guess
		if (tieExists) {
			Random random = new Random();
			bestAnswer = String.valueOf(tiedAnswers.get(random
					.nextInt((tiedAnswers.size()))));
		}

		return bestAnswer;
	}
    
    /******************************************************************************
	 * Method to solve a 2x2 Matrix 
	 * @param problem - Raven's Problem to solve
	 * @return String - the agent's best answer for the problem
	 ******************************************************************************/
	public String solve2x2(VisualRavensProblem problem) {

		System.out.print("Solving 2x2 Question: " + problem.getName());
		// For cases when multiple "best" answers exist
		List<String> tiedAnswers = new ArrayList<String>();
		boolean tieExists = false;

		String bestAnswer = "";
		int bestScore = -999;

		// Get each of the figures in the questions
		HashMap<String, VisualRavensFigure> questionSet = problem.getFigures();

		// Get the example figures (A&B) and the prompt figure (C)
		RavensFigure figureA = processFigure(questionSet.get("A"), "A");
		RavensFigure figureB = processFigure(questionSet.get("B"), "B");
		RavensFigure figureC = processFigure(questionSet.get("C"), "C");

		// Break the object and it's attributes down into an iterable object
		// where the key is object name, and the value is a map of the object's attribute
		// names and the corresponding attribute values
		HashMap<String, HashMap<String, String>> figureAValues = matchUtil
				.getValuesMap(figureA);
		HashMap<String, HashMap<String, String>> figureBValues = matchUtil
				.getValuesMap(figureB);
		HashMap<String, HashMap<String, String>> figureCValues = matchUtil
				.getValuesMap(figureC);

		//match the objects using Analogical Reasoning
		matchUtil.matchObjects(figureAValues, figureBValues);
		matchUtil.matchObjects(figureBValues, figureCValues);
				
		// Calculate the transitions from A -> B and A -> C
		TransitionList transitions = new TransitionList();
		transitions.addAll(frameUtil.calcTransitions(figureAValues, figureBValues));
		transitions.addAll(frameUtil.calcTransitions(figureAValues, figureCValues));

		//The Frames Approach
		Frame frameA = frameUtil.convertToFrame(figureAValues);
		Frame calculatedFrame = new Frame("calculated");
		frameUtil.applyTransformation(frameA, transitions, calculatedFrame);

		HashMap<String, HashMap<String, String>> calculatedFrameValues = matchUtil
				.getValuesMap(calculatedFrame);
		
		//The Semantic Network approach
		//Determine the differences which exist between Figure A and Figure B
    	List<SemanticNetworkObj> baselineDiffAB = smUtil.calculateDifference(figureAValues, figureBValues);
		
    	//Determine the differences which exist between Figure A and Figure C
    	List<SemanticNetworkObj> baselineDiffAC = smUtil.calculateDifference(figureAValues, figureCValues);
    	
		// Step through each of the possible answers and make Semantic Network and Frame Comparisons
		for (int i = 1; i <= 6; i++) {
			
			// Get the next possible answer
			RavensFigure nextAnswer = processFigure(questionSet.get(String.valueOf(i)), String.valueOf(i));
			
			HashMap<String, HashMap<String, String>> nextValues = matchUtil
					.getValuesMap(nextAnswer);

			//Use Analogical Reasoning to match objects
			matchUtil.matchObjects(figureBValues, nextValues);
			matchUtil.matchObjects(figureBValues, figureCValues);
			
			
			//Frames approach
			int score = frameUtil.compareFrames(nextValues, calculatedFrameValues);

			//Semantic Network Approach - 
    		//Determine the differences which exist between Figure B and the possible answer "i"
    		List<SemanticNetworkObj> testDiffBD = smUtil.calculateDifference(figureBValues, nextValues);
    		
    		//Determine the differences which exist between Figure C and the possible answer "i"
    		List<SemanticNetworkObj> testDiffCD = smUtil.calculateDifference(figureCValues, nextValues);
    		
    		//Compare the differences between A&B to those between C&Answer "i"
    		score += smUtil.compareDifference(baselineDiffAB, testDiffCD);
    		
    		//Compare the differences between A&C to those between B&Answer "i"
    		score += smUtil.compareDifference(baselineDiffAC, testDiffBD);
    		
			// Capture if this score is better than the previous best
			if (score > bestScore) {
				bestScore = score;
				bestAnswer = String.valueOf(i);
				tieExists = false;
			}

			// If the scores are the same, prepare to guess...
			else if (score == bestScore) {
				tieExists = true;
				tiedAnswers.add(String.valueOf(i));
				if (!tiedAnswers.contains(bestAnswer)) {
					tiedAnswers.add(bestAnswer);
				}
			}
		}

		// If there is a tie, just make a random guess
		if (tieExists) {
			Random random = new Random();
			bestAnswer = String.valueOf(tiedAnswers.get(random
					.nextInt((tiedAnswers.size()))));
		}

		return bestAnswer;
	}

	/******************************************************************************
	 * Method to solve a 3x3 Matrix 
	 * @param problem - Raven's Problem to solve
	 * @return String - the agent's best answer for the problem
	 ******************************************************************************/
	public String solve3x3(VisualRavensProblem problem) {

		System.out.print("Solving 3x3 Question: " + problem.getName());
		// For cases when multiple "best" answers exist
		List<String> tiedAnswers = new ArrayList<String>();
		boolean tieExists = false;

		String bestAnswer = "";
		int bestScore = -999;

		// Get each of the figures in the questions
		HashMap<String, VisualRavensFigure> questionSet = problem.getFigures();

		// Get the example figures (A&B) and the prompt figure (C)
		RavensFigure figureA = processFigure(questionSet.get("A"), "A");
		RavensFigure figureB = processFigure(questionSet.get("B"), "B");
		RavensFigure figureC = processFigure(questionSet.get("C"), "C");
		
		//Second Row
		RavensFigure figureD = processFigure(questionSet.get("D"), "D");
		RavensFigure figureE = processFigure(questionSet.get("E"), "E");
		RavensFigure figureF = processFigure(questionSet.get("F"), "F");
		
		//Third Row
		RavensFigure figureG = processFigure(questionSet.get("G"), "G");
		RavensFigure figureH = processFigure(questionSet.get("H"), "H");

		// Break the object and it's attributes down into an iterable object
		// where the key is object name, and the value is a map of the object's attribute
		// names and the corresponding attribute values
		HashMap<String, HashMap<String, String>> figureAValues = matchUtil
				.getValuesMap(figureA);
		HashMap<String, HashMap<String, String>> figureBValues = matchUtil
				.getValuesMap(figureB);
		HashMap<String, HashMap<String, String>> figureCValues = matchUtil
				.getValuesMap(figureC);
		
		//Recond Row
		HashMap<String, HashMap<String, String>> figureDValues = matchUtil
				.getValuesMap(figureD);
		HashMap<String, HashMap<String, String>> figureEValues = matchUtil
				.getValuesMap(figureE);
		HashMap<String, HashMap<String, String>> figureFValues = matchUtil
				.getValuesMap(figureF);
		
		//Third Rowo
		HashMap<String, HashMap<String, String>> figureGValues = matchUtil
				.getValuesMap(figureG);
		HashMap<String, HashMap<String, String>> figureHValues = matchUtil
				.getValuesMap(figureH);

		//match the objects using Analogical Reasoning
		matchUtil.matchObjects(figureAValues, figureBValues);
		matchUtil.matchObjects(figureBValues, figureCValues);
		matchUtil.matchObjects(figureCValues, figureDValues);
		matchUtil.matchObjects(figureDValues, figureEValues);
		matchUtil.matchObjects(figureEValues, figureFValues);
		matchUtil.matchObjects(figureFValues, figureGValues);
		matchUtil.matchObjects(figureGValues, figureHValues);
				
		// Calculate the transitions each of the columns/rows
		TransitionList firstRow = new TransitionList();
		firstRow.addAll(frameUtil.calcTransitions(figureAValues, figureBValues));
		firstRow.addAll(frameUtil.calcTransitions(figureBValues, figureCValues));
		
		TransitionList secondRow = new TransitionList();
		secondRow.addAll(frameUtil.calcTransitions(figureDValues, figureEValues));
		secondRow.addAll(frameUtil.calcTransitions(figureEValues, figureFValues));
		
		TransitionList firstCol = new TransitionList();
		firstCol.addAll(frameUtil.calcTransitions(figureAValues, figureDValues));
		firstCol.addAll(frameUtil.calcTransitions(figureDValues, figureGValues));

		TransitionList secondCol = new TransitionList();
		secondCol.addAll(frameUtil.calcTransitions(figureBValues, figureEValues));
		secondCol.addAll(frameUtil.calcTransitions(figureEValues, figureHValues));
		
		//Calculate what the answer should look like based on the transitions found for each col/row
		Frame frameG = frameUtil.convertToFrame(figureGValues);
		Frame calculatedFrame = new Frame("calculated");
		frameUtil.applyTransformation(frameG, firstRow, calculatedFrame);
		frameUtil.applyTransformation(frameG, secondRow, calculatedFrame);
		
		Frame frameC = frameUtil.convertToFrame(figureCValues);
		Frame calculatedColFrame = new Frame("calculatedCol");
		frameUtil.applyTransformation(frameC, firstCol, calculatedColFrame);
		frameUtil.applyTransformation(frameC, secondCol, calculatedColFrame);

		HashMap<String, HashMap<String, String>> calculatedFrameValues = matchUtil
				.getValuesMap(calculatedFrame);
		
		HashMap<String, HashMap<String, String>> calculatedColFrameValues = matchUtil
				.getValuesMap(calculatedFrame);
		
		//The Semantic Network approach
		//Determine the differences which exist in the first row
    	List<SemanticNetworkObj> baselineDiffRowOne = smUtil.calculateDifference(figureAValues, figureBValues);
    	baselineDiffRowOne.addAll(smUtil.calculateDifference(figureBValues, figureCValues));
    	
    	//Determine the differences which exist in the second row
    	List<SemanticNetworkObj> baselineDiffRowTwo = smUtil.calculateDifference(figureDValues, figureEValues);
    	baselineDiffRowTwo.addAll(smUtil.calculateDifference(figureEValues, figureFValues));
    	
    	//Determine the differences which exist in the first column
    	List<SemanticNetworkObj> baselineDiffColOne = smUtil.calculateDifference(figureAValues, figureDValues);
    	baselineDiffColOne.addAll(smUtil.calculateDifference(figureDValues, figureGValues));
    	
    	//Determine the differences which exist in the second column
    	List<SemanticNetworkObj> baselineDiffColTwo = smUtil.calculateDifference(figureBValues, figureEValues);
    	baselineDiffColTwo.addAll(smUtil.calculateDifference(figureEValues, figureHValues));
  
    	
		// Step through each of the possible answers and make Semantic Network and Frame Comparisons
		for (int i = 1; i <= 6; i++) {
			
			// Get the next possible answer
			RavensFigure nextAnswer = processFigure(questionSet.get(String.valueOf(i)), String.valueOf(i));
			
			HashMap<String, HashMap<String, String>> nextValues = matchUtil
					.getValuesMap(nextAnswer);

			//Use Analogical Reasoning to match objects
			matchUtil.matchObjects(figureHValues, nextValues);
			//matchUtil.matchObjects(figureBValues, figureCValues);
			
			//-----------------------------------------
			//Frames approach
			//------------------------------------------
			int score = frameUtil.compareFrames(nextValues, calculatedFrameValues);
			
			//Frames approach
			score = frameUtil.compareFrames(nextValues, calculatedColFrameValues);
			
			//------------------------------------------------------
			//Propositional Logic Approach -     		
			//------------------------------------------------------
				
			for(String objName : nextValues.keySet()) {
				if(nextValues.get(objName) != null) {
					for(String attrName : nextValues.get(objName).keySet()) {
						
						//See if the agent can find a shape pattern within a row/column
				    	boolean hasColumnShapePattern = false;
				    	boolean hasRowShapePattern = false;
				    	
				    	//Determine if there is a shape pattern in the first row
				    	Pattern row1ShapePattern = logicUtil.findThreeShapePattern(figureAValues, figureBValues, figureCValues, attrName);
				    	
				    	//Determine if there is a shape pattern in the second row
				    	Pattern row2ShapePattern = logicUtil.findThreeShapePattern(figureDValues, figureEValues, figureFValues, attrName);
				    	
				    	//Determine if there is a shape pattern in the first column
				    	Pattern col1ShapePattern = logicUtil.findThreeShapePattern(figureAValues, figureDValues, figureGValues, attrName);
				    	
				    	//Determine if there is a shape pattern in the second column
				    	Pattern col2ShapePattern = logicUtil.findThreeShapePattern(figureBValues, figureFValues, figureHValues, attrName);
				    	
				    	//See if the same pattern was found in the first and second rows 
				    	if(row1ShapePattern != null && row2ShapePattern != null && row1ShapePattern.getPattern().containsAll(row2ShapePattern.getPattern())) {
				    		hasRowShapePattern = true;
				    	}
				    	
				    	//See if the same pattern was found in the first and second columns 
				    	if(col1ShapePattern != null && col2ShapePattern != null &&  col1ShapePattern.getPattern().containsAll(col2ShapePattern.getPattern())) {
				    		hasColumnShapePattern = true;
				    	}
				    	
						if(hasRowShapePattern) {
							
							//Find the attribute value that we are missing from the pattern
							String missingShape = logicUtil.findMissingPatternValue(figureGValues, figureHValues, row1ShapePattern, attrName);
							
							//See if the current object has the attribute value we are looking to find for the attribute with the "attrName"
							if(logicUtil.checkHasAttr(nextValues, missingShape, attrName)) {
								score += Agent.PATTERNWEIGHT;
							}
						}
						if(hasColumnShapePattern){
							
							//Find the attribute value that we are missing from the pattern
							String missingShape = logicUtil.findMissingPatternValue(figureCValues, figureFValues, col1ShapePattern, attrName);
							
							//See if the current object has the attribute value we are looking to find for the attribute with the "attrName"
							if(logicUtil.checkHasAttr(nextValues, missingShape, attrName)) {
								score += Agent.PATTERNWEIGHT;
							}
						}
					}
				}
			}
			
			//------------------------------------------------------
			//Semantic Network Approach -     		
			//------------------------------------------------------
    		//Determine the differences which exist between Figure A and Figure B
        	List<SemanticNetworkObj> testDiffColOne = smUtil.calculateDifference(figureCValues, figureFValues);
        	testDiffColOne.addAll(smUtil.calculateDifference(figureFValues, nextValues));
        	
        	//Determine the differences which exist between Figure A and Figure C
        	List<SemanticNetworkObj> testDiffRowOne = smUtil.calculateDifference(figureGValues, figureHValues);
        	testDiffRowOne.addAll(smUtil.calculateDifference(figureHValues, nextValues));

    		//Compare the differences between A&B to those between C&Answer "i"
    		score += (smUtil.compareDifference(testDiffColOne, baselineDiffColOne) * Agent.SEMANTICWEIGHT);
    		
    		//Compare the differences between A&B to those between C&Answer "i"
    		score += (smUtil.compareDifference(testDiffColOne, baselineDiffColTwo) * Agent.SEMANTICWEIGHT);
    		
    		//Compare the differences between A&C to those between B&Answer "i"
    		score += (smUtil.compareDifference(testDiffRowOne, baselineDiffRowOne) * Agent.SEMANTICWEIGHT);
    		
    		//Compare the differences between A&C to those between B&Answer "i"
    		score += (smUtil.compareDifference(testDiffRowOne, baselineDiffRowTwo) * Agent.SEMANTICWEIGHT);
    		
			// Capture if this score is better than the previous best
			if (score > bestScore) {
				bestScore = score;
				bestAnswer = String.valueOf(i);
				tieExists = false;
			}

			// If the scores are the same, prepare to guess...
			else if (score == bestScore) {
				tieExists = true;
				tiedAnswers.add(String.valueOf(i));
				if (!tiedAnswers.contains(bestAnswer)) {
					tiedAnswers.add(bestAnswer);
				}
			}
		}

		// If there is a tie, just make a random guess
		if (tieExists) {
			//System.out.println("  Tie EXISTED: " + tiedAnswers.toString());
			Random random = new Random();
			bestAnswer = String.valueOf(tiedAnswers.get(random
					.nextInt((tiedAnswers.size()))));
		}

		return bestAnswer;
	}


}
