package project4;

import java.util.ArrayList;
import java.util.List;

public class TransitionList {

	List<Transition> list;
	
	public TransitionList() {
		list = new ArrayList<Transition>();
	}
	
	public void add(Transition transition) {
		list.add(transition);
	}
	public void addAll(TransitionList list)  {
		this.list.addAll(list.getAll());
	}
	public List<Transition> getAll() {
		return list;
	}
	public TransitionList getTransitionsForObjName(String name) {
		TransitionList toReturn = new TransitionList();
		for(Transition t : list) {
			if(t.getObjectId().equals(name)) {
				toReturn.add(t);
			}
		}
		return toReturn;
	}
}
