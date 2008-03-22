package org.opensourcephysics.stp.percolation;

import java.awt.*;
import java.util.Random;
import org.opensourcephysics.display.*;
// import org.opensourcephysics.display2d.Lattice;

public class PercolationLattice implements Measurable {
    public int L;
    int[] currentSite = new int[2];
    Color selectedColor;
    static Color OCCUPIED_COLOR = Color.red;
    static Color EMPTY_COLOR = Color.white;
    int NONE = -99;
    // no cluster number is selected
    int selectedCluster = NONE;
    // the selected cluster number
    public int[][] rsite;
    boolean displayClusterNumbers = true;

    public void initialize(int L, double p, Random random) {
	this.L = L;
	rsite = new int[L + 1][L + 1];
	fill(random, L, p);
	selectedCluster = NONE;
    }
    
    public void setDisplayCLusterNumbers(boolean b) {
	displayClusterNumbers = b;
    }
    
    public void draw(DrawingPanel drawingPanel, Graphics g) {
	if(rsite == null)
	    return;
	int cellSizeX = (int) drawingPanel.getXPixPerUnit();
	int cellSizeY = (int) drawingPanel.getYPixPerUnit();
	for(int y = 1; y <= L; y++)
	    for(int x = 1; x <= L; x++) {
		int state = rsite[x][y];
		if(state == selectedCluster)
		    g.setColor(selectedColor);
		else if(state == HoshenKopelman.EMPTY)
		    g.setColor(EMPTY_COLOR);
		else
		    g.setColor(OCCUPIED_COLOR);
		int px0 = drawingPanel.xToPix(x);
		int py0 = drawingPanel.yToPix(y+1);
		if(currentSite != null && currentSite[0] == x && currentSite[1] == y)
		    g.setColor(Color.yellow);
		
		g.fillRect(px0, py0, cellSizeX, cellSizeY);
		
		if(displayClusterNumbers && state != 0) {
		    g.setColor(Color.black);
		    FontMetrics fm = g.getFontMetrics();
		    String text = String.valueOf(state);
		    int sw=fm.stringWidth(text);
		    int sh=fm.getHeight();
		    g.drawString(text, px0 + cellSizeX/2 - sw/2, py0 + cellSizeY/2 + sh/2 );
		}
	    }
    }

    public static void fill(Random random, int[][] rsite, int L, double p) {
	for(int y = 1; y <= L; y++)
	    for(int x = 1; x <= L; x++) {
		double d = random.nextDouble();
		if(d < p)
		    rsite[x][y] = HoshenKopelman.OCCUPIED;
		else
		    rsite[x][y] = HoshenKopelman.EMPTY;
	    }
    }
    
    public void fill(Random random, int L, double p) {
	for(int y = 1; y <= L; y++)
	    for(int x = 1; x <= L; x++) {
		double d = random.nextDouble();
		if(d < p)
		    rsite[x][y] = HoshenKopelman.OCCUPIED;
		else
		    rsite[x][y] = HoshenKopelman.EMPTY;
	    }
    }
    
    public double getXMin() {
	return 0;
    }
    
    public double getYMin() {
	return 0;
    }
    
    public double getXMax() {
	return L + 2;
    }
    
    public double getYMax() {
	return L + 2;
    }
    
    public boolean isMeasured() {
	if(rsite==null) 
	    return false;;
	return true;
    }

}
