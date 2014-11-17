package project4;

import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
 * A representation of a pattern which exists in a column or row for a given
 * object
 *
 *@author Craig Graham.
 ********************************************************************************/
public class Pattern {

	private String objectName;
	private List<String> pattern;
	
	/*******************************************************************************
	 * Default constructor that takes the list for the pattern and the object name
	 * 
	 * @param objectName - String: object name
	 * @param pattern - List<String>: the attribute pattern
	 ********************************************************************************/
	public Pattern(String objectName, List<String> pattern) {
		this.objectName = objectName;
		this.pattern = pattern;
	}

	/*******************************************************************************
	 * Getters and Setters
	 * 
	 ********************************************************************************/
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public List<String> getPattern() {
		if(pattern == null) {
			pattern = new ArrayList<String>();
		}
		return pattern;
	}

	public void setPattern(List<String> pattern) {
		this.pattern = pattern;
	}
	
	
}
