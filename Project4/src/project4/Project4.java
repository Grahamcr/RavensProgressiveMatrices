/**
 * DO NOT MODIFY THIS FILE.
 * 
 * When you submit your project, an alternate version of this file will be used
 * to test your code against the sample Raven's problems in this zip file, as
 * well as other problems from the Raven's Test and former students.
 * 
 * Any modifications to this file will not be used when grading your project.
 * If you have any questions, please email the TAs.
 * 
 */

package project4;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * The main driver file for Project4. You may edit this file to change which
 * problems your Agent addresses while debugging and designing, but you should
 * not depend on changes to this file for final execution of your project. Your
 * project will be graded using our own version of this file.
 * 
 */
public class Project4 {
    /**
     * The main method of Project4.
     */
    public static void main(String[] args) {
//    	int bestScore = 0;
//    	int bestSemantic = 0;
//    	int bestLogic = 0;
//    	int bestFrame = 0;
//    	for(int i = 0; i < 5; i++) {
//	      for(int sermantic = 0; sermantic < 15; sermantic = sermantic+1) {
//	    	  for(int logic = 0; logic < 15; logic = logic + 1) {
//	    		  for(int frame = 0; frame < 15; frame = frame + 1) {
//	    			  int count = doSolve(sermantic, frame, logic);
//	    			  if(count >= 10)  {
//	    				  bestScore = count;
//	    				  bestSemantic = sermantic;
//	    				  bestLogic = logic;
//	    				  bestFrame = frame;
//	    				  System.out.println("Best Score: " + bestScore + " Logic: "+ bestLogic  + " Semantic: " + bestSemantic + " Frame: " + bestFrame);
//	    			  }
//	    			  System.out.println("Frame Iteration Done: "  + frame);
//	    		  }
//	    		  System.out.println("Logic Iteration Done "  + logic);
//	    	  }
//	    	  System.out.println("Semantic Iteration Done " + sermantic);
//	      }
//	      System.out.println("Outer Iteration Done  "  + i);
//	      }
//    	System.out.println("Done");
    	doSolve();
    	System.out.println("Done");
    }
    
    private static int doSolve() {
    	int correctAnswer = 0;
    	  // Loading problems from files
        ArrayList<VisualProblemSet> sets = new ArrayList<VisualProblemSet>();       // The variable 'sets' stores multiple problem sets.
                                                                        // Each problem set comes from a different folder in /Problems/
                                                                        // Additional sets of problems will be used when grading projects.
                                                                        // You may also write your own problems.
        
        for(File file : new File("Problems (Image Data)").listFiles()) {             // One problem set per folder in /Problems/
            VisualProblemSet newSet = new VisualProblemSet(file.getName());         // Each problem set is named after the folder in /Problems/
            sets.add(newSet);
            for(File problem : file.listFiles()) {                      // Each file in the problem set folder becomes a problem in that set.
                newSet.addProblem(problem);                             // Make sure to add only problem files to subfolders of /Problems/
            }
        }
        
        // Initializing problem-solving agent from Agent.java
        Agent agent=new Agent();                                        // Your agent will be initialized with its default constructor.

        
        // Running agent against each problem set
        try {
            PrintWriter results=new PrintWriter("Results.txt");         // Results will be written to Results.txt.
                                                                        // Note that each run of the program will overwrite the previous results.
                                                                        // Do not write anything else to Results.txt during execution of the program.

            for(VisualProblemSet set : sets) {                            
                results.println(set.getName());                         // Your agent will solve one problem set at a time.
                results.println("-----------");                         // Problem sets will be individually categorized in the results file.
                
                for(VisualRavensProblem problem : set.getProblems()) {        // Your agent will solve one problem at a time.

                        problem.setAnswerReceived(agent.Solve(problem));// The problem will be passed to your agent as a VisualRavensProblem object as a parameter to the Solve method
                                                                        // Your agent should return its answer at the conclusion of the execution of Solve.
                                                                        // Note that if your agent makes use of VisualRavensProblem.check to check its answer, the answer passed to check() will be used.
                                                                        // Your agent cannot change its answer once it has checked its answer.
                    
                        if(problem.checkAnswer("").equals(problem.getGivenAnswer())) {
                        	correctAnswer++;
                        }
                        results.println(problem.getCorrect() + ": " + problem.getGivenAnswer() + " " + problem.getCorrect() + " (Correct Answer: " + problem.checkAnswer("") + ")");

                }
                results.println("");
            }
            results.close();
        } catch(IOException ex) {
            System.out.println("Unable to create results file:");
            System.out.println(ex);
        }
        return correctAnswer;
    }
}
