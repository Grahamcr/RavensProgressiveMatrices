package project4;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/********************************************************************************
 * The ImageProcessor class is a Utility used to trace out the shapes contained
 * within a single image file.  The Agent will convert the pixels to a 2D
 * Array which contains either a 0 for a white pixel for a 1 for a black pixel.
 * Using this map, the agent will trace the outside edge of the shape in order
 * to learn it's shape and size.  Finally it will trace the inside
 * of the shape to see if it is filled or not.
 * 
 * @author Craig Graham
 ********************************************************************************/
public class ImageProcessor {


	/**************************************************************************
	 * Trace an outline of each object in the image in order to gain knowledge
	 * about the object.  Based on the knowledge gathered about the object's
	 * width, height and cordinate points, determine what type of object it is
	 * and some of it's attributes. 
	 * 
	 * @param in - BufferedImage: Image to process 
	 * @param name - Name of the figure
	 * @return
	 **************************************************************************/
	public RavensFigure processImage(BufferedImage in, String name) {
		
		RavensFigure toReturn = new RavensFigure(name);
		ArrayList<RavensObject> ravenObjects = new ArrayList<RavensObject>();
		int objectCount = 0;
		
		//Process Image top to bottom left to right  - one row at a time starting from the top
		int maxX = in.getWidth();
		int maxY = in.getHeight();
		int minX = in.getMinX();
		int minY = in.getMinY();
		
		//Create an image map
		int[][] map = new int[maxY-minY][maxX-minX];
		
		for(int y = minY; y < maxY; y++)  {
			for(int x = minX; x < maxX; x++)  {
				int rgb = in.getRGB(x, y);				
				int r = (rgb)&0xFF;
				int g = (rgb>>8)&0xFF;
				int b = (rgb>>16)&0xFF;
				map[y][x] = (r == 0 && g == 0 && b == 0) ? 1 : 0;
			}
		}
		for(int y = minY; y < maxY; y++)  {
			for(int x = minX; x < maxX; x++)  {
				int leftOf = 0;
				int topLeftOf = 0;
				int topLeftOfPlus1 = 0;
				int topLeftOfPlus2 = 0;
				int topLeftOfPlus3 = 0;
				int topOf = 0;
				int topRightOf = 0;
				int topRightOfPlus1 = 0;
				int topRightOfPlus2 = 0;
				int topRightOfPlus3 = 0;
				
				try {
					leftOf = map[y][x-1];
					topLeftOf = map[y-1][x-1];
					topLeftOfPlus1 = map[y-1][x-2];
					topLeftOfPlus2 = map[y-1][x-3];
					topLeftOfPlus3 = map[y-1][x-7];
					topOf = map[y-1][x];
					topRightOf = map[y-1][x+1];
					topRightOfPlus1 = map[y-1][x+2];
					topRightOfPlus2 = map[y-1][x+3];
					topRightOfPlus3 = map[y-1][x+7];
				}catch(ArrayIndexOutOfBoundsException e) {
					
				}
				int current = map[y][x];
				
				//check to see if we ran into the top of a new shape
				if(current == 1 && (leftOf == 0 && topLeftOf == 0 && topOf == 0 && topRightOf == 0 && 
						topRightOfPlus1 == 0 && topLeftOfPlus1 == 0  &&  topRightOfPlus2 == 0 && topLeftOfPlus2 == 0  && 
						topRightOfPlus3 == 0 && topLeftOfPlus3 == 0)) {
					
					//Double check there is nothing to the left
					boolean foundaOne = false;
					for(int toLeft = x-1; toLeft > 0; toLeft--) {
						int leftOfCheck = map[y][toLeft];
						if(leftOfCheck ==  1) {
							foundaOne = true;
							break;
						}
					}
					if(!foundaOne)  {
						//New shape has been found - create an object to represent it
						objectCount++;
						RavensObject ravenObj = new RavensObject(String.valueOf(objectCount));
						
						
						//Start to figure out what this shape is by seeing how far right it goes
						int topRight = x;
						int topLeft = x;
						while(map[y][topRight] == 1) {
							topRight++;
						}
						
						//Now that we know how wide the top is, shoot down the middle to find the height
						int height = 0;
						int objectTop = y;
						int middle = topLeft + ((topRight - topLeft) / 2);
						
						
						//Next we need to find the height, so start following the outline of the shape
						//until we run into the line that goes down the center of the shape.
						int[] cords =  new int[2];
						int[] last = new int[2];
						//X Coordinate
						cords[0] = topRight;
						
						//Y Coordinate
						cords[1] = objectTop;
						last = cords;
						int curveCount = 0;
						ArrayList<CPair> edges = new ArrayList<CPair>();
						while(true) {
							int[] tmp = findNextInOutline(cords, map, last);
							last = cords;
							cords = tmp;
							edges.add(new CPair(cords[0], cords[1]));
							if(cords[0] != last[0] && cords[1] != last[1]) {
								curveCount++;
							}
							if(cords[0] == middle && Math.abs(cords[1] - objectTop) > 5)  {
								//found our way to the bottom middle
								break;
							}else if(cords[0] == -99 && cords[1] == -99)  {
								//found our way into a pickle....
								cords = last;
								cords[0] = cords[0] + 1;
								cords[1] = cords[1] + 1;
								break;
							}
							
						}
						
						height = cords[1] - objectTop;
						
						//From the height we know where the bottom is, so find out how wide the bottom is
						int bottomLeft = middle;
						int bottomRight = middle;
						
						//See how far right we can go from the bottom middle point
						while(map[objectTop + (height)][bottomRight] == 1) {
							bottomRight++;
						}
						
						//See how far left we can go from the bottom middle point
						while(map[objectTop + (height)][bottomLeft] == 1) {
							bottomLeft--;
						}
						
						//From the height we know where the bottom is, so find out how wide the middle is
						int middleRight = middle;
						int centerY = objectTop + (height/2);
						
						//See how far right we can go from the bottom middle point
						for(CPair pair : edges)  {
							if(pair.getY() == centerY && middleRight < pair.getX()) {
								middleRight = pair.getX();
							}
						}
						
						int middleLeft = middle - (middleRight - middle);

						//try to figure out if the object is filled or not
						int fillCheck = 1;
						boolean isFilled = false;
						while(map[objectTop+fillCheck][middle] == 1) {
							fillCheck++;
						}
						isFilled = fillCheck > 5;
						
						//See how likely this object is to have followed a curved path
						boolean followsCurve = curveCount > 10;
						
						int topWidth = topRight - topLeft;
						int bottomWidth = bottomRight - bottomLeft;
						int middleWidth = middleRight - middleLeft;
						
						CoordinateObject object = new CoordinateObject(new CPair(topLeft, objectTop), new CPair(topRight, objectTop),
								new CPair(middleLeft, (objectTop + (height/2))), new CPair(middleRight, (objectTop + (height/2))),
								new CPair(bottomLeft, (objectTop + height)), new CPair(bottomRight, (objectTop + height)), topWidth, 
								middleWidth, bottomWidth, height, followsCurve, isFilled);
						
						populateObject(ravenObj, object);
						ravenObjects.add(ravenObj);
						//Mark the edges so that we can recognize them
						for(CPair pair : edges) {
							try {
								map[pair.getY()][pair.getX()] = objectCount;
							} catch(ArrayIndexOutOfBoundsException e) {
								
							}
						}
					}
				}
			}
		}
		toReturn.setObjects(ravenObjects);
		return toReturn;
	}
	/********************************************************************************
	 * Create a RavensObject from the infromation gathered to map out an object
	 * The only attributes supported at this point are fill, size and shape
	 * This method depends on pass by reference.
	 * 
	 * @param ravenObj - RavensObject to be populated with gathered knowledge
	 * @param object - CoordinateObject which contains the information known.
	 *******************************************************************************/
	private void populateObject(RavensObject ravenObj, CoordinateObject object) {
		ArrayList<RavensAttribute> attrs = new ArrayList<RavensAttribute>();
		
		//Set the shape
		attrs.add(new RavensAttribute("shape", object.getShape().toString()));
		
		//Set the fill
		attrs.add(new RavensAttribute("fill", object.isFilled() ? "yes" : "no"));
		
		//Set the size
		if(object.getHeight() >= 140)  {
			attrs.add(new RavensAttribute("size", "large"));
		}else if(object.getHeight() >= 90  && object.getHeight() < 140)  {
			attrs.add(new RavensAttribute("size", "medium"));
		}else if(object.getHeight() < 90)  {
			attrs.add(new RavensAttribute("size", "small"));
		}
		ravenObj.setAttributes(attrs);
	}

	
	/**********************************************************************************
	 * Find the next pixel in the outline of the shape.  Assumes that we are moving
	 * from top-center to bottom-center
	 * @param cords - int[], where x-Cord is 0 and y-Cord is 1
	 * @param map - int[][], representation of the image's pixels (0=white, 1=black)
	 * 
	 * @return int[] - where x-Cord is 0, y-Cord is 1
	 ***********************************************************************************/
	private int[] findNextInOutline(int[] cords, int[][] map, int[] last) {
		int[] toReturn = new int[2];
		int x =  cords[0];
		int y = cords[1];
		//Start looking for the next pixel in the outline, give preference to going 
		//right and down since we are starting at the top and working to the bottom going right
		
		try {
			//look to the right
			if(map[y][x+1] == 1 && (last[0] != (x+1))) {
				toReturn[0] = x+1;
				toReturn[1] = y;
			
			//look down and to the right
			}else if(map[y+1][x+1] == 1  && (last[0] != (x+1) && last[1] != (y+1))) {
				toReturn[0] = x+1;
				toReturn[1] = y+1;
				
			//Look down
			}else if(map[y+1][x] == 1  && (last[1] != (y+1))) {
				toReturn[0] = x;
				toReturn[1] = y+1;
			
			//look down and to the left
			}else if(map[y+1][x-1] == 1  && (last[0] != (x-1) && last[1] != (y+1))) {
				toReturn[0] = x-1;
				toReturn[1] = y+1;
			
			//look to the left  -- Should this be removed??
			}else if(map[y][x-1] == 1  && (last[0] != (x-1) && last[1] != (y))) {
				toReturn[0] = x-1;
				toReturn[1] = y;
			
			//look ?
			}else {
				//Ok.... there isn't a clear path forward when only looking one pixel away, stretch it out....
				//Find the farthest right 1 that is cloest to the last x in the next row and call it good;
				boolean oneFound = false;
				int bestX = 0;
				for(int i = 0; i < map[y+1].length; i++) {
					if(map[y+1][i] == 1)  {
						if(i > bestX && ((Math.abs(bestX - i) <= Math.abs(bestX - x)))) {
							bestX = i;
							oneFound = true;
						}
					}
				}
				
				
				//No 1 exists in the next row, so look in the same row, but move left
				if(!oneFound)  {
					for(int i = 0; i < map[y].length; i++) {
						if(map[y][i] == 1)  {
							if(i > bestX && ((Math.abs(bestX - i) <= Math.abs(bestX - x)))) {
								bestX = i;
							}
						}
					}
					toReturn[1] = y;
					toReturn[0] = bestX-1;
				}else {
					toReturn[1] = y+1;
					toReturn[0] = bestX;
				}
				
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			//System.out.println("Index Out Of Bounds Finding Next 1");
			toReturn[0] = -99;
			toReturn[1] = -99;
		}
		return toReturn;
	}

}
