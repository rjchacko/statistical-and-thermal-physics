// Note - the "bin offset" function is no longer available, but it doesn't seem to matter
// as the data are relatively continuous (no spikes).
package org.opensourcephysics.stp.boltzmann;

import java.text.NumberFormat;
import java.util.Random;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.HistogramFrame;
/**
 * example of Metropolis algorithm for a particle in one dimension
 *
 * @author Peter Sibley
 * @author Joshnua Gold
 */
public class BoltzmannApp extends AbstractCalculation
{
	Random random;
	HistogramFrame energyFrame = new HistogramFrame("E", "H(E)", "H(E)");
	HistogramFrame velocityFrame = new HistogramFrame("v", "H(v)", "H(v)");
	// kinetic energy, maximum change in velocity
	double energyAccumulator, velocityAccumulator, beta, velocity, temperature,
			energy, delta;
	// number of MC steps, number of bins
	int mcs, numberOfBins, nequil, accept = 0;
	NumberFormat nf;

	public BoltzmannApp()
	{
		velocityFrame.setBinWidth(0.1);
		// /velocityFrame.setBinOffset(0.05);
		energyFrame.setBinWidth(0.1);
		// /energyFrame.setBinOffset(0.05);
		random = new Random(System.currentTimeMillis());
		energyFrame.setAutoscaleX(true);
		energyFrame.setPreferredMinMaxX(0, 50);
//		energyFrame.setPreferredMinMaxY(0, 10);
		energyFrame.setAutoscaleY(true);
		velocityFrame.setPreferredMinMaxX(-10, 10);
		velocityFrame.setPreferredMinMaxY(0, 10);
		velocityFrame.setAutoscaleX(true);
		velocityFrame.setAutoscaleY(true);
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
	}

	public void calculate()
	{
		clearData();
		temperature = control.getDouble("Temperature");
		beta = 1.0 / temperature;
		mcs = control.getInt("Number of MC steps");
		nequil = mcs / 10;
		velocity = control.getDouble("Initial Speed");
		energy = 0.5 * velocity * velocity;
		delta = 4.0;//control.getDouble("Maximum change in velocity");
		control.println(" running ... ");
		for (int time = 1; time <= mcs; time++)
		{ // equibrate system
			metropolis();
		}
		accept = 0;
		for (int time = 1; time <= mcs; time++)
		{
			metropolis();
			update(); // update data after each trial
		}
		double acceptanceProbability = (double) accept / mcs;
		control.println("acceptance probability = "
				+ nf.format(acceptanceProbability));
		control.println("mean velocity = "
				+ nf.format((double) velocityAccumulator / mcs));
		double meanEnergy = (double) energyAccumulator / mcs;
		control.println("<E> = " + nf.format(meanEnergy));
		energyFrame.repaint();
		velocityFrame.repaint();
	}

	public void reset()
	{
		control.setValue("Temperature", 4.0);
		control.setValue("Number of MC steps", 100000);
		control.setValue("Initial Speed", 1.0);
		//control.setValue("Maximum change in velocity", 4.0);
		control.clearMessages();
		clearData();
		control.println("acceptance probability = 0");
		control.println("mean velocity = 0");
		control.println("<E> = 0");
	}

	public void clearData()
	{
		velocityFrame.clearData();
		energyFrame.clearData();
		energyAccumulator = 0;
		velocityAccumulator = 0;
		accept = 0;
		velocityFrame.repaint();
		energyFrame.repaint();
	}

	public void metropolis()
	{
		double dv = (2.0 * random.nextDouble() - 1.0); // trial velocity change
		double velocityTrial = velocity + dv;
		double dE = 0.5 * (velocityTrial * velocityTrial - velocity * velocity); // trial
		// energy
		// change
		if (dE > 0)
		{
			if (Math.exp(-beta * dE) < random.nextDouble())
				return; // change not accepted
		}
		velocity = velocityTrial;
		energy += dE;
		accept++;
	}

	public void update()
	{
		energyAccumulator += energy;
		velocityAccumulator += velocity;
		velocityFrame.append(velocity);
		energyFrame.append(energy);
	}

	public static void main(String args[])
	{
		CalculationControl.createApp(new BoltzmannApp(),args);
	}
}
