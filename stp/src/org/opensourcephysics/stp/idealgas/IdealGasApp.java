package org.opensourcephysics.stp.idealgas;

import org.opensourcephysics.stp.util.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

public class IdealGasApp extends AbstractSimulation
{
	PlotFrame densityPlot;
	PlotFrame histogramPlot;

	int mcs; // Number of Monte-Carlo steps performed, per particle
	int N; // Number of particles
	double[] v; // v[i] is the i'th particle's velocity (the total particle
	// specification)
	double vdel; // Maximum perturbation to a velocity
	double E; // Energy of current velocity configuration
	double E_max; // Upper bound of allowed energy
	int E_bins; // Number of energy bins; length of g[] and H[]
	int[] H; // Log of histogram; when this becomes flat, reduce f, zero H[],
	// and continue
	double[] g; // ln (g(E)), where g(E) is the density of states as a function
	// of energy
	double f; // The granularity of updates to g(E); decreases with time.

	int energyIndex(double e)
	{
		return (int) (e * E_bins / E_max);
	}

	void perturbParticles()
	{
		for (int iter = 0; iter < N; iter++)
		{
			// Find an arbitrary perturbation
			int r = MathPlus.randomInt(0, N - 1); // Random particle index
			double p = MathPlus.random(-vdel, vdel); // Perturbation of r'th
			// particle
			double dE = p * p + 2 * p * v[r]; // dE = (v+p)^2 - v^2

			// Optionally accept perturbation
			if (E + dE < E_max)
			{
				if (Math.random() < Math.exp(g[energyIndex(E)]
						- g[energyIndex(E + dE)]))
				{
					v[r] += p;
					E += dE;
				}
			}

			// Always update density of states and histogram
			g[energyIndex(E)] += Math.log(f);
			H[energyIndex(E)] += 1;
		}

		double acc = 0;
		for (int i = 0; i < N; i++)
		{
			acc += MathPlus.sqr(v[i]);
		}
		// assert(E - acc < 1e-9);
	}

	boolean isFlat()
	{
		double avgH = (double) sum(H) / E_bins;
		for (int e = 0; e < E_bins; e++)
			if (H[e] == 0 || H[e] < 0.8 * avgH || H[e] > 1.2 * avgH)
				return false;
		return true;
	}

	int sum(int H[])
	{
		int s = 0;
		for (int e = 0; e < E_bins; e++)
		{
			s += H[e];
		}
		return s;
	}

	public void doStep()
	{
		int mcsMax = mcs + Math.max(10000 / N, 1);
		for (; mcs < mcsMax; mcs++)
			perturbParticles();

		control.println("mcs = " + mcs / N);
		if (isFlat())
		{
			f = Math.sqrt(f);
			control.println("f = " + f);
			// IntArray.fill(H, 0);
		}
		densityPlot.clearData();
		histogramPlot.clearData();
		for (int e = 0; e < E_bins; e++)
		{
			densityPlot.append(0, Math.log(e * E_max / E_bins), g[e]);
			histogramPlot.append(0, e * E_max / E_bins, H[e]);
		}
	}

	public void initialize()
	{
		mcs = 0;
		N = control.getInt("Num. particles")
				* control.getInt("Num. dimensions");
		v = new double[N]; // Initialized to [0]
		E = 0;
		E_bins = control.getInt("Num. energy bins");
		E_max = control.getDouble("Maximum energy");
		vdel = control.getDouble("\u2206 Velocity");
		control.clearMessages();
		H = new int[E_bins]; // Initialized to [0]
		g = new double[E_bins];
		f = Math.exp(1);
		control.println("f = " + f);
	}

	public void reset()
	{
		control.setValue("Num. particles", 4);
		control.setValue("Num. dimensions", 1);
		control.setValue("Num. energy bins", 128);
		control.setValue("Maximum energy", 10);
		control.setValue("\u2206 Velocity", 0.4);
		densityPlot.clearDataAndRepaint();
		histogramPlot.clearDataAndRepaint();
	}

	public IdealGasApp()
	{
		densityPlot = new PlotFrame("ln E", "ln \u03C9(E)",
				"Density of States for an Ideal Gas");
		densityPlot.setAnimated(true);
		histogramPlot = new PlotFrame("E", "H(E)", "Histogram for an Ideal Gas");
		histogramPlot.setAnimated(true);
	}

	public static void main(String[] args)
	{
		SimulationControl.createApp(new IdealGasApp(),args);
	}
}
