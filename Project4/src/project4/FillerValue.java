package project4;

/***************************************************
 * This class is used to track the value of an
 * attribute a certain object has in the first
 * figure and then in the second figure.  A 
 * collection of this class for one object makes 
 * up the transition lines for that object within 
 * a Semantic Network.
 * 
 * @author Craig Graham
 ***************************************************/
public class FillerValue {

	/** Name for this attribute as taken from the text file***/
	private String name;
	
	/** Value of attribute ***/
	private String val;
		
	/**************************************************
	 * Default Constructor
	 **************************************************/
	public FillerValue() {
		
	}
	
	/**************************************************
	 * Constructor with Values
	 **************************************************/
	public FillerValue(String name, String val) {
		super();
		this.name = name;
		this.val = val;
	}

	@Override
	public String toString() {
		return "[" + name + ", " + val + "]";
	}

	/***************************************************
	 * Getters and Setters for Instance Variables
	 ***************************************************/
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getVal() {
		return val;
	}	
}
