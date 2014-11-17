package project4;

import project4.CPair;
import project4.Shape;

public class CoordinateObject {

	CPair topLeft;
	CPair topRight;
	CPair bottomLeft;
	CPair bottomRight;
	CPair middleLeft;
	CPair middleRight;
	int topWidth;
	int middleWidth;
	int bottomWidth;
	int height;
	boolean followsCurve;
	boolean isFilled;
	Shape shape;
	final int TOLERANCE = 8;
	
	public CoordinateObject() {

	}
	
	
	public CoordinateObject(CPair topLeft, CPair topRight, CPair middleLeft, CPair middleRight, CPair bottomLeft,
			CPair bottomRight, int topWidth, int middleWidth, int bottomWidth, int height, boolean followsCurve,
			boolean isFilled) {
		this.topLeft = topLeft;
		this.topRight = topRight;
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
		this.middleLeft = middleLeft;
		this.middleRight = middleRight;
		this.topWidth = topWidth;
		this.bottomWidth = bottomWidth;
		this.middleWidth = middleWidth;
		this.height = height;
		this.followsCurve = followsCurve;
		this.isFilled = isFilled;
		shape = determineShape();
	}

	private Shape determineShape() {
		Shape toReturn = Shape.UNKNOWN;
		if((Math.abs(topWidth - bottomWidth) < TOLERANCE) && ((middleWidth == 0) || (Math.abs(bottomWidth - middleWidth) < TOLERANCE))  && (Math.abs(height - topWidth) < TOLERANCE) && !followsCurve)  {
			toReturn = Shape.SQUARE;
		}else if(topWidth < middleWidth && ((middleWidth < bottomWidth) || bottomWidth == 0) && (Math.abs(topLeft.getX() - topRight.getX()) < TOLERANCE)) {
			toReturn = Shape.TRIANGLE;
		}else if(bottomWidth < middleWidth && ((middleWidth < topWidth)) && (Math.abs(bottomLeft.getX() - bottomRight.getX()) < TOLERANCE)) {
			toReturn = Shape.UPSIDEDOWNTRIANGLE;
		}else if(Math.abs(middleWidth - height) < TOLERANCE && followsCurve) {
			if(height >= 140 && topWidth > 50 && bottomWidth > 50) {
				toReturn = Shape.HEXAGON;
			}else {
				toReturn = Shape.CIRCLE;
			}
		}else if(Math.abs(middleWidth - height) < TOLERANCE && (Math.abs(bottomWidth - topWidth) < TOLERANCE) && !followsCurve && Math.abs(middleWidth - bottomWidth) > TOLERANCE) {
			toReturn = Shape.CROSS;
		}else if(Math.abs(middleWidth - bottomWidth) < TOLERANCE && (Math.abs((topWidth / 3) - bottomWidth) < TOLERANCE)) { 
			toReturn = Shape.CROSS_BOTTOM;
		}
		return toReturn;
	}

	public CPair getMiddleLeft() {
		return middleLeft;
	}

	public void setMiddleLeft(CPair middleLeft) {
		this.middleLeft = middleLeft;
	}

	public CPair getMiddleRight() {
		return middleRight;
	}

	public void setMiddleRight(CPair middleRight) {
		this.middleRight = middleRight;
	}

	public int getMiddleWidth() {
		return middleWidth;
	}

	public void setMiddleWidth(int middleWidth) {
		this.middleWidth = middleWidth;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isFollowsCurve() {
		return followsCurve;
	}

	public void setFollowsCurve(boolean followsCurve) {
		this.followsCurve = followsCurve;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public void setFilled(boolean isFilled) {
		this.isFilled = isFilled;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public CPair getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(CPair topLeft) {
		this.topLeft = topLeft;
	}

	public CPair getTopRight() {
		return topRight;
	}

	public void setTopRight(CPair topRight) {
		this.topRight = topRight;
	}

	public CPair getBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(CPair bottomLeft) {
		this.bottomLeft = bottomLeft;
	}

	public CPair getBottomRight() {
		return bottomRight;
	}

	public void setBottomRight(CPair bottomRight) {
		this.bottomRight = bottomRight;
	}

	public int getTopWidth() {
		return topWidth;
	}

	public void setTopWidth(int topWidth) {
		this.topWidth = topWidth;
	}

	public int getBottomWidth() {
		return bottomWidth;
	}

	public void setBottomWidth(int bottomWidth) {
		this.bottomWidth = bottomWidth;
	}
	
	
}
