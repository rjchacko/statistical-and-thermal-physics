package org.opensourcephysics.stp.randomwalk.randomwalk2;

import java.awt.*;
import java.util.Random;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;
import org.opensourcephysics.frames.*;

/**
 * random walk in two dimensions Input Parameters: nwalkers, number of walkers
 * pleft, probability of step to the left pright, probability of step to the
 * right pdown, probability of of step to the down
 *
 * @author Joshua Gould
 * @author Macneil Shonle
 * @author Peter Sibley
 */
public class TwoDimensionalWalkApp extends AbstractSimulation
		implements
			Drawable
{
	int nwalkers, xmax, ymax, xmin, ymin, time = 0;
	int xpositions[], ypositions[];
	double xbar, ybar, x2, y2,r2;
	
	DisplayFrame displayFrame = new DisplayFrame("Walkers");
	HistogramFrame histogramFrame = new HistogramFrame("r", "H(r)",
			"H(r) versus r");

	Random random;
	double pRight, pLeft, pDown; // probability of walker moving right, left,

	// down

	public TwoDimensionalWalkApp()
	{
		displayFrame.addDrawable(this);
		// /displayFrame.setGutters(10, 10, 10, 10);
		displayFrame.setPreferredMinMax(-100, 100, -100, 100);
		displayFrame.setAutoscaleX(false);
		displayFrame.setAutoscaleY(false);
		random = new Random();
		histogramFrame.setBinWidth(.01);

		histogramFrame.setPreferredMinMaxX(0,10);
		histogramFrame.setAutoscaleX(true);
		histogramFrame.setAutoscaleY(true);
		// histogramFrame.setBinOffset(.005);
	}

	public boolean isMeasured()
	{
		return true;
	}

	public double getXMax()
	{
		return xmax;
	}

	public double getYMax()
	{
		return ymax;
	}

	public double getXMin()
	{
		return xmin;
	}

	public double getYMin()
	{
		return ymin;
	}

	public void initialize()
	{
		nwalkers = control.getInt("nwalkers");
		pLeft = control.getDouble("p left");
		pRight = control.getDouble("p right");
		pDown = control.getDouble("p down");
		double pSum = pLeft + pRight + pDown;
		if(pSum > 1) {   // normalization
		  pLeft = pLeft/pSum;
		  pRight = pRight/pSum;
		  pDown = pDown/pSum;
		}
		xbar = ybar = x2 = y2 = r2 = 0.0;
		
		xpositions = new int[nwalkers];
		ypositions = new int[nwalkers];
		random.setSeed(System.currentTimeMillis());
		time = 0;
		displayFrame.repaint();
	}

	public void reset()
	{
		xmax = 0;
		ymax = 0;
		xmin = 0;
		ymin = 0;
		pRight = 0.25;
		pDown = 0.25;
		pLeft = 0.25;
		control.setValue("nwalkers", 1000);
		control.setValue("p left", pLeft);
		control.setValue("p right", pRight);
		control.setValue("p down", pDown);
		histogramFrame.clearData();
		time = 0;
	}

	public void draw(DrawingPanel drawingPanel, Graphics g)
	{
		int size = 4; // size of a walker in pixels
		g.setColor(Color.red);
		for (int i = 0; i < nwalkers; i++)
		{
			double x = xpositions[i];
			double y = ypositions[i];
			int px = drawingPanel.xToPix(x) - size / 2;
			int py = drawingPanel.yToPix(y) - size / 2;
			g.fillRect(px, py, size, size); // walkers represented as rectangles
			// with a width of 4 pixels

		}
	}

	// Move each walker left, right, up, or down
	public void move()
	{
        time++;
		if (time % 10 == 0)
		{
			histogramFrame.clearData();
		}
		for (int i = 0; i < nwalkers; i++)
		{
			double r = random.nextDouble();
			if (r <= pRight)
			{
				xpositions[i] = xpositions[i] + 1;
			}
			else if (r < pRight + pLeft)
			{
				xpositions[i] = xpositions[i] - 1;
			}
			else if (r < pRight + pLeft + pDown)
			{
				ypositions[i] = ypositions[i] - 1;
			}
			else
			{
				ypositions[i] = ypositions[i] + 1;
			}
			if (time % 10 == 0)
			{
				histogramFrame.append(Math.sqrt(xpositions[i] * xpositions[i]
						+ ypositions[i] * ypositions[i]));
			}
			xmax = Math.max(xpositions[i], xmax);
			ymax = Math.max(ypositions[i], ymax);
			xmin = Math.min(xpositions[i], xmin);
			ymin = Math.min(ypositions[i], ymin);
		}
	}

	public void getAverages(){
		xbar = ybar = x2 = y2 = r2 = 0.0;
		for(int i = 0; i < nwalkers; i++){
			xbar += xpositions[i];
			ybar += ypositions[i];
			x2 += xpositions[i]*xpositions[i];
			y2 += ypositions[i]*ypositions[i];
			r2 += xpositions[i]*xpositions[i] + ypositions[i]*ypositions[i];
		}
		xbar /= nwalkers;
		ybar /= nwalkers;
		x2 /= nwalkers;
		y2 /= nwalkers;
		r2 /= nwalkers;	
	}
	public static void main(String[] args)
	{
		SimulationControl.createApp(new TwoDimensionalWalkApp(),args);
	}

	protected void doStep()
	{
		move();
		displayFrame.render();
		displayFrame.setMessage("time = " + time);
		histogramFrame.render();
		
		getAverages();
		control.println("time = " + time);
		control.println("<x> = " + xbar + "\t\t<y> = " + ybar);
		control.println("<x\u00b2> = " + x2 + "\t\t<y\u00b2> = " + y2);
		control.println("<r\u00b2> = " + r2);
	}
}
