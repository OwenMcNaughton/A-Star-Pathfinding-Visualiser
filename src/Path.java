import java.util.ArrayList;

public class Path {

	public ArrayList<Intpo> steps;

	public Path() {
		steps = new ArrayList<Intpo>();
	}
	
	public void add(Intpo here) {
		steps.add(here);
		
	}
	
	public int getX(int i) {
		return this.steps.get(i).x;
	}
	
	public int getY(int i) {
		return this.steps.get(i).y;
	}

	public int getLength() {
		return this.steps.size();
	}
	
	
	
}
