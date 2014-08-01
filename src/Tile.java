import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


public class Tile {

	public int x, y, type, locx, locy;
	
	public Color color;
	
	public static double WIDTH = 40;
	public static double HEIGHT = 40;
	
	public static final int WALL = 0;
	public static final int PATH = 1;
	public static final int GRASS = 2;
	public static final int WATER = 3;
	
	public Tile(int argx, int argy, int t) {
		this.x = argx;
		this.y = argy;
		this.type = t;
		
		switch(this.type) {
		case WALL: color = Color.DARK_GRAY; break;
		case PATH: color = new Color(200, 200, 200); break;
		case GRASS: color = new Color(160, 220, 160); break;
		case WATER: color = new Color(160, 160, 220); break;
		}
		
		this.locx = (int) (this.x*WIDTH+10);
		this.locy = (int) (this.y*HEIGHT+10);
	}
	
	public Graphics draw(Graphics g) {
		
		g.setColor(this.color);
		
		g.fillRect(this.locx, this.locy, (int)WIDTH, (int)HEIGHT); 
		g.setColor(Color.WHITE);
		g.drawRect(this.locx, this.locy, (int)WIDTH, (int)HEIGHT); 
		
		return g;
	}
	
	public boolean isTilePathable() {
		switch(this.type) {
		case WALL: return false;
		}
		
		return true;
	}
	
}
