package dotspin.ui;

import java.awt.Point;

public class PointDouble extends Point {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6589350430084582274L;
	public double x;
	public double y;
	
	public PointDouble (double x, double y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	public PointDouble () {
		
		x = 0;
		y = x;
		
	}

}
