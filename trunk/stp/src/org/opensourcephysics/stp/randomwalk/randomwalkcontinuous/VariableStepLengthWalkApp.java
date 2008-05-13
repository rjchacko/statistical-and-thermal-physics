package org.opensourcephysics.stp.randomwalk.randomwalkcontinuous;

import java.text.NumberFormat;
import java.util.Random;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.HistogramFrame;

import java.awt.Color;
/**
 * A random walk in 1D with continous step size
 *
 * @author Hui Wang
 */
public class VariableStepLengthWalkApp extends AbstractSimulation
{
	int N; // maximum number of steps in one trial
	double p; // probability of step to right
	int trials; // number of trials
	double x; // initial position of walker
	double xcum; // accumulate values of x after N steps
	double x2cum; // accumulate values of x*x after N steps

	HistogramFrame histogramFrame = new HistogramFrame("x", "H(x)", "Histogram");

	NumberFormat numberFormat;
	Random random;

	public VariableStepLengthWalkApp()
	{
		numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		random = new Random();
		histogramFrame.setBinStyle((short)1);
		histogramFrame.setDiscrete(false);
//		set different colors for bin and edge
		histogramFrame.setBinColor(null, Color.red);
	}

	public void initialize()
	{
		N = control.getInt("N");
		p = control.getDouble("p");
		trials = 0;
		x = 0;
		xcum = 0;
		x2cum = 0;
		histogramFrame.setPreferredMinMax(x - N, x + N,0,10);
		histogramFrame.setBinWidth(control.getDouble("bin width"));
		histogramFrame.setAutoscaleY(true);
		
		random.setSeed(System.currentTimeMillis());
	}

	public void reset()
	{
		N = 16;
		p = 0.5;
		control.setAdjustableValue("N", N);
		control.setAdjustableValue("p", p);
		control.setAdjustableValue("bin width",0.5);
		
		histogramFrame.clearData();
		histogramFrame.repaint();
	}

	public static void main(String[] args)
	{
		SimulationControl.createApp(new VariableStepLengthWalkApp(),args);
	}

	public double sample_x_exponential() {
	      // Generate distribution f(x) = 1 / e^x, for x > 0
	      return -0.5*Math.log(1 - Math.random());   
	}

	public double sample_x_uniform() {
	      // Generate uniform distribution
	      return Math.random();   
	}

	protected void doStep()
	{
		for (int steps = 0; steps < N; steps++)
		{
			if (random.nextDouble() < p)
			{
				x += sample_x_uniform();
			}
			else
			{
				x -= sample_x_uniform();
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
