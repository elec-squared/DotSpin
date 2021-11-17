package dotspin.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class DotSimulator extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7233593544797879072L;
	
	public ArrayList<PointDouble> dots = new ArrayList<PointDouble>();
	public int dotsNumber = 150;
	int dotsThickness = 5;
	public Thread runningThr;
	public boolean shouldRun = true;
	
	public double rotRate = 5;
	public double falloff = 50;
	public int waitTimeNano = 10 * 1000000; // 1 000 000 nanoseconds = 1 millisecond
	
	long debugSleepTimeNano = 10;
	long debugExecTimeNano = 0;
	long debugWaitMeasuredTimeNano = 0;
	boolean mouseDown = false;
	boolean rightMouseDown = false;
	
	public DotSimulator() {
		
		setOpaque(true);
		setFocusable(true);
		
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					mouseDown = true;
				} else {
					rightMouseDown = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					mouseDown = false;
				} else {
					rightMouseDown = false;
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
			
		});
		
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_N) {
					
					dots.add(getMouseRelPos());
					
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_R) {
					
					DotSimulator.this.generateDots();
					
				}
				
				if (e.getKeyCode() == KeyEvent.VK_A) {
					
					int amt = Integer.parseInt(
							JOptionPane.showInputDialog(null, "Amount of dots (int):", "Dot amount query",
									JOptionPane.QUESTION_MESSAGE)
							);
					
					dotsNumber = amt;
					DotSimulator.this.generateDots();
					
				}
				
				if (e.getKeyCode() == KeyEvent.VK_Z) {
					
					double amt = Double.parseDouble(
							JOptionPane.showInputDialog(null, "Rotation rate (double):", "Rotation query",
									JOptionPane.QUESTION_MESSAGE)
							);
					
					if (amt < 1) amt = 1;
					rotRate = amt;
					
				}
				
				if (e.getKeyCode() == KeyEvent.VK_X) {
					
					double amt = Double.parseDouble(
							JOptionPane.showInputDialog(null, "Falloff rate (double):", "Falloff query",
									JOptionPane.QUESTION_MESSAGE)
							);
					if (amt < 5) amt = 5;
					falloff = amt;
					
				}
				
			}
			
		});
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(getFont());
		FontMetrics fm = g2.getFontMetrics();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		g2.setColor(getForeground());
		for (int i = 0; i < dots.size(); i++) {
			
			g.fillRect((int) dots.get(i).x-dotsThickness/2,
					(int) dots.get(i).y-dotsThickness/2, dotsThickness, dotsThickness);
			
		}
		
		g2.setColor(new Color(getBackground().getRed()/255f, getBackground().getGreen()/255f,
				getBackground().getBlue()/255f, 0.7f)); //semi transparent bg for text
		
		g2.fillRect(0, 0, getWidth(), fm.getHeight()*5 + fm.getDescent());
		g2.fillRect(0,
				getHeight() - (fm.getHeight()*2) - fm.getDescent() - fm.getLeading(),
				getWidth(),
				(fm.getHeight()*2) + fm.getDescent() + fm.getLeading());
		
		g2.setColor(getForeground());
		g2.drawString("Press R to regenerate dots.", 0, g.getFontMetrics().getHeight());
		g2.drawString("Press A to change amount of dots. (" + dotsNumber + ")", 0, fm.getHeight() *2);
		g2.drawString("Press Z to change rotation rate. (" + rotRate + ")", 0, fm.getHeight() *3);
		g2.drawString("Press X to change falloff factor. (" + falloff + ")", 0, fm.getHeight() *4);
		g2.drawString("Press or hold N to create a dot at pointer.", 0, fm.getHeight() *5);
		
		g2.drawString("runtime " + (double)debugExecTimeNano/1000000 + " ms", 0, getHeight() - fm.getDescent()
				- fm.getLeading());
		g2.drawString("clock time " + 
					String.format("%05.2f", (double)(debugWaitMeasuredTimeNano)/1000000) + " ms of intended "
				+ String.format("%05.2f", (double)waitTimeNano/1000000) + " ms",
				0, getHeight() - fm.getHeight() - fm.getDescent()
				- fm.getLeading());
		
	}
	
	public void startThread() {
		
		runningThr = new Thread() {
			
			@Override
			public void run() {
				
				while (shouldRun) {
					
					long start = System.nanoTime();
					
					updateSim();
					repaint();
					
					long end = System.nanoTime();
					
					debugExecTimeNano = end-start;
					debugSleepTimeNano = waitTimeNano - debugExecTimeNano;
					if (debugSleepTimeNano<0) debugSleepTimeNano = 0;
					
					try {
						Thread.sleep(debugSleepTimeNano/1000000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					debugWaitMeasuredTimeNano = System.nanoTime() - start;
					
				}
				
			}
			
		};
		
		runningThr.setName("operational thread");
		runningThr.start();
		
	}
	
	public void generateDots() {
		
		dots.clear();
		
		for (int i = 0; i < dotsNumber; i++) {
			
			PointDouble p = new PointDouble();
			p.x = (int) (Math.random() * getWidth());
			p.y = (int) (Math.random() * getHeight());
			
			dots.add(p);
			
		}
		
	}
	
	public void updateSim() {
		
		if (!mouseDown && !rightMouseDown) return;
		
		PointDouble relPos = getMouseRelPos();
		
		// calculate dots SPEEEN
		
		for (int i = 0; i < dots.size(); i++) {
			
			PointDouble current = dots.get(i);
			PointDouble posRelativeToMouse = new PointDouble(current.x - relPos.x, current.y - relPos.y);
			double dist = Math.sqrt(posRelativeToMouse.x * posRelativeToMouse.x
					+ posRelativeToMouse.y * posRelativeToMouse.y);
			double angle = Math.toDegrees(Math.atan2(posRelativeToMouse.y, posRelativeToMouse.x));
			
//			System.out.println(angle);
			
			double change = rotRate - (dist/falloff);
			if (change > rotRate) change = rotRate;
			if (change < 0) change = 0;
			
			if (mouseDown) angle += change;
			if (rightMouseDown) angle -= change;
//			angle += rotRate;
			angle = validateAngle(angle);
			
			double newY = (Math.sin(Math.toRadians(angle)) * dist) + relPos.y;
			double newX = (Math.cos(Math.toRadians(angle)) * dist) + relPos.x;
			
			current.x = newX;
			current.y = newY;
			
		}
		
	}
	
	public double validateAngle(double angle) {
		
		while (angle > 360) angle -= 360;
		while (angle < 0) angle += 360;
		
		return angle;
		
	}
	
	public PointDouble getMouseRelPos() {
		
		PointDouble mousePos = new PointDouble(MouseInfo.getPointerInfo().getLocation().x, 
				MouseInfo.getPointerInfo().getLocation().y);
		PointDouble compPos = new PointDouble( getLocationOnScreen().x, getLocationOnScreen().y );
		PointDouble relPos = new PointDouble(mousePos.x - compPos.x, mousePos.y - compPos.y);
		
		return relPos;
		
	}

}
