import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Window extends JPanel implements ActionListener, ChangeListener {
	
	private static final long serialVersionUID = 1L;
	
	public static int width;
	public static int height;
	
	public static int displayWidth = width;
	public static int displayHeight = height;
	
	public static Tile[][] grid;
	public Agent[] agents;
	public Intpo[] waypoints;
	
	public JButton[] buttons;
	public JSlider[] sliders;
	 
	public String[] errors; public boolean[] errorB;
	public String[] successes; public boolean[] successB;
	
	public static long start, end, frames;
	public String fps;

	public class Update implements ActionListener {
		
		public Update() {}
		
		public void actionPerformed(ActionEvent e) {
			for(Agent a : agents) {
				if(a != null) {
					if(a.calculatingPath) {
						
						a.gx = waypoints[a.target].x;
						a.gy = waypoints[a.target].y;
						
						Path p;
						
						if(a.paused) {
							p = a.path(a.x, a.y, a.gx, a.gy, true);
						} else {
							p = a.path(a.x, a.y, a.gx, a.gy, false);
						}
						
						
						if(!a.paused) {
							if(p == null) {
								
								errorB[a.target] = true;
								a.target++;
								
								if(a.target > waypoints.length - 1) {
									a.calculatingPath = false;
								}
							} else {
								successB[a.target] = true;
								a.compTickMax = 0;
								a.calculatingPath = false;
								a.pathing = true;
								Path c = new Path();
								c.add(new Intpo(a.x, a.y));
								for(int i = 0; i != p.getLength(); i++) {
									c.add(p.steps.get(i));
								}
								
							a.currentPath = c;
							}
						}
					}
					
					if(a.pathing) {
						
						if(grid[a.x][a.y].type == Tile.PATH) {
							switch(sliders[6].getValue()) {
							case 0: 
							case 1:
							case 2:
							case 3: a.pathTickMax = 30; break;
							case 4:
							case 5:
							case 6:
							case 7: a.pathTickMax = 10; break;
							case 8:
							case 9:
							case 10: a.pathTickMax = 3; break;
							case 11:
							case 12: a.pathTickMax = 1; break;
							}
							
						} else if(grid[a.x][a.y].type == Tile.GRASS) {
							switch(sliders[6].getValue()) {
							case 0: 
							case 1:
							case 2:
							case 3: a.pathTickMax = 60; break;
							case 4:
							case 5:
							case 6:
							case 7: a.pathTickMax = 20; break;
							case 8:
							case 9:
							case 10: a.pathTickMax = 6; break;
							case 11:
							case 12: a.pathTickMax = 2; break;
							}
						} else if(grid[a.x][a.y].type == Tile.WATER) {
							switch(sliders[6].getValue()) {
							case 0: 
							case 1:
							case 2:
							case 3: a.pathTickMax = 300; break;
							case 4:
							case 5:
							case 6:
							case 7: a.pathTickMax = 100; break;
							case 8:
							case 9:
							case 10: a.pathTickMax = 30; break;
							case 11:
							case 12: a.pathTickMax = 10; break;
							}
						}
						if(a.pathTick > a.pathTickMax) {
							a.pathTick = 0;
							a.xoffset = 0;
							a.yoffset = 0;
							a.x = a.currentPath.steps.get(a.pathIter).x;
							a.y = a.currentPath.steps.get(a.pathIter++).y;
							
							if(a.x == a.gx && a.y == a.gy) {
								a.pathing = false;
								a.currentPath = null;
								a.pathIter = 0;
								a.target++;
								if(a.target < waypoints.length) {
									a.calculatingPath = true;
								}
							}
								
							
							if(a.currentPath != null) {
							    if(a.pathIter < a.currentPath.getLength()) {
									if(a.currentPath.getX(a.pathIter)<a.x) a.orientation = 3;
									else if(a.currentPath.getX(a.pathIter)>a.x) a.orientation = 1;
									else if(a.currentPath.getY(a.pathIter)<a.y) a.orientation = 2;
									else if(a.currentPath.getY(a.pathIter)>a.y) a.orientation = 0;
								}
						    }
							
						} else {
							a.pathTick++;
							switch(a.orientation) {
							case 0: a.yoffset += Tile.HEIGHT/(double)a.pathTickMax; break;
							case 1: a.xoffset += Tile.HEIGHT/(double)a.pathTickMax; break;
							case 2: a.yoffset -= Tile.HEIGHT/(double)a.pathTickMax; break;
							case 3: a.xoffset -= Tile.HEIGHT/(double)a.pathTickMax; break;
							}
							
						}
					}
				}
			}
			
			Window.this.repaint();
		}

	}

	public Window() {
	    setLayout(null);
	    setDoubleBuffered(true);
	    
	    addMouseListener(new Window.MouseController());
	    
	    setupButtons();
	    
	    setupWindow();
	}
	
	public void setupWindow() {	
		setupSize();
		
		fps = "0";
		frames = 0;
		
		grid = new Tile[width][height];
		agents = new Agent[1];
		waypoints = new Intpo[10];
	    
		setupGrid();
		setupAgents();
		setupWaypoints();
		
		errors = new String[10];
		errorB = new boolean[10];
		for(int i = 0; i != errors.length; i++) {
			errors[i] = i + " is not pathable";
			errorB[i] = false;
		}
		
		successes = new String[10];
		successB = new boolean[10];
		for(int i = 0; i != successes.length; i++) {
			successes[i] = i + " computed";
			successB[i] = false;
		}
		
		Timer timer1 = new Timer(1, new Window.Update());
	    timer1.setRepeats(true);
	    timer1.setCoalesce(true);
	    timer1.start();
	}
	
	public void setupSize() {
		switch(sliders[6].getValue()) {
		case 0: width = 20; height = 12; Tile.WIDTH = 50; Tile.HEIGHT = 50; break;
		case 1: width = 30; height = 18; Tile.WIDTH = 33.3333; Tile.HEIGHT = 33.3333; break;
		case 2: width = 40; height = 24; Tile.WIDTH = 25; Tile.HEIGHT = 25; break;
		case 3: width = 50; height = 30; Tile.WIDTH = 20; Tile.HEIGHT = 20; break;
		case 4: width = 60; height = 36; Tile.WIDTH = 16.6667; Tile.HEIGHT = 16.6667; break;
		case 5: width = 70; height = 42; Tile.WIDTH = 14.2857; Tile.HEIGHT = 14.2857; break;
		case 6:	width = 80; height = 48; Tile.WIDTH = 12.5; Tile.HEIGHT = 12.5; break;
		case 7: width = 100; height = 60; Tile.WIDTH = 10; Tile.HEIGHT = 10; break;
		case 8: width = 125; height = 75; Tile.WIDTH = 8; Tile.HEIGHT = 8; break;
		case 9: width = 200; height = 120; Tile.WIDTH = 5; Tile.HEIGHT = 5; break;
		case 10: width = 250; height = 150; Tile.WIDTH = 4; Tile.HEIGHT = 4; break;
		case 11: width = 333; height = 200; Tile.WIDTH = 3; Tile.HEIGHT = 3; break;
		case 12: width = 500; height = 300; Tile.WIDTH = 2; Tile.HEIGHT = 2; break;
		}
	}
	
	public void setupButtons() {
		this.buttons = new JButton[10];
		
		this.buttons[0] = new JButton("New Map");
	    this.buttons[0].setActionCommand("NEW_MAP");
	    this.buttons[0].addActionListener(this);
	    Dimension size = this.buttons[0].getPreferredSize();
	    this.buttons[0].setBounds(1050, 20, size.width, size.height);
	    add(this.buttons[0]);
	    
	    this.sliders = new JSlider[10];
	    
	    this.sliders[0] = new JSlider(0, 1, 100, 95);
	    size = this.sliders[0].getPreferredSize();
	    this.sliders[0].setBounds(1020, 80, 140, size.height);
	    add(this.sliders[0]);
	    
	    this.sliders[1] = new JSlider(0, 0, 100, 10);
	    size = this.sliders[1].getPreferredSize();
	    this.sliders[1].setBounds(1020, 120, 140, size.height);
	    add(this.sliders[1]);
	    
	    this.sliders[2] = new JSlider(0, 1, 100, 1);
	    size = this.sliders[2].getPreferredSize();
	    this.sliders[2].setBounds(1020, 180, 140, size.height);
	    add(this.sliders[2]);
	    
	    this.sliders[3] = new JSlider(0, 0, 10, 0);
	    size = this.sliders[3].getPreferredSize();
	    this.sliders[3].setBounds(1020, 220, 140, size.height);
	    add(this.sliders[3]);
	    
	    this.sliders[4] = new JSlider(0, 1, 100, 1);
	    size = this.sliders[4].getPreferredSize();
	    this.sliders[4].setBounds(1020, 280, 140, size.height);
	    add(this.sliders[4]);
	    
	    this.sliders[5] = new JSlider(0, 0, 10, 0);
	    size = this.sliders[5].getPreferredSize();
	    this.sliders[5].setBounds(1020, 320, 140, size.height);
	    add(this.sliders[5]);
	    
	    this.sliders[6] = new JSlider(0, 0, 12, 7);
	    this.sliders[6].addChangeListener(this);
	    size = this.sliders[6].getPreferredSize();
	    this.sliders[6].setBounds(1020, 380, 140, size.height);
	    add(this.sliders[6]);
	    
	    
	}
	
	public void setupGrid() {
		Random gen = new Random();
		
		for(int i = 0; i != width; i++) {
			for(int j = 0; j != height; j++) {
				int type = Tile.PATH;
				if(sliders[0].getValue() != 1) {
					if(gen.nextInt(101-sliders[0].getValue()) == 0) {
						type = Tile.WALL;
					}
				}
				grid[i][j] = new Tile(i, j, type);
			}
		} 
		
		for(int k = 0; k != sliders[1].getValue(); k++) {
			for(int i = 0; i != width; i++) {
				for(int j = 0; j != height; j++) {
					if(i > 0) {
						if(grid[i-1][j].type == Tile.WALL && gen.nextInt(30) == 0) {
							grid[i][j] = new Tile(i, j, Tile.WALL);
						}
					}
					if(i < width - 1) {
						if(grid[i+1][j].type == Tile.WALL && gen.nextInt(30) == 0) {
							grid[i][j] = new Tile(i, j, Tile.WALL);
						}
					}
					if(j > 0) {
						if(grid[i][j-1].type == Tile.WALL && gen.nextInt(30) == 0) {
							grid[i][j] = new Tile(i, j, Tile.WALL);
						}
					}
					if(j < height - 1) {
						if(grid[i][j+1].type == Tile.WALL && gen.nextInt(30) == 0) {
							grid[i][j] = new Tile(i, j, Tile.WALL);
						}
					}
				}
			}
		}
		
		for(int i = 0; i != width; i++) {
			for(int j = 0; j != height; j++) {
				if(sliders[2].getValue() != 1) {
					if(grid[i][j].type == Tile.PATH && gen.nextInt(101-sliders[2].getValue()) == 0) {
						grid[i][j] = new Tile(i, j, Tile.GRASS);
					}
				}
			}
		}
		
		for(int i = 0; i != width; i++) {
			for(int j = 0; j != height; j++) {
				if(sliders[4].getValue() != 1) {
					if(grid[i][j].type == Tile.PATH && gen.nextInt(101-sliders[4].getValue()) == 0) {
						grid[i][j] = new Tile(i, j, Tile.WATER);
					}
				}
			}
		}
		
		for(int k = 0; k != sliders[3].getValue(); k++) {
			for(int i = 0; i != width; i++) {
				for(int j = 0; j != height; j++) { 
					if(grid[i][j].type == Tile.PATH) {
						if(i > 0) {
							if(grid[i-1][j].type == Tile.GRASS && gen.nextInt(3) == 0) {
								grid[i][j] = new Tile(i, j, Tile.GRASS);
								
							}
						}
						if(i < width - 1) {
							if(grid[i+1][j].type == Tile.GRASS && gen.nextInt(3) == 0) {
								grid[i][j] = new Tile(i, j, Tile.GRASS);
							}
						}
						if(j > 0) {
							if(grid[i][j-1].type == Tile.GRASS && gen.nextInt(3) == 0) {
								grid[i][j] = new Tile(i, j, Tile.GRASS);
							}
						}
						if(j < height - 1) {
							if(grid[i][j+1].type == Tile.GRASS && gen.nextInt(3) == 0) {
								grid[i][j] = new Tile(i, j, Tile.GRASS);
							}
						}
					}
				}
			}
		}
		
		for(int k = 0; k != sliders[5].getValue(); k++) {
			for(int i = 0; i != width; i++) {
				for(int j = 0; j != height; j++) { 
					if(grid[i][j].type == Tile.PATH) {
						if(i > 0) {
							if(grid[i-1][j].type == Tile.WATER && gen.nextInt(3) == 0) {
								grid[i][j] = new Tile(i, j, Tile.WATER);
								
							}
						}
						if(i < width - 1) {
							if(grid[i+1][j].type == Tile.WATER && gen.nextInt(3) == 0) {
								grid[i][j] = new Tile(i, j, Tile.WATER);
							}
						}
						if(j > 0) {
							if(grid[i][j-1].type == Tile.WATER && gen.nextInt(3) == 0) {
								grid[i][j] = new Tile(i, j, Tile.WATER);
							}
						}
						if(j < height - 1) {
							if(grid[i][j+1].type == Tile.WATER && gen.nextInt(3) == 0) {
								grid[i][j] = new Tile(i, j, Tile.WATER);
							}
						}
					}
				}
			}
		}
		
	}
	
	public void setupAgents() {
		Random gen = new Random();
		for(int i = 0; i != agents.length; i++) {
			
			int iter = 0;
			int x = -1; int y = -1;
			do {
				x = gen.nextInt(width); 
				y = gen.nextInt(height);
				iter++;
				if(iter> 1000) break;
			} while(!grid[x][y].isTilePathable());
			
			if(x == -1) {
				for(int j = 0; j != width; j++) {
					for(int k = 0; k != height; k++) {
						if(grid[x][y].isTilePathable()) {
							x = j; y = k;
						}
					}
				}
			}
			
			agents[i] = new Agent(x, y);
			
		}
	}
	
	public void setupWaypoints() {
		Random gen = new Random();
		for(int i = 0; i != waypoints.length; i++) {
			
			int x; int y;
			do {
				x = gen.nextInt(width); 
				y = gen.nextInt(height);
			} while(!grid[x][y].isTilePathable());
			
			waypoints[i] = new Intpo(x, y);
			
		}
	}
	
	public void paintComponent(Graphics g) {
		
		if(frames == 0) {
			start = System.currentTimeMillis();
		}
		
		frames++;
		
		
		if(System.currentTimeMillis() - start > 1000) {
			fps = "" + frames + " fps";
			frames = 0;
		}
		
		super.paintComponent(g);
		setBackground(Color.BLACK);
		
		g.setColor(Color.WHITE);
		
		g.drawString("Wall Spawn Rate", 1030, 74);
		g.drawString("Wall Size", 1030, 114);
		g.drawString("Grass Spawn Rate", 1030, 164);
		g.drawString("1/2 movement speed", 1030, 174);
		g.drawString("Grass Size", 1030, 214);
		g.drawString("Water Spawn Rate", 1030, 264);
		g.drawString("1/10 movement speed", 1030, 275);
		g.drawString("Water Size", 1030, 314);
		g.drawString("Map Size = " + displayWidth + " x " + displayHeight, 1030, 374);
		g.drawString(fps, 1030, 414);
		
		for(int i = 0; i != errors.length; i++) {
			if(errorB[i] == true) {
				g.setColor(Color.RED);
				g.drawString(errors[i], 1030, 500+i*10);
			} else if(successB[i] == true) { 
				g.setColor(Color.GREEN);
				g.drawString(successes[i], 1030, 500+i*10);
			}
		}
		
		for(int i = 0; i != width; i++) {
			for(int j = 0; j != height; j++) {
				grid[i][j].draw(g);
			}
		}
		
		for(Agent a : agents) {
			if(a != null) {
				a.draw(g);
			}
		}
		
		g.setColor(Color.RED);
		for(int i = 0; i != waypoints.length; i++) {
			g.drawString("" + i, (int)(waypoints[i].x*Tile.WIDTH + 10), (int)(waypoints[i].y*Tile.HEIGHT + 20));
			//g.fillRect(waypoints[i].x*Tile.WIDTH + 10, waypoints[i].y*Tile.HEIGHT + 10, Tile.WIDTH, Tile.HEIGHT);
		}

	}

	public void actionPerformed(ActionEvent e) {
		String a = e.getActionCommand();
		
		if(a.equals("NEW_MAP")) {
			setupWindow();
		}
	}

	public class MouseController implements MouseListener {

		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}
	}

	public void stateChanged(ChangeEvent arg0) {
		switch(sliders[6].getValue()) {
			case 0: displayWidth = 20; displayHeight = 12;  break;
			case 1: displayWidth = 30; displayHeight = 18; break;
			case 2: displayWidth = 40; displayHeight = 24; break;
			case 3: displayWidth = 50; displayHeight = 30; break;
			case 4: displayWidth = 60; displayHeight = 36; break;
			case 5: displayWidth = 70; displayHeight = 42; break;
			case 6:	displayWidth = 80; displayHeight = 54; break;
			case 7: displayWidth = 100; displayHeight = 60; break;
			case 8: displayWidth = 125; displayHeight = 75; break;
			case 9: displayWidth = 200; displayHeight = 120; break;
			case 10: displayWidth = 250; displayHeight = 150; break;
			case 11: displayWidth = 333; displayHeight = 200; break;
			case 12: displayWidth = 500; displayHeight = 300; break;
		}
		
	}
}
