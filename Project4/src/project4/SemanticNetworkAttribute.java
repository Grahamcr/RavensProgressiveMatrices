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
public class SemanticNetworkAttribute {

	/** Name for this attribute as taken from the text file***/
	private String name;
	
	/** Value of attribute in the second figure ***/
	private String oldVal;
	
	/** Value of attribute in second figure ***/
	private String newVal;
	
	/**boolean if the value for this attribute is 
	 different in the first figure then it is in the second ***/
	private boolean change;
	
	/**Percent which this attribute changed by (if applicable) ***/
	private int percent;
	
	/**************************************************
	 * Default Constructor
	 **************************************************/
	public SemanticNetworkAttribute() {
		
	}
	
	/**************************************************
	 * Constructor with Values
	 **************************************************/
	public SemanticNetworkAttribute(String name, String oldVal, String newVal,
			boolean change, int percent) {
		super();
		this.name = name;
		this.oldVal = oldVal;
		this.newVal = newVal;
		this.change = change;
		this.percent = percent;
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

	public String getOldVal() {
		return oldVal;
	}

	public void setOldVal(String oldVal) {
		this.oldVal = oldVal;
	}

	public String getNewVal() {
		return newVal;
	}

	public void setNewVal(String newVal) {
		this.newVal = newVal;
	}

	public boolean getChange() {
		return change;
	}

	public void setChange(boolean change) {
		this.change = change;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}


	@Override
	public String toString() {
		return "[name=" + name + ", oldVal=" + oldVal
				+ ", newVal=" + newVal + ", change=" + change + ", percent="
				+ percent + "]";
	}
	
	
}
