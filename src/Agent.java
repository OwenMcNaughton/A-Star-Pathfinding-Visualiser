import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Agent {

	public int x, y, gx, gy, type, pathIter, orientation, target, pathTick, pathTickMax, compTick, compTickMax;
	
	public double xoffset, yoffset;
	
	public Path currentPath;
	
	public ArrayList<Intpo> computed;
	
	public boolean calculatingPath, pathing, showing, paused;
	
	public ArrayList<MemPoint> reachable;
	public ArrayList<MemPoint> closed;
	
	public Agent(int argx, int argy) {
		this.x = argx;
		this.y = argy;
		
		this.xoffset = 0; this.yoffset = 0;
		
		this.orientation = 0;
		this.target = 0;
		this.calculatingPath = true;
		this.pathing = false;
		this.pathTick = 0;
		this.showing = false;
		this.paused = false;
		
		
	}
	
	public Graphics draw(Graphics g) {
		
		g.setColor(new Color(250, 100, 100));
		
		if(this.computed != null) {
			this.compTick = 0;
			for(Intpo i : computed) {
				
					g.fillRect((int)(i.x*Tile.WIDTH+10+Tile.WIDTH/4), 
							(int)(i.y*Tile.HEIGHT+10+Tile.HEIGHT/4), 
							(int)(Tile.WIDTH*.5), (int)(Tile.HEIGHT*.5)); 
				
				
				
			}
		}
		
		g.setColor(Color.YELLOW);
		if(this.currentPath != null) {
			
				for(Intpo i : this.currentPath.steps) {
					g.fillRect((int)(i.x*Tile.WIDTH+10+Tile.WIDTH/4d), (int)(i.y*Tile.HEIGHT+10+Tile.HEIGHT/4d), 
							(int)(Tile.WIDTH/2), (int)(Tile.HEIGHT/2)); 
				
			}
		}
		
		
		int[] xCoords = new int[3];
		int[] yCoords = new int[3];
		
		switch(this.orientation) {
		case 0: xCoords[0] = (int)(this.x*Tile.WIDTH + Tile.WIDTH/2);
				yCoords[0] = (int) (this.y*Tile.HEIGHT + Tile.HEIGHT);
				xCoords[1] = (int) (this.x*Tile.WIDTH);
				yCoords[1] = (int) (this.y*Tile.HEIGHT);
				xCoords[2] = (int) (this.x*Tile.WIDTH + Tile.WIDTH);
				yCoords[2] = (int) (this.y*Tile.HEIGHT); break;
		case 1: xCoords[0] = (int) (this.x*Tile.WIDTH + Tile.WIDTH);
				yCoords[0] = (int) (this.y*Tile.HEIGHT + Tile.HEIGHT/2);
				xCoords[1] = (int) (this.x*Tile.WIDTH);
				yCoords[1] = (int) (this.y*Tile.HEIGHT + Tile.HEIGHT);
				xCoords[2] = (int) (this.x*Tile.WIDTH);
				yCoords[2] = (int) (this.y*Tile.HEIGHT); break;
		case 2: xCoords[0] = (int) (this.x*Tile.WIDTH + Tile.WIDTH/2);
				yCoords[0] = (int) (this.y*Tile.HEIGHT);
				xCoords[1] = (int) (this.x*Tile.WIDTH);
				yCoords[1] = (int) (this.y*Tile.HEIGHT + Tile.HEIGHT);
				xCoords[2] = (int) (this.x*Tile.WIDTH + Tile.WIDTH);
				yCoords[2] = (int) (this.y*Tile.HEIGHT + Tile.HEIGHT); break;
		case 3: xCoords[0] = (int) (this.x*Tile.WIDTH);
				yCoords[0] = (int) (this.y*Tile.HEIGHT + Tile.HEIGHT/2);
				xCoords[1] = (int) (this.x*Tile.WIDTH + Tile.WIDTH);
				yCoords[1] = (int) (this.y*Tile.HEIGHT);
				xCoords[2] = (int) (this.x*Tile.WIDTH + Tile.WIDTH);
				yCoords[2] = (int) (this.y*Tile.HEIGHT+ Tile.HEIGHT); break;
		}
		
		xCoords[0] += 10 + this.xoffset; xCoords[1] += 10 + this.xoffset; xCoords[2] += 10 + this.xoffset;
		yCoords[0] += 10 + this.yoffset; yCoords[1] += 10 + this.yoffset; yCoords[2] += 10 + this.yoffset;
		
		g.setColor(Color.WHITE);
		g.fillPolygon(xCoords, yCoords, 3);
		//g.fillRect(this.x*Tile.WIDTH+10, this.y*Tile.HEIGHT+10, Tile.WIDTH, Tile.HEIGHT);
		
		
		
		return g;
	}
	
	public Path path(int startx, int starty, int goalx, int goaly, boolean resuming) {	
		
		this.pathIter = 0;
	    
		Path p = aStar(new Intpo(startx, starty), new Intpo(goalx, goaly), resuming);
		
	    if(p != null) {
		    if(this.pathIter < p.getLength()) {
				if(p.getX(this.pathIter)<this.x) this.orientation = 2;
				else if(p.getX(this.pathIter)>this.x) this.orientation = 3;
				else if(p.getY(this.pathIter)<this.y) this.orientation = 0;
				else if(p.getY(this.pathIter)>this.y) this.orientation = 1;
			}
	    }
	    
	    return p;
	}
	
	public Path aStar(Intpo start, Intpo end, boolean resuming) {
		if(!resuming) {
			reachable = new ArrayList<MemPoint>();
			MemPoint m = new MemPoint(start); 
			m.from = m;
			m.cost = 0;
			reachable.add(m);
			closed = new ArrayList<MemPoint>();
			this.computed = new ArrayList<Intpo>();
		}
		
		int max = 1;
		int k = 0;
		this.paused = false;
		
		while(reachable.size() != 0) {
			
			if(k > max) {
				this.paused = true;
				break;
			}
			
			k++;
			
			MemPoint i = bestDirection(reachable, end, closed);
			if(i != null) {
				this.computed.add(i.here);
				if(i.here.x == end.x && i.here.y == end.y) {
					return buildPath(i);
				}
			} else {
				break;
			}
			
			reachable.remove(i);
			if(!closed.contains(i)) {
				closed.add(i);
			}
			
			ArrayList<MemPoint> reachable2 = i.adjacents(closed);
			
			for(MemPoint j : reachable2) {
				if(!reachable.contains(j.here)) {
					
					reachable.add(j);
				}
				
				if(i.cost + 1 < j.cost) {
					j.from = i;
					
					int cost = 0;
					if(Window.grid[j.here.x][j.here.y].type == Tile.PATH) {
						cost = 1;
					} else if(Window.grid[j.here.x][j.here.y].type == Tile.GRASS) {
						cost = 2;
					} else if(Window.grid[j.here.x][j.here.y].type == Tile.WATER) {
						cost = 10;
					}
					
					j.cost = i.cost + cost;
				}
			}
		}
		
		return null;
		
	}
	
	public MemPoint bestDirection(ArrayList<MemPoint> reachable, Intpo goal, ArrayList<MemPoint> closed) {
		int min = Integer.MAX_VALUE;
		MemPoint best = null;
		
		for(MemPoint m : reachable) {
			int costToHere = m.cost;
			int costToGoal = manhattanDistance(m.here, goal);
			int totalCost = costToHere + costToGoal;
			
			if(totalCost < min) {
				boolean inClosed = false;
				for(MemPoint c : closed) {
					if(m.here.x == c.here.x && m.here.y == c.here.y) {
						inClosed = true;
						break;
					}
				}
				if(!inClosed) {
					min = totalCost;
					best = m;
				}
				
				
			}
		}
		
		return best;
	}

	public int manhattanDistance(Intpo here, Intpo goal) {
		
		int r = Math.abs(here.x - goal.x) + Math.abs(here.y - goal.y);
		
		return r;
	}

	public Path buildPath(MemPoint m) {
		Path p = new Path();
		
		while(m != null && m.from.here != m.here) {
			p.add(m.here);
			m = m.from;
		}
		
		Path copy = new Path();
		
		for(int i = p.steps.size()-1; i != -1; i--) {
			copy.add(p.steps.get(i));
		}
		
		return copy;
	}
	
}
