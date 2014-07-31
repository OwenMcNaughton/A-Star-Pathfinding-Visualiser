import java.awt.Color;
import java.awt.Graphics;


public class Tile {

	public int x, y, type;
	
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	
	public static final int WALL = 0;
	public static final int PATH = 1;
	public static final int GRASS = 2;
	public static final int WATER = 3;
	
	public Tile(int argx, int argy, int t) {
		this.x = argx;
		this.y = argy;
		this.type = t;
	}
	
	public Graphics draw(Graphics g) {
		switch(this.type) {
		case WALL: g.setColor(Color.DARK_GRAY); break;
		case PATH: g.setColor(new Color(200, 200, 200)); break;
		case GRASS: g.setColor(new Color(160, 220, 160)); break;
		case WATER: g.setColor(new Color(160, 160, 220)); break;
		}
		
		g.fillRect(this.x*WIDTH+10, this.y*HEIGHT+10, WIDTH, HEIGHT); 
		g.setColor(Color.WHITE);
		g.drawRect(this.x*WIDTH+10, this.y*HEIGHT+10, WIDTH, HEIGHT); 
		
		return g;
	}
	
	public boolean isTilePathable() {
		switch(this.type) {
		case WALL: return false;
		}
		
		return true;
	}
	
}
