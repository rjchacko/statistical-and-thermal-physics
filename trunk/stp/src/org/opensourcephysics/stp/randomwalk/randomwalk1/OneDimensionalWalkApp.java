package org.opensourcephysics.stp.randomwalk.randomwalk1;

import java.text.NumberFormat;
import java.util.Random;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.HistogramFrame;

/**
 * A random walk in 1D. Input Parameters are N, number of steps in one trial and
 * p, probability of right step
 *
 * @author Joshua Gould
 * @author Macneil Shonle
 * @author Peter Sibley
 */
public class OneDimensionalWalkApp extends AbstractSimulation
{
	int N; // maximum number of steps in one trial
	double p; // probability of step to right
	int trials; // number of trials
	int x; // initial position of walker
	double xcum; // accumulate values of x after N steps
	double x2cum; // accumulate values of x*x after N steps

	HistogramFrame histogramFrame = new HistogramFrame("x", "H(x)", "Histogram");

	NumberFormat numberFormat;
	Random random;

	public OneDimensionalWalkApp()
	{
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		random = new Random();
	}

	public void initialize()
	{
		N = control.getInt("N");
		p = control.getDouble("p");
		trials = 0;
		x = 0;
		xcum = 0;
		x2cum = 0;
		histogramFrame.setPreferredMinMaxX(x - N, x + N);
		histogramFrame.setPreferredMinMaxY(0, 10);
		histogramFrame.setAutoscaleY(true);
		random.setSeed(System.currentTimeMillis());
	}

	public void reset()
	{
		N = 16;
		p = 0.5;
		control.setAdjustableValue("N", N);
		control.setAdjustableValue("p", p);
		histogramFrame.clearData();
		histogramFrame.repaint();
	}

	public static void main(String[] args)
	{
		SimulationControl.createApp(new OneDimensionalWalkApp(),args);
	}

	protected void doStep()
	{
		for (int steps = 0; steps < N; steps++)
		{
			if (random.nextDouble() < p)
			{
				x++;
			}
			else
			{
				x--;
			}
		}
		trials++;
		histogramFrame.append(x);
		xcum += x;
		x2cum += x * x;
		double xbar = xcum / trials;
		double x2bar = x2cum / trials;
		x = 0; // move walker back to initial position
		histogramFrame.render();
		control.clearMessages();
		control.println("trials = " + trials);
		control.println("<x> = " + numberFormat.format(xbar));
		control.println("<x^2> = " + numberFormat.format(x2bar));
	}
}
