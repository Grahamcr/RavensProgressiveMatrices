package project4;

import java.util.ArrayList;
import java.util.List;

/********************************************************************
 * The Frame class is used to represent the Raven's Figure using the
 * data representation method of Frames. Each frame contains a name
 * and a list of filler-value pairs
 *
 *@author Craig Graham
**********************************************************************/
public class Frame {

	/*** List of fillers and their values for this frame */
	private List<FrameFiller> fillers;
	
	/*** Name of the frame */
	private String name;
	

	/***************************************************************
	 * Constructor with name 
	****************************************************************/
	public Frame(String name) {
		fillers = new ArrayList<FrameFiller>();
		this.name = name;
	}

	/***************************************************************
	 * Default constructor
	****************************************************************/
	public Frame() {
		fillers = new ArrayList<FrameFiller>();
		this.name = name;
	}
	
	
	/***************************************************************
	 *
	 * Setters and Getters for class instance variables 
	 *
	****************************************************************/
	public void addFillers(List<FrameFiller> toAdd) {
		fillers.addAll(toAdd);		
	}
	
	public FrameFiller getFillerByName(String objectName) {
		FrameFiller toReturn = null;
		for(FrameFiller filler : fillers) {
			if(filler.getName().equals(objectName)) {
				toReturn = filler;
			}
		}
		return toReturn;
	}
	
	public List<FrameFiller> getFillers() {
		return fillers;
	}

	public void setFillers(List<FrameFiller> filler) {
		this.fillers = filler;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void remove(FrameFiller filler) {
		fillers.remove(filler);
	}
	
	@Override
	public String toString() {
		return "Frame [" + fillers + ", name=" + name + "]";
	}

	
}
