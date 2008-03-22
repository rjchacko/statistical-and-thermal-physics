/**
 *  computes the area of a function using monte carlo estimation
 *
 * @author    Joshua Gould
 */
package org.opensourcephysics.stp.estimation;
import org.opensourcephysics.display.*;
import org.opensourcephysics.frames.PlotFrame;
import org.opensourcephysics.numerics.*;
import org.opensourcephysics.controls.*;
import java.util.Random;
import java.awt.Color;

public class MonteCarloEstimationApp extends AbstractCalculation
{
	Random rng = new Random();
	int n; // number of trials
	long seed;
	Function function = new MyFunction();
	PlotFrame plotFrame = new PlotFrame("x", "y",
			"Monte Carlo Estimation hits / misses");
	FunctionDrawer functionDrawer;
	double ymax = 1, a = 0, b = 1;

	public MonteCarloEstimationApp()
	{
		functionDrawer = new FunctionDrawer(function);
		functionDrawer.initialize(0, 1, 1000, false);
		plotFrame.setMarkerColor(0, Color.blue);
		plotFrame.setMarkerColor(1, Color.red);
		plotFrame.addDrawable(functionDrawer);
	}

	public void calculate()
	{
		plotFrame.clearData();
		n = control.getInt("n");
		seed = Long.parseLong(control.getString("seed"));
		rng.setSeed(seed);
		long hits = 0;
		double x;
		double y;

		for (int i = 0; i < n; i++)
		{
			x = rng.nextDouble() * (b - a);// nextDouble returns a random
											// double between 0 (inclusive) and
											// 1 (exclusive)
			y = rng.nextDouble() * ymax;
			if (y <= function.evaluate(x))
			{
				hits++;
				plotFrame.append(0, x, y);
			}
			else
			{
				plotFrame.append(1, x, y);
			}
		}

		double estimatedArea = (hits * (b - a) * ymax) / n;
		control.println("estimated area = " + estimatedArea);
	}

	public void resetCalculation()
	{
		control.setValue("n", 1000);
		control.setValue("seed", String.valueOf(System.currentTimeMillis()));
		plotFrame.clearData();
		control.clearMessages();
	}

	public static void main(String[] args)
	{
		CalculationControl.createApp(new MonteCarloEstimationApp(),args);
	}
	private static class MyFunction implements Function
	{
		public double evaluate(double x)
		{
			return Math.sqrt(1 - x * x);
		}
	}
}
