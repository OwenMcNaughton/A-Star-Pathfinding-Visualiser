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


public class Window extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	public static final int width = 100;
	public static final int height = 60;
	
	public static Tile[][] grid;
	public Agent[] agents;
	public Intpo[] waypoints;
	
	public JButton[] buttons;
	public JSlider[] sliders;
	 
	public String[] errors; public boolean[] errorB;
	public String[] successes; public boolean[] successB;

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
							a.pathTickMax = 10;
						} else if(grid[a.x][a.y].type == Tile.GRASS) {
							a.pathTickMax = 20;
						} else if(grid[a.x][a.y].type == Tile.WATER) {
							a.pathTickMax = 100;
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
	
	public void setupButtons() {
		this.buttons = new JButton[10];
		
		this.buttons[0] = new JButton("New Map");
	    this.buttons[0].setActionCommand("NEW_MAP");
	    this.buttons[0].addActionListener(this);
	    Dimension size = this.buttons[0].getPreferredSize();
	    this.buttons[0].setBounds(1050, 20, size.width, size.height);
	    add(this.buttons[0]);
	    
	    this.sliders = new JSlider[10];
	    
	    this.sliders[0] = new JSlider(0, 1, 100, 96);
	    size = this.sliders[0].getPreferredSize();
	    this.sliders[0].setBounds(1020, 80, 140, size.height);
	    add(this.sliders[0]);
	    
	    this.sliders[1] = new JSlider(0, 0, 100, 8);
	    size = this.sliders[1].getPreferredSize();
	    this.sliders[1].setBounds(1020, 120, 140, size.height);
	    add(this.sliders[1]);
	    
	    this.sliders[2] = new JSlider(0, 1, 100, 1);
	    size = this.sliders[2].getPreferredSize();
	    this.sliders[2].setBounds(1020, 170, 140, size.height);
	    add(this.sliders[2]);
	    
	    this.sliders[3] = new JSlider(0, 0, 10, 0);
	    size = this.sliders[3].getPreferredSize();
	    this.sliders[3].setBounds(1020, 210, 140, size.height);
	    add(this.sliders[3]);
	    
	    this.sliders[4] = new JSlider(0, 1, 100, 1);
	    size = this.sliders[4].getPreferredSize();
	    this.sliders[4].setBounds(1020, 260, 140, size.height);
	    add(this.sliders[4]);
	    
	    this.sliders[5] = new JSlider(0, 0, 10, 0);
	    size = this.sliders[5].getPreferredSize();
	    this.sliders[5].setBounds(1020, 300, 140, size.height);
	    add(this.sliders[5]);
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
							grid[i][j].type = Tile.WALL;
						}
					}
					if(i < width - 1) {
						if(grid[i+1][j].type == Tile.WALL && gen.nextInt(30) == 0) {
							grid[i][j].type = Tile.WALL;
						}
					}
					if(j > 0) {
						if(grid[i][j-1].type == Tile.WALL && gen.nextInt(30) == 0) {
							grid[i][j].type = Tile.WALL;
						}
					}
					if(j < height - 1) {
						if(grid[i][j+1].type == Tile.WALL && gen.nextInt(30) == 0) {
							grid[i][j].type = Tile.WALL;
						}
					}
				}
			}
		}
		
		for(int i = 0; i != width; i++) {
			for(int j = 0; j != height; j++) {
				if(sliders[2].getValue() != 1) {
					if(grid[i][j].type == Tile.PATH && gen.nextInt(101-sliders[2].getValue()) == 0) {
						grid[i][j].type = Tile.GRASS;
					}
				}
			}
		}
		
		for(int i = 0; i != width; i++) {
			for(int j = 0; j != height; j++) {
				if(sliders[4].getValue() != 1) {
					if(grid[i][j].type == Tile.PATH && gen.nextInt(101-sliders[4].getValue()) == 0) {
						grid[i][j].type = Tile.WATER;
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
								grid[i][j].type = Tile.GRASS;
								
							}
						}
						if(i < width - 1) {
							if(grid[i+1][j].type == Tile.GRASS && gen.nextInt(3) == 0) {
								grid[i][j].type = Tile.GRASS;
							}
						}
						if(j > 0) {
							if(grid[i][j-1].type == Tile.GRASS && gen.nextInt(3) == 0) {
								grid[i][j].type = Tile.GRASS;
							}
						}
						if(j < height - 1) {
							if(grid[i][j+1].type == Tile.GRASS && gen.nextInt(3) == 0) {
								grid[i][j].type = Tile.GRASS;
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
								grid[i][j].type = Tile.WATER;
								
							}
						}
						if(i < width - 1) {
							if(grid[i+1][j].type == Tile.WATER && gen.nextInt(3) == 0) {
								grid[i][j].type = Tile.WATER;
							}
						}
						if(j > 0) {
							if(grid[i][j-1].type == Tile.WATER && gen.nextInt(3) == 0) {
								grid[i][j].type = Tile.WATER;
							}
						}
						if(j < height - 1) {
							if(grid[i][j+1].type == Tile.WATER && gen.nextInt(3) == 0) {
								grid[i][j].type = Tile.WATER;
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
		
		super.paintComponent(g);
		setBackground(Color.BLACK);
		
		g.setColor(Color.WHITE);
		
		g.drawString("Wall Spawn Rate", 1030, 74);
		g.drawString("Wall Size", 1030, 114);
		g.drawString("Grass Spawn Rate", 1030, 164);
		g.drawString("Grass Size", 1030, 204);
		g.drawString("Water Spawn Rate", 1030, 254);
		g.drawString("Water Size", 1030, 294);
		
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
			g.drawString("" + i, waypoints[i].x*Tile.WIDTH + 10, waypoints[i].y*Tile.HEIGHT + 20);
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
}
