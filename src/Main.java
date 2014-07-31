import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Main extends JFrame {

	private static final long serialVersionUID = 1L;

	public Main() {
		
		setDefaultCloseOperation(3);
		setSize(1175, 645);
	    setLocationRelativeTo(null);
	    setTitle("Pathing");
	    
	    setBackground(Color.BLACK);
	    
	    add(new Window());
	    
	    setResizable(false);
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				JFrame ex = new Main();
				ex.setVisible(true);
				
			}
			
		});
	}

}
