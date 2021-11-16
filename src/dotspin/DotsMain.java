package dotspin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;

import dotspin.ui.DotSimulator;

public class DotsMain {
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame("Dots Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DotSimulator ds = new DotSimulator();
		ds.setBackground(new Color(0.1f, 0.1f, 0.1f));
		ds.setForeground(new Color(0.8f, 0.8f, 0.8f));
		ds.setFont(new Font("Consolas", Font.PLAIN, 12));
		ds.setPreferredSize(new Dimension(350, 350));
		ds.setSize(ds.getPreferredSize());
		
		frame.add(ds);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		ds.generateDots();
		ds.startThread();
		
	}

}
